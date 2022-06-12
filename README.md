# Genesis
Genesis is a high-performance, Java-based open source RPC framework. Glad to accept your contribution.

采用了“微内核+插件”架构，借鉴JDK SPI机制实现了自定义的SPI机制，通过@SPI和@Adaptive注解来标记扩展点，配置文件来标记扩展点实现，进行实现类的加载和实例化



使得插件模块是独立存在的模块，包含特定的功能，能拓展内核系统的功能
