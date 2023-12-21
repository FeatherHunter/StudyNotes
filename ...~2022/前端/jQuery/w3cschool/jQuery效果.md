1、显示和隐藏方法：hide()和show()
> 1.语法：`$(selector).hide(speed,callback)`和`$(selector).show(speed,callback)`
> 2.可选的speed参数规定隐藏/显示的速度，可以取值："slow"、"fast"或毫秒；
> 3.可选的callback参数是隐藏或显示完成后所执行的函数名称；
```
<script type="text/javascript">
    $(document).ready(function(){
        $("#hide").click(function(){
            $("p").hide(1000);
        });
        $("#show").click(function(){
            $("p").show();
        });
    });
</script>

<p>如果点击"隐藏"，我就会消失</p>
<button id="hide">隐藏</button>
<button id="show">显示</button>
```

2、切换显示和隐藏：toggle()
> 1.语法：`$(selector).toggle(speed,callback)`
> 2.可选的speed参数规定隐藏/显示的速度，可以取值："slow"、"fast"或毫秒；
> 3.可选的callback参数是toggle()方法完成后执行的函数名称；
```
$(document).ready(function(){
    $("#toggle").click(function(){
        $("p").toggle();
    });
});

<p>如果点击"隐藏"，我就会消失</p>
<button id="toggle">切换显示和隐藏</button>
```

3、淡入淡出方法：Fading方法
> 1.fadeIn():淡入已隐藏的元素；
> 2.fadeOut();淡出可见元素；
> 3.fadeToggle():在前两个方法之间切换；
> 4.fadeTo():允许渐变为给定的不透明度(值介于0-1之间)；

4、fadeIn()方法
> 1.作用：淡入已隐藏的元素；
> 2.语法：`$(selector).fadeIn(speed,callback)`
> 3.可选的speed参数规定效果的时长，可以取值："slow"、"fast"或毫秒；
> 4.可选的callback参数是fading完成之后所执行的函数名称；
```
.div1{
    width: 80px;
    height: 80px;
    background-color:#2e6da4;
    display: none;
}
.div2{
    width: 80px;
    height: 80px;
    background-color:#398439;
    display: none;
}
.div3{
    width: 80px;
    height: 80px;
    background-color:#761c19;
    display: none;
}

<script type="text/javascript">
    $(document).ready(function(){
        $("#fadeIn").click(function(){
            $("#div1").fadeIn("fast");
            $("#div2").fadeIn("slow");
            $("#div3").fadeIn(3000);
        });
    });
</script>

<button id="fadeIn">淡入已隐藏的元素</button><br><br>
<div class="div1" id="div1"></div><br>
<div class="div2" id="div2"></div><br>
<div class="div3" id="div3"></div><br>
```

5、fadeOut()方法
> 1.作用：用于淡出可见元素；
> 2.语法：`$(selector).fadeOut(speed,callback)`
> 3.可选的speed参数规定效果的时长，可以取值："slow"、"fast"或毫秒；
> 4.可选的callback参数是fading完成后所执行的函数名称；

6、fadeToggle()方法
> 1.作用：在fadeIn()和fadeOut()方法之间切换；
> 2.语法：`$(selector).fadeToggle(speed,callback)`

7、fadeTo()方法
> 1.作用：允许渐变为给定的不透明度(值介于0与1之间)；
> 2.语法：`$(selector).fadeTo(speed,opacity,callback)`
> 3.必须的speed参数规定效果的时长，可以取值："slow"、"fast"或毫秒；
> 4.必须的opacity参数将淡入淡出效果设置为给定的不透明度(值介于0与1之间)；
> 5.可选的callback参数是fading完成后所执行的函数名称；
```
$(document).ready(function(){
    $("#fadeTo").click(function(){
        $("#div111").fadeTo(3000,0.15);
        $("#div222").fadeTo("slow",0.4);
        $("#div333").fadeTo("fast",0.6);
    })
})
```

8、jQuery滑动方法
> 1.slideDown():向下滑动元素；
> 2.slideUp():向上滑动元素；
> 3.slideToggle():在向上和向下滑动之间切换；

9、slideDown()方法
> 1.作用：向下滑动元素；
> 2.语法：`$(selector).slideDown(speed,callback)`
> 3.可选的speed参数规定效果的时长，可以取值："slow"、"fast"或毫秒；
> 4.可选的callback参数是滑动完成后所执行的函数名称；

10、slideUp()方法
> 1.作用：向上滑动元素；
> 2.语法：`$(selector).slideUp(speed,callback)`
> 3.可选的speed参数规定效果的时长，可以取值："slow"、"fast"或毫秒；
> 4.可选的callback参数是滑动完成后所执行的函数名称；

