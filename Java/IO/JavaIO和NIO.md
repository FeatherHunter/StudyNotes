转载请注明链接：https://blog.csdn.net/feather_wch/article/details/82665902

> Java提供了哪些IO方式？ NIO如何实现多路复用？

# Java IO和NIO

版本号：2018/9/13-1(0:22)

---

[TOC]

![IO 和  NIO](https://static001.geekbang.org/resource/image/68/54/689506651da549777f11cfb98f1c5a54.jpg)

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
> 1. 它提供了一种高效的机制，可以检测到注册在Selector 上的多个 Channel 中，是否有 Channel 处于就绪状态，进而实现了单线程对多
Channel 的高效管理。
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

8、NIO能解决什么问题？

9、为什么需要多路复用？


2、NIO 提供的高性能数据操作方式是基于什么原理，如何使用？

3、从开发者的角度来看，NIO 自身实现存在哪些问题？有什么改进的想法吗？

### NIO2
2、NIO2的基本组成。

或者，从开发者的角度来看，你觉得 NIO 自身实现存在哪些问题？有什么改进的想法吗？
IO 的内容比较多，专栏一讲很难能够说清楚。IO 不仅仅是多路复用，

3、NIO 2 也不仅仅是异步


## 问题汇总

## 参考资料
1. [极客时间-第11讲 | Java提供了哪些IO方式？ NIO如何实现多路复用？](https://time.geekbang.org/column/article/8369)
1. [Java NIO 英文博客详解](http://tutorials.jenkov.com/java-nio/nio-vs-io.html)
