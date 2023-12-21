1、委托是什么？
> 1. 作用：把方法传递给其他方法。
> 2. 委托只是一种特殊类型的对象-以前定义的所有对象都包含数据，而委托包含的是一个或多个方法的地址。

2、委托的声明
> 1. 语法结构：`delegate void IntMethodInvoker(int x);`
> 2. 声明了一个委托IntMethodInvoker，
> 3. 指定该委托的每个实例都可以包含一个方法的引用，
> 4. 该方法带有一个int参数，并返回void。
> 5. 委托的一个要点是：类型安全性非常高。在定义委托时，必须给出它所表示的方法的签名和返回类型等全部细节。

3、委托的使用
> 1. 委托在语法上总是接受一个参数的构造函数，这个参数就是委托引用的方法
> 2. 这个方法必须匹配最初定义委托时的签名，
> 3. 所以，下面例子中，用不带参数并返回一个字符串的方法来初始化firstString1，会产生一个编译错误。
>- **注：** 在经过实际实验以后，发现这样声明一个委托不会产生任何错误，程序会正常执行。（vs2017 .net core项目）
> ```
>     private delegate string GetAString();  //委托在语法上总是接受一个参数的构造函数GetAString(string x)
>     int x = 40;
>     GetAString firstString1 = new GetAString(x.ToString);
>     WriteLine(firstString1);
>     //等价于
>     GetAString firstString2 = x.ToString;
>     WriteLine(firstString2);
> ```

4、Action<T>和Func<T>委托
> 1. 泛型Action<T>委托表示引用一个void返回类型的方法。
> 2. 这个委托类存在不同的变体，可以传递至多16种不同的参数类型。
> 3. 没有泛型参数的Action类可调用没有参数的方法。例如`Action<in T1>`，`Action<in T1, in T2, in T3, in T4>`
> 4. Func<T>委托可以以类似的方式使用。
> 5. Func<T>允许调用带返回类型的方法。
> 6. Func<T>定义了不同的变体，至多可以传递16个参数类型和一个返回类型。例如`Func<out TResult>`，`Func<in T1, in T2, in T3, out TResult>`

5、多播委托
> 1. 多播委托就是委托包含了多个方法。
> 2. 如果调用多播委托，就可以按顺序连续调用多个方法。
> 3. 因此，委托的签名就必须返回void，否则，就只能得到委托调用的最后一个方法的结果。
> 4. 多播委托可以识别运算符“+”和“+=”，还有“-”和“-=”。
>- **注意**
> > 1. 对于同一个委托，调用其方法链的顺序并未正式定义。
> > 2. 因此，应避免编写依赖于以特定顺序调用方法的代码；
> > 3. 多播委托包含一个逐个调用的委托集合。
> > 4. 如果通过委托调用的其中一个方法抛出异常，整个迭代就会停止。（解决方法：Delegate类定义了GetInvocationList()方法，返回一个Delegate对象数组）
> > ```
> >     public class Delegates
> >     {
> >         public static void One()
> >         {
> >             WriteLine("One");
> >             throw new Exception("Error in one");
> >         }
> >         public static void Two()
> >         {
> >             WriteLine("Two");
> >         }
> >     }
> >     //使用Delegate类避免异常问题
> >     Action d1 = Delegates.One;
> >     d1 += Delegates.Two;
> >     Delegate[] delegates = d1.GetInvocationList();
> >     foreach (Action d in delegates)
> >     {
> >         try
> >         {
> >             d();
> >         }
> >         catch (Exception)
> >         {
> >             WriteLine("Exception caught");
> >             WriteLine();
> >         }                
> >     }
> > ```

6、委托传递匿名方法的特点
>- 1、减少了要编写的代码。在为事件定义委托时，不必定义仅由委托使用的方法；
>- 2、降低代码的复杂性。定义了好几个事件时，代码会比较简单；
>- 3、代码执行速度并没有加快。编译器仍定义了一个方法，该方法只有一个自动指定的名称，我们不需要知道这个名称。

7、委托使用匿名方法的规则
> 1. 在匿名方法中不能使用跳转语句(break、goto或continue)跳到该匿名方法的外部，
> 2. 匿名方法外部的跳转语句不能跳到匿名方法的内部；
> 3. 在匿名方法的内部不能访问不安全的代码。
> 4. 不能访问在匿名方法外部使用的ref和out参数。
> 5. 但可以使用在匿名方法外部定义的其他变量。

8、委托中如何使用匿名方法
> 使用关键字delegate。
> ```
>     string mid = ", middle part, ";
>     Func<string, string> anonDel = delegate (string param)
>     {
>     param += mid;
>     param += "and this was added to the string.";
>     return param;
>     };
>     WriteLine(anonDel("Start of string"));
> ```

9、lambda表达式
> 作用：把实现代码赋予委托。只要有参数类型的地方，就可以使用lambda表达式。
> ```
>     string mid = ", middle part, ";
>     Func<string, string> lambda = param =>
>     {
>     param += mid;
>     param += "and this was added to the string.";
>     return param;
>     };
>     WriteLine(lambda("Start of string"));
> ```

10、lambda表达式中定义参数的方式
>- 1、只有一个参数：只写出参数名
> ```
>     Func<string string> oneParam = s => $"change uppercase {s.ToUpper()}";
>     WriteLine(oneParam("test"));
> ```
> 解释：委托类型定义了一个string参数，所以s的类型就是string
>- 2、多个参数，把这些参数名放在花括号中
> ```
>     Func<double, double, double> twoParams = (x, y) => x * y;
>     WriteLine(twoParams(3, 2));
> ```

11、什么是闭包
> 通过lambda表达式可以访问lambda表达式块外部的变量，这称为闭包。

12、事件

13、弱事件
> 通过事件，可直接连接发布程序和侦听器。但是，垃圾回收方面存在问题。例如，如果不再直接引用侦听器，发布程序就仍有一个引用。垃圾回收器不能清空侦听器占用的内存，因为发布程序仍保有一个引用，会对侦听器触发事件。这种强连接可以通过弱事件模式来解决，即使用WeakEventManager作为发布程序和侦听器之间的中介。
