package com.hatsukoi.genesis.common.timer;

import org.apache.log4j.Logger;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * 时间轮
 * 高效的、批量管理定时任务的调度模型
 *
 * 应用：
 * 1. 失败重试， 例如，Provider 向注册中心进行注册失败时的重试操作，或是 Consumer 向注册中心订阅时的失败重试等
 * 2. 周期性定时任务， 例如，定期发送心跳请求，请求超时的处理，或是网络连接断开后的重连机制
 * 时间轮一般会实现成一个环形结构，类似一个时钟，分为很多槽，一个槽代表一个时间间隔
 * 每个槽使用双向链表存储定时任务；指针周期性地跳动，跳动到一个槽位，就执行该槽位的定时任务
 *
 * HashedWheelTimer 是 Timer 接口的实现，它通过时间轮算法实现了一个定时器。
 * HashedWheelTimer 会根据当前时间轮指针选定对应的槽（HashedWheelBucket），从双向链表的头部开始迭代，
 * 对每个定时任务（HashedWheelTimeout）进行计算，属于当前时钟周期则取出运行，不属于则将其剩余的时钟周期数减一操作。
 *
 * @author gaoweilin
 * @date 2022/06/06 Mon 1:28 AM
 */
public class HashedWheelTimer implements Timer {
    private static final Logger logger = Logger.getLogger(HashedWheelTimer.class);
    /**
     * 时间轮当前所处状态
     */
    private volatile int workerState;
    /**
     * 当前时间轮的启动时间
     */
    private volatile long startTime;
    /**
     * 该数组就是时间轮的环形队列
     */
    private final HashedWheelBucket[] wheel;
    /**
     * timeouts 队列用于缓冲外部提交时间轮中的定时任务
     */
    private final Queue<HashedWheelTimeout> timeouts = new LinkedBlockingQueue<>();
    /**
     * cancelledTimeouts 队列用于暂存取消的定时任务
     */
    private final Queue<HashedWheelTimeout> cancelledTimeouts = new LinkedBlockingQueue<>();
    /**
     * 掩码， mask = wheel.length - 1，执行 ticks & mask 便能定位到对应的时钟槽
     */
    private final int mask;
    private final Thread workerThread;
    /**
     * 真正执行定时任务的逻辑封装这个 Runnable 对象中
     */
    private final Worker worker = new Worker();

    public HashedWheelTimer(HashedWheelBucket[] wheel, int mask) {
        this.wheel = wheel;
        this.mask = mask;
    }


