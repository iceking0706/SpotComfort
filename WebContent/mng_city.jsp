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
<script type="text/javascript" src="js/mng_city.js"></script>
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
<div data-options="region:'east',collapsible:false" title="城市详细信息" style="width:280px;">
<form id="form1" method="POST" enctype="multipart/form-data">
<table width="99%" border="0" align="center" cellspacing="0">
	<tr>
	    <td colspan="2" class="info1">城市基本信息</td>
	</tr>
	<tr>
	    <td width="25%" align="right">城市名称：</td>
	    <td><input type="hidden" name="id" id="id" value="0" />
	    <input type="text" name="name" id="name" style="width:95%;"/></td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">天气代码：</td>
	    <td><input type="text" name="wcode" id="wcode" style="width:95%;"/></td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">所属省份：</td>
	    <td><input type="text" name="province" id="province" style="width:95%;"/></td>
	  </tr>
	  <tr>
	    <td colspan="2" class="info1">拼音用于排序，留空则自动生成</td>
	</tr>
	<tr>
	    <td width="25%" align="right">城市拼音：</td>
	    <td><input type="text" name="pinyin" id="pinyin" style="width:95%;"/></td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">推荐值：</td>
	    <td><input class="easyui-numberspinner" data-options="min:0,max:1000,editable:true,increment:1,value:0" name="rcmd" id="rcmd" style="width:198px;"/></td>
	  </tr>
	  <tr>
	    <td colspan="2" class="info1">上传图片</td>
	</tr>
	<tr>
	    <td colspan="2">
	    <input id="fileToUpload" type="file" style="width:200px;" name="fileToUpload">&nbsp;
	    <input type="button" value="上传" onClick="fnSubmitForm();"></td>
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
	    <td colspan="2" align="center">
		    <input type="button" value="新增" onClick="fnAdd();">&nbsp;
		    <input type="button" value="修改" onClick="fnModify();">&nbsp;
		    <input type="button" value="删除" onClick="fnDelete();"></td>
		</tr>
</table>
</form>
</div>
<div data-options="region:'center'" title="城市列表">
	<div id="toolbar" style="padding-left:10px;">
		省份：<input name="kw_province" type="text" id="kw_province" style="width:100px;">
		城市：<input name="kw_city" type="text" id="kw_city" style="width:100px;">
		<input type="button" value="查询" onClick="fnSearch();">
	</div>
	<table id="dg1" class="easyui-datagrid" width="100%" height="100%"
        data-options="url:'listCity',fitColumns:true,singleSelect:true,fit:true,
        pagination:true,rownumbers:true,toolbar:'#toolbar',pageSize:30,pageList:[10,20,30,50,100,150,200],queryParams:{fistTime:true}">
	    <thead>
	        <tr>
	            <th data-options="field:'province',width:100">省份</th>
	            <th data-options="field:'name',width:100">城市</th>
	            <th data-options="field:'wcode',width:100">天气代码</th>
	            <th data-options="field:'picUrl',formatter:formatUrl">缩略图</th>
	            <th data-options="field:'rcmd'">推荐</th>
	        </tr>
	    </thead>
	</table>
</div>
</body>
</html>