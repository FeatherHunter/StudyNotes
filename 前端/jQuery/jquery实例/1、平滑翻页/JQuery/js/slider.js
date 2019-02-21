/*
功能说明：
* 1、点击向右（左）图标，平滑切换到下（上）一页；
* 2、无限循环切换：第一页的上一页为最后一页，最后一页的下一页为第一页；
* 3、每隔3s自动滑到下一页；
* 4、当鼠标进入图片区域时，自动停止切换；当鼠标离开时，又开始自动切换；
* 5、切换页面时，下面的圆点也同步更新；
* 6、点击圆点图标切换到对应的页；
* */

$(function () {

    let $container = $('#container');
    let $imgList = $('#img-list');
    let $points = $('#pointDiv>span');
    let $prev = $('#prev');
    let $next = $('#next');
    const PAGE_WIGTH = 500;                 //一张图片的宽度
    const TIME = 400;                       //翻页的持续时间
    const ITEM_TIME = 20;                   //单元移动的间隔时间

    //1、点击向右（左）图标，平滑切换到下（上）一页；
    $next.click(function () {
        //平滑翻到下一页
        nextPage(true);
    });
    $prev.click(function () {
        //平滑翻到上一页
        nextPage(false);
    });

    /*
    * 方法：平滑翻页
    * 参数：true为下一页，false为上一页
    * */
    function nextPage(next) {

        /*
        * 总的偏移量：offset
        * 总的翻页时间：TIME = 400
        * 单元移动的间隔时间：ITEM_TIME = 20
        * 单元移动的偏移量：itemOffset = offset/(TIME/ITEM_TIME)
        * 启动循环定时器不断更新$imgList的left值，到达目标处停止定时器
        * */
        let offset = 0;
        offset = next ? -PAGE_WIGTH : PAGE_WIGTH;     //偏移量：下一页为负值，上一页为正值
        let itemOffset = offset / (TIME / ITEM_TIME);
        let currLeft = $imgList.position().left;         //当前图片的left值
        let targetLeft = currLeft + offset;              //计算目标位置的left值

        let intervalId = setInterval(function () {      //启动循环定时器
            currLeft += itemOffset;                     //计算最新的图片的left值
            if(currLeft === targetLeft){                //到达目标位置
                clearInterval(intervalId);              //清除定时器
            }
            $imgList.css('left', currLeft);
        }, ITEM_TIME);


        // $imgList.css('left', currLeft + offset);          //瞬间翻页

    }
});
