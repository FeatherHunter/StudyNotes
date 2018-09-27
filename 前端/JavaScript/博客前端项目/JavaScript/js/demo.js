window.onload = function () {
    // console.log(Base.getId('box').innerHTML);

    /*    var base = new Base();             //共用同一个对象，会导致后面的样式覆盖前面的

        base.getId('box').css('color', 'red').css('background', 'green').html('box').click(function () {
            alert('a')
        });
        base.getTagName('p').css('color', 'red').css('background', 'pink');*/

    /*    $().getId('box').css('color', 'red').css('background', 'green').html('标题').click(function () {
            alert('a')
        });
        $().getTagName('p').css('color', 'red').css('background', 'pink');*/

    /*    console.log($().getId('box').css('fontSize'));
        console.log($().getTagName('p').html());

        console.log($().getClass('p1').getElement(0).html());

        console.log($().getClass('p1','aaa').css('color','blue'));*/


    /*    $().getId('box').addClass('a').addClass('b').addClass('aaa');
        $().getId('box').removeClass('b');
        $().addRule(0,'#bbb','color:orange',2);
        $().removeRule(0);*/

    $().getClass('info').hover(function () {
        console.log('鼠标移入');
        $(this).css('background', 'green url("../img/icon.png") no-repeat 57px -383px');
        $().getTagName('ul').show();
    }, function () {
        console.log('鼠标移出');
        $(this).css('background', 'black url("../img/icon.png") no-repeat 57px -343p');
        $().getTagName('ul').hide();
    });

};
