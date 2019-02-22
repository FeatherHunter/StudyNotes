/*
功能说明：
* 1、点击向右（左）图标，平滑切换到下（上）一页；
* 2、无限循环切换：第一页的上一页为最后一页，最后一页的下一页为第一页；
* 3、每隔2s自动滑到下一页；
* 4、当鼠标进入图片区域时，自动停止切换；当鼠标离开时，又开始自动切换；
* 5、切换页面时，下面的圆点也同步更新；
* 6、点击圆点图标切换到对应的页；
* bug：快速点击上下页，翻页不正常：原因是正在翻页过程中或获得当前的left值，但不是我们需要的left值；
* 解决方法：正在翻页过程中，点击上下页无效；
* */

$(function () {

    let $container = $('#container');
    let $imgList = $('#img-list');
    let $points = $('#pointDiv>span');
    let $prev = $('#prev');
    let $next = $('#next');
    let index = 0;                          //当前图片的下标
    let moving = false;                     //标志是否正在翻页
    const PAGE_WIGTH = 500;                 //一张图片的宽度
    const TIME = 400;                       //翻页的持续时间
    const ITEM_TIME = 20;                   //单元移动的间隔时间
    const imgCount = 5;                     //图片的张数

    //1、点击向右（左）图标，平滑切换到下（上）一页；
    $next.click(function () {
        //平滑翻到下一页
        nextPage(true);
    });
    $prev.click(function () {
        //平滑翻到上一页
        nextPage(false);
    });

    //3、每隔2s自动滑到下一页；
    let timeIntervalId = setInterval(function () {               //2s循环定时器
        nextPage(true);
    }, 2000);

    //4、当鼠标进入图片区域时，自动停止切换；当鼠标离开时，又开始自动切换；
    $container.hover(function () {
        //鼠标移入图片区域，停止自动切换，清除定时器
        clearInterval(timeIntervalId);
    }, function () {
        //鼠标移出图片区域，切换自动切换，清除定时器
        timeIntervalId = setInterval(function () {               //2s循环定时器
            nextPage(true);
        }, 2000);
    });

    //6、点击圆点图标切换到对应的页；
    $points.click(function () {
        //目标页的下标
        let targetIndex = $(this).index();
        if (targetIndex !== index) {       ////点击的不是当前圆点，才翻页
            nextPage(targetIndex);
        }
    });

    /*
    * 方法：平滑翻页
    * 参数：true为下一页，false为上一页，数值为指定页
    * */
    function nextPage(next) {

        /*
        * 总的偏移量：offset
        * 总的翻页时间：TIME = 400
        * 单元移动的间隔时间：ITEM_TIME = 20
        * 单元移动的偏移量：itemOffset = offset/(TIME/ITEM_TIME)
        * 启动循环定时器不断更新$imgList的left值，到达目标处停止定时器
        * */

        //如果正在翻页，直接结束
        if(moving){
            return ;
        }

        moving = true;                                //标识正在翻页

        let offset = 0;
        //计算偏移量
        if (typeof next === 'boolean') {
            offset = next ? -PAGE_WIGTH : PAGE_WIGTH;     //偏移量：下一页为负值，上一页为正值
        } else {
            offset = -(next - index) * PAGE_WIGTH;        //偏移量：指定的目标页
        }

        let itemOffset = offset / (TIME / ITEM_TIME);
        let currLeft = $imgList.position().left;         //当前图片的left值
        let targetLeft = currLeft + offset;              //计算目标位置的left值

        let intervalId = setInterval(function () {      //启动循环定时器
            currLeft += itemOffset;                     //计算最新的图片的left值
            if (currLeft === targetLeft) {              //到达目标位置
                clearInterval(intervalId);              //清除定时器
                moving = false;                                    //标识翻页结束
                if (currLeft === -(imgCount + 1) * PAGE_WIGTH) {   //到达最右边的1.jpg，跳转到最左边的第二张图
                    currLeft = -PAGE_WIGTH;
                } else if (currLeft === 0) {                       //到达最左边的5.jpg，跳转到最右边的倒数第二张图
                    currLeft = -imgCount * PAGE_WIGTH;
                }

            }
            $imgList.css('left', currLeft);

        }, ITEM_TIME);

        //更新圆点
        updatePoints(next);

        // $imgList.css('left', currLeft + offset);          //瞬间翻页
    }

    //5、切换页面时，下面的圆点也同步更新；
    function updatePoints(next) {
        //计算出目标圆点的下标
        let targetIndex = 0;
        if (typeof next === 'boolean') {
            if (next) {
                targetIndex = index + 1;                   //目标圆点为当前页的下一页
                if (index === imgCount - 1) {              //当前页为第五张图片，则目标圆点为第一张图片（index=4，targetIndex=0）
                    targetIndex = 0;
                }
            } else {
                targetIndex = index - 1;        //目标圆点为当前页的上一页
                if (index === 0) {              //当前页为第一张图片，则目标圆点为第五张图片（index=0，targetIndex=4）
                    targetIndex = imgCount - 1;
                }
            }
        } else {
            targetIndex = next;
        }

        //给当前index的span标签移除class='on'；
        $points.eq(index).removeClass('on');
        //给目标圆点添加class='on'；
        $points.eq(targetIndex).addClass('on');
        //将index更新为目标圆点
        index = targetIndex;
    }
});
