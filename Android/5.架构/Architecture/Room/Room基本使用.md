转载请注明链接: https://blog.csdn.net/feather_wch/article/details/88744278

>

#  Room基本使用

版本号:2019-03-22(16:30)

---

[toc]
## 介绍

1、Room是Google提供的ORM，提供了三种主要的注解:
> * `@Database`
>     1. 用于注解抽象类，该抽象类必须继承自`RoomDatabase`
>     1. 用于`创建数据库`和`创建Daos对象-数据访问对象`
> * `@Entity`
>     1. 用于注解实体类
>     1. `@Database`通过`entities属性`引用`被@Entity注解的类`
>     1. 会利用`@Entity`注解的类的所有字段作为表的列名来`创建表`
> * `@Dao`
>     1. `@Dao`用于注解一个接口或者抽象类，用于`提供访问数据库的方法`
>     1. 在使用`@Database注解`的类中必须有一个不带参数的方法，这个方法返回使用`@Dao注解的类`。

## 集成依赖

2、Room的依赖添加
> 添加google maven 仓库
```
// 添加google maven 仓库
allprojects {
    repositories {
        jcenter()
        maven { url 'https://maven.google.com' }
    }
}
```
> 添加Room的依赖
```
// 添加Room
compile "android.arch.persistence.room:runtime:1.1.0"
annotationProcessor "android.arch.persistence.room:compiler:1.1.0"

// 方便调试，添加Android-Debug-Database的依赖
debugCompile 'com.amitshekhar.android:debug-db:1.0.1'
```
## 基本使用

### 实体: User

1、User用户信息实体
```java
@Entity
public class User
{
    @PrimaryKey
    private int uid;
    @ColumnInfo(name = "name")
    private String mName;
    @ColumnInfo(name = "age")
    private int mAge;

    public User(String name, int age)
    {
        mName = name;
        mAge = age;
    }

    public String getName()
    {
        return mName == null ? "" : mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public int getAge()
    {
        return mAge;
    }

    public void setAge(int age)
    {
        mAge = age;
    }
}
```

### UserDao

2、UserDao用于`提供访问数据库的方法`
```java
@Dao
public interface UserDao
{
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE name LIKE :uname AND "
            + "age LIKE :uage LIMIT 1")
    User findByName(String uname, int uage);

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);
}
```

### RoomDatabase

3、AppDatabase使用`@Database注解`用于`创建数据库`和`创建Daos对象-数据访问对象`
```java
@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase
{
    private static AppDatabase INSTANCE;
    private static final Object sLock = new Object();

    public abstract UserDao userDao();

    public static AppDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user.db")
                                .build();
            }
            return INSTANCE;
        }
    }
}
```

4、Room的使用
```java
// 1. 获取到Database和UserDao
AppDatabase db = AppDatabase.getInstance(this);
UserDao userDao = db.getUserDao();

// 2. 调用UserDao的插入操作
userDao.insertAll(new User("wang", 10), new User("chen", 23), new User("hao", 16));
// ......
// 其他UserDao的操作都可以调用
List<User> userList = userDao.getAll();
```

## 详细教程

1、Room不允许在主线程中访问数据库，除非在buid的时候使用allowMainThreadQueries()方法
```java
Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user.db")
.allowMainThreadQueries()
.build();
```

### Entity

1、Room默认使用`类名`作为`表名`，`字段名`作为`列名`

### 忽略字段

2、`@Ignore`注解不希望存储的字段
```java
@Ignore
Bitmap picture;
```

### 列名自定义

3、`@ColumnInfo`自定义列名
```java
@ColumnInfo(name = "name")
private String mName;
```

### 主键

4、`@PrimaryKey`指定主键
```java
@PrimaryKey
private int uid;
// 等效
@PrimaryKey(autoGenerate = true)
private int uid;
```

#### 复合主键
5、`@Entity的primaryKeys`指定复合主键
```java
@Entity(primaryKeys = {"name", "age"})
public class User
{
    @ColumnInfo(name = "name")
    private String mName;
    @ColumnInfo(name = "age")
    private int mAge;
}
```

### 索引

6、索引有什么用?
> 1. 可以加快查找速度
> 1. 当`表的更新和插入较为频繁时`，`不适合建立索引`


7、`@Entity的indices`可以建立索引
```java
@Entity(indices = {@Index("name"), @Index(value = {"age", "address"})})
public class User
{
    @ColumnInfo(name = "name")
    private String mName;
    @ColumnInfo(name = "age")
    private int mAge;
    @ColumnInfo(name = "address")
    private String mAddress;
}
```

#### 索引唯一性

8、`@Entity的indices中使用unique`可以保证多个字段形成的组，不会同时具有相同的数据
> 两个User的年龄和地址不能同时相同
```java
@Entity(indices = {@Index(value = {"age", "address"}, unique = true)})
public class User
{
    @ColumnInfo(name = "name")
    private String mName;
    @ColumnInfo(name = "age")
    private int mAge;
    @ColumnInfo(name = "address")
    private String mAddress;
}
```

### 关系

9、Room禁止使用直接关系，但可以在两个实体之间定义`外键`
> 1. User的id和Book的user_id具有关系
```java
@Entity
public class User
{
    @ColumnInfo(name = "id")
    private int id;
}

// Book.java中定义外键
@Entity(foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "id",
        childColumns = "user_id"))
public class Book
{
    @ColumnInfo(name = "book_id")
    private int bookId;
    @ColumnInfo(name = "user_id")
    private int userId;
}

```

10、外键有什么用?
> 1. 例如当`删除了一个user`后，具有相同`user_id`的`book也会全部删除`
> 1. `@ForeignKey的onDelete需要为CASCADE`
```java
@Entity(foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "id",
        childColumns = "user_id",
        onDelete = CASCADE))
public class Book
{
    @ColumnInfo(name = "book_id")
    private int bookId;
    @ColumnInfo(name = "user_id")
    private int userId;
}
```

### 嵌套对象


## 参考资料

1. [Android Room使用](https://www.jianshu.com/p/7354d5048597)
