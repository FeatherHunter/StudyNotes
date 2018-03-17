1、Web应用程序的核心Web技术是什么？
> 核心技术：HTML、CSS、JavaScript和JQuery。
>- HTML是由Web浏览器解释的标记语言。它定义的元素显示各种标题、表格、列表和输入元素。现在，HTML5已经成为了W3C推荐标准，所有主流浏览器都提供了它。
>- CSS定义了Web页面内容的外观。在CSS样式中，HTML元素可以使用灵活的选择器来选择，还可以为这些元素定义样式。元素通过其ID或名称来选择。也可以定义CSS类，在HTML代码中引用。
>- JavaScript是一种函数式编程语言，不是面向对象的，但它添加了面向对象功能。
>- JQuery是一个脚本库，它抽象出了访问DOM元素和响应事件时的浏览器的差异。

2、创建一个空的`ASP.NET Core Web Application`项目，项目中各个文件的作用？
>- **Properties——launchSettings.json**：是一个启动配置文件。该文件为一个`ASP.NET Core`应用保存特有的配置标准，用于应用的启动准备工作，包括环境变量，开发端口等。
>- **Startup.cs**：是`ASP.NET Core`的启动入口文件。项目运行时，编译器会在程序集中自动查找该文件读取启动配置。除了构造函数，可以定义`Configure`和`ConfigureServices`方法。
> > >- 构造函数：用来启动配置文件configuration。
> > > ```
> > >         public Startup(IHostingEnvironment env)
> > >         {
> > >             var builder = new ConfigurationBuilder()
> > >                 .SetBasePath(env.ContentRootPath)
> > >                 .AddJsonFile("appsettings.json")
> > >             .AddJsonFile($"appsettings.{env.EnvironmentName}.json", optional: true);
> > >
> > >             if(env.IsDevelopment())
> > >             {
> > >                 builder.AddUserSecrets<Startup>();
> > >             }
> > >             Configuration = builder.Build();
> > >         }
> > >         public IConfigurationRoot Configuration { get; }
> > > ```
> > >- ConfigureServices：配置应用程序中的服务。
> > > ```
> > >         public void ConfigureServices(IServiceCollection services)
> > >         {
> > >             services.AddTransient<ISampleService, DefaultSampleService>();  //每次注入服务时都会实例化新的服务
> > >
> > >             services.AddTransient<HomeController>();   //调用控制器
> > >
> > >             services.AddDistributedMemoryCache();
> > >             services.AddSession(options =>
> > >             options.IdleTimeout = TimeSpan.FromMinutes(10));
> > >
> > >         }
> > > ```
> > >- Configure:处理程序中的各种中间件，这些中间件决定了我们应用程序将如何响应每一个HTTP请求。
> > > ```
> > > public void Configure(IApplicationBuilder app, ILoggerFactory loggerFactory)
> > > {
> > >     loggerFactory.AddConsole(); //添加的提供程序把日志信息写到控制台
> > >     loggerFactory.AddDebug(); //添加的提供程序把日志信息写到控制台
> > >     app.UseStaticFiles();   //扩展方法处理静态文件
> > >     app.Map("/RequestAndResponse", app1 =>
> > >     {
> > >         app1.Run(async (context) =>
> > >         {
> > >              await context.Response.WriteAsync(RequestAndResponseSample.GetRequestInformation(context.Request));
> > >         });
> > >     });
> > > }
> > > ```
>- **wwwroot**：存放静态内容的文件夹，例如css，js，img等文件。
>- **appsettings.json**：应用配置文件。
> ```
> {
>   "AppSettings": {
>     "SiteName": "Professional C# Sample"
>   },
>   "Data": {
>     "DefaultConnection": {
>       "ConnectionString": "Server=localhost;Database=Turing;User ID=sa;Password=123456;"
>     }
>   }
> }
> ```
>- **Program.cs**：Web应用程序的入口点是Main方法。
> ```
>         public static void Main(string[] args)
>         {
>             var host = new WebHostBuilder()
>                 .UseKestrel()
>                 .UseContentRoot(Directory.GetCurrentDirectory())
>                 .UseIISIntegration()
>                 .UseStartup<Startup>()
>                 .Build();
>
>             host.Run();
>         }
> ```

