转载请注明链接: https://blog.csdn.net/feather_wch/article/details/88744278

>

#  Room基本使用

版本号:2019-03-23(15:30)

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

## 报错

### Schema export directory is not provided to the annotation processor so we cannot export the schema. You can either provide `room.schemaLocation` annotation processor argument OR set exportSchema to false.

1、报错`Schema export directory is not provided to the annotation processor so we cannot export the schema. You can either provide `room.schemaLocation` annotation processor argument OR set exportSchema to false.`
> 给Entity加上属性
```java
@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase
{
  // 省略
}
```

### Cannot access database on the main thread since it may potentially lock the UI for a long period of time.

2、报错`Cannot access database on the main thread since it may potentially lock the UI for a long period of time.`
> 不能在主线程访问db
```java
// 开启子线程访问db
```

### UNIQUE constraint failed: User.account (code 1555 SQLITE_CONSTRAINT_PRIMARYKEY)

3、报错`UNIQUE constraint failed: User.account (code 1555 SQLITE_CONSTRAINT_PRIMARYKEY)`
> 插入了键值相同的实体，避免做出这样的操作。

### Please provide the necessary Migration path via RoomDatabase.Builder.addMigration(Migration ...) or allow for destructive migrations via one of the RoomDatabase.Builder.fallbackToDestructiveMigration* methods.

4、报错` or allow for destructive migrations via one of the RoomDatabase.Builder.fallbackToDestructiveMigration* methods.`
>

## 详细教程

1、Room不允许在主线程中访问数据库，除非在buid的时候使用allowMainThreadQueries()方法
```java
Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user.db")
.allowMainThreadQueries()
.build();
```

### Entity

2、Room默认使用`类名`作为`表名`，`字段名`作为`列名`

### 忽略字段

3、`@Ignore`注解不希望存储的字段
```java
@Ignore
Bitmap picture;
```

### 列名自定义

4、`@ColumnInfo`自定义列名
```java
@ColumnInfo(name = "name")
private String mName;
```

### 主键

5、`@PrimaryKey`指定主键
```java
@PrimaryKey
private int uid;
// 等效
@PrimaryKey(autoGenerate = true)
private int uid;
```

#### 复合主键
6、`@Entity的primaryKeys`指定复合主键
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

7、索引有什么用?
> 1. 可以加快查找速度
> 1. 当`表的更新和插入较为频繁时`，`不适合建立索引`


8、`@Entity的indices`可以建立索引
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

9、`@Entity的indices中使用unique`可以保证多个字段形成的组，不会同时具有相同的数据
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

10、Room禁止使用直接关系，但可以在两个实体之间定义`外键`
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

11、外键有什么用?
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

12、嵌套对象，User实体的字段为其他类型的对象
> 地址Address
```java
@Entity
public class Address {
    String name;
    String address;

    public Address(String name, String address) {
        this.name = name;
        this.address = address;
    }

  // xxx
}
```
> User中通过`@Embedded`在内部嵌套对象
```java
@Entity
public class User{
    @NonNull
    @PrimaryKey
    String account;
    String password;
    String name;
    int age;
    String headurl;

    @Embedded(prefix = "address")
    Address address;

    public User(){

    }

    public User(String name, String account, String password, int age, String headUrl, Address address) {
        this.name = name;
        this.account = account;
        this.password = password;
        this.age = age;
        this.headurl = headUrl;
        this.address = address;
    }
}

```

13、如果嵌套的对象的字段名重复怎么办?
> 1. 使用`@Embedded(prefix = "address")`在前面加上前缀，避免冲突

## Daos

1、Dao类是Room的重要组件，该类提供了所有操作数据库的方法

### 插入`@Insert`

2、`@Insert注解`用于将所有参数插入到数据库中
```java
@Dao
public interface MyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUsers(User... users);

    @Insert
    public void insertBothUsers(User user1, User user2);

    @Insert
    public void insertUsersAndFriends(User user, List<User> friends);
}
```

