转载请注明链接：https://blog.csdn.net/feather_wch/article/details/82665902

> Java提供了哪些IO方式？ NIO如何实现多路复用？

# Java IO和NIO

版本号：2018/9/13-1(18:22)

---

![IO 和  NIO](https://static001.geekbang.org/resource/image/68/54/689506651da549777f11cfb98f1c5a54.jpg)

[TOC]

---

## 基本概念(12)

1、Java IO方式有哪些?
> 1. 传统java.io包：文件的抽象、输入流输出流
> 1. java.net包: 网络通信同样是IO行为
> 1. java.nio包：Java1.4中引入了NIO框架
> 1. java7的NIO2：引入了异步非阻塞IO方式

2、按照阻塞方式分类
> 1. BIO: 同步、阻塞
> 1. NIO：同步、非阻塞
> 1. NIO2/AIO：异步、非阻塞

3、传统java.io包中的IO有什么特点？
> 1. 基于stream模型实现
> 1. 提供了常见的IO功能: File抽象、输入输出流
> 1. 交互方式：同步、阻塞的方式
> 1. 在读写动作完成前，线程会一直阻塞在那里，它们之间的调用是可靠的线性顺序。

4、Stream(流)到底是什么？有什么用？
> 1. Out：代表能产出数据的数据源对象
> 1. In：代表能接受数据的数据源对象
> 1. 作用：为数据源和目的地搭建一个传输通道

5、java.io包的好处和缺点
> 1. 优点：代码简单、直观
> 1. 缺点：IO效率、扩展性存在局限性，会成为应用性能的瓶颈

6、java.net下的网络通信的IO行为
> 1. java.net下的网络API：Socket、ServerSocket、HttpURLConnection
> 1. 这些也都属于同步阻塞IO类库

7、NIO框架是什么?
> 1. Java 1.4中引入
> 1. 位于java.nio 包
> 1. 提供了 Channel、Selector、Buffer 等新的抽象

8、NIO的特点?
> 1. 可以构建多路复用的、同步非阻塞 IO 程序
> 1. 同时提供了更接近操作系统底层的高性能数据操作方式。

9、NIO2或者AIO是什么？
> 1. NIO 2，又称为AIO（Asynchronous IO）。
> 1. 在 Java 7 中，对NIO进一步改进。
> 1. 引入了异步非阻塞 IO 方式，也
> 1. 异步 IO 操作基于事件和回调机制---应用操作直接返回，而不会阻塞，当后台处理完成后，操作系统会通知相应线程进行后续工作。

### 同步和异步

10、同步和异步的区别？
> 1. 同步-synchronous
> 1. 异步-asynchronous
> 1. 同步是一种可靠的有序运行机制，同步操作时，后续的任务会等待当前调用的返回。
> 1. 异步中，其他任务不会等待当前调用返回，通常依靠事件、回调等级制来实现任务间次序关系

### 阻塞和非阻塞

11、阻塞和非阻塞的区别?
> 1. 阻塞-blocking
> 1. 非阻塞-non-blocking
> 1. 阻塞操作时，当前线程会处于阻塞状态，无法进行其他任务，只有当满足一定条件时，才继续执行
> 1. 非阻塞状态，不会去等待IO操作结束，会立即返回。相应操作会在后台处理

12、阻塞和同步就是低效的操作？
> 错误！
> 需要根据应用的实际场景。有些时候必须要进行阻塞和同步。

## IO/BIO(29)

1、传统IO操作就是指对文件进行操作?
> 错误！
> 1. 文件操作是IO操作
> 1. 网络编程中，如Socket通信，都是典型的IO操作

2、IO流是什么吗？有什么用
> 1. Input流和Output流
> 1. 主要用于处理设备间的数据传输

3、IO流的两种分类方式
> 1. 字节流和字符流
> 1. 输入流和输出流

4、字节流的抽象基类？
> 1. InputStream
> 1. OutputStream

5、字符流的抽象基类
> 1. Reader
> 1. Writer

6、字符流中融合了编码表
> 系统默认的一般采用GBK

7、字符流与字节流的区别
> 1. 处理对象不同：
>       1. 字节流能处理所有类型的数据（如图片、多媒体等）
>       1. 字符流只能处理字符类型的文本数据。
> 1. 读写单位不同：
>       1. 字节流以字节byte为单位，1byte=8bit。
>       1. 字符流以字符为单位，1个字符=2个字节(java中采用unicode编码)。根据码表映射字符，一次可能读多个字节。
> 1. 有无缓冲区：
>       1. 字节流没有缓冲区，是直接输出的。字节流不调用colse()方法时，信息就已经输出了。
>       1. 字符流是输出到缓冲区的。只有在调用close()方法关闭缓冲区时，信息才输出。要想字符流在未关闭时输出信息，则需要手动调用flush()方法。

8、字符流让信息输出的两种办法
> 1. close()关闭缓冲区时，信息会输出。
> 1. 手动调用flush()来输出信息。

9、字符流和字节流如何选择？
> 1. 只要是处理纯文本数据，就优先考虑使用字符流
> 1. 除此之外都使用字节流

10、Closeable 接口
> 1. 很多 IO 工具类都实现了 Closeable 接口，因为需要进行资源的释放。
> 1. 需要利用try-with-resources、 try-finally 等机制保证 资源被释放
> 1. Cleaner 或 finalize 机制作为资源释放的最后把关，也是必要的。

11、Java传统IO相关的类图
![Java传统IO相关的类图](https://static001.geekbang.org/resource/image/43/8b/4338e26731db0df390896ab305506d8b.png)

12、java.io包中六大类和接口
> 1. File、RandomAccessFile、InputStream、OutputStream、Reader、Writer
> 1. Serializable

13、InputStream/OutputStream 和 Reader/Writer 的关系和区别。
> 1. 都实现了Closeable接口，用于资源的释放。
> 1. 字节流: InputStream/OutputStream
> 1. 字符流: Reader/Writer

14、Java I/O 主要的三个部分
> 1. 流式部分-IO主体部分
> 1. 非流式部分-一些辅助流式部分的类：File、RandomAccessFile、FileDescriptor
> 1. 其他类-文件读取部分、安全相关的类

### File

15、File类
> 1. 采用File文件作为类名并不准确.
> 2. 本质上是文件路径，使用FilePath会更准确。

16、创建的新文件，为什么只有很少的内容，也会占据几KB?
> 操作系统有最小分配空间

17、不同文件的开头会包含该文件类型相关信息

18、文件的创建
```java
//创建文件
File file = new File("d:\\a.txt");
if(file.exists() == false)
{
    file.createNewFile();
}
```

19、文件夹的创建
```java
//创建文件夹
File fileFolder = new File("d:\\New Folder");
if(fileFolder.isDirectory() == false)
{
    fileFolder.mkdir(); //创建folders
}
```

20、列出文件夹内所有文件
```java
//列出所有文件
File fileFolder = new File("d:\\New Folder");
if(fileFolder.isDirectory() == false)
{
    File []files = fileFolder.listFiles();
    for (File file : files) {
        file.getName();
    }
}
```

### RandomAccessFile

21、RandomAccessFile是什么?
> 1. 随机文件操作
> 1. 一个独立的类，直接继承至Object.
> 1. 功能丰富，可以从文件的任意位置进行存取（输入输出）操作。

### InputStream

22、输入流/输出流的作用?
> 1. InputStream/OutputStream
> 1. 是用于读取或写入字节的，例如操作图片文件。

23、FileInputStream的注意点
> 1. 打开 FileInputStream，会获取相应的文件描述符（FileDescriptor）
> 1. 需要利用try-with-resources、 try-finally 等机制保证 FileInputStream 被明确关闭，进而相应文件
描述符也会失效，否则将导致资源无法被释放。

#### FileInputStream

24、FileInputStream读取文件中数据
```java
        // 1、创建文件
        File file = new File("d:\\a.txt");
        // 2、创建输入流(字节流)
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[1024];
        int n;
        // 3、从输入流中读取数据，存放到byte数组中
        while((n = fileInputStream.read(bytes)) != -1)
        {
            // 4、创建String并且显示
            String s = new String(bytes, 0, n);
            System.out.println(s + "\r\n"); //换行
        }
        // 5、关闭输入流
        fileInputStream.close();
```


### OutputStream

#### FilterOutputStream

##### BufferedOutputStream

25、BufferedOutputStream的作用?
> 1. BufferedOutputStream 等带缓冲区的实现，
> 1. 可以避免频繁的磁盘读写，进而提高 IO 处理效率。
> 1. 这种设计利用了缓冲区，将批量数据进行一次操作，
> 1. 使用中一定要`flush`。

### Reader

26、Reader/Writer的作用?
> 1. Reader/Writer 则是用于操作字符
> 1. 增加了字符编解码等功能
> 1. 适用于从文件中读取或者写入文本信息等操作。
> 1. 本质上计算机操作的都是字节(不管是网络通信还是文件读取)，Reader/Writer 相当于构建了应用逻辑和原始数据之间的桥梁。

#### InputStreamReader

##### FileReader

27、FileReader读取文件中数据
```java
        // 1、创建文件
        File file = new File("d:\\a.txt");
        // 2、创建输入流(字符流)
        FileReader fileReader = new FileReader(file);
        char[] chars = new char[1024];
        int n;
        // 3、从输入流中读取数据，存放到byte数组中
        while((n = fileReader.read(chars)) != -1)
        {
            // 4、创建String并且显示
            String s = new String(chars, 0, n);
            System.out.println(s + "\r\n"); //换行
        }
        // 5、关闭输入字符流
        fileReader.close();
```

#### BufferedReader

28、BufferedReader的作用
> 1. 包装Reader的子类
> 1. 增加缓存区的功能

29、BufferedReader的使用
```java
        // 1、创建FileReader
        FileReader fileReader = new FileReader(new File("d:\\a.txt"));
        // 2、创建BufferedReader，利用缓存区增强性能，并且提供readline()功能
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        // 3、从输入流中读取数据，存放到byte数组中
        String str;
        while((str = bufferedReader.readLine()) != null)
        {
            System.out.println(str); //换行
        }
        // 4、关闭BufferedReader
        bufferedReader.close();
```

### Writer

#### PrintWriter

30、PrintWriter的作用
> 1. 向文本输出流, 以格式化的形式, 打印数据。
> 1.

31、PrintWriter的使用
```java
        // 1、创建PrintWriter
        PrintWriter printWriter = new PrintWriter(new File("d:\\a.txt"));
        // 2、向文件中写入数据。原来的所有数据会先删除。然后依次写入数据
        printWriter.append("Hello");
        printWriter.write("World!");
        printWriter.print("Godebye");
        // 3、刷新缓存区
        printWriter.flush();
        printWriter.close();
```

32、write、print、append之间的区别？
> 1. 效果上没有区别，都是写入数据。
> 1. 返回值上会有区别，append()会返回`printWriter`，可以进行链式调用。
> 1. write和print都没有返回值。
> 1. print()参数为(String)null，会打印出null
> 1. write()参数为null，会有空指针异常。

## 网络IO(4)

1、Socket简单实现客户端和服务端通信
> 1-服务端：建立ServerSocket，等待客户端连接，然后处理数据。
```java
public class DemoSocketServer extends Thread{
    private ServerSocket serverSocket;
    public int getPort(){
        return serverSocket.getLocalPort();
    }
    @Override
    public void run() {
        try {
            // 1、服务端启动ServerSocket，端口=0，表示自动绑定一个空闲端口
            serverSocket = new ServerSocket(0);
            while (true){
                // 2、阻塞等待一客户端的连接
                Socket socket = serverSocket.accept();
                // 3、处理客户端(新建一个线程)
                RequestHandler requestHandler = new RequestHandler(socket);
                requestHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            // 任何情况下都要保障Socket资源关闭。
            if(serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    // 客户端请求的Handler
    public static class RequestHandler extends Thread{
        private Socket mSocket;
        RequestHandler(Socket socket){
            mSocket = socket;
        }
        @Override
        public void run() {
            try {
                // 1、Socket的输出流来创建printWriter
                PrintWriter printWriter = new PrintWriter(mSocket.getOutputStream());
                // 2、写入数据
                printWriter.println("Hello World!");
                printWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```
> 2-客户端(简单的打印数据)：借助try-with-resources，用Reader去读取数据。
```
// 客户端
public class Main {
    public static void main(String[] args) throws IOException {
        DemoSocketServer server = new DemoSocketServer();
        server.start();
        // 1、Socket客户端，绑定Server端Host地址，和Server端的端口。(这边是本机)
        try (Socket client = new Socket(InetAddress.getLocalHost(), server.getPort())) {
            // 2、通过客户端的inpustream，创建Reader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            // 3、从Reader中读取到数据，并且打印。
        }
    }
}
```

2、线程池改进服务端
> 1. 需要减少线程频繁创建和销毁的开销
```java
// 线程池
private Executor mExecutor;
@Override
public void run() {
    try {
        serverSocket = new ServerSocket(0);
        // 1、创建线程池：只有核心线程数，没有非核心线程数。任务队列无限。空闲线程会立即停止
        mExecutor = Executors.newFixedThreadPool(8);
        while (true){
            Socket socket = serverSocket.accept();
            RequestHandler requestHandler = new RequestHandler(socket);
            // 2、线程池进行处理
            mExecutor.execute(requestHandler);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }finally {
        // 任何情况下都要保障Socket资源关闭。
        if(serverSocket != null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

3、服务端采用线程池来提供服务的典型工作模式图
![典型工作模式](https://static001.geekbang.org/resource/image/da/29/da7e1ecfd3c3ee0263b8892342dbc629.png)

4、服务端采用线程池处理客户端连接的缺点？
> 1. 连接数几百时，这种模式没有问题。
> 1. 但是在高并发，客户端及其多的情况下，就会出现问题。
> 1. 线程上下文切换的开销会在高并发时非常明显。
> 1. 这就是同步阻塞方式的低扩展性的体现

## NIO

1、NIO的主要组成部分
> 1. Buffer
> 1. Channel
> 1. Selector
> 1. ChartSet

2、Buffer的作用？
> 1.高效的数据容器
> 1.除了布尔类型，所有原始数据类型都有相应的 Buffer 实现。

3、Channel的作用？
> 1. 类似在Linux操作系统上的文件描述符
> 1. 一种操作系统底层的抽象
> 1. 用来支持批量式IO操作

4、Channel 是操作系统底层的一种抽象。
> 1. File 或者 Socket，通常被认为是比较高层次的抽象
> 1. Channel 是更加操作系统底层的一种抽象
> 1. 使得 NIO 得以充分利用现代操作系统底层机制，获得特定场景的性能优化，
> 1. 例如，DMA（Direct Memory Access）等。
> 1. 不同层次的抽象是相互关联的，Socket和Channel之间能相互获取。

5、Selector的作用
> 1. 是 NIO 实现多路复用的基础，
> 1. 它提供了一种高效的机制，可以检测到注册在Selector 上的多个 Channel 中，是否有 Channel 处于就绪状态，进而实现了单线程对多Channel 的高效管理。
> 1. Selector也是基于底层操作系统机制的，不同模式、不同版本都存在区别。
> 1. Linux 上依赖于epoll
> 1. Windows 上 NIO2（AIO）模式则是依赖于iocp

6、Linux中的epoll是什么?

7、Chartset的作用
> 1. 提供 Unicode 字符串定义，
> 1. NIO 也提供了相应的编解码器等，
> 1. 例如，通过下面的方式将字符串转换到 ByteBuffer：
```java
Charset.defaultCharset().encode("Hello world!"));
```

### SelectionKey
8、SelectionKey是什么？
> 1. 表示`SelectableChannel`在Selector中注册的句柄/标记
> 1. `serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);`会返回注册事件的句柄。

9、一个Selector对象包含三种类型的SelectionKey集合
||||
|---|---|---|
|all-keys|当前所有向Selector注册的Channel的句柄(SelectionKey)的集合|           selector.keys()|
|selected-keys|相关事件已经被Selector捕获的SelectionKey的集合|           selector.selectedKeys()|
|cancelled-keys|已经被取消的SelectionKey的集合|          无API|


10、SelectionKey何时被新建？何时会被加入到Selector的all-keys集合中？
> 1. Channel注册到Selector中时, 会新建一个SelectionKey，然后加入到all-keys集合中。
> 1. serverSocketChannel.register(selector, xxx)

11、SelectionKey对象何时会被遗弃(加入到cancelled-keys集合中)？
> 1. SelectionKey相关的Channel被关闭
> 1. 调用了`SelectionKey.cancel()`方法

### ChartSet

12、ByteBuffer转换为String
```java
Charset charset = Charset.defaultCharset();
// asReadOnlyBuffer将Buffer复制一份出来。
CharBuffer charBuffer = charset.decode(byteBuffer.asReadOnlyBuffer());
String string = charBuffer.toString();
```

### ByteBuffer
13、ByteBuffer是什么？
> 1. NIO中使用的Byte Buffer
> 1. 包含两个实现方法：
>       1. HeapByteBuffer: 基于Java堆的实现
>       1. DirectByteBuffer: 堆外的实现方法，采用了`unsafe API`

14、从Channel中读取数据到ByteBuffer中
```java
byteBuffer = ByteBuffer.allocate(N);
//读取数据，写入byteBuffer
socketChannel.read(byteBuffer);