3、Web应用程序的启动过程：
> 程序的入口点是Main方法：在这个方法中，使用WebHostBuilder实例配置。使用WebHostBuilder调用UseKestrel方法来完成对KestrelServer的注册。UseContentRoot方法则是设置站点目录。UseIISIntegration方法将IIS集成中间件合并到应用程序中。UseStartup方法相当于注册了一个IStartup服务。然后调用Build方法，返回一个实现IWebApplication接口的对象。最后对该对象调用Run方法，启动托管引擎，服务器在监听和等待请求。

4、添加静态文件(在wwwroot文件夹中添加)
> 要在web服务器上处理静态文件，可以在`StartUp.cs`的`Configure`方法中添加扩展方法`app.UseStaticFiles()`。此时，需要在NuGet中下载包`Microsoft.AspNetCore.StaticFiles`

5、JavaScript包管理器：npm
> Node Package Manager(npm)是一个JavaScript库的包管理器。创建一个package.json文件，保存该文件就可以从服务器中加载npm包。加载的库会存放在“依赖项”中。`gulp-concat`连接JavaScript文件，`gulp-cssmin`压缩CSS文件，`gulp-uglify`压缩JavaScript文件，`rimraf`允许删除层次结构中的文件。
> ```
> {
>   "version": "1.0.0",
>   "name": "asp.net",
>   "private": true,
>   "devDependencies": {
>     "gulp": "3.9.1",
>     "gulp-concat": "2.6.1",
>     "gulp-cssmin": "0.2.0",
>     "gulp-uglify": "3.0.0",
>     "rimraf": "2.6.2"
>   }
> }
> ```

6、gulp构建
> gulp是一个用于JavaScript的构建系统。构建系统可以把Syntactically Awesome Stylesheets（SASS）文件（带有脚本功能的CSS）转换为CSS，可以缩小和压缩文件，可以启动脚本的单元测试，可以分析JavaScript代码。
> ```新建一个gulpfile.js文件
> "use strict"
> var gulp = require('gulp'),    //定义这个文件所需要的库
>     rimraf = require('rimraf'),
>     concat = require('gulp-concat'),
>     cssmin = require('gulp-cssmin'),
>     uglify = require('gulp-uglify');
>
> var paths = {
>     webroot: "./wwwroot"
> };
>
> paths.js = paths.webroot + "js/**/*.js";
> paths.minJs = paths.webroot + "js/**/*.min.js";
> paths.css = paths.webroot + "css/**/*.css";
> paths.minCss = paths.webroot + "css/**/*.min.css";
> paths.concatJsDest = paths.webroot + "js/site.min.js";
> paths.concatCssDest = paths.webroot + "css/site.min.css";
>
> gulp.task("clean:js", function (cb){
>     rimraf(paths.concatJsDest, cb);
>  });
>
> gulp.task("clean:css", function (cb) {
>     rimraf(paths.concatCssDest, cb);
> });
>
> gulp.task("clean", ["clean:js", "clean:css"]);
>
> gulp.task("min:js", function () {
>     gulp.src([paths.js, "!" + paths.minJs], { base: "." })
>         .pipe(concat(paths.concatJsDest))
>         .pipe(uglify())
>         .pipe(gulp.dest("."));
> });
>
> gulp.task("min:css", function () {
>     gulp.src([paths.css, "!" + "paths.minCss"])
>         .pipe(concat(paths.concatCssDest))
>         .pipe(cssmin())
>         .pipe(gulp.dest("."));
> });
>
> gulp.task("min", ["min:js", "min:css"]);
> ```

7、通过Bower使用客户端库（非常类似NuGet）
> Bower是一个像npm那样的包管理器。npm项目用JavaScript库启动服务器端代码，而Bower提供了成千上万的JavaScript客户端库。添加bower.json文件，保存该文件，从服务器中检索库，脚本库中的文件被复制到wwwroot/lib文件夹中。
> ```
> {
>   "name": "asp.net",
>   "private": true,
>   "dependencies": {
>     "bootsharp": "0.8.3",
>     "jquery": "3.3.1",
>     "jquery-validation": "1.17.0",
>     "jquery-validation-unobtrusive": "3.2.6"
>   }
> }
> ```