3、`@Insert注解的onConflict `的作用
> 1. 用于指定放生冲突时的`策略`
> 1. 当`@Index的unique属性为true时`，发生冲突因为默认策略是`OnConflictStrategy.ABORT`会直接崩溃
> 1. 使用`OnConflictStrategy.REPLACE`能替换老数据
> 1. [SQLite冲突策略](https://sqlite.org/lang_conflict.html)


4、`@Insert`方法的返回值
```java
    @Insert
    long insert(User user);
    Long insert(User user);

    @Insert
    List<Long> insert(List<User> users);
    List[] insert(List<User> users);
```
> 1. 插入单个实体，返回`新条目的行id`
> 1. 插入一组实体，返回`新条目的行id的集合`

### 更新`@Update`

5、`@Update注解`进行数据更新(主键相同)
```java
@Dao
public interface MyDao {
    @Update
    public void updateUsers(User... users);
}
```

### 删除`@Delete`

6、`@Delete注解`进行数据删除(主键相同)
```java
@Dao
public interface MyDao {
    @Delete
    public void deleteUsers(User... users);
}
```

### 查询`@Query`

7、`@Query`能进行数据的查询
> 1. `@Query`的值为SQL语句，可以被SQLite执行。
> 1. `@Query`支持查询语句，删除语句和更新语句，不支持插入语句。
```java
@Dao
public interface MyDao {
    @Query("SELECT * FROM user")
    public User[] loadAllUsers();
}
```

#### 参数

8、`@Query`的value中支持添加绑定参数
> `:minAge`对应于方法的参数`int minAge`
```java
@Dao
public interface MyDao {
//传入单个参数
    @Query("SELECT * FROM user WHERE age > :minAge")
    public User[] loadAllUsersOlderThan(int minAge);
}
```

9、允许传入多个参数，或者多次引用相同的参数
```java
@Dao
public interface MyDao {
  // 1. 多个参数
    @Query("SELECT * FROM user WHERE age BETWEEN :minAge AND :maxAge")
    public User[] loadAllUsersBetweenAges(int minAge, int maxAge);

  // 2. 多次引用相同的参数
    @Query("SELECT * FROM user WHERE first_name LIKE :search "
           + "OR last_name LIKE :search")
    public List<User> findUserWithName(String search);
}
```

10、Room允许传入一个参数集合
```java
@Dao
public interface MyDao {
    @Query("SELECT first_name, last_name FROM user WHERE region IN (:regions)")
    public List<NameTuple> loadUsersFromRegions(List<String> regions);
}
```

#### 只获取部分字段

11、很多时候只需要实体的少数几个字段，这时就可以将要获取的列打包取出
> 1. 这样可以节省资源，并且查询速度更快
> 1. Room支持将查询的结果映射到返回的字段上
```java
// 该类只获取姓名
public class NameTuple {
    @ColumnInfo(name="first_name")
    public String firstName;

    @ColumnInfo(name="last_name")
    public String lastName;
}
```
> 获取部分字段
```java
@Dao
public interface MyDao {
    @Query("SELECT first_name, last_name FROM user")
    public List<NameTuple> loadFullName();
}
```

#### RxJava

12、Room支持返回RxJava2的`Plubisher`和`Flowable`对象
```java
@Dao
public interface MyDao {
    @Query("SELECT * from user where id = :id LIMIT 1")
    public Flowable<User> loadUserById(int id);
}
```

#### Cursor

13、Room可以直接返回Cursor对象
```java
@Dao
public interface MyDao {
    @Query("SELECT * FROM user WHERE age > :minAge LIMIT 5")
    public Cursor loadRawUsersOlderThan(int minAge);
}
```

#### 多表查询

14、Room中的多表查询
```java
@Dao
public interface MyDao {
    @Query("SELECT * FROM book "
           + "INNER JOIN loan ON loan.book_id = book.id "
           + "INNER JOIN user ON user.id = loan.user_id "
           + "WHERE user.name LIKE :userName")
   public List<Book> findBooksBorrowedByNameSync(String userName);
}
```

## 类型转换器

1、Room支持字符串和基本数据类型的存储，但是如果不是基本类型，就需要`类型转换器`
> 1. User对象有个Date类型的字段birthday
> 1. 利用`@TypeConverter`可以将不可存储的类型转换为可以存储的类型
>
> 转换器
```java
public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
```
> Room使用转换器: `@TypeConverters`将转换器添加到`AppDatabase`上
```java
@Database(entities = {User.java}, version = 1)
@TypeConverters({Converter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
```

## 数据库升级

1、数据库升级是在新增表或者修改原来的结构时所需要的
> 1. Room提供了 Migration 类用于迁移数据库
> 1. 每一个 Migration 需要在构造函数里指定开始版本和结束版本
> 1. 在运行时，Room会按照提供版本的顺序顺序执行每个Migration的migrate()方法，将数据库升级到最新的版本。
```java
Room.databaseBuilder(getApplicationContext(), MyDb.class, "database-name")
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3).build();

static final Migration MIGRATION_1_2 = new Migration(1, 2) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("CREATE TABLE `Fruit` (`id` INTEGER, "
                + "`name` TEXT, PRIMARY KEY(`id`))");
    }
};

static final Migration MIGRATION_2_3 = new Migration(2, 3) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE Book "
                + " ADD COLUMN pub_year INTEGER");
    }
};
```

## 知识扩展

### Cursor

1、Android中Cursor是什么？

## 参考资料

1. [Android Room使用](https://www.jianshu.com/p/7354d5048597)
1. [SQLite冲突策略](https://sqlite.org/lang_conflict.html)
