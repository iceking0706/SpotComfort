//通过json对象，将内容添加到html的模版里面去
//构造函数参数定义：
//templet: html的模版，其中的数据使用{data1}表示
//args: 数据对象，一般从json获得
(function($){
	$.HtmlTemplet = function(templet,args){
		//私有变量定义
		//最终数据填写之后的html
		var html = null;
		//数据对象
		var opts = null;
		//模版中数据的tag，默认是{}
		var tagL = '{';
		var tagR = '}';
		//记录中间步骤，tagL所在的index
		var indexTagL = -1;
		var indexTagR = -1;
		var blankTag = '';
		
		//构造的参数设置为私有变量
		if(templet != null)
			html = templet;
		if(args != null)
			opts = args;
		
		//私有方法定义
		//判断当前的html中，是否有需要替换的tag内容
		var hasTag = function(){
			indexTagL= html.indexOf(tagL);
			indexTagR = html.indexOf(tagR);
			if(indexTagL>=0 && indexTagR>=0)
				return true;
			return false;
		};
		
		//判断属性是否存在的
		var hasProperty = function(propStr){
			try{
				if(typeof(eval('opts.'+propStr)) == 'undefined')
					return false;
				return true;
			}catch(e){
				return false;
			}
		};
		
		//找到tag之后，去替换
		var fillTag = function(){
			//tagL左边的
			var strL = html.substring(0,indexTagL);
			//tagR右边的
			var strR = html.substring(indexTagR+1);
			//tag的名字
			var strM = html.substring(indexTagL+1,indexTagR);
			//新的html
			if(hasProperty(strM))
				html = strL+eval('opts.'+strM)+strR;
			else
				html = strL+blankTag+strR;
		};
		
		//加上this，表示共有方法
		//将数据填写到html中，返回html
		this.parse = function(){
			if(html==null)
				return null;
			if(opts == null)
				return html;
			//不停的去判断是否有需要替换的tag，并解析
			while(hasTag()){
				fillTag();
			}
			return html;
		};
		
		//直接返回最终的html，一般不用，使用parse即可
		this.getHtml = function(){
			return html;
		};
	};
})(jQuery);