// 翻转，才能打印出来
byteBuffer.flip();
System.out.println("receive msg from client: "+Charset.defaultCharset().decode(byteBuffer.asReadOnlyBuffer()).toString());
```

15、向Channel中写入数据
```java
socketChannel.write(Charset.defaultCharset().encode("Hello World!"));
```

#### flip

16、ByteBuffer.flip()
> 1. 进行翻转。将limit设置到position，然后将position复位到0。
> 1. 从Channel中read后，立即用于写数据。
```java
ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
// 1、读取到数据
socketChannel.read(byteBuffer);
// 2、翻转
byteBuffer.flip();
// 3、发送给Client
socketChannel.write(byteBuffer);
```



### NIO实例


17、NIO优化服务端连接问题的实例
```
public class NIOServer extends Thread{
    @Override
    public void run() {
        try (// 1、创建Selector。调度员的角色。
             Selector selector = Selector.open();
             /**-------------------------
              * 2、创建Channel。并进行配置。
              *---------------------------*/
             ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()){
             // 1. 绑定IP和端口
             serverSocketChannel.bind(new InetSocketAddress(InetAddress.getLocalHost(), 8888));
             // 2. 非阻塞模式。因为阻塞模式下是不允许注册的。
             serverSocketChannel.configureBlocking(false);
            /**-------------------------
             * 3、向Selector进行注册。通过OP_ACCEPT，表明关注的是新的连接请求
             *---------------------------*/
             serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
             while(true){
                 // 4、Selector调度员，阻塞在select操作。当有Channel有接入请求是，会被唤醒
                 selector.select();
                 // 5、被唤醒，获取到事件已经被捕获的SelectionKey的集合
                 Set<SelectionKey> selectionKeys = selector.selectedKeys();
                 Iterator<SelectionKey> iterator = selectionKeys.iterator();
                 while (iterator.hasNext()){
                     SelectionKey selectionKey = iterator.next();
                     // 6、从SelectionKey中获取到对应的Channel
                     handleRequest((ServerSocketChannel) selectionKey.channel());
                     iterator.remove();
                 }
             }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 处理客户端的请求
    private void handleRequest(ServerSocketChannel server){
        // 1、获取到连接到该Channel Socket的连接
        try(SocketChannel client = server.accept()) {
            // 2、向Channel中写入数据
            client.write(Charset.defaultCharset().encode("Hello World!"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

18、NIO为什么比IO同步阻塞模式要更好？
> 1. 同步阻塞模式需要多线程来处理多任务。
> 1. NIO利用了单线程轮询事件的机制，高效定位就绪的Channel。
> 1. 仅仅是`select`阶段是阻塞的，可以避免大量客户端连接时，频繁切换线程带来的问题。

19、NIO实现网络通信的工作模式图
![工作模式图](https://static001.geekbang.org/resource/image/ad/a2/ad3b4a49f4c1bff67124563abc50a0a2.png)


20、NIO能解决什么问题？
> 1. 服务端多线程并发处理任务，即使使用线程池，高并发处理依然会因为上下文切换，导致性能问题。
> 1. NIO是利用单线程轮询事件的机制，高效的去选择来请求连接的Channel仅提供服务。

21、为什么需要多路复用？

22、NIO多路复用的局限性
> 1. 当有IO请求在数据拷贝阶段。
> 1. 由于资源类型过于庞大，会导致线程长期阻塞
> 1. 造成性能瓶颈


22、NIO 提供的高性能数据操作方式是基于什么原理，如何使用？

23、从开发者的角度来看，NIO 自身实现存在哪些问题？有什么改进的想法吗？

24、NIO的请求接收和处理都是在一个线程处理，如果有多个请求的处理顺序是什么？
> 1. 多个请求会按照顺序处理
> 1. 如果一个处理具有耗时操作，会阻塞后续操作。
> 1.

25、NIO是否应该在服务端开启多线程进行处理？
> 1. 我觉得是可以的

25、NIO遇到大量耗时操作该怎么办？
> 1. 如果有大量耗时操作，那么整个`NIO模型`就不适用于这种场景。？？感觉可以开多线程。
> 1. 过多的耗时操作，可以采用传统的IO方式。

26、selector在单线程下的处理监听任务会成为性能瓶颈？
> 1. 是的。单线程中需要依次处理监听。会导致性能问题。
> 1. 在并发数数万、数十万的情况下，会导致性能问题。
> 1. Doug Lea推荐使用多个`selector`，在多个线程中并发监听Socket事件

## NIO2
1、NIO2
> 1. Java 7引入了NIO 2
> 1. 提供了一种额外的异步IO模式
> 1. 利用事件和回调，处理`Accept、Read`等操作。

3、NIO 2 也不仅仅是异步

2、Future

3、CompletionHandler

4、Reactor和Proactor模式需要和Netty主题一起

5、NIO和NIO2的类似处
> 1. AsynchronousServerSocketChannel对应ServerSocketChannel
> 1. AsynchronousSocketChannel对应SocketChannel

6、NIO2的局限性

### AsynchronousServerSocketChannel

1、AsynchronousServerSocketChannel
```
// 1、创建AsynchronousServerSocketChannel
AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open()){
// 2、绑定IP和端口
serverSocketChannel.bind(new InetSocketAddress(InetAddress.getLocalHost(), 8888));

// 3、为异步操作，指定CompletionHandler回调。
serverSocketChannel.accept(serverSocketChannel, new CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>() {
        @Override
        public void completed(AsynchronousSocketChannel result, AsynchronousServerSocketChannel attachment) {
               serverSocketChannel.accept(serverSocketChannel, this);
            handleRequest(result);
        }

        @Override
        public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {

        }});
```

## 知识扩展

1、开启一个线程需要多少内存消耗？(32位和64位)

## 问题汇总

## 参考资料
1. [极客时间-第11讲 | Java提供了哪些IO方式？ NIO如何实现多路复用？](https://time.geekbang.org/column/article/8369)
1. [Java NIO 英文博客详解](http://tutorials.jenkov.com/java-nio/nio-vs-io.html)
1. [【Java.NIO】Selector，及SelectionKey](https://blog.csdn.net/robinjwong/article/details/41792623)
1. [图解ByteBuffer](https://my.oschina.net/flashsword/blog/159613)
