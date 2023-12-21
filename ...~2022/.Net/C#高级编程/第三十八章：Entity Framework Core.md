*注意*
> 要使用EntityFrameworkCore框架，需要引入NuGet包，要将Microsoft.EntityFrameworkCore.SqlServer包和Microsoft.EntityFrameworkCore.Tools包安装到项目中。

1、什么是Entity Framework架构
> Entity Framework是一个提供了实体-关系映射的架构，通过它，可以创建映射到数据库表的类型，使用LINQ创建数据库查询，创建和更新对象，把它们写入数据库。

2、Entity Framework的发展历程
>- Entity Framework 1：第一个版本没有准备用于.NET3.5,但不久就可用于.NET3.5 SPI。另一个产品是LINQ to SQL，只用于访问SQL Server。第一个版本有一个缺点：要求模型类型派生自EntityObject基类。
>- Entity Framework 4：可用于.NET4，进行了重大改变，许多想法都来自LINQ to SQL。这个版本中，增加了延迟加载，在访问属性时获取关系。设计模型后，可以使用SQL数据定义语言（DDL）创建数据库。其中，两个模型是Database First或Model First。增加的最重要的特性是支持Plain Old CLR Object（POCO），所以不再需要派生自EntityObject基类。
>- Entity Framework 5：支持.NET4.5和.NET4应用程序。这个版本，新增了性能改进，支持新的SQL Server功能，如空间数据类型。
>- Entity Framework 6：解决了5的部分问题，Entity Framework的完整代码都移动到了NuGet包上。
>- Entity Framework Core1.0：完全重写了，删除旧的行为。只支持Code First模型，这不意味着数据库不存在，可以先创建数据库，或纯粹从代码中定义数据库。这个版本基于.NET Core，可以在Linux和Mac系统上使用这个框架。它不仅支持关系数据库，也支持NoSql数据库。

3、如何将实体类型映射到SQL Server数据库中的表中，把记录写到数据库，然后读取、更新和删除它们？
>- **创建实体模型**：Book类是简单的实体类型，定义了三个属性。BookID映射到表的主键，Title映射到Title列，Publisher映射到Publisher列。
> ```
>     [Table("Books")]
>     public class Book
>     {
>         public int BookId { get; set; }
>         public string Title { get; set; }
>         public string Publisher { get; set; }
>     }
> ```
>- **创建上下文**：创建BooksContext类，实现Book表与数据库的关系。这个类派生自DbContext。BooksContext类定义了DbSet<Book>类型的Books属性，允许创建查询，添加Book实例存储在数据库中。
> ```
>     public class BooksContext : DbContext
>     {
>         private const string ConnectionString = @"server=localhost;database=Turing;uid=sa;pwd=123456";  //连接数据库字符串
>
>         public DbSet<Book> Books { get; set; }
>         protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
>         {
>             base.OnConfiguring(optionsBuilder);
>             optionsBuilder.UseSqlServer(ConnectionString);  //用UseSqlServer扩展方法将上下文文映射到SQL Server数据库
>         }
>     }
> ```
>- **写入数据库**：
> ```
>     private async Task AddBookAsync(int id, string title, string publisher)   //写入单个记录
>     {
>         using (var context = new BooksContext())  //确保打开了数据库连接
>         {
>             var book = new Book
>             {
>                 BookId = id,
>                 Title = title,
>                 Publisher = publisher
>             };
>             context.Add(book);  //将对象添加到上下文
>             int records = await context.SaveChangesAsync();  //将实体写入数据库
>
>             WriteLine($"{records} record added");
>         }
>         WriteLine();
>     }
> ```
> ```
>         private async Task AddBooksAsync()  //写入一组数据
>         {
>             using (var context = new BooksContext())
>             {
>                 var b1 = new Book { BookId = 11, Title = "Professional C# 5 and .NET 4.5.1", Publisher = "Wrox Press" };
>                 var b2 = new Book { BookId = 12, Title = "Professional C# 2012 and .NET 4.5", Publisher = "Wrox Press" };
>                 var b3 = new Book { BookId = 13, Title = "JavaScript for Kids", Publisher = "Wrox Press" };
>                 var b4 = new Book { BookId = 14, Title = "Web Design with HTML and CSS", Publisher = "For Dummies" };
>                 var b5 = new Book { BookId = 15, Title = "Conflict Handling", Publisher = "Test" };
>                 context.AddRange(b1, b2, b3, b4, b5);
>                 int records = await context.SaveChangesAsync();
>
>                 WriteLine($"{records} record added");
>             }
>             WriteLine();
>         }
> ```
>- **读取数据库**：调用BooksContext，访问Books属性，可以读取数据库中所有的记录。
> ```
>     private void ReadBooks()
>     {
>         using (var context = new BooksContext())
>         {
>             var books = context.Books;
>             foreach (var b in books)
>             {
>                 WriteLine($"{b.Title}  {b.Publisher}");
>             }
>         }
>     }
> ```
> ```
>     private void QueryBooks()  //创建LINQ查询来访问数据库
>     {
>         using (var context = new BooksContext())
>         {
>             //var wroxBooks = context.Books.Where(b => b.Publisher == "Wrox Press");
>             var wroxBooks = from b in context.Books
>                         where b.Publisher == "Wrox Press"
>                         select b;
>             foreach(var b in wroxBooks)
>             {
 >                WriteLine($"{b.Title} {b.Publisher}");
>             }
>         }
>     }
> ```
>- **更新记录**：用上下文加载对象，并调用SaveChangesAsync()方法。
> ```
>     private async Task UpdateBookAsync()
>     {
>         using (var context = new BooksContext())
>         {
>             int records = 0;
>             var book = context.Books.Where(b => b.Title == "Professional C# 5 and .NET 4.5.1").FirstOrDefault();  //FirstOrDefault方法返回第一个满足条件的记录，如果不存在则返回null
>             if(book != null)
>             {
>                  book.Title = "Professional C# 6";
>                  records = await context.SaveChangesAsync();
>             }
>             WriteLine($"{records} record updated");
>         }
>     }
> ```
>- **删除记录**：调用Remove或RemoveRange方法，把上下文中对象的状态设置为删除，再调用SaveChangesAsync方法，从数据库中删除记录。
> ```
>     private async Task DeleteBooksAsync()
>     {
>         using (var context = new BooksContext())
>         {
>             var books = context.Books;
>             context.Books.RemoveRange(books);
>             int records = await context.SaveChangesAsync();
>             WriteLine($"{records} records deleted");
>         }
>     }
> ```
>- **在main函数中的调用**
> ```
>         var p = new Program();
>         p.AddBookAsync(10, "Professional", "Wrox").Wait();
>         p.AddBooksAsync().Wait();
>         p.ReadBooks();
>         p.QueryBooks();
>         p.UpdateBookAsync().Wait();
>         p.DeleteBooksAsync().Wait();
> ```