8、请求与响应
> 客户端通过HTTP协议向服务器发出请求，这个请求用响应来回答。HttpRequest对象访问Scheme，Host，Path，QueryString，Method和Protocol属性，将结果写入Response对象。
> ```
>     public class RequestAndResponseSample
>     {
>
>         /// <summary>
>         /// 创建一个div元素
>         /// </summary>
>         /// <param name="key"></param>
>         /// <param name="value"></param>
>         /// <returns></returns>
>         public static string GetDiv(string key, string value) =>
>             $"<div><span>{key}:</span> <span>{value}</span></div>";
>
>         public static string GetRequestInformation(HttpRequest request)   //HttpRequest对象访问各个属性
>         {
>             var sb = new StringBuilder();
>             sb.Append(GetDiv("scheme", request.Scheme));
>             sb.Append(GetDiv("host", request.Host.HasValue ? request.Host.Value : "no host"));
>             sb.Append(GetDiv("path", request.Path));  
>             sb.Append(GetDiv("query string", request.QueryString.HasValue ? request.QueryString.Value : "no query string"));
>
>             sb.Append(GetDiv("method", request.Method));
>             sb.Append(GetDiv("protocol", request.Protocol));
>
>             return sb.ToString();
>         }
>
>         /// <summary>
>         /// 获取标题信息
>         /// </summary>
>         /// <param name="request"></param>
>         /// <returns></returns>
>         public static string GetHeaderInformation(HttpRequest request)
>         {
>             var sb = new StringBuilder();
>
>             IHeaderDictionary headers = request.Headers;
>             foreach (var header in headers)
>             {
>                 sb.Append(GetDiv(header.Key, string.Join(";", header.Value)));
>             }
>             return sb.ToString();
>         }
>
>         /// <summary>
>         /// 使用查询字符串
>         /// </summary>
>         /// <param name="request"></param>
>         /// <returns></returns>
>         public  static string QueryString(HttpRequest request)
>         {
>             var sb = new StringBuilder();
>             string xtext = request.Query["x"];
>             string ytext = request.Query["y"];
>             if(xtext==null||ytext==null)
>             {
>                 return "x and y must be set".Div();
>             }
>
>             int x, y;
>             if(!int.TryParse(xtext, out x))
>             {
>                 return $"Error parsing {xtext}".Div();
>             }
>             if(!int.TryParse(ytext, out y))
>             {
>                 return $"Error parsing {ytext}".Div();
>             }
>
>             return $"{x} +{y} ={x + y}".Div();
>         }
>
>         /// <summary>
>         /// 读取cookie
>         /// </summary>
>         /// <param name="request"></param>
>         /// <returns></returns>
>         public static string ReadCookie(HttpRequest request)
>         {
>             var sb = new StringBuilder();
>             IRequestCookieCollection cookies = request.Cookies;
>             foreach (var key in cookies.Keys)
>             {
>                 sb.Append(GetDiv(key, cookies[key]));
>             }
>             return sb.ToString();
>         }
>
>         public static string GetJson(HttpResponse response)
>         {
>             var b = new
>             {
>                 Title = "Professional C# 6",
>                 Publisher = "Wrox Press",
>                 Author = "Christian Nagel"
>             };
>
>             string json = JsonConvert.SerializeObject(b);
>             response.ContentType = "application/json";
>             return json;
>         }
>
>         /// <summary>
>         /// 写cookie，在多个请求之间记住用户数据
>         /// </summary>
>         /// <param name="response"></param>
>         /// <returns></returns>
>         public static string WriteCookie(HttpResponse response)
>         {
>             response.Cookies.Append("color", "red",
>                 new CookieOptions
>                 {
>                     Path = "/cookies",   //限制浏览器何时返回cookie
>                     Expires = DateTime.Now.AddDays(1)  //cookie是永久性的，存储在客户端
>                 });
>             return "cookie written".Div();
>         }
>
>         /// <summary>
>         /// 编码：只返回相应的字符串
>         /// </summary>
>         /// <param name="request"></param>
>         /// <returns></returns>
>         public static string Content(HttpRequest request) => request.Query["data"];
>
>         /// <summary>
>         /// HTML编码
>         /// </summary>
>         /// <param name="request"></param>
>         /// <returns></returns>
>         public static string ContentEncoded(HttpRequest request) =>
>             HtmlEncoder.Default.Encode(request.Query["data"]);
>
>        /// <summary>
>         /// 表单通过GET请求发送到客户端，用户填写表单用POST请求提交数据
>        /// </summary>
>         /// <param name="request"></param>
>         /// <returns></returns>
>         public static string GetForm(HttpRequest request)
>         {
>             string result = string.Empty;
>             switch(request.Method)
>             {
>                 case "GET":
>                     result = GetForm();
>                     break;
>                 case "POST":
>                     result = ShowForm(request);
>                     break;
>                 default:
>                     break;
>             }
>             return result;
>         }
>
>         private static string ShowForm(HttpRequest request)
>         {
>             var sb = new StringBuilder();
>             if (request.HasFormContentType)
>             {
>                 IFormCollection coll = request.Form;  //读取表单数据
>                 foreach (var key in coll.Keys)
>                 {
>                     sb.Append(GetDiv(key, HtmlEncoder.Default.Encode(coll[key])));
>                 }
>                 return sb.ToString();
>             }
>             else return "no form".Div();
>         }
>
>         /// <summary>
>         /// 创建表单，包含输入元素text1和一个Submit按钮
>         /// </summary>
>         /// <returns></returns>
>         private static string GetForm() =>
>             "<form method=\"post\" action= \"form\">" +
>             "<input type=\"text\" name=\"text1\"/>" +
>             "<input type=\"submit\" value=\"Submit\"/>" +
>             "</form>";
>     }
> ```
> ```
>           //在startup。cs中的configure方法中实现
>             app.Map("/RequestAndResponse", app1 =>
>             {
>                 app1.Run(async (context) =>
>                 {
>                     await context.Response.WriteAsync(RequestAndResponseSample.GetRequestInformation(context.Request));
>                 });
>             });
>             app.Map("/RequestAndResponse2", app1 =>
>             {
>                 app1.Run(async (context) =>
>                 {
>                     string result = string.Empty;
>                     switch (context.Request.Path.Value.ToLower())
>                     {
>                         case "/header":
>                             result = RequestAndResponseSample.GetHeaderInformation(context.Request);
>                             break;
>                         case "/add":
>                             result = RequestAndResponseSample.QueryString(context.Request);
>                             break;
>                         case "/content":
>                             result = RequestAndResponseSample.Content(context.Request);
>                             break;
>                         case "/encoded":
>                             result = RequestAndResponseSample.ContentEncoded(context.Request);
>                             break;
>                         case "/form":
>                             result = RequestAndResponseSample.GetForm(context.Request);
>                             break;
>                         case "/writecookie":
>                             result = RequestAndResponseSample.WriteCookie(context.Response);
>                             break;
>                         case "/readcookie":
>                             result = RequestAndResponseSample.ReadCookie(context.Request);
>                             break;
>                         case "/json":
>                             result = RequestAndResponseSample.GetJson(context.Response);
>                             break;
>                         default:
>                             result = RequestAndResponseSample.GetRequestInformation(context.Request);
>                             break;
>                     }
>                     await context.Response.WriteAsync(HtmlHelper.DocType() + HtmlHelper.HtmlStart() + HtmlHelper.Head() + HtmlHelper.BodyStart());
>                     await context.Response.WriteAsync(result);
>                     await context.Response.WriteAsync(HtmlHelper.BodyEnd() + HtmlHelper.HtmlEnd());
>                 });
>             });
> ```
>- 请求标题：
> > HttpRequest对象定义了Headers属性，这是IHeaderDictionary类型，它包含以标题命名的字典和一个值的字符串数组。
>- 查询字符串
> > HttpRequest对象的QueryString属性访问完整的查询字符串，Query属性访问查询字符串的各个部分。
>- 编码
> > 返回用户输入的数据可能很危险。如果定义了一个方法直接返回查询字符串传递的数据，不进行编码，就会直接执出我们输入的内容，很容易就会被重定向。因此我们可以使用HtmlEncoder类进行HTML编码。
>- 表单数据
> > 代码中我们是在后台创建的表单，这样会产生一个问题，并不能够直接在浏览器中显示，我们就需要写一个HtmlHelper类，在这个类中定义html的相关设置。
> > ```
> >     public static class HtmlHelper
> >     {
> >         public static string DocType() => "<!DOCTYPE HTML>";
> >         public static string Head()=>"<head><meta charset=\"utf-8\"><title>Sample</title></head>";
> >         public static string HtmlStart() => "<html lang=\"en\">";
> >         public static string HtmlEnd() => "</html>";
> >         public static string BodyStart() => "<body>";
> >         public static string BodyEnd() => "</body>";
> >     }
> > ```
> > 然后，在创建表单之前调用方法`await context.Response.WriteAsync(HtmlHelper.DocType() + HtmlHelper.HtmlStart() + HtmlHelper.Head() + HtmlHelper.BodyStart());`，在最后调用`await context.Response.WriteAsync(HtmlHelper.BodyEnd() + HtmlHelper.HtmlEnd());`方法就可以显示出表单。
>- cookie
> > cookie的作用是在多个请求之间记住用户数据。我们的代码中，只有cookie来自同一个域且使用/cookies路径，才返回cookie。
>- 发送JSON
> > 服务器不仅返回Html代码，还返回许多不同的数据格式，例如CSS文件、图像和视频等。客户端通过响应标题中的mime类型，确定接收什么类型的数据。
JSON格式的mime类型是application/json，可以通过HttpResponse的ContentType属性设置。

