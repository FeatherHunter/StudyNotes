# C语言
基本样例：
```c
#include <stdio.h>
// <> 寻找系统的h
// "" 寻找自己的h
// .h .hpp 头
// .c .cpp 实现
int main(){
    printf("Hello")
    // getChar(); // 会阻塞程序
    return 0;
}
```
C和Java的区别？
1. Java万物皆对象
2. C万物皆指针
3. Linux万物皆文件
## 基本数据类型
1、各种数据类型和打印
```c
int a = 100;
double b = 200;
float c = 300;
long d = 400;
short e = 500;
char f = 'c';
char * str = "hello"
printf("%d", a);
printf("%lf", b);
printf("%f", c);
printf("%d", d); // long、short、int 用d
printf("%d", e); // short
printf("%c", f);
printf("%s", str); // 字符串
```
2、基本数据类型占用多少字节？
```c
char/unsigned char 1
short 2
int/unsigned int 4
float 4
double 8
char * 4/8
long 4/8
long long 8
long double 12/16
printf("int占用多少字节：%d\n", sizeof(int));
```
3、NULL等价于0

## 指针

1、打印地址
```c
printf("address = %p", &a); // p打印地址
printf("address = %d", *(&a)); // 从a地址取出数值
```
2、main函数执行流程
```c
1. main进栈 // 进的什么栈？ 会给main函数分配内存，根据写的程序顺序，以堆栈形式在这块内存上存取数据
// 调用main函数后，系统将ebp栈底指针和esp栈顶指针，压入到“运行时堆栈-栈帧”中，然后
2. int a分配内存地址，存放数据 100
3. 找到a对应的地址，打印数据
4. main出栈
```

什么是指针？
> 就是内存地址
什么是指针变量？
> 变量：存储的是地址，通过地址可以找到目标
用指针打印数据：
```c
#include <stdio.h>
int main(){
    int a = 100;
    double b = 300;
    int *pint = &a;
    double *pdouble = &b;
    printf("a = %d\n", *pint);
    printf("b = %lf\n", *pdouble);
    return 0;
}
// a = 100
// b = 300.000000
```
指针就是地址，地址就是指针
```c
*pint = 400; //修改地址里面的数值
```
### 函数修改值
```c
#include <stdio.h>
/**
 * C是结构型代码，要使用，需要提前声明
*/
void change(int);
void change2(int*);
int main(){
    int a = 100;
    change(a); // 修改无效
    change2(&a); // 用指针进行修改
    printf("%d", a);
    return 0;
}
/**
 * 修改无效
 */
void change(int i){
    i = 400;
}
void change2(int *i){
    *i = 400;
}
```
交换两个地址的值
```c
void change(int*a, int*b){
    int temp = *a;
    *a = *b;
    *b = temp;
}
// printf("%p %d", &a, a); 打印指针/内存地址和数值
```


### 指针的指针
```c
    int a = 100;
    int *p = &a;
    int **pp = &p;
    int ***ppp = &pp; // 项目中最多三级指针
    
    printf("%d %d", **pp, *p);
```
### 数组和数组指针
C中的遍历：
```c
    int i = 0;
    for(i = 0; i < 4; ++i){
        // xxx
    }
```
数组定义：
```c
    // 数组1
    int a[] = {1,2,3,4};
    // 数组2
    int b[10];
```
数组首地址：
```c
    /**
     * 数组和指针直接挂钩，因为指针就是内存地址
     * 下面数据完全一致：
    */
   printf("%p\n", a);
   printf("%p\n", &a);
   printf("%p\n", &a[0]);
```
指针遍历数组：
```c
    printf("%d", p[1]); // 指针直接当做数组使用
   int *p = a;
   int i = 0;
   for(i = 0; i < 4; i++){
    printf("%d", *p);
    p++; // 设定p的类型为int *，在++时候才知道移动的距离
   }
   *p = a;
   int i = 0;
   for(i = 0; i < 4; i++){
    printf("%d", *(p+i)); // 指针作为数组使用
    printf("%d", p[i]);
   }
```
指针给数组赋值
```c
    int a[] = {1,2,3,4};
    int *p = a;
    
    int i = 0;
    for(i = 0; i < 4; i++){
        p[i] = i*2; // 修改数据
        printf("%d", a[i]);
    }
```
用sizeof遍历数组：
```c
    int i;
    // sizeof
    for(i = 0; i < sizeof(a)/sizeof(int); i++){
//     for(i = 0; i < sizeof(a)/sizeof(p); i++){ // 错误！p作为int *指针，大小为8字节（64位），int为4字节
        p[i] = i*2; // 修改数据
        printf("%d", a[i]);
    }
```
打印int、int[]、int *的sizeof大小：
```c
    printf("%d", sizeof(a)); // 16
    printf("%d", sizeof(p)); // 8 
    printf("%d", sizeof(int)); // 4
```
### 指针类型
指针的类型不匹配：
```c
    int a = 100;
    int *p = &a;
    double *d = &a;// 会报错
    printf("%lf", *d); // 打印的数据位0.000000
```
为什么不用统一的指针？
1. 指针统一是4字节/8字节
2. 指针类型是为了处理数据时，知道数据的具体格式，适合处理
### 函数指针
1、初步使用函数指针
> 是什么？存放函数的地址
```c
void change(int *a, int *b){
    int temp = *a;
    *a = *b;
    *b = temp;
}
// 函数指针
void operate(void (*fp)(int*, int*)){
    int a = 100;
    int b = 200;
    printf("%d %d\n", a, b);
    fp(&a, &b);
    printf("%d %d\n", a, b);
}
int main(){
    operate(change);
    return 0;
}
```
2、函数指针有什么用？
> 依赖倒置，控制反转。将执行的操作交由外部实现。

