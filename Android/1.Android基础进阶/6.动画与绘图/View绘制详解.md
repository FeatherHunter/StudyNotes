
1、View的自定义绘制包含哪些部分
>1. 方式：重写绘制方法（`onDraw`）
>2. `Canvas`的绘制类方法：`drawXXX()-关键参数Paint`
>3. `Canvas`的辅助类方法： `cipXXX()-范围裁切`和`几何变换-Matrix`
>4. 使用`不同绘制方法`来控制`遮盖关系`

[Canvas官方文档](http://www.android-doc.com/reference/android/graphics/Canvas.html)

2、Canvas的方法分类
>1. 颜色绘制
>2. 图形绘制


3、Canvas颜色绘制-drawColor/drawRGB/drawARGB
```java
        //纯色
        canvas.drawColor(Color.CYAN);
        //具有透明度
        canvas.drawColor(Color.parseColor("#5500aa00"));
        //通过RBG值设置颜色(范围：0~255)
        canvas.drawRGB(255, 0, 0);
        //通过ARGB值设置颜色
        canvas.drawARGB(100, 0, 0, 255);
```

4、Canvas的drawCircle/drawRect
>1、圆形
```java
        //蓝色圆
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        canvas.drawCircle(200, 200, 180, paint);
        //空心的线宽为10的圆
        paint.reset();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(30); //单位为像素
        canvas.drawCircle(600, 200, 180, paint);
        //开启抗锯齿的圆（抗锯齿会导致图形的失真）
        paint.reset();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawCircle(200, 580, 180, paint);
```
>2、矩形
```java
        //1. 绘制填充型矩形
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, 300, 200, paint);
        //2. 通过Rect绘制矩形(Rect参数为整数)
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        Rect rect = new Rect(320, 0, 700, 150);
        canvas.drawRect(rect, paint);
        //3. 通过RectF绘制矩形
        RectF rectF = new RectF(0, 255.4f, 288.88f, 466.66f);
        paint.setColor(Color.parseColor("#88880000"));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(rectF, paint);

        //4. 绘制圆角矩形
        paint.setColor(Color.MAGENTA);
        paint.setStyle(Paint.Style.FILL);
        // API >= 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //第4\5参数分别为圆角在横向和纵向的半径
            canvas.drawRoundRect(320, 250, 650, 450, 20, 20, paint);
        }else{
            rectF = new RectF(320, 250, 650, 450);
            //第2\3参数分别为圆角在横向和纵向的半径
            canvas.drawRoundRect(rectF, 20, 20, paint);
        }
```
>3、点
```java
        //1. BUII 方形原点
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeWidth(50);
        canvas.drawPoint(60, 60, paint);
        //2. SQUARE 方点
        paint.setColor(Color.RED);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        canvas.drawPoint(200, 60, paint);
        //3. ROUND 圆点
        paint.setColor(Color.GRAY);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawPoint(60, 200, paint);
        //4. 多个点-1(两个数组成一点)
        paint.setColor(Color.CYAN);
        paint.setStrokeWidth(30);
        float[] points = {60, 400, 140, 400, 220, 400, 300, 400};
        canvas.drawPoints(points, paint);
        //5. 多个点-2(会去除前offest个float数值，并按照count取接下来相应数量的float数值)
        float[] points2 = {60, 500, 140, 500, 220, 500, 300, 500};
        canvas.drawPoints(points2, 2, 4, paint);
```
>4、椭圆
```java
        Paint paint = new Paint();
        //1. 绘制椭圆
        RectF rectF = new RectF(50, 50, 350, 200);
        canvas.drawOval(rectF, paint);
        //2. API >= 21 才可以用
        paint.setColor(Color.BLUE);
        canvas.drawOval(50, 300, 350, 550, paint);
```
>5、线
```java
        Paint paint = new Paint();
        paint.setStrokeWidth(20);
        //1. 一条线
        canvas.drawLine(10, 10, 300, 100, paint);
        //2. 一组线
        float[] lines = {10, 110, 300, 110,
                         100, 50, 100, 300};
        paint.setColor(Color.RED);
        canvas.drawLines(lines, paint);
        //3. 一组线(去除offest个float数值，再选取count个float数值作为线的数组)
        float[] lines2 = {10, 210, 200, 210,
                          50, 150, 50, 300,
                          40, 300, 200, 340};
        paint.setColor(Color.GREEN);
        canvas.drawLines(lines2, 4, 8, paint); //没有画第一条线，只有后面两条
```
>6、弧线和扇形
```java
        Paint paint = new Paint();
        //类似于椭圆等API >= 21时才可以用类似API(否则用RectF版本)
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawArc(50, 50, 400, 300, //所在椭圆的四个顶点
                10, //以X轴正方向为0度，此处为10度
                130, //扇形划过的度数，相对于X轴正方向 = 10度+130度=140度
                false, //是否连接到圆心(true=扇形；false=弧线)
                paint);
        //2. 填充 + 连接到圆心
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        canvas.drawArc(450, 50, 800, 300, 10, 130, true, paint);
        //3. 勾线 + 不连接到圆心
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(20);
        canvas.drawArc(50, 450, 400, 700, 10, 130, false, paint);
        //4. 勾线 + 连接到圆心
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(20);
        canvas.drawArc(450, 450, 800, 700, 10, 130, true, paint);
```
>7、自定义图形：Path
```java
        //1. 获取路径
        Path path = new Path();
        path.addArc(200, 200, 400, 400, -225, 225);
        path.arcTo(400, 200, 600, 400, -180, 225, false);
        path.lineTo(400, 542);
        //2. 用Path绘制
        Paint paint = new Paint();
        canvas.drawPath(path, paint);
```

1. drawPicture
2. drawTextOnPath
5. drawBitmap
6. drawBitmapMesh
12. drawPaint()-给Canavs的Bitmap用特定画笔进行填充
1. drawPosText
1. drawText
1. drawVertices-绘制顶点数组

3、Canvas的范围裁剪
1. clipPath
2. clipRect
3. clipRegion

4、Paint类的作用和方法

4、Path是什么？作用？
1. 封装了复合几何路径(由直线部分、二次曲线和三次曲线组成)
2. 能通过`Canvas`的`drawPath`进行绘制(由Paint决定是填充还是线)
3. 能用于`clipping`裁剪过程
4. 也可以在`path`上绘制`Text文本`

5、Path添加图形
>形如`addXXX()`
```java
        Path path = new Path();
        //1. 圆
        path.addCircle(100, 100, 80, Path.Direction.CW); //CW:clockwise(顺时针) CCW: counter-clockwise(逆时针)
        //2. 椭圆
        path.addOval(0, 0, 200, 250, Path.Direction.CW);
        //3. 矩形
        path.addRect(0, 0, 200, 300, Path.Direction.CW);
        path.addRoundRect(0, 0, 200, 300, 20, 20, Path.Direction.CW);
        //4. 弧线
        path.addArc(0, 0, 200, 300, 10, 160); //抬着笔过去画弧线
        //5. 添加path
        Path path2 = new Path();
        path.addPath(path2);
```

6、Path画线(直线或曲线)
```java
        Path path = new Path();
        //0. 设置起点-不会绘制横线
        path.moveTo(20, 50);
        //1. 画线
        path.lineTo(100, 100);    //绝对坐标:当前位置画线到(100, 100)
        path.rLineTo(100, 200); //相对坐标: 画线到(当前X + 100， 当前Y + 200)

        //2. 贝塞尔曲线(quadTo/rQuadTo=绝对/相对)
        path.quadTo(300, 200, //控制点
                400, 300);    //终点

        //3. 三次贝塞尔曲线(相对坐标-rCubicTo)
        path.cubicTo(200, 200, //控制点1
                400, 400,      //控制点2
                250, 250);     //终点

        //4. 直接到目标椭圆区域绘制椭圆
        path.arcTo(300, 300, 500, 600, -10, 150,
                false); //forceMoveTo=true: 不留下移动痕迹; =false: 会留下移动痕迹
        /**=============================
         *5. 封闭路径：当前位置绘制到起点
         *  1. Paint.Style = FILL / FILL_AND_STROKE
         *      会自动封闭路径，不需要close
         *==============================*/
        path.close(); //=lineTo(起点坐标)
        //绘制路径
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        canvas.drawPath(path, paint);
```

8、贝塞尔曲线是什么？
>1.

5、Rect和RectF之间的区别和联系
>1. `Rect`的参数是`int`，`RectF`的参数为`float`
>2. 都是通过`四个坐标`来确定一个矩形的区域。
>3. 通过`RectF的contain(RectF f)`能判断一个矩形是否在该举行内

6、Region是什么？作用？
>1. 使用`Region`可以对图形进行操作，比如`区域的合并-取交集、取异或`等等
https://blog.csdn.net/cquwentao/article/details/51365099

http://hencoder.com/ui-1-1/

##知识储备-考考你
1、矢量是什么？
> 1. `矢量（vector）`是一种既有`大小`又有`方向`的量，又称为`向量`。
> 2. 一般在物理学中称作`矢量`，例如速度、加速度、力等等就是这样的量。
> 3. 舍弃实际含义，就抽象为`数学`中的概念──`向量`。
> 4. 在计算机中，`矢量图`可以`无限放大永不变形`。

##参考资料
1. [贝塞尔曲线扫盲](http://www.html-js.com/article/1628)