9、依赖注入
> 依赖注入深深地集成在`ASP.NET Core`中。这种设计模式提供了松散耦合，因为一个服务只用一个接口。实现接口的具体类型是注入。在`ASP.NET`内置的依赖注入机制中，注入通过构造函数来实现，构造函数的参数是注入的接口类型。
>- 定义服务
> ``` 定义接口
>     public interface ISampleService
>     {
>         IEnumerable<string> GetSampleStrings();
>     }
> ```
> ``` 实现接口
>     public class DefaultSampleService:ISampleService
>     {
>         private List<string> _strings = new List<string> { "one", "two", "three" };
>         public IEnumerable<string> GetSampleStrings() => _strings;
>     }
> ```
>- 注册服务
> > 使用AddTransient方法，DefaultSampleService类型被映射到ISampleService。
> > ```
> > services.AddTransient<ISampleService, DefaultSampleService>();  //在startup.cs中的ConfigureServices方法中实现
> > ```
> > >- 1、AddTransient方法：每次注入服务时，都会实例化新的服务；
> > >- 2、AddSingleton方法：服务只实例化一次，每次注入都使用相同的实例；
> > >- 3、AddInstance方法：需要实例化一个服务，并将实例传递给该方法。
> > > ```
> > > var sampleService = new DefaultSampleService();
> > > services.AddInstance<ISampleService>(sampleService);
> > > ```
>- 注入服务
> > 在Controllers目录中创建一个控制器类型HomeController，定义一个构造函数来接收ISampleService接口。
> > ```
> >     public class HomeController:Controller
> >     {
> >         private readonly ISampleService _service;
> >         public HomeController(ISampleService service)
> >         {
> >             _service = service;
> >         }
> >         public async Task<int> Index(HttpContext context)
> >         {
> >             var sb = new StringBuilder();
> >             sb.Append(HtmlHelper.DocType() + HtmlHelper.HtmlStart() + HtmlHelper.Head() + HtmlHelper.BodyStart());
> >             sb.Append("<ul>");
> >             sb.Append(string.Join("", _service.GetSampleStrings().Select(
> >                 s => $"<li>{s}</li>").ToArray()));
> >             sb.Append("</ul>");
> >             sb.Append(HtmlHelper.BodyEnd() + HtmlHelper.HtmlEnd());
> >             await context.Response.WriteAsync(sb.ToString());
> >             return 200;
> >         }
> >     }
> > ```
>- 调用控制器
> > 在startup.cs中的ConfigureServices方法中实现
> > ```
> > services.AddTransient<HomeController>();   //调用控制器
> > ```
> > 在Configure方法中使用
> > ```
> >             app.Map("/homeService", homeApp =>
> >             {
> >                 homeApp.Run(async (context) =>
> >                 {
> >                     if (context.Request.Path.Value.ToLower() == "/home")
> >                     {
> >                         HomeController controller =
> >                         app.ApplicationServices.GetService<HomeController>();
> >                         int statusCode = await controller.Index(context);
> >                         context.Response.StatusCode = statusCode;
> >                         return;
> >                     }
> >                 });
> >             });
> > ```

