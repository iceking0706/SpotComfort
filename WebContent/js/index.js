
//获得ip地址的归属地
var fnFetchIPLocation = function(){
	//返回结果，var remote_ip_info = {"ret":1,"start":-1,"end":-1,"country":"\u4e2d\u56fd","province":"\u5317\u4eac","city":"\u5317\u4eac","district":"","isp":"","type":"","desc":""};
	$.getScript('http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js', function(response,status){  
		if(status=='success'){
			$('#iploc').text(remote_ip_info.province+'->'+remote_ip_info.city);
			fnSetCurCity(remote_ip_info.city);
		}else{
			fnSetCurCity(dftCityName);
		}
	}); 
};

//当切换城市时候的一系列操作
var fnSetCurCity = function(cityname){
	$("#area").text(cityname);
	$('#tq_cityname').text(cityname+': ');
	//去获取城市天气
	$.post('weatherByCityName',{
		cityName:cityname
	},function(data){
		if(data.success){
			$('#tq_wthpic').attr('src',data.wthPic);
			$('#tq_wendu').text(data.weather+'  '+data.temp+'℃');
		}else{
			$('#tq_wthpic').attr('src','icons/icon128/overcast.png');
			$('#tq_wendu').text('--℃');
		}
	});
};

//点击查询按钮时候
//页面，从1开始
var schPage = 1;
//总页数，最后计算得到的
var schTotalpage = 1;
//判断是否进行推荐查询，而不是按照城市和景点查询
var schIsRcmd = true;
//根据舒适度指数，算出几颗星
var fnGnrCDIndexStarHtml = function(spotCD){
	
	var html1 = '';
	var count = 0;
	if(spotCD == 100)
		count = 5;
	else if(spotCD == 0)
		count = 1;
	else{
		count = parseInt(spotCD / 20);
		if(spotCD>=90 && count==4)
			count++;
	}
	//明显的星星
	for(var i=0;i<count;i++){
		html1 += '<img src="images/star1.png" alt=""/>';
	}
	//不明显的星星
	for(var i=0;i<(5-count);i++){
		html1 += '<img src="images/star2.png" alt=""/>';
	}
	return html1;
};
//根据查询返回的结果组装html对象
var fnGnrHtmlBySchResult = function(data){
	var html = '';
	if(data.rows==null || data.rows.length==0)
		return '查询无结果';
	
	html += '<ul>';
	for(var i=0;i<data.rows.length;i++){
		var row = data.rows[i];
		html += '<li>';
		html += '<div class="box">';
		html += '<a class="img" href="####"><img src="'+row.spotPic+'" alt="" width="216" height="170"/></a>';
		html += '<div class="row1">';
		html += '<h4>舒适度：<b>'+row.cdIndex+'分</b></h4>';
		html += '<p class="clearfix stars">'+fnGnrCDIndexStarHtml(row.cdIndex)+'</p>';
		html += '</div>';
		html += '<div class="row2">';
		html += '<h4>客流：<b>'+row.psgr+'</b></h4>';
		//html += '<p>'+row.psgrFlow+'人/小时</p>';
		html += '</div>';
		html += '<div class="row3">';
		html += '<h4>景色：<b>'+row.view+'</b></h4>';
		html += '<p>'+row.viewL+' 级</p>';
		html += '</div>';
		html += '<div class="name">'+row.name+'</div>';
		html += '</div>';
		html += '</li>';
	}
	
	html += '</ul>';
	
	return html;
};

var fnSearchSpot = function(){
	var keyword_spotname = $('#keyword_spotname').val();
	if(keyword_spotname == '请输入景区名字')
		keyword_spotname = '';
	/*if(!schIsRcmd && keyword_spotname == '请输入景区名字'){
		$('#keyword_spotname').focus();
		return;
	}*/
	var keyword_cityname = $("#area").text();
	
	var param = new Object();
	param.page = schPage;
	param.rows = $('#sch_size').val();
	if(schIsRcmd){
		param.justRcmd = true;
	}else{
		param.cityName = keyword_cityname;
		param.spotName = keyword_spotname;
	}
	
	//发送请求
	$.post('spotComfortByCityAndName',param,function(data){
		if(!data.succ)
			return;
		if(data.total == 0)
			return;
		var str = JSON.stringify(data);
		//alert(str);
		//计算总页面
		schTotalpage = parseInt(data.total / data.size);
		if(data.total % data.size != 0)
			schTotalpage++;
		$('#sch_pageinfo').text('[ '+schPage+' / '+schTotalpage+' ]');
		
		//组装查询结果并设置到div中  fnGnrHtmlBySchResult
		$('#div_spotview').html(fnGnrHtmlBySchResult(data));
		
		//页面滚动到指定位置，div_spotmain
		if(!schIsRcmd){
			var topspotmain = $('#div_spotmain').offset().top;
			$("html,body").animate({scrollTop: topspotmain}, 500);
		}
	});
};

//四个翻页操作
var fnToFirstPage = function(){
	if(schPage == 1)
		return;
	schPage = 1;
	fnSearchSpot();
};
var fnToPrePage = function(){
	if(schPage == 1)
		return;
	schPage--;
	fnSearchSpot();
};
var fnToNextPage = function(){
	if(schPage == schTotalpage)
		return;
	schPage++;
	fnSearchSpot();
};
var fnToLastPage = function(){
	if(schPage == schTotalpage)
		return;
	schPage = schTotalpage;
	fnSearchSpot();
};

$(function(){
	
	$(".banner").slide({
		titCell:".hd ul",
		mainCell:".bd ul",
		effect:"leftLoop",
		autoPage:true,
		autoPlay:true
	});


	$(".area").hover(function(){
		$(this).find(".areaAll").show();
	},function(){
		$(this).find(".areaAll").hide();
	}).find(".areaAll a").click(function(){

		$("#area").text($(this).text()).attr({
			"data-id":$(this).attr("data-id")
		})
		
		fnSetCurCity($(this).text());

		return false;
	});
	
	//新添加的
	$('#keyword_spotname').keypress(function(e){
		if(e.which == 13){
			schIsRcmd=false;
			schPage=1;
			fnSearchSpot();
		}
	});
	
	$('#tq_wthpic').hide();
	fnFetchIPLocation();
	fnSearchSpot('justRcmd');
});