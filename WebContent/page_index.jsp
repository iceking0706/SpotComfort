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
<script type="text/javascript" src="js/page_index.js"></script>
<title>景区舒适度指数</title>
<style type="text/css">
body {
	font-family: "微软雅黑";
	font-size: 12px;
}
.p-title {
	font-size: 42px;
}
.mark1 {
	font-family: 黑体;
	font-size: 24px;
	color: #F00;
}
.mark2 {
	font-weight: bold;
}
.mark3{
	font-size:30px;
	color:#f00;
}
.wz1 {
	font-style: oblique;
	font-weight: normal;
	color: #03f;
	font-size: 14px;
}
.justBorderLeft{
	border-right-style: none;
	border-bottom-style: none;
	border-top-style: none;
}
.justBorderRight{
	border-left-style: none;
	border-bottom-style: none;
	border-top-style: none;
}
.justBorderTop{
	border-right-style: none;
	border-bottom-style: none;
	border-left-style: none;
}
.justBorderBottom{
	border-right-style: none;
	border-left-style: none;
	border-top-style: none;
}
.icon16-array-undo{
	background:url('icons/icon16/arrow_undo.png') no-repeat center center;
}
.icon16-array-left{
	background:url('icons/icon16/arrow_left.png') no-repeat center center;
}
.icon16-array-right{
	background:url('icons/icon16/arrow_right.png') no-repeat center center;
}
.icon16-array-redo{
	background:url('icons/icon16/arrow_redo.png') no-repeat center center;
}
</style>
</head>
<body class="easyui-layout">
<div data-options="region:'north',border:false" style="height:80px;padding-top:5px;">
<table width="95%" border="0" cellspacing="0" align="center">
	<tr>
		<td style="width:70px;"><img src="icons/icon128/png-1359.png" width="64" height="64"></td>
		<td style="width:450px;" class="mark1">“出门旅游，先看看舒适度！”</td>
		<td valign="bottom" align="right" class="mark2">
		<span class="mark3">（logo、标语等）</span>
		[IP地址归属：<span id="iploc">unknow</span>]<br>[选城市]
		</td>
	</tr>
</table>
</div>



<div data-options="region:'center'" class="easyui-layout">
<div data-options="region:'center',border:false" style="padding:2px;padding-top:10px;">
<!-- 每行显示两个景点信息 -->
<table width="98%" border="0" align="center" cellspacing="0">
<tr>
	<td colspan="3" style="height:100px;"><img src="images/guagg1.png" />
	<span class="mark3">---广告位子</span></td>
</tr>
<tr>
	<td colspan="3" style="height:60px;">
	<img src="images/sous1.png" />
	<span class="mark3">---城市搜索，可以弹出城市搜藏列表</span>
	</td>
</tr>
<tr>
	<td colspan="3" style="height:60px;">
	<span class="mark3">下方核心部分，每行3个景点信息，图片要多张可以切换的</span>
	</td>