10、使用映射的路由
> 就是使用app.Map方法，还可以使用app.MapWhen管理映射。
> ```
>             //路径以"/configuration"开头，应用MapWhen管理的映射
>             PathString remaining;
>             app.MapWhen(context =>
>             context.Request.Path.StartsWithSegments("/configuration", out remaining),
>             configApp =>
>             {
>                 configApp.Run(async context =>
>                 {
>                     HomeController controller =
>                     app.ApplicationServices.GetService<HomeController>();
>                     int statusCode = await controller.Index(context);
>                 });
>             });
> ```

11、使用中间件
> `ASP.NET Core`很容易创建在调用控制器之前调用的模块。这可以用于添加标题信息、验证令牌、构建缓存、创建日志跟踪等。一个中间件链接另一个中间件模块，知道调用所有连接的中间件类型为止。在一个中间件类型中，创建构造函数，接收对下一个中间件类型的引用。RequestDelegate是一个委托，它接收HttpContext作为参数，并返回一个Task。

12、会话状态
> 使用中间件实现的服务是会话状态。会话状态允许在服务器上暂时记忆客户端的数据。用户第一次从服务器请求页面时，会启动会话状态。用户在服务器上保持页面一直打开时，会话状态会继续到超时(通常是10分钟)为止。用户导航到新页面时，为了仍在服务器上保持状态，可以把状态写入一个会话。超时后，会话数据会被删除。为了识别会话，可在第一个请求上创建一个带会话标识符的临时cookies。这个cookie与每个请求一起从客户端返回到服务器，在浏览器关闭后，就删除cookie。
> >- 配置会话
> > > 首先需要添加NuGet包（Microsoft.AspNetCore.Session）。这个包提供了AddSession扩展方法，可以在Startup类的ConfigureServices方法中调用。该参数允许配置闲置超时和cookie选项。
> > > ```
> > >             services.AddDistributedMemoryCache();
> > >             services.AddSession(options =>
> > >             options.IdleTimeout = TimeSpan.FromMinutes(10));
> > > ```
> >- 使用会话：在相应的方法之前调用UseSession
> > ```
> >         app.UseSession();
> >         app.UseHeaderMiddleware();
> >         app.UseHeading1Middleware();
> > ```
> >- 编写会话状态：使用Set...方法
> > ```
> >     public static class SessionSample
> >     {
> >         private const string SessionVisits = nameof(SessionVisits);
> >         private const string SessionTimeCreated = nameof(SessionTimeCreated);
> >
> >         public static async Task SeesionAsync(HttpContext context)
> >         {
> >             int visits = context.Session.GetInt32(SessionVisits) ?? 0;
> >             string timeCreated = context.Session.GetString(SessionTimeCreated) ?? string.Empty;
> >             if(string.IsNullOrEmpty(timeCreated))
> >             {
> >                 timeCreated = DateTime.Now.ToString("t", CultureInfo.InvariantCulture);
> >                 context.Session.SetString(SessionTimeCreated, timeCreated);
> >             }
> >             DateTime timeCreated2 = DateTime.Parse(timeCreated);
> >             context.Session.SetInt32(SessionVisits, ++visits);
> >             await context.Response.WriteAsync(
> >                 $"Number of visits within this session:{visits}" +
> >                 $"that was created at {timeCreated2:T};" +
> >                 $"current time:{DateTime.Now:T}");
> >         }
> >     }
> > ```

