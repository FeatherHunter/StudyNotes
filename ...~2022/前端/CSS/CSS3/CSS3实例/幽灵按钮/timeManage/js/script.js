$(function () {
    $('.link .button').hover(function () {
            //鼠标滑入button时
            let tipWidth;  //tip的宽
            let aWidth;    //a标签的宽
            let tipDis;    //a标签的中点和tip的中点之间的距离
            let offset;    //偏移量
            let left;      //位于按钮中间的left值

            let $title = $(this).attr('data');
            $('.tip i').text($title);
            let pos = $(this).position().left + 12.5;         //获取距离最近的（加了定位）父元素的距离，(12.5是a标签的margin-left值，position方法没有算进去)
            console.log(pos);
            tipWidth = $('.tip').outerWidth();                //tip的宽度，包括padding
            aWidth = $(this).outerWidth();                    //按钮的宽度
            if (tipWidth >= aWidth) {
                tipDis = parseInt((tipWidth - aWidth) / 2);             //a标签的中点和tip的中点之间的距离                 //取整数
                left = pos - tipDis;
            } else if (tipWidth < aWidth) {
                tipDis = parseInt((aWidth - tipWidth) / 2);
                left = pos + tipDis;
            }

            $('.tip').css('left', left + 'px').animate({'opacity': 1, 'top': 122}, 300);
        },
        function () {
            //鼠标滑出button时，将部分样式清除
            //判断tip上是否存在动画，如果存在动画，就不加动画了，解决按钮之间频繁移动出现的bug
            if (!$('.tip').is(':animated')) {
                $('.tip').animate({'top': 90, 'opacity': 0}, 300);
            }
        })
});