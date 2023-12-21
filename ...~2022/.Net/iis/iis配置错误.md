1、HTTP 错误 500.21 - Internal Server Error
处理程序“SimpleHandlerFactory-Integrated”在其模块列表中有一个错误模块“ManagedPipelineHandler”；
2、HTTP 错误 404.17 - Not Found 请求的内容似乎是脚本，因而将无法由静态文件处理程序来处理。
**原因：在安装Framework v4.0之后，再启用IIS，导致Framework没有完全安装**
**解决方法：**
> 1.控制面板》程序和功能》打开或关闭Windows功能 > Internet信息服务 > 万维网服务 > 应用程序开发功能 > ASP.NET（看这个是否选上）；
> 2.“处理程序映射”中缺少ASP.NET 4.0的映射，需要添加映射。操作方法：在管理员身份打开命令行，运行以下命令：
`C:\Windows\Microsoft.NET\Framework\v4.0.30319\aspnet_regiis.exe -i`
> 3.如果运行再次出现“由于 Web 服务器上的“ISAPI 和 CGI 限制”列表设置，无法提供您请求的页面”的错误提示：可按如下方法解决：
　　IIS的根节点->右侧“ISAPI和CGI限制”->把禁止的DotNet版本项设置为允许

3、IIS 无法识别的属性“targetFramework”。请注意属性名称区分大小写。
**原因：部署网站时，使用的应用程序池版本不对！**　　
**解决方法：**
> 1.https://blog.csdn.net/jiankunking/article/details/50547601
