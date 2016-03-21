<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="com.xie.spot.pojo.spotshow.SpotsShowCfgByJson" %>
<%@page import="com.xie.spot.pojo.spotshow.OneSpotCfg" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="easyui/themes/icon.css">
<script type="text/javascript" src="easyui/jquery.min.js"></script>
<script type="text/javascript" src="easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="js/mng_index.js"></script>
<title><%= com.xie.spot.sys.VersionInfo.pageTitle %>-数据管理</title>
<style type="text/css">
body {
	font-family: "微软雅黑";
	font-size: 12px;
}
.p-title {
	font-size: 42px;
}
.mark1 {
	font-family: 华文彩云;
	font-size: 24px;
}
.mark2 {
	font-weight: bold;
}
</style>
</head>
<body class="easyui-layout">
<div data-options="region:'north',border:false" style="height:90px;padding-top:5px;">
<table width="95%" border="0" cellspacing="0" align="center">
	<tr>
		<td style="width:70px;"><img src="images/logo_sy.png" width="220" height="78"></td>
		<td style="width:450px;" class="mark1">“出门旅游，先看看舒适度！”</td>
		<td valign="bottom" align="right" class="mark2">
		<!-- 各个景点的演示页面 -->
		[
		<select id="selectShowSpot" onchange="fnSelectShowSpot();">
			<option value="0" selected>--各景点演示--</option>
			<%
				for(int i=0;i<SpotsShowCfgByJson.getInstance().getSpotsCfg().length;i++){
					OneSpotCfg oneSpotCfg = SpotsShowCfgByJson.getInstance().getSpotsCfg()[i];
			%>
			<option value="<%= oneSpotCfg.getSpotNo() %>"><%= oneSpotCfg.getSpotName() %></option>
			<%
				}
			%>
		</select>
		]
		</td>
	</tr>
</table>
</div>
<div data-options="region:'center'" style="background-image: url(images/bgsd1.jpg);background-position: center;">
<table width="100%" height="100%" border="0" cellspacing="0">
	<tr valign="bottom" >
		<td>&nbsp;</td>
		<td style="width:50px;">帐号：</td>
		<td style="width:110px;"><input name="txt_username" type="text" id="txt_username" style="width:100px;" value=""></td>
		<td style="width:50px;">密码：</td>
		<td style="width:110px;"><input name="txt_password" type="password" id="txt_password" style="width:100px;" value=""></td>
		<td style="width:70px;"><a href="#" class="easyui-linkbutton" data-options=""  onclick="jscript: fnLogin();" >登入</a></td>
		<td style="width:80px;">&nbsp;</td>
	</tr>
</table>
</div>
<div data-options="region:'south',border:false" style="height:35px;padding-top:8px;">
<table width="90%" border="0" cellspacing="0" align="center">
	<tr>
    <td align="center">版权所有：杭州酥游科技有限公司&nbsp;&nbsp;版本：<%= com.xie.spot.sys.VersionInfo.VERSION_NO %></td>
  </tr>
</table>
</div>
</body>
</html>