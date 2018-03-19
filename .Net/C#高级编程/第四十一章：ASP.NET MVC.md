1、`ASP.NET MVC`概述
> 1.基于MVC(模型-视图-控制器)模式。
> 2.该模式定义了一个实现了数据实体和数据访问的模型；
> 3.一个表示显示给用户的信息的视图
> 4.和一个利用模型并将数据发送给视图的控制器；
> 5.控制器接收来自浏览器的请求并返回一个响应。

2、MVC模式的优点：
> 1.可以使用单元测试方便地测试功能。
> 2.控制器只包含方法，其参数和返回值可以轻松地在单元测试中覆盖。

3、建立`ASP.NET MVC`服务
> 1.添加NuGet包Microsoft.AspNetCore.Mvc；
> 2.添加MVC服务——在ConfigureServices方法中调用AddMvc扩展方法；
>```
>public void ConfigureServices(IServiceCollection services)    //在Startup.cs文件中
>{
>    services.AddMvc();   //添加MVC服务
>}
>```

4、AddMvc扩展方法中配置的信息：
> 1.添加和配置了`ASP.NET MVC`核心服务；
> 1. 配置特性：带有MvcOptions和RouteOptions的IConfigureOptions；
> 2. 控制器工厂和控制器激活程序：IControllerFactory、IController-Activator;
> 3. 动作方法选择器、调用器和约束提供程序：IActionSelector、IActionInvokerFactory、IActionConstraintProvider；
> 4. 参数绑定器和模型验证器：IControllerActionArgumentBinder、IObjectModel-Validator；
> 5. 过滤器提供程序：IFilterProvider；
> 2.增加了`ASP.NET MVC`服务来支持授权、CORS、数据注解、视图、Razor视图引擎。

4、定义路由：把URL映射到控制器的动作方法上
> 1.创建默认路由：调用`UseMvcWithDefaultRoute()`；
> 1. 路由是由控制器类型的名称(没有Controller后缀)和方法名构成的，也可以使用一个可选参数；
> 2. 指定默认的路由使用`UseMvc()`方法；
> 3. 该方法接收Action<IRouteBuilder>类型的参数，IRouteBuilder接口包含一个映射的路由列表；
>```
>public void Configure(IApplicationBuilder app, IHostingEnvironment env)
>       {
>           app.UseStaticFiles();
>           app.UseMvcWithDefaultRoute();
>
>           app.UseMvc(routes =>
>           {
>               routes.MapRoute(
>                   name: "default",
>                   template: "{controller}/{action}/{id?}",    //定义了URL，id？表示参数可选
>                   defaults: new { controller="Home",action ="Index" }
>                   );    //定义了URL中的默认值
>           });
>       }
>```

5、修改路由
> 1.目的：
> 1. 以便使用带链接的动作；
> 2. 将Home定义为默认控制器；
> 3. 向链接添加额外的项或使用多个参数。
>```
>app.UseMvc(routes =>      //在路由中添加一个变量language
>{
>    routes.MapRoute(
>        name: "default",
>        template: "{controller}/{action}/{id?}",
>        defaults: new { controller = "Home", action = "Index" }
>        ).MapRoute(
>        name: "language",
>        template: "{language}/{controller}/{action}/{id?}",
>        defaults: new { controller = "Home", action = "Index" }
>        );
>});
>```

6、添加路由
> 1.定义一个路由通过`http://<server>/About`的链接使用Home控制器中的About方法，不传递控制器名称；
>```
>app.UseMvc(routes =>
>{
>    routes.MapRoute(
>        name: "default",
>        template: "{action}/{id?}",    //URL中省略了控制器
>        defaults: new { controller = "Home", action = "About" }   //controller关键字必须有，可以定义为默认值
>        );
>});
>```

7、使用路由约束
> 1.约束定义的URL。
> 2.配置`constraints`里面的内容；
>```
>app.UseMvc(routes =>
>{
>    routes.MapRoute(
>        name: "language",
>        template: "{language}/{controller}/{action}/{id?}",
>        defaults: new { controller = "Home", action = "Index" },
>        constraints :new {language = @"(en)|(de)"}    //使用正则表达式（language只能是en或de）
>        );
>});
>```

