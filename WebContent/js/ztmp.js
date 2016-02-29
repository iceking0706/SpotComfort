var fnLogin = function(){
	$.post('login',function(data){
		alert(data.succ+' , '+data.stmt);
	});
};

var fnMyCls1 = function(){
	var mycls1 = new $.MyTestCls1({title:'标题111'});
	mycls1.testFn1();
};

var fnMyCls2 = function(){
	var mycls2 = new $.MyTestCls1({title:'标题222'});
	mycls2.testFn1();
};

//测试HtmlTemplet的调用
var fnTestTemplet1 = function(){
	//var html = '<div id="div_tplt_1" class="abcd"><span>{name}</span>&nbsp;<span>{title}</span></div>';
	var html = $('#divHiddenTemplet').html();
	var obj = {name:'张三',title:'测试标题'};
	//alert(typeof(eval('obj.nr.c')));
	//alert(obj.hasOwnProperty('name'));
	var ttemplet = new $.HtmlTemplet(html,obj);
	alert(ttemplet.parse());
};

var fnTestTemplet2 = function(){
	//var html = '<div id="div_tplt_1" class="abcd"><span>{name}</span>&nbsp;<span>{title}</span></div>';
	var html = $('#divHiddenTemplet').html();
	var obj = {name:'李四',title:'回家吃饭',nr:{c:1,b:3.2}};
	//alert(obj.hasOwnProperty('nr.a'));
	//alert(typeof(eval('obj.nr.c')));
	var ttemplet = new $.HtmlTemplet(html,obj);
	alert(ttemplet.parse());
};

//测试微信接口
var fnTestWechatJson = function(){
	var city = $('#text33').val();
	if(city == '')
		return;
	$.post('wxcity',{
		keyword:city
	},function(data){
		var str = JSON.stringify(data);
		alert(str);
		$('#text22').val(str);
	});
};

$(function(){
	
});