</tr>
<%
	for(int i=0;i<5;i++){
%>
<tr>
	<td width="33%" style="padding-right:5px;">
		<div class="easyui-panel" title="杭州：断桥" style="height:220px;margin-bottom:10px;">
			<table width="100%" height="100%" border="0" cellspacing="0">
			<tr valign="top">
				<td style="background-image: url(http://img.redocn.com/photo/20120210/Redocn_2012020905181885.jpg);background-position: center;">&nbsp;</td>
				<td style="width:160px;padding-left:4px;background-image: url('');background-position: center bottom;background-repeat: no-repeat;">
					<p><span class="mark2">舒适度：<br/></span><img src="icons/icon16/smiley.png" width="16" height="16">
					<img src="icons/icon16/smiley.png" width="16" height="16">
					<img src="icons/icon16/smiley.png" width="16" height="16">
					<img src="icons/icon16/smiley.png" width="16" height="16">
					<img src="icons/icon16/smiley.png" width="16" height="16">
					</p>
					<p><span class="mark2">客流：</span><span class="wz1">较拥挤</span></p>
					<p><span class="mark2">景色：</span><span class="wz1">超美</span></p>
					<p><span class="mark2">天气：</span><span class="wz1">晴，22℃</span></p>
				</td>
			</tr>
			</table>
		</div>
	</td>
	<td width="33%" style="padding-right:5px;">
		<div class="easyui-panel" title="杭州：三潭映月" style="height:220px;margin-bottom:10px;"">
			<table width="100%" height="100%" border="0" cellspacing="0">
			<tr valign="top">
				<td style="background-image: url(http://file21.mafengwo.net/M00/AB/1F/wKgB2lGys72ADVp7AAGzUMcgy4031.rbook_comment.w1920.jpeg);background-position: center;">&nbsp;</td>
				<td style="width:160px;padding-left:4px;background-image: url('');background-position: center bottom;background-repeat: no-repeat;">
					<p><span class="mark2">舒适度：<br/></span><img src="icons/icon16/smiley.png" width="16" height="16">
					<img src="icons/icon16/smiley.png" width="16" height="16">
					<img src="icons/icon16/smiley.png" width="16" height="16">
					<img src="icons/icon16/smiley.png" width="16" height="16">
					</p>
					<p><span class="mark2">客流：</span><span class="wz1">较拥挤</span></p>
					<p><span class="mark2">景色：</span><span class="wz1">超美</span></p>
					<p><span class="mark2">天气：</span><span class="wz1">多云，18℃</span></p>
				</td>
			</tr>
			</table>
		</div>
	</td>
	<td style="padding-left:5px;">
		<div class="easyui-panel" title="杭州：柳浪闻莺" style="height:220px;margin-bottom:10px;"">
			<table width="100%" height="100%" border="0" cellspacing="0">
			<tr valign="top">
				<td style="background-image: url(http://dimg02.c-ctrip.com/images/tg/739/232/583/30448f8574014fa6bb2e534e8f1f4809_C_350_230.jpg);background-position: center;">&nbsp;</td>
				<td style="width:160px;padding-left:4px;background-image: url('');background-position: center bottom;background-repeat: no-repeat;">
					<p><span class="mark2">舒适度：<br/></span><img src="icons/icon16/smiley.png" width="16" height="16">
					<img src="icons/icon16/smiley.png" width="16" height="16">
					<img src="icons/icon16/smiley.png" width="16" height="16">
					<img src="icons/icon16/smiley.png" width="16" height="16">
					</p>
					<p><span class="mark2">客流：</span><span class="wz1">较拥挤</span></p>
					<p><span class="mark2">景色：</span><span class="wz1">超美</span></p>
					<p><span class="mark2">天气：</span><span class="wz1">多云，18℃</span></p>
				</td>
			</tr>
			</table>
		</div>
	</td>
</tr>
<%
	}
%>
</table>
</div>

<div data-options="region:'south',border:true" style="height:30px;padding-top:2px;padding-left:5px;" class="justBorderTop">
<table width="100%" border="0" cellspacing="0">
<tr>
	<td style="width:120px;">每页数量：
	<select style="width:50px;">
	    <option value="10" selected>10</option>
	    <option value="20">20</option>
	    <option value="30">30</option>
	    <option value="50">50</option>
	    </select>
	</td>
	<td style="width:85px;"><a href="#" class="easyui-linkbutton" data-options="iconCls:'icon16-array-undo'">首页</a></td>
	<td style="width:98px;"><a href="#" class="easyui-linkbutton" data-options="iconCls:'icon16-array-left'">上一页</a></td>
	<td style="width:70px;" align="center" class="mark2">1 / 3</td>
	<td style="width:98px;"><a href="#" class="easyui-linkbutton" data-options="iconCls:'icon16-array-right'">下一页</a></td>
	<td style="width:85px;"><a href="#" class="easyui-linkbutton" data-options="iconCls:'icon16-array-redo'">末页</a></td>
	<td>&nbsp;</td>
</tr>
</table>
</div>

</div>

<div data-options="region:'south',border:false" style="height:60px;padding-top:8px;">
<table width="90%" border="0" cellspacing="0" align="center">
	<tr>
    <td align="center" class="mark3">版权所有、合作单位等</td>
  </tr>
</table>
</div>
</body>
</html>