8、创建控制器
> 1.控制器作用：对用户请求做出反应，然后发回一个响应。
> 2.约定：
> 1. 控制器位于目录Controllers中；
> 2. 控制器类的名称必须带有Controller后缀。

9、使用参数
> 1.动作方法声明为带有参数：
> 2.URL中name为参数传递的值：`http://localhost:59232/Home/Greeting?name=world`
>```
>public string Greeting(string name)=>
>    HtmlEncoder.Default.Encode($"hello,{name}");
>```
> 3.URL中使用可选参数：`http://localhost:59232/Home/Greeting/world`
> **注意：可选参数名要跟方法中的参数名一致**
>```
>public string Greeting(string id)=>
>    HtmlEncoder.Default.Encode($"hello,{id}");
>```

10、返回数据
> 1.控制器会返回一个实现了IActionResult接口的对象。
> 2. 返回值可以是字符串值，文本内容，JSON数据，图片等。

11、POCO控制器
> 1.POCO（Plain Old CLR Objects）控制器：不继承Controller基类。
> 2.因此，不能访问Controller中的一些属性；
> 3.需要自己自定义这些属性，但是要给自定义的属性注入实际的方法；
>```
>public class POCOController
>{
>    public string Index() =>
>        "this is a POCO controller";
>
>    [ActionContext]                //注入实际的ActionContext
>    public ActionContext ActionContext { get; set; }     //自定义属性
>    public HttpContext Context => ActionContext.HttpContext;
>    public ModelStateDictionary ModelState => ActionContext.ModelState;
>
>    public string UserAgentInfo()     //返回请求中的User-Agent标题信息
>    {
>        if(Context.Request.Headers.ContainsKey("User-Agent"))
>        {
>            return Context.Request.Headers["User-Agent"];
>        }
>        return "No user-agent information";
>    }
>}
>```

12、Razor语法
> 1.使用@字符作为转换字符，@字符之后的代码时C#代码。
> 2.调用有返回值的方法：直接使用返回值
>```
>public IActionResult PassingData()
>{
>    ViewBag.MyData = "hello from the controller";
>    return View();
>}
>
><div>@ViewBag.MyData</div>
>```
> 3.调用没有返回值的方法或指定其他没有饭怀志的语句，要用Razor代码块
> **注意：使用Razor语句时，引擎在找到HTML元素时，会自动认为代码结束。但是，有些时候，无法自动看出来，可用圆括号标记变量**
>```
>@{
>  string name="John";
>}
><div>@name</div>
><div>@(name)</div>
>```

13、创建强类型视图
> 1.向视图传递模型。
>```
>public IActionResult PassingAModel()
>{
>    var menus = new List<Menu>
>    {
>        new Menu { Id=1, Text="Schweinsbraten mit Knödel und Sauerkraut",
>                   Price=6.9, Category="Main" },
>        new Menu { Id=2, Text="Erdäpfelgulasch mit Tofu und Gebäck",
>                   Price=6.9, Category="Vegetarian" },
>        new Menu { Id=3,
>                   Text="Tiroler Bauerngröst'l mit Spiegelei und Krautsalat",
>                   Price=6.9, Category="Main" }
>    };
>    return View(menus);
>}
>
>   //在.cshtml文件中
>@using MVCSampleApp.Models   //引入了Menu模型
>@model IEnumerable<Menu>     //泛型参数IEnumerable<Menu>定义了Model属性
><div>
>    <ul>
>        @foreach (var item in Model)   //访问Model中的属性
>        {
>            <li>@item.Text</li>
>        }
>    </ul>
></div>
>```

14、使用布局页
> 1.目的：管理布局中的共享内容。
> 2.方法：要设置视图的Layout属性；
> 3.在Shared文件夹中创建共享页面；
>```
>@{
>  layout="_layout";
>}
>```

15、使用分区
> 1.目的：把视图内定义的内容放在固定的位置；
> 2.`@RenderSection("PageNavigation", required: false)`中的required参数设为false，该分区可选；
> 3.默认情况下，要有分区，如果没有，加载视图会失败；
>```
><div>    //在_layout.cshtml（布局页中）中定义分区
>    @RenderSection("PageNavigation", required: false)
></div>
>
>@section PageNavigation   //在视图中使用
>{
>    <div>Navigation defined from the view</div>
>    <ul>
>        <li>Nav1</li>
>        <li>Nav2</li>
>    </ul>
>}
>```
