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
<script type="text/javascript" src="js/mng_advtpic.js"></script>
<title>Engin-Lock-Cloud-Platform</title>
<style type="text/css">
body {
	font-family: "微软雅黑";
	font-size: 12px;
}
.info1 {
	font-size: 14px;
	font-weight: bold;
	background-color: #FFC;
}
</style>

</head>
<body class="easyui-layout">
<div data-options="region:'east',collapsible:false" title="图片的详细信息" style="width:280px;">
<form id="form1" method="POST" enctype="multipart/form-data">
<table width="99%" border="0" align="center" cellspacing="0">
	<tr>
	    <td colspan="2" class="info1">上传图片</td>
	</tr>
	<tr>
	    <td colspan="2">
	    <input id="fileToUpload" type="file" style="width:200px;" name="fileToUpload">&nbsp;
	    <input type="button" value="上传" onClick="fnSubmitForm();"></td>
	</tr>
	<tr>
	    <td colspan="2" class="info1">设置图片</td>
	</tr>
	<tr>
	    <td colspan="2">图片路径：</td>
	</tr>
	<tr>
	    <td colspan="2"><input type="text" name="url" id="url" style="width:95%;"/></td>
	</tr>
	<tr>
	    <td colspan="2">图片预览：(点击下方图片可以预览图片)</td>
	</tr>
	<tr>
	    <td colspan="2" align="center">
	    <img id="imgPreview" src="" style="width:260px;height:148px;" onClick="fnImgPreview();" />
	    </td>
	</tr>
	<tr>
	    <td colspan="2">推荐值：(数值高的图片将在主页展示)</td>
	</tr>
	<tr>
	    <td colspan="2">
	    <input class="easyui-numberspinner" data-options="min:0,max:1000,editable:true,increment:1,value:0" name="mainRcmd" id="mainRcmd" style="width:260px;"/>
	    </td>
	</tr>
	<tr>
	    <td colspan="2">图片备注：</td>
	</tr>
	<tr>
	    <td colspan="2"><input type="text" name="mark" id="mark" style="width:95%;"/></td>
	</tr>
	<tr>
	    <td colspan="2">点击链接：</td>
	</tr>
	<tr>
	    <td colspan="2"><input type="text" name="linkUrl" id="linkUrl" style="width:95%;"/></td>
	</tr>
	<tr>
	    <td colspan="2" align="center">
		    <input type="button" value="新增" onClick="fnAdd();">&nbsp;
		    <input type="button" value="修改" onClick="fnModify();">&nbsp;
		    <input type="button" value="删除" onClick="fnDelete();">&nbsp;
		    <input type="button" value="重置" onClick="fnReset();">&nbsp;
		    </td>
		</tr>
	
</table>
</form>
</div>
<div id="divCenter" data-options="region:'center'" title="主页广告位图片列表 [广告位最多可放置5张图片，以推荐值从高到低顺序]">
<table id="dg1" class="easyui-datagrid" width="100%" height="100%"
        data-options="url:'listAdvtPicture',fitColumns:true,singleSelect:true,fit:true,
        pagination:true,rownumbers:true,pageSize:30,pageList:[10,20,30,50,100,150,200],queryParams:{fistTime:true}">
	    <thead>
	        <tr>
	            <th data-options="field:'timeShow'">时间</th>
	            <th data-options="field:'url',formatter:formatUrl">缩略图</th>
	            <th data-options="field:'mark',width:100">备注</th>
	            <th data-options="field:'mainRcmd'">推荐值</th>
	            <th data-options="field:'linkUrl',formatter:formatLinkUrl">预览</th>
	        </tr>
	    </thead>
	</table>
</div>
</body>
</html>