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
<script type="text/javascript" src="easyui/jquery.form.js"></script>
<script type="text/javascript" src="js/mng_mysqlbackup.js"></script>
<title>Engin-Lock-Cloud-Platform</title>
<style type="text/css">
body {
	font-family: "微软雅黑";
	font-size: 12px;
}

</style>
</head>
<body class="easyui-layout">
<div data-options="region:'center'" title="数据库备份">
	<div id="toolbar">
		操作：<input type="button" value="备份数据库" onClick="fnDoBackup();">
		<input type="button" value="删除备份文件" onClick="fnDeleteFile();">
		<input type="button" value="刷新" onClick="fnSearch();">
		历史数据迁移：
		<input id="search_dt_end" type="text" name="search_dt_end" class="easyui-datebox" data-options="value:'',formatter:fnFormatter_DateTime,width:100">
		之前. 
		<input type="button" value="分析&迁移" onClick="fnAnalyTrans();">
	</div>
	<table id="dg1" class="easyui-datagrid" width="100%" height="100%"
        data-options="url:'listMysqlDumpFiles',fitColumns:true,singleSelect:true,
        pagination:false,rownumbers:true,showHeader:true,toolbar:'#toolbar',pageSize:60,queryParams:{fistTime:true}">
	    <thead>
	        <tr>
	            <th data-options="field:'name'">文件名</th>
	            <th data-options="field:'time'">创建时间</th>
	            <th data-options="field:'size'">文件大小</th>
	            <th data-options="field:'a',width:100,formatter:formatDownload">文件下载</th>
	        </tr>
	    </thead>
	</table>
</div>
</body>
</html>