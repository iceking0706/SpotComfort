<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="easyui/themes/icon.css">
<script type="text/javascript" src="easyui/jquery.min.js"></script>
<script type="text/javascript" src="easyui/jquery.easyui.min.js"></script>
<title>Engin-Lock-Cloud-Platform</title>
<style type="text/css">
body {
	font-family: "微软雅黑";
	font-size: 12px;
}
</style>
<script type="text/javascript" >
function rotateMe() {
    i = 0;
    int = setInterval(
      function () {
        video.style.WebkitTransform = 'rotate(' + (i++ %360) + 'deg)';
        video.style.MozTransform = 'rotate(' + (i++ %360) + 'deg)';
        video.style.OTransform = 'rotate(' + (i++ %360) + 'deg)';
      }, 50);
  }

  function resetRotation() {
    video.style.WebkitTransform = 'rotate(0)';
    video.style.MozTransform = 'rotate(0)';
    video.style.OTransform = 'rotate(0)';
  }

$(function(){
	
});
</script>
</head>
<body class="easyui-layout">
<div data-options="region:'north',border:false" style="height:50px;padding-top:5px;">
	bbbb
</div>
<div data-options="region:'center'">
	  <div style="text-align: center;">
    <video style="transform: rotate(0deg);" id="v1" controls="controls" onmouseover="" height="240" width="320"> 
      <source src="rtsp://192.168.0.106:5050/lkjx">
      html5 video
    </video>
  </div>

  <script>
    var video = document.getElementsByTagName('video')[0];
  </script>

  <div style="text-align: center;">
    <button onclick="video.play()"> &#9654; </button>
    <button onclick="video.pause()"> &#9724; </button>
    <button onclick="video.volume += 0.25">Volume Up</button>
    <button onclick="video.volume -= 0.25">Volume Down</button>
    <button onclick="video.muted = true">Mute</button>
    <button onclick="video.muted = false">Unmute</button>
    <button onclick="rotateMe()"> Start Rotation</button>
    <button onclick="int=window.clearInterval(int);resetRotation()"> Stop Rotation</button>
  </div>
  
  
  <!--
  <div class="video">
        <object class="video-plugin" classid="clsid:9BE31822-FDAD-461B-AD51-BE1D1C159921" 
        		codebase="http://downloads.videolan.org/pub/videolan/vlc/latest/win32/axvlc.cab" 
        		width="800" height="400"
        		events="True">
            <param name="MRL" value="rtsp://192.168.9.101/h264ESVideoTest">  
            <param name="ShowDisplay" value="True">
            <param name="AutoLoop" value="True">
            <param name="AutoPlay" value="True">
        </object>
    </div>
  -->
</div>
</body>
</html>