11、slideToggle()方法
> 1.作用：切换上下滑动；
> 2.语法：`$(selector).slideToggle(speed,callback)`
> 3.可选的speed参数规定效果的时长，可以取值："slow"、"fast"或毫秒；
> 4.可选的callback参数是滑动完成后所执行的函数名称；
```
$(document).ready(function(){
    $("#toggle").click(function(){
        $("#change").slideToggle();
    });
});

<button id="toggle">滑动切换</button>
<p id="change">你可以找到你所需要的所有网站建设教程</p>
```

12、jQuery动画：animate()方法
> 1.作用：创建自定义动画；
> 2.语法：`$(selector).animate({params},speed,callback)`
> 3.必须的params参数定义形成动画的css属性；
> 4.可选的speed参数规定效果的时长，可以取值："slow"、"fast"或毫秒；
> 5.可选的callback参数是动画完成后所执行的函数名称；
**注意：对位置进行操作，首先要把元素的CSS position属性设置为relative、fixed或absolute**
```
<script type="text/javascript">
    $(document).ready(function(){
        $("#start").click(function(){
            $("#startAnimate").animate({left:'250px'});
        });
    });
</script>
<style>
    .start{
        height: 50px;
        width:50px;
        background-color:#f00;
        position:relative;       //设置属性
    }
</style>

<button id="start">开始动画</button>
<div class="start" id="startAnimate"></div>
```

13、animate操作多个属性
> 1.animate()方法几乎可以操作所有的css属性，
> 2.但是，在使用时，必须使用Camel标记法书写所有的属性名，例如，paddingLeft而不是padding-left；
> 3.色彩动画不包含在核心jQuery库中，需要下载 Color Animations插件；
```
$(document).ready(function(){
    $("#start").click(function(){
        $("#startAnimate").animate({
        left:'250px',
        opacity:'0.5',
        height:'150px'
        });
    });
});
```

14、animate()使用相对值
> 1.在值的前面加上`+=`或`-=`；
```
$(document).ready(function(){
    $("#start").click(function(){
        $("#startAnimate").animate({
        left:'250px',
        height:'+=150px',
        width:'-=50px'
        });
    });
});
```

15、animate()使用预定义值
> 1.show:显示；
> 2.hide:隐藏
> 3.toggle:切换显示和隐藏；
```
$(document).ready(function(){
    $("#start").click(function(){
        $("#startAnimate").animate({
          height:'toggle'
          });
    });
});
```

16、animate()队列功能
> 1.编写多个animate()调用，然后会逐一调用这些方法；
```
$(document).ready(function(){
    $("#start").click(function(){
         var temp = $("#startAnimate");
         temp.animate({height:'300px',opacity:'0.4'},"slow");
         temp.animate({width:'300px',opacity:'0.8'},"slow");
         temp.animate({height:'100px',opacity:'0.4'},"slow");
         temp.animate({width:'100px',opacity:'0.8'},"slow");
    });
});
```

17、stop()方法
> 1.作用：用于停止动画或效果(在他们完成之前)；
> 2.适用的效果：包括滑动，淡入淡出和自定义动画；
> 3.语法：`$(selector).stop(stopAll,goToEnd)`
> 4.可选的stopAll参数规定是否应该清楚动画队列。默认是false，仅停止活动的动画，允许任何排入队列的动画向后执行；
> 5.可选的goToEnd参数规定是否立即完成当前动画。默认是false。
**注意：默认的无参数方法：stop()会清除在被选元素上指定的当前动画**

18、stop()滑动效果，无参数
```
<script type="text/javascript">
    $(document).ready(function(){
        $("#down").click(function(){
            $("p").slideDown(5000);
        });
        $("#stop").click(function(){
            $("p").stop();
        });
    });
</script>
<style>
    p{
        display:none;
        background-color: #2e6da4;
    }
</style>

<button id="stop">停止滑动</button>
<button id="down">点击这里，向下滑动</button>
<p>Hello World!</p>
```

19、stop()动画效果，有参数
```
$(document).ready(function(){
    $("#top1").click(function(){
        $("#animate").animate({left:'100px'},5000);
        $("#animate").animate({fontSize:'3em'},5000);
    });
    $("#top2").click(function(){
       $("#animate").stop();
    });
    $("#top3").click(function(){
        $("#animate").stop(true);
    });
    $("#top4").click(function(){
        $("#animate").stop(true,true);
    });
});
.animate{
    height:100px;
    width:100px;
    background-color: #1b6d85;
    position: relative;
}

<button id="top1">开始</button>
<button id="top2">停止</button>
<button id="top3">停止所有</button>
<button id="top4">停止但要完成</button>
<div class="animate" id="animate">Hello</div>
```

20、callback函数