4、使用依赖注入框架来注入连接和SQL Server选项
>- **创建实体模型**：同上方法；
>- **BooksContext类**：只需要定义Books属性；
> ```
>     public class BooksContext:DbContext
>     {
>         public BooksContext(DbContextOptions options)
>             :base(options)
>         {
>         }
>         public DbSet<Book> Books { get; set; }
>     }
> }
> ```
>- **BooksService类**：在这个类中，BooksContext通过构造函数注入功能来注入；
> ```
>     public class BooksService
>     {
>         private readonly BooksContext _booksContext;
>         public BooksService(BooksContext context)   //注入BooksContext
>         {
>             _booksContext = context;
>         }
>
>         public async Task AddBooksAsync()
>         {
>             var b1 = new Book { BookId = 20, Title = "Professional C# 5 and .NET 4.5.1", Publisher = "Wrox Press" };
>             var b2 = new Book { BookId = 21, Title = "Professional C# 2012 and .NET 4.5", Publisher = "Wrox Press" };
>             var b3 = new Book { BookId = 22, Title = "JavaScript for Kids", Publisher = "Wrox Press" };
>             var b4 = new Book { BookId = 23, Title = "Web Design with HTML and CSS", Publisher = "For Dummies" };
>             _booksContext.AddRange(b1, b2, b3, b4);  //使用BooksService类的上下文成员
>             int records = await _booksContext.SaveChangesAsync();
>
>             WriteLine($"{records} records added");
>         }
>
>         public void ReadBooks()
>         {
>             var books = _booksContext.Books;   //使用BooksService类的上下文成员
>             foreach (var b in books)
>             {
>                 WriteLine($"{b.Title} {b.Publisher}");
>            }
>         }
>     }
> ```
>- **依赖注入框架的容器需要初始化**：在InitializeServices方法中初始化。创建ServiceCollection实例，在这个集合中添加BooksService类，进行短暂的生命周期管理。为了注册Entity Framework和SQL Server，可以使用扩展方法AddEntityFramework、AddSqlServer和AddDbContext。
> ```
>     private void InitializeServices()
>     {
>         const string ConnectionString = @"server=localhost;database=Turing;uid=sa;pwd=123456";
>
>         var services = new ServiceCollection();
>         services.AddTransient<BooksService>();
>         services.AddDbContext<BooksContext>(options =>
>         options.UseSqlServer(ConnectionString));
>         Container = services.BuildServiceProvider();
>     }
>     public IServiceProvider Container { get; set; }
> ```
>- **服务的初始化和使用BooksService在main方法中完成**：通过调用IServiceProvider的GetService()方法检索BooksService。
> ```
>     static void Main(string[] args)
>     {
>         var p = new Program();
>         p.InitializeServices();
>
>         var services = p.Container.GetService<BooksService>();
>         services.AddBooksAsync().Wait();
>         services.ReadBooks();
>     }
> ```
