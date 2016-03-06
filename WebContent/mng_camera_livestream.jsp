<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String sn = request.getParameter("sn");
	String rtmpurl = request.getParameter("rtmpurl");
	String picurl = request.getParameter("picurl");
%>
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
	<input type="button" class="player-stop" value="停止并关闭" />

	<script type="text/javascript">
		//从jsp中获取到的地址
		var sn = '<%= sn %>';
		var rtmpurl = '<%= rtmpurl %>';
		var picurl = '<%= picurl %>'; 
		//播放器1的控制句柄
		var thePlayer = null;
		var init = function() {
			//初始化控件
			thePlayer = jwplayer('container').setup({
				flashplayer : 'easyui/jwplayer-7.3.4/jwplayer.flash.swf',
				file : rtmpurl,
				image: picurl,
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
				//发送请求到后台去停止转发
				$.post('cmr/livestream',{
					sn:sn,
					oper:'stop'
				},function(data){
					if(data.succ){
						alert('直播停止');
						window.close();
					}else
						$.messager.alert('Information','失败：'+data.stmt);
				});
			});
		};
		$(init);
	</script>
</body>
</html>