13、配置`ASP.NET`
> 创建appsettings.json文件，随后进行设置,
> ```
> {
>   "AppSettings": {
>     "SiteName": "Professional C# Sample"
>   },
>   "Data": {
>     "DefaultConnection": {
>       "ConnectionString": "Server=localhost;Database=Turing;User ID=sa;Password=123456;"
>     }
>   }
> }
> ```
> 然后，在Startup类的构造函数中完成配置，最后读取配置信息。
> ```
>         public Startup(IHostingEnvironment env)
>         {
>             var builder = new ConfigurationBuilder()
>                 .SetBasePath(env.ContentRootPath)
>                 .AddJsonFile("appsettings.json")
>                 .AddJsonFile($"appsettings.{env.EnvironmentName}.json", optional: true);
>
>             if(env.IsDevelopment())
>             {
>                 builder.AddUserSecrets<Startup>();
>             }
>             Configuration = builder.Build();
>         }
> ```
> >- **注意：用户密钥存储在特定的文件中**
> > > 这里直接调用AddUserSecrets方法会产生一个错误，会告诉我们找不到UserSecretsId。此时，我们需要在.csproj文件中配置，UserSecretsId的值是VS2017自动生成的，保存在C:\Users\admin\AppData\Roaming\Microsoft\UserSecrets\17d2ccdb-ef09-4020-90bf-5ba654f433dc，其中17d2ccdb-ef09-4020-90bf-5ba654f433dc就是UserSecretsId的值。（找文件夹更简单的方式，直接右键项目，点击"Manage User Secrets"，就可以找到文件路径）。
