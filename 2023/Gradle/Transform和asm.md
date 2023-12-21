# Gradle Transform和ASM

本文链接：<https://blog.csdn.net/feather_wch/article/details/131753544>

[TOC]

关键词：
transform
asm
org.ow2.asm\:asm

项目路径：<https://github.com/FeatherHunter/StudyNotes/blob/master/assets/DemoProjects/ASMInject.zip>

https://juejin.cn/post/6970614836434731021
transform增量编译


## Trasnform

1、Gradle Transform是什么。😊

1.  Gradle Transform是Android官方提供给开发者在项目构建阶段（.class ->.dex转换期间）用来修改.class文件的一套标准API
2.  可以在不修改源代码的情况下，对.class文件进行自定义的操作，例如插入字节码，修改注解，替换资源等。
3.  Gradle Transform可以用于实现一些高级的功能，例如热修复，代码注入，AOP等。

2、Gradle构建的阶段

*   **初始化阶段**：Gradle会加载项目的配置信息，检查依赖关系，创建任务图等。
*   **编译阶段**：Gradle会调用Java编译器将源代码转换成.class文件，并调用AAPT工具将资源文件转换成二进制格式。
*   **Transform阶段**：Gradle会调用Transform API对输入的.class文件进行自定义的操作，例如插入字节码，修改注解，替换资源等。Transform可以用于实现一些高级的功能，例如热修复，代码注入，AOP等。
*   **DEX阶段**：radle会调用DX工具将.class文件转换成DEX文件，以适应Android平台的运行环境。
*   **打包阶段**：Gradle会调用APK打包器将DEX文件和已编译资源合并成单个APK文件，并对其进行签名和对齐等操作。
*   **安装和运行阶段**：Gradle会将APK文件安装到目标设备或模拟器上，并启动应用程序。


3、Transform阶段的资源来自于

1.  class
2.  jar
3.  resources：特指和java平级的resources文件夹中资源。不是Android的res文件。

4、> Task \:app\:preBuild UP-TO-DATE => 代表最新的没有变化

### Transform实战

ASMPlugin.groovy：Plugin插件，apply中注册Transform

```groovy
class ASMPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

// 注册Transform ASMTransform
        project.extensions.getByType(AppExtension.class).registerTransform(new ASMTransform())
    }
}
```

ASMTransform.groovy：

```groovy
class ASMTransform extends Transform {
// 1、任务的名称
    @Override
    String getName() {
        return "asm";
    }
// 2、可以是Class可以是resources这里只需要Class
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }
// 3、只对引入插件的模块生效
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.PROJECT_ONLY
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        //清理文件
        outputProvider.deleteAll()

        def inputs = transformInvocation.inputs
        inputs.each {
// 4、因为是transformManager.PROJECT_ONLY 只管当前的src目录，不管jar目录，不需要 it.jarInputs
            def directoryInputs = it.directoryInputs
            directoryInputs.each {
                String dirName = it.name
                File src = it.getFile();
                println("源目录：" + src)
// 5、transform是一个一个执行的，上一个transoform的输出是下一个的输入。
// 源目录：F:\5.Android\Projects\ASMInject\ASMInject\app\build\intermediates\javac\debug\compileDebugJavaWithJavac\classes
// 目标目录：F:\5.Android\Projects\ASMInject\ASMInject\app\build\intermediates\transforms\asm\debug\0
                String md5Name = DigestUtils.md5Hex(src.absolutePath)
                File dest = outputProvider.getContentLocation(dirName + md5Name, // 必须保证是唯一的
                        it.contentTypes, // 类型
                        it.scopes, // 作用域
                        Format.DIRECTORY);
//根据这些参数，outputProvider.getContentLocation 会返回一个 File 对象，表示这个内容的位置。如果格式是 Format.DIRECTORY ，那么结果是一个目录的文件位置；如果格式是 Format.JAR ，那么结果是一个表示要创建的 jar 包的文件。
                println("目标目录：" + dest)
                println("dirName：" + dirName) // dirName：e47eef71c08b0d42c571e631eb55645c006d92a2
                println("md5Name：" + md5Name) // md5Name：5f3e38731d3c990d7ad79d6404c3b796
                //插桩
                processInject(src, dest);
            }
        }
    }

    void processInject(File src, File dest) {
        String dir = src.absolutePath
        FluentIterable<File> allFiles = FileUtils.getAllFiles(src)
        for (File file : allFiles) {
            println("需要插桩的文件：" + file.name)
            FileInputStream fis = new FileInputStream(file)
            //插桩
            ClassReader cr = new ClassReader(fis)
            // 写出器
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES)
            //分析，处理结果写入cw
            cr.accept(new ClassInjectTimeVisitor(cw, file.name), ClassReader.EXPAND_FRAMES)

            byte[] newClassBytes = cw.toByteArray()
            //class文件绝对地址
            String absolutePath = file.absolutePath
            //class文件绝对地址去掉目录，得到全类名
            String fullClassPath = absolutePath.replace(dir, "")
            File outFile = new File(dest, fullClassPath)
            FileUtils.mkdirs(outFile.parentFile)
            FileOutputStream fos = new FileOutputStream(outFile)
            fos.write(newClassBytes)
            fos.close()
        }

    }
}


```

ClassInjectTimeVisitor.groovy

