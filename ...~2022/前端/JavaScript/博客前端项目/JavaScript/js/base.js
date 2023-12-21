//函数式
/*function $(id){
    return document.getElementById(id);
}*/

//对象式
/*
var Base = {
    getId:function(id){
        return document.getElementById(id);
    },
    getName:function(name){
        return document.getElementsByName(name);
    },
    getTagName:function(tagName){
        return document.getElementsByName(tagName);
    }
};*/

//连缀
var $ = function (_this) {      //创建不同的对象
    return new Base(_this);

};

function Base(_this) {
//创建一个数组，来保存获取的节点和节点数组
    this.elements = [];
    if (_this !== undefined) {     //_this是一个对象
        this.elements[0] = _this;
    }
}


//获取ID节点
Base.prototype.getId = function (id) {
    this.elements.push(document.getElementById(id));
    return this;             //返回Base对象
};
//获取元素节点
Base.prototype.getTagName = function (tagName) {
    let tags = document.getElementsByTagName(tagName);
    for (let i = 0; i < tags.length; i++) {
        this.elements.push(tags[i]);
    }
    return this;
};
//获取相同的节点(区域化)
Base.prototype.getClass = function (className, idName) {
    let node = null;
    if (arguments.length === 2) {
        node = document.getElementById(idName);
    } else {
        node = document;
    }
    let all = node.getElementsByTagName('*');        //获取所有的节点
    console.log(all.length);
    for (let i = 0; i < all.length; i++) {
        if (all[i].className === className) {        //判断是否为需要的节点
            this.elements.push(all[i]);
        }
    }
    return this;
};
//获取节点数组的某一个
Base.prototype.getElement = function (num) {
    let element = this.elements[num];
    this.elements = [];
    this.elements[0] = element;
    return this;
};
//设置css样式
Base.prototype.css = function (attr, value) {
    for (let i = 0; i < this.elements.length; i++) {
        if (arguments.length === 1) {         //获取内容，只传入一个参数为属性名
            if (typeof window.getComputedStyle !== 'undefined') {     //w3c标准，获取计算后的样式(包括外联样式表中的样式)
                return window.getComputedStyle(this.elements[i], null)[attr];
            }
            return this.elements[i].style[attr];   //直接返回这个属性值
        }
        this.elements[i].style[attr] = value;
    }
    return this;                 //返回Base对象
};
//设置link或style中的CSS规则
Base.prototype.addRule = function (num, selector, cssText, position) {
    let sheet = document.styleSheets[num];        //获取第num个样式
    if (typeof sheet.insertRule !== 'undefined') {
        sheet.insertRule(selector + "{" + cssText + "}", position);
    } else if (typeof sheet.addRule !== 'undefined') {     //低版本IE兼容
        sheet.addRule(selector, cssText, position);
    }
    return this;
};
//移除link或style中的CSS规则
Base.prototype.removeRule = function (num, index) {
    let sheet = document.styleSheets[num];
    if (typeof sheet.deleteRule !== 'undefined') {
        sheet.deleteRule(index);
    } else if (typeof sheet.removeRule !== 'undefined') {    //低版本IE兼容
        sheet.removeRule(index);
    }
    return this;
};
//添加class
Base.prototype.addClass = function (className) {
    for (let i = 0; i < this.elements.length; i++) {
        if (!this.elements[i].className.match(new RegExp('(^|\\s)' + className + '(\\s|$)'))) {    //判断className是否存在
            this.elements[i].className += ' ' + className;   //连续添加多个className，并且在两个之间留一个空格
        }
    }
    return this;
};
//移除class
Base.prototype.removeClass = function (className) {
    for (let i = 0; i < this.elements.length; i++) {
        if (this.elements[i].className.match(new RegExp('(^|\\s)' + className + '(\\s|$)'))) {    //判断className是否存在
            this.elements[i].className = this.elements[i].className.replace(new RegExp('(^|\\s)' + className + '(\\s|$)'), ' ');
        }
    }
    return this;
};
//设置内容
Base.prototype.html = function (str) {
    for (let i = 0; i < this.elements.length; i++) {
        if (arguments.length === 0) {     //获取内容，不传入参数
            return this.elements[i].innerHTML;
        }
        this.elements[i].innerHTML = str;
    }
    return this;
};
//触发点击事件
Base.prototype.click = function (fn) {
    for (let i = 0; i < this.elements.length; i++) {
        this.elements[i].onclick = fn;
    }
    return this;
};
//设置隐藏
Base.prototype.hide = function () {
    for (let i = 0; i < this.elements.length; i++) {
        this.elements[i].style.display = 'none';
    }
    return this;
};
//设置显示
Base.prototype.show = function () {
    for (let i = 0; i < this.elements.length; i++) {
        this.elements[i].style.display = 'block';
    }
    return this;
};
//设置鼠标移入移出
Base.prototype.hover = function (over, out) {
    for (let i = 0; i < this.elements.length; i++) {
        this.elements[i].onmouseover = over;
        this.elements[i].onmouseout = out;
    }
    return this;
};