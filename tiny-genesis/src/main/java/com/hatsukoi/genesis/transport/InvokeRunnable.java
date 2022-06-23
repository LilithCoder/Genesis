package com.hatsukoi.genesis.transport;

import com.hatsukoi.genesis.factory.BeanManager;
import com.hatsukoi.genesis.protocol.Header;
import com.hatsukoi.genesis.protocol.Message;
import com.hatsukoi.genesis.protocol.Request;
import com.hatsukoi.genesis.protocol.Response;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;

/**
 * Server端的Runnable任务
 * @author gaoweilin
 * @date 2022/06/22 Wed 12:29 AM
 */
public class InvokeRunnable implements Runnable {
    private static final Logger logger = Logger.getLogger(InvokeRunnable.class);
    private Message<Request> message;
    private ChannelHandlerContext ctx;

    public InvokeRunnable(Message<Request> message, ChannelHandlerContext ctx) {
        this.message = message;
        this.ctx = ctx;
    }

    /**
     * 实际业务逻辑，调用远程目标方法
     */
    @Override
    public void run() {
        // 远程调用返回结果
        Object result = null;
        Request payload = message.getContent();
        String serviceName = payload.getServiceName();
        String methodName = payload.getMethodName();
        Class[] argTypes = payload.getArgTypes();
        Object[] args = payload.getArgs();
        // 通过的 Bean 管理器获取已注册的 Provider 服务 Bean
        Object bean = BeanManager.getBean(serviceName);
        // 通过反射调用Bean中的相应方法
        try {
            Method method = bean.getClass().getMethod(methodName, argTypes);
            result = method.invoke(bean, args);
        } catch (Throwable t) {
            String err = "Failed to invoke the method: " + methodName + " with the arguments: " + argTypes.toString();
            logger.error(err, t);
        }

        // 构造响应
        Response content = new Response();
        Header header = message.getHeader();
        header.setBaseInfo((byte) 1);
        content.setData(result);

        // 将响应消息返回给客户端
        ctx.writeAndFlush(new Message<Response>(header, content));
    }
}
