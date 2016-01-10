<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String rtmp = request.getParameter("rtmp");
	if(rtmp==null || !rtmp.startsWith("rtmp")){
		out.println("请在网址中指定RTMP的播放地址，参数名：rtmp");
	}else{
%>	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="easyui/video-js-4.12.7/video-js.min.css">
<script type="text/javascript" src="easyui/video-js-4.12.7/video.js"></script>
<script type="text/javascript">
_V_.options.flash.swf = "easyui/video-js-4.12.7/video-js.swf";
var myPlayer = null;
var fnPlay = function(){
	if(myPlayer == null){
		myPlayer = _V_("example_video_1");
	}
	myPlayer.play();
};
var fnPause = function(){
	if(myPlayer == null){
		myPlayer = _V_("example_video_1");
	}
	myPlayer.pause();
};
var fnFullScreen = function(){
	if(myPlayer == null){
		myPlayer = _V_("example_video_1");
	}
	myPlayer.requestFullscreen();
};
</script>
<title>Test Red5 RTMP</title>
<style type="text/css">
body {
	font-family: "微软雅黑";
	font-size: 12px;
}
</style>
</head>
<body>
<p>测试RTMP直播流：
<input type="button" value="播放" onclick="fnPlay();">
<input type="button" value="暂停" onclick="fnPause();">
<input type="button" value="全屏" onclick="fnFullScreen();">
</p>
<video id="example_video_1" class="video-js vjs-default-skin" controls preload="none" width="480" height="320"
      poster="easyui/video-js-4.12.7/bgsd1.jpg"
      data-setup="{}">
<source src="<%= rtmp %>" type='rtmp/mp4' />
  </video>
</body>
</html>
<%
	}
%>