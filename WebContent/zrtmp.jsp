<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css"
	href="easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="easyui/themes/icon.css">
<link rel="stylesheet" type="text/css"
	href="easyui/video-js-4.12.7/video-js.min.css">
<script type="text/javascript" src="easyui/jquery.min.js"></script>
<script type="text/javascript" src="easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyui/video-js-4.12.7/video.js"></script>
<script type="text/javascript">
_V_.options.flash.swf = "easyui/video-js-4.12.7/video-js.swf";
</script>
<title>测试red5直播流</title>
<style type="text/css">
body {
	font-family: "微软雅黑";
	font-size: 12px;
}
</style>
</head>
<body>
	<video id="example_video_1" class="video-js vjs-default-skin" controls preload="none" width="480" height="320"
      poster="easyui/video-js-4.12.7/bgsd1.jpg"
      data-setup="{}">
	<source src="rtmp://127.0.0.1:1935/suyou/aa-bb-cc" type='rtmp/mp4' />
  </video>
  
  <script type="text/javascript">
	  videojs("example_video_1").ready(function(){
	      var myPlayer = this;
	      myPlayer.play();
	  });
  </script>
</body>
</html>