    @Override
    public Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
        return null;
    }

    /**
     * 时间轮中双向链表的节点，即定时任务 TimerTask 在 HashedWheelTimer 中的容器
     * 定时任务 TimerTask 提交到 HashedWheelTimer 之后返回的句柄（Handle），用于在时间轮外部查看和控制定时任务
     */
    private static final class HashedWheelTimeout implements Timeout {
        private static final int ST_INIT = 0;
        private static final int ST_CANCELLED = 1;
        private static final int ST_EXPIRED = 2;

        /**
         * 当前定时任务在链表中的前驱节点
         */
        HashedWheelTimeout next;
        /**
         * 当前定时任务在链表中的后继节点
         */
        HashedWheelTimeout prev;
        /**
         * 实际被调度的任务
         */
        private final TimerTask task;
        /**
         * 定时任务执行的时间 (时间单位为纳秒)
         */
        private final long deadline;
        /**
         * 定时任务当前所处状态
         */
        private volatile int state = ST_INIT;

        private final HashedWheelTimer timer;

        /**
         * 当前任务剩余的时钟周期数
         */
        long remainingRounds;

        HashedWheelBucket bucket;

        private static final AtomicIntegerFieldUpdater<HashedWheelTimeout> STATE_UPDATER =
                AtomicIntegerFieldUpdater.newUpdater(HashedWheelTimeout.class, "state");

        public HashedWheelTimeout(TimerTask task, long deadline, HashedWheelTimer timer) {
            this.task = task;
            this.deadline = deadline;
            this.timer = timer;
        }

        @Override
        public Timer timer() {
            return timer;
        }

        @Override
        public TimerTask task() {
            return task;
        }

        @Override
        public boolean isExpired() {
            return this.state == ST_EXPIRED;
        }

        @Override
        public boolean isCancelled() {
            return this.state == ST_CANCELLED;
        }

        /**
         * 取消当前任务
         *
         * @return
         */
        @Override
        public boolean cancel() {
            // 将当前 HashedWheelTimeout 的状态设置为 CANCELLED
            if (!STATE_UPDATER.compareAndSet(this, ST_INIT, ST_CANCELLED)) {
                return false;
            }
            // 将当前 HashedWheelTimeout 添加到 cancelledTimeouts 队列中等待销毁
            timer.cancelledTimeouts.add(this);
            return true;
        }

        /**
         * 任务到期
         */
        public void expire() {
            // 当任务到期时，会调用该方法将当前 HashedWheelTimeout 设置为 EXPIRED 状态
            if (!STATE_UPDATER.compareAndSet(this, ST_INIT, ST_EXPIRED)) {
                return;
            }
            // 调用其中的 TimerTask 的 run() 方法执行定时任务
            try {
                task.run(this);
            } catch (Throwable t) {
                logger.warn("An exception was thrown by " + TimerTask.class.getSimpleName() + '.', t);
            }
        }
    }

    /**
     * HashedWheelBucket 是时间轮中的一个槽，时间轮中的槽实际上就是一个用于缓存和管理双向链表的容器，
     * 双向链表中的每一个节点就是一个 HashedWheelTimeout 对象，也就关联了一个 TimerTask 定时任务
     * HashedWheelBucket 持有双向链表的首尾两个节点，分别是 head 和 tail 两个字段，
     * 再加上每个 HashedWheelTimeout 节点均持有前驱和后继的引用，这样就可以正向或是逆向遍历整个双向链表了。
     */
    private static final class HashedWheelBucket {
        private HashedWheelTimeout head;
        private HashedWheelTimeout tail;

        /**
         * 新增 HashedWheelTimeout 到双向链表的尾部
         *
         * @param timeout
         */
        void addTimeout(HashedWheelTimeout timeout) {
            assert timeout.bucket == null;
            timeout.bucket = this;
            if (head == null) {
                head = tail = timeout;
            } else {
                tail.next = timeout;
                timeout.prev = tail;
                tail = timeout;
            }
        }

        /**
         * 移除双向链表中的头结点，并将其返回
         *
         * @return
         */
        private HashedWheelTimeout pollTimeout() {
            HashedWheelTimeout head = this.head;
            if (head == null) {
                return null;
            }
            HashedWheelTimeout next = head.next;
            if (next == null) {
                tail = this.head = null;
            } else {
                this.head = next;
                next.prev = null;
            }

            // null out prev and next to allow for GC.
            head.next = null;
            head.prev = null;
            head.bucket = null;
            return head;
        }

        /**
         * 从双向链表中移除指定的 HashedWheelTimeout 节点
         *
         * @param timeout
         * @return
         */
        public HashedWheelTimeout remove(HashedWheelTimeout timeout) {
            HashedWheelTimeout next = timeout.next;
            // remove timeout that was either processed or cancelled by updating the linked-list
            if (timeout.prev != null) {
                timeout.prev.next = next;
            }
            if (timeout.next != null) {
                timeout.next.prev = timeout.prev;
            }

            if (timeout == head) {
                // if timeout is also the tail we need to adjust the entry too
                if (timeout == tail) {
                    tail = null;
                    head = null;
                } else {
                    head = next;
                }
            } else if (timeout == tail) {
                // if the timeout is the tail modify the tail to be the prev node.
                tail = timeout.prev;
            }
            // null out prev, next and bucket to allow for GC.
            timeout.prev = null;
            timeout.next = null;
            timeout.bucket = null;
            timeout.timer.pendingTimeouts.decrementAndGet();
            return next;
        }

        /**
         * 循环调用 pollTimeout() 方法处理整个双向链表，并返回所有未超时或者未被取消的任务
         *
         * @param set
         */
        void clearTimeouts(Set<Timeout> set) {
            for (; ; ) {
                HashedWheelTimeout timeout = pollTimeout();
                if (timeout == null) {
                    return;
                }
                if (timeout.isExpired() || timeout.isCancelled()) {
                    continue;
                }
                set.add(timeout);
            }
        }

        /**
         * 遍历双向链表中的全部 HashedWheelTimeout 节点。
         * 在处理到期的定时任务时，会通过 remove() 方法取出，并调用其 expire() 方法执行；
         * 对于已取消的任务，通过 remove() 方法取出后直接丢弃；
         * 对于未到期的任务，会将 remainingRounds 字段（剩余时钟周期数）减一
         *
         * @param deadline
         */
        void expireTimeouts(long deadline) {
            HashedWheelTimeout timeout = head;

            // process all timeouts
            while (timeout != null) {
                HashedWheelTimeout next = timeout.next;
                if (timeout.remainingRounds <= 0) {
                    next = remove(timeout);
                    if (timeout.deadline <= deadline) {
                        timeout.expire();
                    } else {
                        // The timeout was placed into a wrong slot. This should never happen.
                        throw new IllegalStateException(String.format(
                                "timeout.deadline (%d) > deadline (%d)", timeout.deadline, deadline));
                    }
                } else if (timeout.isCancelled()) {
                    next = remove(timeout);
                } else {
                    timeout.remainingRounds--;
                }
                timeout = next;
            }
        }
    }

    private final class Worker implements Runnable {
        /**
         * 时间轮的指针，是一个步长为 1 的单调递增计数器
         */
        private long tick;

        @Override
        public void run() {

        }
    }
}