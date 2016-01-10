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
<script type="text/javascript" src="js/mycls/commdef.js"></script>
<script type="text/javascript" src="js/mycls/HtmlTemplet.js"></script>
<script type="text/javascript" src="js/ztmp.js"></script>
<title>Engin-Lock-Cloud-Platform</title>
<style type="text/css">
body {
	font-family: "微软雅黑";
	font-size: 12px;
}
</style>
</head>
<body>
JSP页面模版<br>
<a href="#" class="easyui-linkbutton" data-options=""  onclick="jscript: fnLogin();" >登入</a><br>
<input type="button" value="自定义类1" onclick="fnMyCls1();" /><input type="button" value="自定义类2" onclick="fnMyCls2();" /><br>
<input type="button" value="模版测试" onclick="fnTestTemplet1();" /><input type="button" value="模版测试22" onclick="fnTestTemplet2();" /><br />
<input type="button" value="测试微信接口json" onclick="fnTestWechatJson();" /><input type="text" id="text33" style="width:100px;" value="杭州"><br />
<input type="text" id="text22" style="width:90%">

<div id="divHiddenTemplet" style="display:none;">
<div>
<table>
	<tr>
		<td>{name}</td>
		<td>{title}</td>
		<td>{nr.a}</td>
	</tr>
</table>
</div>
</div>

</body>
</html>