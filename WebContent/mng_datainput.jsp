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
<script type="text/javascript" src="js/mng_datainput.js"></script>
<title>Engin-Lock-Cloud-Platform</title>
<style type="text/css">
body {
	font-family: "微软雅黑";
	font-size: 12px;
}
</style>
</head>
<body class="easyui-layout">
<div data-options="region:'north',collapsible:false" title="基础数据导入步骤：1、下载Excel模板。2、填写数据。3、选择文件。4、开始导入" style="height:90px;">
<form id="form1" method="POST" enctype="multipart/form-data">
	<table width="100%" border="0" align="center" cellspacing="0">
			<tr>
		    	<td style="font-size:14px;">
		    	<a href="uploadfiles/templateDir/Template_SpotBasic.xls">点击下载“景点基础数据”导入模板</a>
		    	</td>
		  	</tr>
		  	<tr>
		    	<td>选择文件：<input id="fileToUpload" type="file" style="width:220px;" name="fileToUpload">&nbsp;
		    	<input type="button" onclick="fnSubmitForm()" value="开始导入">
		    	</td>
		  	</tr>
	</table>
</form>
</div>
<div id="div_center" data-options="region:'center'" title="导入结果分析">
		<table id="dg1" class="easyui-datagrid" width="100%" height="100%"
	        data-options="fitColumns:true,singleSelect:true,fit:true,
	        pagination:false,rownumbers:true">
		    <thead>
		        <tr>
		            <th data-options="field:'succ',formatter:formatterSucc">&nbsp;&nbsp;&nbsp;</th>
		            <th data-options="field:'result',width:100">导入结果说明</th>
		        </tr>
		    </thead>
		</table>
</div>
</body>
</html>