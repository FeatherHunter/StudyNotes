转载请注明：https://blog.csdn.net/feather_wch/article/details/79783182

#View绘制详解
版本: 2018/4/1-1

[TOC]

##Canvas

1、View的自定义绘制包含哪些部分
>1. 方式：重写绘制方法（`onDraw`）
>2. `Canvas`的绘制类方法：`drawXXX()-关键参数Paint`
>3. `Canvas`的辅助类方法： `cipXXX()-范围裁切`和`几何变换-Matrix`
>4. 使用`不同绘制方法`来控制`遮盖关系`

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

4、Canvas图形绘制-drawXXX
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

##Path

5、Path是什么？作用？
>1. 封装了复合几何路径(由直线部分、二次曲线和三次曲线组成)
>2. 能通过`Canvas`的`drawPath`进行绘制(由Paint决定是填充还是线)
>3. 能用于`clipping`裁剪过程
>4. 也可以在`path`上绘制`Text文本`

6、Path添加图形
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

7、Path画线(直线或曲线)
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

8、Path的辅助类方法：
>`path.setFillType(Path.FillType.EVEN_ODD);`
>1. `EVEN_ODD`: 在平面内任意一点向任意方向射出一条射线，该射线和整个图形相交的次数，如果是`奇数`则该点`在图形内`；如果是`偶数`则该点`不在图形内`。
> 2. `WINDING`: 在平面内任意一点向任意方向射出一条射线, 该线和整个图形的线`(都是有向线)`相交的点数(`顺时针和逆时针点数一一抵消`)如果有剩余则表示`在图形内`，否则`不在图形内`.
> 3. `INVERSE_EVEN_ODD`和`EVEN_ODD`规则相反。
> 4. `INVERSE_WINDING`和`WINDING`规则相反
```java
        Path path = new Path();
        path.addCircle(100, 100, 80, Path.Direction.CW);
        path.addCircle(222, 100, 80, Path.Direction.CW);
        //1. EVEN_ODD是奇内偶外原则
        path.setFillType(Path.FillType.EVEN_ODD);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(10);
        canvas.drawPath(path, paint);
```
![setFillType](https://ws2.sinaimg.cn/large/006tNc79ly1fig820pdt3j30kw0ummzx.jpg)

9、Canvas的Bitmap绘制
```java
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(10);
        //1. 0,0位置开始绘制
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        //2. 在`src的Rect`的区域中绘制(src为RectF版本只是参数为float而已)
        Rect rect = new Rect(0, 50, 500, 400);
        canvas.drawBitmap(mBitmap, null, //Bitmap需要被绘制的子集
                rect, //Bitmap会被缩放并平移至Rect(参数为int)指明的去榆中
                paint);
        //3. Matrix对图片处理(平移，缩放，倾斜，旋转等等)
        Matrix matrix = new Matrix();
        matrix.preRotate(15, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);// 设置旋转，后面2个参数为变换的中心位置，下同
        canvas.drawBitmap(mBitmap, matrix, paint);
        //4. 将Bitmap用网格分隔-实现扭曲效果
        float verts[] = {10, 20};
        canvas.drawBitmapMesh(mBitmap, 5, 5, //纵向和横向的格数
                verts,        //扭曲后图片各顶点坐标(meshWidth + 1)*(meshHeight + 1)*2 + vertOffset
                0,  //从第几个顶点开始扭曲，一般为0
                null,  //数组-设置顶点颜色，可以和Bitmap对应像素的颜色相加。一般为null
                0, //从第几个顶点开始转换颜色，一般为0
                null);
```

##Paint
12、Paint类的作用和方法
>1. 翻译为`颜料`，可以理解为`画笔`的作用。
>2. `Paint`的API大致分为四类：1-初始化；2-颜色；3-效果；4-文字相关

13、Paint的初始化类方法
```java
        Paint paint = new Paint();
        //1. 重置所有属性为默认值。比new性能高。
        paint.reset();
        //2. 从oldPaint中将所有属性复制到当前Paint
        paint.set(oldPaint);
        //3. 批量设置flags(如抗锯齿、抖动)
        paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.getFlags(); //相反能获得Flags
```

14、Canvas有三层对颜色的处理
> 1. 基本颜色处理-Canvas的drawColor、drawBitmap以及用`paint(setColor和使用Shader着色器)`绘制颜色
> 2. ColorFilter-颜色过滤`Paint.setColorFilter(ColorFilter)`
> 3. Xfermode-`Paint.setXfermode(Xfermode)`

15、Paint基本颜色处理的方法:
> 1. 直接用`paint.setColor/ARGB()`设置颜色
> 2. 用`Shader(着色器)`指定着色方案
>
>1、直接设置颜色
```java
        Paint paint = new Paint();
        paint.setTextSize(200);
        //1. setColor设置颜色
        paint.setColor(Color.parseColor("#009966"));
        //2. ARGB设置颜色
        paint.setARGB(100, 0, 0, 0);
        canvas.drawText("Feather", 200, 200, paint);
```
>2、`Shader(着色器)`指定-`Shader类`具有5种子类
```java
        Paint paint = new Paint();
        paint.setTextSize(200);
//线性渐变(CLAMP-钳子模式；MIRROR-镜面模式；REPEAT-重复模式)
        LinearGradient linearGradient = new LinearGradient(
                50, 50, 500, 500, //两个端点坐标
                Color.BLUE, Color.CYAN,          //两个端点颜色
                Shader.TileMode.CLAMP);          //着色规则
        paint.setShader(linearGradient);
//辐射渐变---小贴士绘制文本给定的坐标是基线
        RadialGradient radialGradient = new RadialGradient(
                400, 150, 100, //圆心和半径
                Color.BLUE, Color.RED,  //中心颜色和边缘颜色
                Shader.TileMode.MIRROR);
        paint.setShader(radialGradient);
//扫描渐变
        SweepGradient sweepGradient = new SweepGradient(400, 150, //圆心坐标
                Color.BLUE, Color.RED);
        paint.setShader(sweepGradient);
//Bitmap着色器
        BitmapShader bitmapShader = new BitmapShader(mBitmap, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR); //tileX横向Mode,tileY纵向Mode
        paint.setShader(bitmapShader);
//混合着色器(相同类型shader会在硬件加速下无效)
        ComposeShader composeShader = new ComposeShader(linearGradient, bitmapShader, PorterDuff.Mode.DST_OVER);
        paint.setShader(composeShader);
        canvas.drawCircle(200, 200, 200, paint);
```

16、PorterDuffMode一共有17种
>1. 分为`Alpha Compositing透明度组合`和`Blending混合`
> 2. `Alpha组合`一共12种，均是由`Porter和Duff(姓)`联合发表的
> 3. `Blending`混合
![Alpha组合](https://ws3.sinaimg.cn/large/52eb2279ly1fig6im3hhcj20o50zt7bj.jpg)
![Blending混合](https://ws3.sinaimg.cn/large/52eb2279ly1fig6iw04v0j20ny0hzmzj.jpg)

17、Paint颜色过滤-setColorFilter
```java
        Paint paint = new Paint();
        /**==========================================================
         * 1、光照效果：目标像素颜色-最终红色=R * mul.R / 0xff + add.R(其他颜色一致)
         *    mul=0x00ffff会导致红色为0，add=0x003000中绿色的0x30会导致整体绿色加强
         *==========================================================*/
        LightingColorFilter lightingColorFilter = new LightingColorFilter(0x00ffff, 0x003000);
        paint.setColorFilter(lightingColorFilter);
        // 2、基于PorteDuff.Mode将Color与源图进行处理(和ComposeShader的区别在于只能指定Color作为源而无法使用Bitmap)
        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.parseColor("#88880000"),
                PorterDuff.Mode.DST_IN);
        paint.setColorFilter(porterDuffColorFilter);
        // 3、颜色矩阵进行颜色处理
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.11f); //设置饱和度
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixColorFilter);
        //test
        canvas.drawBitmap(mBitmap, 100, 100, paint);
```

18、Paint的setXfermode(最后一层颜色处理)
> 1. 全写`transfer mode`
> 2. 作用：将你`要绘制的内容`作为`源图像`，以`View中已有的内容`作为`目标图像`，选取一个`PorerDuff.Mode`作为处理方案。
> 3. `setXfermode`有两个注意点：1要使用off-screen buffer；2要控制好透明区域；
> [Paint的setXfermode](http://hencoder.com/ui-1-2/)
```java
        //离屏缓冲(View的setLayerType会直接把整个View都绘制在离屏缓冲中)
        int saved = canvas.saveLayer(null ,null, Canvas.ALL_SAVE_FLAG);

        canvas.drawBitmap(mBitmap, 0, 0, paint);
        Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
        paint.setXfermode(xfermode);
        canvas.drawRect(200, 0, 600, 600, paint);
        paint.setXfermode(null);

        //恢复
        canvas.restoreToCount(saved);
```

19、Paint的效果类API(抗锯齿、风格、线条宽度等)
```java
        //1、抗锯齿
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); //构造时使用
        paint.setAntiAlias(true); //设置
        //2、风格
        paint.setStyle(Paint.Style.FILL); //填充
        paint.setStyle(Paint.Style.STROKE); //画线
        paint.setStyle(Paint.Style.FILL_AND_STROKE); //填充+画线
        //3、线条宽度
        paint.setStrokeWidth(0); //宽度为1，不会随着几何变换而改变宽度-发际线模式
        paint.setStrokeWidth(1); //宽度为1，会因为几何变换而放大
        paint.setStrokeWidth(40);//宽度可以为40等具体数值
        //4、线头形状(像素=1时，三种线头完全一致；像素>1时，会产生不同)
        paint.setStrokeCap(Paint.Cap.BUTT);   //平头
        paint.setStrokeCap(Paint.Cap.SQUARE); //平头(在BUTT的平头上要超出基准虚线一部分)
        paint.setStrokeCap(Paint.Cap.ROUND);  //圆头
        //5、拐角形状
        paint.setStrokeJoin(Paint.Join.MITER); //尖角(角长度受到控制)
        paint.setStrokeJoin(Paint.Join.BEVEL); //平角
        paint.setStrokeJoin(Paint.Join.ROUND); //圆角
        //6、控制拐角中尖角的角长度，超过一定长度就按照BEVEL平角处理(https://ws3.sinaimg.cn/large/006tNc79ly1fig7btolhij30e706dglp.jpg)
        paint.setStrokeMiter(5);//(miter默认值 = 4，大约是29度锐角)
```
![paint_setStrokeMiter](https://github.com/FeatherHunter/ChenWen_StudyNotes/blob/master/assets/paint_setStrokeMiter.jpg?raw=true)

20、Paint色彩优化
>1. setDither-设置图像的抖动
> 2. setFilterBitmap-双线性过滤
> 3. 抖动的原理就是`图像`从`较高色彩深度`向`较低色彩深度`绘制时，在`图像`中加入`噪点`是的推向更真实(如：只有黑白两色的系统，通过黑格和白格交错，密度极大时产生灰色效果)
```java
        //开启抖动(优化色彩深度降低时的绘制效果)
        paint.setDither(true);
        //采用双线性过滤来绘制Bitmap(适合用于放大绘制的时候)
        paint.setFilterBitmap(true);
```

21、Paint设置路线效果(setPathEffect)
>有七种：1、CornerPathEffect; 2、DsicretePathEffect; 3、DashPathEffect; 4、PathDashPathEffect 5、SumPathEffect 6、ComposePathEffect
> * canvas.drawLine/s时，不支持硬件加速。
```java
        Paint paint = new Paint(); //构造时使用
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        Path path = new Path();
        path.moveTo(100, 100);
        path.rLineTo(300, 300);
        path.rLineTo(300, -200);
        path.rLineTo(300, 300);
//1、圆角路径
        CornerPathEffect cornerPathEffect = new CornerPathEffect(20); //半径
        paint.setPathEffect(cornerPathEffect);
//2、随机偏离
        DiscretePathEffect discretePathEffect = new DiscretePathEffect(
                20, //每个小线段的长度
                5); //偏移值-越大越乱
        paint.setPathEffect(discretePathEffect);
//3、虚线
        DashPathEffect dashPathEffect = new DashPathEffect(
                new float[]{20, 10, 5, 10, 40, 10}, //必须为偶数，奇数位的值表示画几个像素，偶数位的值表示空白几个像素(画20空5，画5空10)
                200); //整个虚线的偏移值
        paint.setPathEffect(dashPathEffect);
//4、Path绘制虚线-需要关闭硬件加速
        Path dashPath = new Path();
        dashPath.rLineTo(30,30);
        dashPath.rLineTo(30, -30);
        dashPath.close();

        PathDashPathEffect pathDashPathEffect = new PathDashPathEffect(dashPath,
                30, //两个虚线path的起点的间隔
                50,//整个虚线的偏移值
                PathDashPathEffect.Style.TRANSLATE); //TRANSLATE:位移 ROTATE:旋转 MORPH:变体
        paint.setPathEffect(pathDashPathEffect);
//5、组合效果-按两种PathEffect分别绘制
        SumPathEffect sumPathEffect = new SumPathEffect(discretePathEffect, dashPathEffect);
        paint.setPathEffect(sumPathEffect);
//6、组合效果-按照PathEffect依次绘制
        ComposePathEffect composePathEffect = new ComposePathEffect(
                dashPathEffect, //后应用
                discretePathEffect);    //先应用
        paint.setPathEffect(composePathEffect);

        canvas.drawPath(path, paint);
```

22、Paint的效果类
>1-setShadowLayer-设置背景阴影
>2-setMaskFilter-设置前景效果(需要关闭硬件加速)
```java
        /**=========================
         * 给绘制内容加一层阴影(clearShadowLayer能清除阴影)
         * 1. 硬件加速的情况下，只支持文字绘制
         * 2. 如果参数中颜色有透明度，阴影的透明度就使用该透明度
         *    否则，阴影的透明度跟随Paint的透明度
         *==========================*/
        paint.setShadowLayer(20, //阴影的模糊范围
                10, 10, //阴影在xy上的偏移量
                Color.BLUE);
        canvas.drawPath(path, paint);
```
```java
        /**==========================
         * 1、模糊前景
         *   1-Normal: 内外都模糊
         *   2-Solid: 内外正常绘制，外部模糊
         *   3-Iner: 内部模糊，外部不绘制
         *   4-Outer: 内部不绘制，外部模糊
         *===========================*/
        paint.reset();
        paint.setMaskFilter(new BlurMaskFilter(30, //模糊半径
                BlurMaskFilter.Blur.NORMAL));
        /**============
         * 2、浮雕效果
         *============*/
        paint.setMaskFilter(new EmbossMaskFilter(new float[]{0, 1, 1}, //x,y,z三个方向上的值，用于确定光源方向
                0.2f,   //环境光的强度，0~1
                8,      //镜面反射系数，越接近0，反射光越强
                3    //模糊半径，值越大，越明显
        ));
        canvas.drawBitmap(mBitmap, 100, 100, paint);
```

23、Paint获取绘制的Path?
> 1. `绘制的Path`与初始的`Path`并不同，会计算上后期的各种处理。
> 2. `文字的Path`就是整个文字的边框(文字在绘制中转换为线进行绘制)
> 3. 主要用于`图形和文字`的装饰效果的`位置计算`，如下划线
```java
        //1-获取到实际Path
        paint.getFillPath(srcPath, dstPath);
        //2-获取到文本Path
        paint.getTextPath("Hello", 0, 4, //字符串中字符范围
                100, 100,  //文本的起始坐标
                dstPath);
```

1. drawPicture
2. drawTextOnPath
12. drawPaint()-给Canavs的Bitmap用特定画笔进行填充
1. drawPosText
1. drawText
1. drawVertices-绘制顶点数组

3、Canvas的范围裁剪
1. clipPath
2. clipRect
3. clipRegion

##文本绘制
24、Canvas绘制文字的方式(3种)
```java
        /**===========================
         * 1-文本的绘制
         *  1. 文本绘制以基线为准
         *==========================*/
        paint.setTextSize(50);
        canvas.drawText("Hello Feather", 0, 100, paint);
        // 2. 设置上下文和文字方向(中文和英文无影响)
        canvas.drawTextRun("عربى", 1, 4, //文本的start~end个字符
                1, 4, //上下文-contextstart <= start,contextend >= end
                0, 200, //文字坐标
                true, //文字从右到左
                paint);
        // 3. 在路径上绘图
        String str = "Harry Potter is a series of fantasy novels written by British author J.K.Rowling";
        paint.setStyle(Paint.Style.FILL);
        canvas.drawTextOnPath(str,
                path,
                0, //文字水平偏移
                0, //文字垂直偏移
                paint);
        // 4. 换行
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(50);
        String text = "Harry Potter is a series of fantasy novels written by British author J.K.Rowling";
        StaticLayout staticLayout = new StaticLayout(text, textPaint, //文本画笔
                600, //文字区域宽度，达到会自动换行
                Layout.Alignment.ALIGN_NORMAL, //文字对齐方向
                1, //行间距倍数
                0, //行间距的额外增加值
                true); //文字上下添加额外的空间，避免过高字符
        String text2 = "Harry Potter \nis a series of fantasy novels \nwritten by British author \nJ.K.Rowling";
        StaticLayout staticLayout2 = new StaticLayout(text2, textPaint,600, Layout.Alignment.ALIGN_NORMAL, 1, 0, true);

        canvas.save();
        canvas.translate(0, 100);
        //达到宽度才换行
        staticLayout.draw(canvas);
        canvas.translate(0, 200);
        //达到宽度和遇到“\n”都换行
        staticLayout2.draw(canvas);
        canvas.restore();
```

25、Paint对文本绘制的辅助作用
```java
        //1. 字体大小
        paint.setTextSize(60);
        //2. 字体
        paint.setTypeface(Typeface.SERIF);//内置
        paint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "a.ttf")); //下载的字体
        //3. 是否使用伪粗体
        paint.setFakeBoldText(true);
        //4. 是否添加删除线
        paint.setStrikeThruText(true);
        //5. 下划线
        paint.setUnderlineText(true);
        //6. 文字横向错切角度(倾斜)
        paint.setTextSkewX(-0.3f);
        //7. 文字横向缩放
        paint.setTextScaleX(1.2f);
        //8. 字符间距
        paint.setLetterSpacing(0.05f);
        //9. CSS的字体特性设置
        paint.setFontFeatureSettings("smcp");
        //10. 文字对齐方式
        paint.setTextAlign(Paint.Align.CENTER); //左右中
        //11. Local地域设置(不用在系统里设置)
        paint.setTextLocale(Locale.CHINA);

        canvas.drawText("Hello Feather", 0, 100, paint);
```

26、Paint测量文字尺寸类
```java
        String text = "Hello Feather";
        //1. 获取到推荐行距。方便换行。
        canvas.drawText(text, 0, 100 + paint.getFontSpacing(), paint);
        //2. top/bottom 任何文字的上下范围 ascent/descent 限制普通字符的顶部和底部
        //   top和ascent在baseline的上方，为负数；bottom和descent为正数
        Paint.FontMetrics metrics = paint.getFontMetrics(); //leading就是上者bottom和下者top的距离
        paint.getFontMetrics(metrics); //不需要重新创建，性能更好
        paint.ascent(); //能直接获取
        paint.descent();
        //3. 会之后获取文字的显示范围(第1~第3个字符)-存储在Rect中
        Rect bound = new Rect();
        paint.getTextBounds(text, 0, 2, bound);//不包含预留空隙
        //4. 文本的宽度(包含左右看不见的预留空隙)
        float width = paint.measureText(text);
        //5. 每个字符的宽度
        float[] width = new float[10];
        paint.getTextWidths(text, width);
        //6. 测量宽度，不够用就截断(可用于多行文本的折行计算)
        float[] measuredWidth = {0};
        int  measuredCount = paint.breakText(text, 0, text.length(), true, 300, measuredWidth);
        // 宽度上限 300 （不够用，截断）
        canvas.drawText(text, 0, measuredCount, 150, 150, paint);
```

27、Paint光标相关方法
```java
        String text = "Hello HenCoder \uD83C\uDDE8\uD83C\uDDF3";
        //1. 计算光标的位置(具有emoji表情时不会出现在emoji中间)
        float advance = paint.getRunAdvance(text, 0, text.length(), 0, text.length(), false, text.length());
        canvas.drawText(text,150, 150, paint);
        canvas.drawLine(150 + advance, 150 - 50, 150 + advance, 150 + 10, paint);
        //2. 获取到离某个位置最近的字符的offset(字符串中)
        int offest = paint.getOffsetForAdvance(text, 0, text.length(), 0, text.length(), true,
                100); //位置的像素值。该方法配合getRunAdvance能实现获取用户点击处的文字坐标的需求
        //3. 判断一个字符串中是否是一个单独的字形glyph
        paint.hasGlyph(text); //"a"=true;"b"=true;"ab"=false;"\uD83C\uDDE8\uD83C\uDDF3"=true;
```

##知识储备-考考你
1、矢量是什么？
> 1. `矢量（vector）`是一种既有`大小`又有`方向`的量，又称为`向量`。
> 2. 一般在物理学中称作`矢量`，例如速度、加速度、力等等就是这样的量。
> 3. 舍弃实际含义，就抽象为`数学`中的概念──`向量`。
> 4. 在计算机中，`矢量图`可以`无限放大永不变形`。

8、贝塞尔曲线是什么？
>1.

3、硬件加速是什么？
>1. API>=14默认开启`硬件加速`
> 2. 如果你的应用仅仅有标准的`View`和`Drawable`，`硬件加速`不会出现任何问题。
> 3. `硬件加速`不支持所有的`2D绘图操作`，在`自定义View`和`绘图`时，可能会出现问题(元素不可见、异常、错误渲染了像素)
> 4. `硬件加速`是允许开启或者关闭，可以在`四个层面`控制(Application、Activity、Window、View)
>[硬件加速-谷歌文档](https://developer.android.com/guide/topics/graphics/hardware-accel.html#controlling)

5、Rect和RectF之间的区别和联系
>1. `Rect`的参数是`int`，`RectF`的参数为`float`
>2. 都是通过`四个坐标`来确定一个矩形的区域。
>3. 通过`RectF的contain(RectF f)`能判断一个矩形是否在该举行内

6、Region是什么？作用？
>1. 使用`Region`可以对图形进行操作，比如`区域的合并-取交集、取异或`等等
https://blog.csdn.net/cquwentao/article/details/51365099


##参考资料
1. [Canvas官方文档](http://www.android-doc.com/reference/android/graphics/Canvas.html)
1. [贝塞尔曲线扫盲](http://www.html-js.com/article/1628)
2. [drawBitmapMesh实现水波纹效果](https://juejin.im/post/58fb4c64ac502e0063a2721e)
1. [PortDuff谷歌官方文档](https://developer.android.com/reference/android/graphics/PorterDuff.Mode.html)
1. [HenCoder系列教学](http://hencoder.com/ui-1-1/)
