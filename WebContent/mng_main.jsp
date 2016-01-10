<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="com.xie.spot.entity.User" %>
<%
if(session.getAttribute("loginUser") == null){
	out.println("用户登入信息已失效，请重新<a href=mng_index.jsp>登入</a>");
}else{
	User loginUser = (User)session.getAttribute("loginUser");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="easyui/themes/icon.css">
<script type="text/javascript" src="easyui/jquery.min.js"></script>
<script type="text/javascript" src="easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="js/mng_main.js"></script>
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
.icon16-user{
	background:url('icons/icon16/user_comment.png') no-repeat center center;
}
.icon16-excel{
	background:url('icons/icon16/page.png') no-repeat center center;
}
.icon16-city{
	background:url('icons/icon16/world.png') no-repeat center center;
}
.icon16-spot{
	background:url('icons/icon16/map.png') no-repeat center center;
}
.icon16-spot-pic{
	background:url('icons/icon16/picture.png') no-repeat center center;
}
.icon16-spot-weather{
	background:url('icons/icon16/weather_cloudy.png') no-repeat center center;
}
.icon16-spot-psg{
	background:url('icons/icon16/tux.png') no-repeat center center;
}
.icon16-spot-comfort{
	background:url('icons/icon16/shape_ssd.png') no-repeat center center;
}
.icon16-spot-cc{
	background:url('icons/icon16/monitor_edit.png') no-repeat center center;
}
.icon16-camera-cfg{
	background:url('icons/icon16/camera.png') no-repeat center center;
}
.icon16-camera-data{
	background:url('icons/icon16/camera_link.png') no-repeat center center;
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
		[IP地址归属：<span id="iploc">unknow</span>]<br>
		[管理员：<%= loginUser.getName() %>]&nbsp;[<a href="#" onClick="fnOpenDlg_modpass();">修改密码</a>]&nbsp;[<a href="#" onClick="fnLogout();">退出</a>]
		</td>
	</tr>
</table>
</div>
<div data-options="region:'center'" class="easyui-layout">
<div data-options="region:'west',collapsible:false" title="管理栏目导航" style="width:180px;padding:1px;">
<ul class="easyui-tree">
	<!-- li iconCls="icon16-excel"><span><a href="#" onClick="fnIframeToPage('mng_datainput.jsp','基础数据导入')">基础数据导入</a></span></li>
	<li iconCls="icon16-city"><span><a href="#" onClick="fnIframeToPage('mng_city.jsp','城市信息')">城市信息</a></span></li>
	<li iconCls="icon16-spot"><span><a href="#" onClick="fnIframeToPage('mng_spot.jsp','景点信息')">景点信息</a></span></li>
	<li iconCls="icon16-spot-weather"><span><a href="#" onClick="fnIframeToPage('mng_spot_weather.jsp','景点天气')">景点天气</a></span></li>
	<li iconCls="icon16-spot-psg"><span><a href="#" onClick="fnIframeToPage('mng_spot_psgr.jsp','景点客流')">景点客流</a></span></li>
	<li iconCls="icon16-spot-comfort"><span><a href="#" onClick="fnIframeToPage('mng_spot_comfort.jsp','景点舒适度')">景点舒适度</a></span></li>
	<li><span><a href="#" onClick="fnIframeToPage('mng_advtpic.jsp','主页广告图')">主页广告图</a></span></li-->
	<li iconCls="icon16-camera-cfg"><span><a href="#" onClick="fnIframeToPage('mng_camera_cfg.jsp','相机配置')">相机配置</a></span></li>
	<li iconCls="icon16-camera-data"><span><a href="#" onClick="fnIframeToPage('mng_camera_data.jsp','相机数据')">相机数据</a></span></li>
	<li><span><a href="#" onClick="fnIframeToPage('mng_camera_lostrawio.jsp','数据丢失情况')">数据丢失情况</a></span></li>
	<li><span><a href="#" onClick="fnIframeToPage('mng_camera_alert.jsp','相机报警')">报警记录</a></span></li>
	<li><span><a href="#" onClick="fnIframeToPage('mng_mysqlbackup.jsp','数据库备份')">数据库备份</a></span></li>
</ul>
</div>
<div data-options="region:'center'" >
	<div id="tabs" class="easyui-tabs" data-options="fit:true,tools:'#tab-tools'"></div>
	<div id="tab-tools">
		<a href="#" onClick="fnRefreshCurTab();">刷新本页</a>&nbsp;
		<a href="#" onClick="fnDelAllTabs();">关闭全部</a>
	</div>
</div>
</div>
<div data-options="region:'south',border:false" style="height:35px;padding-top:8px;">
<table width="90%" border="0" cellspacing="0" align="center">
	<tr>
    <td align="center">版权所有：杭州酥游科技有限公司&nbsp;&nbsp;版本：<%= com.xie.spot.sys.VersionInfo.VERSION_NO %></td>
  </tr>
</table>
</div>

<!-- 修改密码的弹出窗口 -->
<div id="dlg_modpass" class="easyui-dialog" title="修改帐号登入密码" style="width:400px;height:200px;"
        data-options="iconCls:'icon-save',resizable:false,modal:true,closed:true,buttons:'#dlg_modpass_btn'">
        <table width="95%" border="0" align="center" cellspacing="0">
        	<tr>
		    	<td width="25%" align="right">原密码：</td>
		    	<td><input type="password" name="dlg_modpass_oldPass" id="dlg_modpass_oldPass" style="width:240px;"></td>
		  	</tr>
		  	<tr>
		    	<td width="25%" align="right">新密码：</td>
		    	<td><input type="password" name="dlg_modpass_newPass" id="dlg_modpass_newPass" style="width:240px;"></td>
		  	</tr>
		  	<tr>
		    	<td width="25%" align="right">再次输入：</td>
		    	<td><input type="password" name="dlg_modpass_newPass2" id="dlg_modpass_newPass2" style="width:240px;"></td>
		  	</tr>
        </table>
</div>
<div id="dlg_modpass_btn">
	<a href="#" class="easyui-linkbutton" onClick="fnSubmit_modpass()">确定</a>
	<a href="#" class="easyui-linkbutton" onClick="$('#dlg_modpass').dialog('close')">关闭</a>
</div>

</body>
</html>
<%
}
%>
