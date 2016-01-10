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
<script type="text/javascript" src="js/mng_spot_psgr.js"></script>
<title>Engin-Lock-Cloud-Platform</title>
<style type="text/css">
body {
	font-family: "微软雅黑";
	font-size: 12px;
}
</style>
</head>
<body class="easyui-layout">
<div data-options="region:'center'" title="天气列表">
	<div id="toolbar" style="padding-left:10px;">
		省份：<input class="easyui-combobox" data-options="editable:false" name="kw_province" id="kw_province" style="width:70px;"/>
		城市：<input class="easyui-combobox" data-options="editable:false"  name="kw_city" id="kw_city" style="width:100px;"/>
	    景点名称：<input type="text" name="kw_spotname" id="kw_spotname" style="width:100px;"/>
		<input type="button" value="查询" onClick="fnSearch();">
		<input type="button" value="删除" onClick="fnDelete();">
	</div>
	<table id="dg1" class="easyui-datagrid" width="100%" height="100%"
        data-options="url:'listPassengerFlow',fitColumns:true,singleSelect:false,fit:true,
        pagination:true,rownumbers:true,toolbar:'#toolbar',pageSize:30,pageList:[10,20,30,50,100,150,200],queryParams:{fistTime:true}">
	    <thead>
	        <tr>
	        	<th data-options="field:'ck',checkbox:true">&nbsp;</th>
	        	<th data-options="field:'timeShow'">获得时间</th>
	            <th data-options="field:'spotFullName',width:100">景点</th>
	            <th data-options="field:'flow',formatter:formatFlow">客流</th>
	            <th data-options="field:'crowdDegree',formatter:formatCD">拥挤度</th>
	        </tr>
	    </thead>
	</table>
</div>
</body>
</html>