```java
    class ClassInjectTimeVisitor extends ClassVisitor {

        String className

        ClassInjectTimeVisitor(ClassVisitor cv, String fileName) {
            super(Opcodes.ASM5, cv);
            className = fileName.substring(0, fileName.lastIndexOf("."))
        }


        @Override
        MethodVisitor visitMethod(int access, String name, String desc, String signature,
                                  String[] exceptions) {

            MethodVisitor mv = super.visitMethod(access, name, desc, signature,
                    exceptions)
    // 修改方法
            return new MethodAdapterVisitor(mv, access, name, desc, className)
        }

    }
```

MethodAdapterVisitor.groovy

```java
    class MethodAdapterVisitor extends AdviceAdapter {

        private String className;
        private String methodName;
        private boolean inject;
        private int index;
        private int start, end;

        protected MethodAdapterVisitor(MethodVisitor mv, int access, String name, String desc,
                                       String className) {
            super(Opcodes.ASM5, mv, access, name, desc);
            methodName = name;
            this.className = className;
        }

        @Override
        AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            //记录方法是不是被 注解
            if ("Lcom/enjoy/asminject/InjectTime;".equals(desc)) {
                inject = true;
            }
            return super.visitAnnotation(desc, visible);
        }


        @Override
        protected void onMethodEnter() {
            if (inject) {
    //                0: invokestatic #2 // Method java/lang/System.currentTimeMillis:()J
    //                3: lstore_1
                //储备本地变量备用
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                index = newLocal(Type.LONG_TYPE);
                start = index;
                mv.visitVarInsn(LSTORE, start);
            }

        }


        @Override
        protected void onMethodExit(int opcode) {
            if (inject) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                index = newLocal(Type.LONG_TYPE);
                end = index;
                mv.visitVarInsn(LSTORE, end);

                // getstatic #3 // Field java/lang/System.out:Ljava/io/PrintStream;
                //获得静态成员 out
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

                // new #4 // class java/lang/StringBuilder
                // 引入类型 分配内存 并dup压入栈顶让下面的INVOKESPECIAL 知道执行谁的构造方法
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);

                //invokevirtual #7   // Method java/lang/StringBuilder.append:
                // (Ljava/lang/String;)Ljava/lang/StringBuilder;
                // 执行构造方法
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>",
                        "()V", false);

                // ldc #6 // String execute:
                // 把常量压入栈顶 后面使用
                mv.visitLdcInsn("==========>" + className + " execute " + methodName + ": ");

                //invokevirtual #7 // Method java/lang/StringBuilder.append: (Ljava/lang/String;)
                // Ljava/lang/StringBuilder;
                // 执行append方法，使用栈顶的值作为参数
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                        "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                // lload_3 获得存储的本地变量
                // lload_1
                // lsub   减法指令
                mv.visitVarInsn(LLOAD, end);
                mv.visitVarInsn(LLOAD, start);
                mv.visitInsn(LSUB);

                // invokevirtual #8 // Method java/lang/StringBuilder.append:(J)
                // Ljava/lang/StringBuilder;
                // 把减法结果append
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                        "(J)Ljava/lang/StringBuilder;", false);

                //append "ms."
                mv.visitLdcInsn("ms.");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                        "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                //tostring
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString",
                        "()Ljava/lang/String;", false);

                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                        "(Ljava/lang/String;)V", false);
            }
        }
    }
```

## ASM实战

ASM:修改字节码的技术

实战分为三个步骤：
1. Java单元测试，实现代码增强
2. buildSrc插件，实现插件+ASM
3. plugin插件，实现插件发布+ASM

### Java单元测试

1、Java单元测试需要添加，单元测试的依赖项
```groovy
//app的build.gradle
//testImplementation Java单元测试
testImplementation 'org.ow2.asm:asm:7.1'
testImplementation 'org.ow2.asm:asm-commons:7.1'

```

2、ASM api => 访问者模式
1. ClassReader
1. ClassWriter

3、ASM读取源class，修改后，生成新的class
```groovy
FileInputStream fis = new FileInputStream(file)
ClassReader cr = new ClassReader(fis)
ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES)
//cr -> 调整 -> cw
cr.accept(new ClassInjectTimeVisitor(cw, file.name), ClassReader.EXPAND_FRAMES)

// byte[] 数据
byte[] newClassBytes = cw.toByteArray()
```

4、ClassVisitor和AdviceAdapter
```groovy
class ClassInjectTimeVisitor extends ClassVisitor{
    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
        // 不能返回原先的，要返回自定义的
        return new MethodAdapterVisitor(mv, access, name, desc, className)
    }
}

class MethodAdapterVisitor extends AdviceAdapter { // extends MethodVisitor

// 在方法入口和出口处，加入代码
    protected void onMethodEnter() {}
    protected void onMethodExit(int opcode) {}
// MethodVisitor.java, 处理注解
    public AnnotationVisitor visitAnnotation(String desc, boolean visible)
}
```

5、Ljava/lang/System;是什么？
1. 是Java字节码中的类型描述符，表示java.lang.System类的类型。
1. L是类型签名
1. ;是结尾


会在需要的目标方法中添加打印时间的日志：

    ==========>MainActivity execute a: 2001ms.
    ==========>MainActivity execute onCreate: 2067ms.

### Android 实战

背景：第三方Jar中API需要在Service中调用，但是内部会调用Dialog，出现问题。
解决办法：
1. 需要弹出Dialog：给Window加标记，允许在Service中弹出
1. 不需要弹出Dialog：直接根据方法名+方法描述找到show方法，直接屏蔽掉
1. Activity弹出、Service不需要：增加条件判断

### 卡顿监控

#### 为什么不能在Handler插桩？===> 双亲委派

SDK的Class文件只是给我们自己用的，最终用的是系统的Handler。

双亲委派导致插桩也没用。



