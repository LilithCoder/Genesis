# Genesis
Genesis is a high-performance, Java-based open source RPC framework. Glad to accept your contribution.

采用了“微内核+插件”架构，借鉴JDK SPI机制实现了自定义的SPI机制，通过@SPI和@Adaptive注解来标记扩展点，配置文件来标记扩展点实现，进行实现类的加载和实例化



使得插件模块是独立存在的模块，包含特定的功能，能拓展内核系统的功能



### 单例工厂BeanFactory

key为class名，value为实例对象

管理Bean，复用已建立的实例对象，单例对象的工厂类

- ~~实例化的一个业务线程池~~
- 实例化zookeeper注册模块
- 创建一个服务注册表，用来保存服务端本地服务（key为的服务名，value为服务实例）
- 实例化一个序列化器
- 扫描服务
  - 扫描启动类有没有ServiceScan注解
  - 扫描包路径下所有class
  - 如果class有注解Service，找到了服务类实例化，命名一下，加到本地缓存，并通过注册模块去注册服务

### 实例化netty服务器

### netty服务器开启

- JVM增加[关闭钩子](https://www.jianshu.com/p/5e6dffd1776f)，在jvm关闭的时候进行清理zookeeper注册、清理所有线程池
- 新建bossGroup, workGroup线程组，启动类开始netty服务，开始配置

- netty添加channelhandler
  - [IdleStateHandler心跳检测](https://blog.csdn.net/u013967175/article/details/78591810)
    - 添加自定义处理Handler类实现userEventTriggered()方法作为超时事件的逻辑处理，每五秒进行一次读检测，未收到心跳包，断开连接。如果30秒内ChannelRead()方法未被调用则触发一次userEventTrigger()方法
  - 编码
    - 通用的编码拦截器
    - 魔数、请求/响应标识、序列化标识、消息体长度、消息体
  - 解码
    - 通用的解码拦截器
  - 自定义处理Handler类serverHandler，继承ChannlInboundHandlerAdapter
    - I/O读到消息后，如果是心跳包返回
    - 如果是业务消息，去获取服务端本地缓存的本地实例 ，用反射去调用目标方法，返回调用结果，写入channel，返沪repsonse，finally释放消息内存
    - 实现其userEventTriggered()方法，在出现超时事件时会被触发，包括读空闲超时
- [绑定端口](https://www.cnblogs.com/wade-luffy/p/6165626.html)（实现自动绑定递增端口），返回channelFuture异步操作结果(用于异步操作的通知回调)，同步等待成功，调用它的同步阻塞方法sync等待绑定操作完成
- 等待服务端监听端口关闭，.sync()方法进行阻塞,等待服务端链路关闭之后main函数才退出
- Finally 优雅退出，释放线程池资源。调用NIO线程组的shutdownGracefully进行优雅退出，它会释放跟shutdownGracefully相关联的资源

### Netty客户端

- 默认负载均衡为随机负载均衡，根据负载均衡来实例化一个服务发现模块
- 实例化一个序列化器
- 从单例工厂获取一个unprocessedRequests（Connection）实例对象，是一个map，key为messag id，value为的异步响应future对象
- 实例化一个Client动态代理
- 根据要调取的接口去获取代理对象
- 用代理对象去调用目标方法（会调用DemoInvokerHandler.invoke()方法）
  - 构建请求，传入接口名、方法名、实参、参数类型 
  - 发送请求
    - 创建future对象
    - 根据服务名称查找服务实体（zookeeper服务发现模块），获取到所有服务实例（地址，端口），根据负载均衡算法去获取一个实例，实现一个channalProvider用来缓存获取Channel对象（key为地址端口的信息，value为channel对象）
    - 根据服务实例的信息去获取Channel对象，没有的话就说明现在和目标服务没有连接，开始连接
      - 新建启动类去，添加channel handler
        - 心跳、编解码器、自定义handler
      - 建立连接（可以添加失败重试逻辑）
      - 缓存Channel对象
      - 返回的连接Channel对象
    - unprocessedRequests里缓存下messagid和future对象
    - 写入请求，发送请求
  - 返回一个future对象，通过get()获取响应rpc调用结果

