1、自定义插件
> 1.扩展`$`的工具方法：`$.extend();`
> 2.扩展`$()`的功能方法；`$.fn.extend();`
```
(function () {              //立即执行函数
    /*
* 给 $ 添加4个工具方法
* min(a,b)：返回较小值；
* max(a,b)：返回较大值；
* leftTrim()：去掉字符串左边的空格；
* rightTrim()：去掉字符串右边的空格；
*
* 例如：$.min(3,8);
* */
    $.extend({
        min: function (a, b) {
            return a < b ? a : b;
        },
        max: function (a, b) {
            return a < b ? b : a;
        },
        leftTrim: function (str) {
            return str.replace(/^\s+/, '');
        },
        rightTrim: function (str) {
            return str.replace(/\s+$/, '');
        }
    });

    /*
* 给jquery对象 添加3个功能方法(针对checkbox的应用)
* checkAll()：全选；
* uncheckAll()：全不选；
* reverseCheck()：全反选；
*
* 例如：$('input').checkAll();
* */
    $.fn.extend({
        checkAll: function () {
            this.prop('checked', true);                   //this指jquery对象
        },
        uncheckAll: function () {
            this.prop('checked', 'false');
        },
        reverseCheck: function () {
            this.each(function () {                       //this指jquery对象
                this.checked = !this.checked;             //this指dom元素
            })
        }
    })
})();
```
