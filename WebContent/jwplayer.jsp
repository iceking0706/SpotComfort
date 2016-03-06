<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>JWPlay测试RTMP</title>
<script type="text/javascript" src="easyui/jquery.min.js"></script>
<script type="text/javascript" src="easyui/jwplayer-7.3.4/jwplayer.js"></script>
<script type="text/javascript">
	jwplayer.key = 'TbslahWj7PoVFfZ1UGu4RZOceTNqe8dvTNbRFA==';
</script>
</head>
<body>
	<div id="container"></div>
	<input type="button" class="player-play" value="播放" />
	<input type="button" class="player-stop" value="停止" />

	<script type="text/javascript">
		//播放器1的控制句柄
		var thePlayer = null;
		var init = function() {
			//初始化控件
			thePlayer = jwplayer('container').setup({
				flashplayer : 'easyui/jwplayer-7.3.4/jwplayer.flash.swf',
				file : 'rtmp://127.0.0.1:1935/live/aa-bb-cc',
				image:'images/11.jpg',
				/*rtmp:{
					bufferlength:0.5
				},*/
				width : 960,
				height : 540,
				dock : false
			});

			//播放 暂停  
			$('.player-play').click(function() {
				if (thePlayer.getState() != 'PLAYING') {
					thePlayer.play(true);
					this.value = '暂停';
				} else {
					thePlayer.play(false);
					this.value = '播放';
				}
			});

			//停止  
			$('.player-stop').click(function() {
				thePlayer.stop();
			});
		};
		$(init);
	</script>
</body>
</html>