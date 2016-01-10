//自定义一些类
//测试类1
(function($){
	$.MyTestCls1 = function(options){
		//私有变量定义
		var name = '你好, MyTestCls1';
		
		if(options!=null && options.title!=null)
			name = options.title;
		
		//私有方法定义
		var fnT1 = function(p1){
			alert('调用内部私有方法 fnT1：'+p1);
		};
		
		//加上this，表示共有方法
		this.testFn1 = function(){
			fnT1(options.title);
			alert('调用外部方法 testFn1：'+name);
		};
	};
})(jQuery);