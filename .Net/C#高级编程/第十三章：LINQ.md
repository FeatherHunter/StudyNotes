1、什么是LINQ？
> LINQ(Language Integrated Query，语言集成查询)，在C#编程语言中集成了查询语法，可以用相同的语法访问不同的数据源。LINQ提供了不同数据源的抽象层，所以可以使用相同的语法。

2、LINQ语句的格式
> 查询表达式必须以from子句开头，以select或group子句结束。在这两个子句之间，可以使用where、orderby、join、let和其他from子句。

3、LINQ筛选
>- 索引筛选：不能使用LINQ查询的是Where()方法的重载。在Where()方法的重载中，可以传递第二个参数-索引。
> ```
>   //使用索引返回姓氏以A开头的、索引为偶数的冠军选手
>     var racers = Formulal.GetChampions().Where((r, index) => r.LastName.StartsWith("A") && index % 2 != 0);
> ```
>- 类型筛选：使用OfType()扩展方法。
```
    object[] data = {"one", 2, 3, "four", "five", 6};
    //使用OfType()扩展方法，把string类型传递给泛型参数
    var query = data.OfType<string>();
    foreach(var s in query)
    {
        WriteLine(s);  //只会显示one four five
    }
```

4、复合的from子句
> C#编译器把复合的from子句和LINQ查询转换为SelectMany()扩展方法。
> ```
>     //筛选出驾驶法拉利的所有冠军选手
>     var ferrariDrivers = from r in Formulal.GetChampions()
>                         from c in r.Cars
>                         where c == "Ferrari"
>                         orderby r.LastName
>                         select r.FirstName + " " + r.LastName;
> ```

5、排序
> 降序排序：orderby descending子句；
> ```
>     var racers = from r in Formulal.GetChampions()
>                  where r.Country == "Brazil"
>                  orderby r.Wins descending
>                  select r;
> ```

6、分组
> 根据一个关键字值对查询结果分组，使用group子句；
> ```
>     var countries = from r in Formulal.GetChampions()
>                     group r by r.Country into g  //定义一个新的标识符g，用于访问分组的结果信息
>                     orderby g.Count() descending, g.Key
>                     where g.Count() >= 2
>                     select new {     //select创建带Country和Count属性的匿名类型
>                         Country = g.Key,
>                         Count = g.Count()
>                     };
> ```

7、LINQ查询中的变量
> 在LINQ查询中定义变量，使用let子句；
> ```
>     var countries = from r in Formulal.GetChampions()
>                     group r by r.Country into g  //定义一个新的标识符g，用于访问分组的结果信息
>                     let count = g.Count()
>                     orderby count descending, g.Key
>                     where g.Count() >= 2
>                     select new {     //select创建带Country和Count属性的匿名类型
>                         Country = g.Key,
>                         Count = g.Count()
>                     };
> ```

8、内连接
> 使用join子句可以根据特定的条件合并两个数据源，但之前要获得两个要连接的列表。
> ```
>     var racers = from r in Formulal.GetChampions()
>                 from y in r.Years
>                 select new
>                 {
>                     Year = y,
>                     Name = r.FirstName + " " + r.LastName
>                 };       //查询冠军赛车手
>     var teams = from t in Formulal.GetContructorChampions()
>                 from y in t.Years
>                 select new
>                 {
>                     Year = y,
>                     Name = t.Name
>                 };       //查询冠军车队    
>     //获得一个年份列表，列出每年的冠军赛车手和冠军车队
>     var racesAndTeams = (from r in races
>                         join t in teams on r.Year equals t.Year
>                         select new
>                         {
>                             r.Year,
>                             Champion = r.Name,
>                             Constructor = t.Name
>                         }).Take(10);
> ```

9、左外连接
> 左外连接：返回左边序列中的全部元素，即使它们在右边的序列中并没有匹配的元素。左外连接用join子句和DefaultEmpty方法定义。

10、组连接
> 可以连接两个独立的序列，对于其中一个序列中的某个元素，另一个序列中存在对应的一个项列表。

11、集合操作
> 扩展方法Distinct()、Union()、Intersect()和Except()都是集合操作。

12、合并
> Zip()方法允许用一个谓词函数把两个相关的序列合并为一个。

13、分区
> 扩展方法Take()和Skip()等的分区操作可用于分页。Skip方法先忽略根据页面的大小和实际页数计算出的项数，Take方法根据页面大小提取一定数量的项。

14、聚合操作符
> 聚合操作符（Count、Sum、Min、Max、Average和Aggregate操作符）不返回一个序列，而返回一个值。
>- Count：返回集合中的项数；
>- Sum：汇总序列中的所有数字，返回这些数字的和；
>- Min：返回集合中的最小值；
>- Max：返回集合中的最大值；
>- Average：计算集合中的平均值；
>- Aggregate：可以传递一个lambda表达式，该表达式对所有的值进行聚合。

15、转换操作符
> 查询可以推迟到访问数据项时再执行。在迭代中使用查询时，查询会执行。而使用转换操作符会立刻执行查询，把查询结果放在数组、列表或字典中。

16、生成操作符
> 生成操作符Range()、Empty()和Repeat()不是扩展方法，而是返回序列的正常静态方法。

17、并行查询

18、分区器

19、表达式树
