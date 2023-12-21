1、jquery-validation
> 1.主要用于做表单验证；
> 2.验证规则：
```
messages: {
	required: "This field is required.",
	remote: "Please fix this field.",
	email: "Please enter a valid email address.",
	url: "Please enter a valid URL.",
	date: "Please enter a valid date.",
	dateISO: "Please enter a valid date ( ISO ).",
	number: "Please enter a valid number.",
	digits: "Please enter only digits.",
	creditcard: "Please enter a valid credit card number.",
	equalTo: "Please enter the same value again.",
	maxlength: $.validator.format( "Please enter no more than {0} characters." ),
	minlength: $.validator.format( "Please enter at least {0} characters." ),
	rangelength: $.validator.format( "Please enter a value between {0} and {1} characters long." ),
	range: $.validator.format( "Please enter a value between {0} and {1}." ),
	max: $.validator.format( "Please enter a value less than or equal to {0}." ),
	min: $.validator.format( "Please enter a value greater than or equal to {0}." )
}
```
```
$.extend($.validator.messages, {
	required: "这是必填字段",
	remote: "请修正此字段",
	email: "请输入有效的电子邮件地址",
	url: "请输入有效的网址",
	date: "请输入有效的日期",
	dateISO: "请输入有效的日期 (YYYY-MM-DD)",
	number: "请输入有效的数字",
	digits: "只能输入数字",
	creditcard: "请输入有效的信用卡号码",
	equalTo: "你的输入不相同",
	extension: "请输入有效的后缀",
	maxlength: $.validator.format("最多可以输入 {0} 个字符"),
	minlength: $.validator.format("最少要输入 {0} 个字符"),
	rangelength: $.validator.format("请输入长度在 {0} 到 {1} 之间的字符串"),
	range: $.validator.format("请输入范围在 {0} 到 {1} 之间的数值"),
	max: $.validator.format("请输入不大于 {0} 的数值"),
	min: $.validator.format("请输入不小于 {0} 的数值")
});
```
> 3.例子
```
<html>
<head>
<meta charset="utf-8">
<title>validation</title>
<script src="js/jquery.js"></script>
<script src="js/jquery.validate.min.js"></script>
<script src="js/messages_zh.js"></script>
<script>
$.validator.setDefaults({
    submitHandler: function() {
      alert("提交事件!");
    }
});
$().ready(function() {
// 在键盘按下并释放及提交后验证提交表单
  $("#signupForm").validate({
	    rules: {
	      firstname: "required",
	      lastname: "required",
	      username: {
	        required: true,
	        minlength: 2
	      },
	      password: {
	        required: true,
	        minlength: 5
	      },
	      confirm_password: {
	        required: true,
	        minlength: 5,
	        equalTo: "#password"
	      },
	      email: {
	        required: true,
	        email: true
	      },
	      "topic[]": {
	        required: "#newsletter:checked",
	        minlength: 2
	      },
	      agree: "required"
	    },
	    messages: {
	      firstname: "请输入您的名字",
	      lastname: "请输入您的姓氏",
	      username: {
	        required: "请输入用户名",
	        minlength: "用户名必需由两个字母组成"
	      },
	      password: {
	        required: "请输入密码",
	        minlength: "密码长度不能小于 5 个字母"
	      },
	      confirm_password: {
	        required: "请输入密码",
	        minlength: "密码长度不能小于 5 个字母",
	        equalTo: "两次密码输入不一致"
	      },
	      email: "请输入一个正确的邮箱",
	      agree: "请接受我们的声明",
	      topic: "请选择两个主题"
	    }
	});
});
</script>
<style>
.error{
	color:red;
}
</style>
</head>
<body>
<form class="cmxform" id="signupForm" method="get" action="">
  <fieldset>
    <legend>验证完整的表单</legend>
    <p>
      <label for="firstname">名字</label>
      <input id="firstname" name="firstname" type="text">
    </p>
    <p>
      <label for="lastname">姓氏</label>
      <input id="lastname" name="lastname" type="text">
    </p>
    <p>
      <label for="username">用户名</label>
      <input id="username" name="username" type="text">
    </p>
    <p>
      <label for="password">密码</label>
      <input id="password" name="password" type="password">
    </p>
    <p>
      <label for="confirm_password">验证密码</label>
      <input id="confirm_password" name="confirm_password" type="password">
    </p>
    <p>
      <label for="email">Email</label>
      <input id="email" name="email" type="email">
    </p>
    <p>
      <label for="agree">请同意我们的声明</label>
      <input type="checkbox" class="checkbox" id="agree" name="agree">
    </p>
    <p>
      <label for="newsletter">我乐意接收新信息</label>
      <input type="checkbox" class="checkbox" id="newsletter" name="newsletter">
    </p>
    <fieldset id="newsletter_topics">
      <legend>主题 (至少选择两个) - 注意：如果没有勾选“我乐意接收新信息”以下选项会隐藏，但我们这里作为演示让它可见</legend>
      <label for="topic_marketflash">
        <input type="checkbox" id="topic_marketflash" value="marketflash" name="topic[]">Marketflash
      </label>
      <label for="topic_fuzz">
        <input type="checkbox" id="topic_fuzz" value="fuzz" name="topic[]">Latest fuzz
      </label>
      <label for="topic_digester">
        <input type="checkbox" id="topic_digester" value="digester" name="topic[]">Mailing list digester
      </label>
      <label for="topic" class="error" style="display:none">至少选择两个</label>
    </fieldset>
    <p>
      <input class="submit" type="submit" value="提交">
    </p>
  </fieldset>
</form>
</body>
</html>
```
