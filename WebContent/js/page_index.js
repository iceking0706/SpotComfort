//获得ip地址的归属地
var fnFetchIPLocation = function(){
	//返回结果，var remote_ip_info = {"ret":1,"start":-1,"end":-1,"country":"\u4e2d\u56fd","province":"\u5317\u4eac","city":"\u5317\u4eac","district":"","isp":"","type":"","desc":""};
	$.getScript('http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js', function(response,status){  
		if(status=='success'){
			$('#iploc').text(remote_ip_info.province+'->'+remote_ip_info.city);
		}
	}); 
};

//给searchbox的查询
var fnSearchBox = function(value,name){
	alert(value+' , '+name);
};

$(function(){
	fnFetchIPLocation();
});