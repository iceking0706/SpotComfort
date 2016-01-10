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
<script type="text/javascript" src="js/mng_camera_data.js"></script>
<title>Engin-Lock-Cloud-Platform</title>
<style type="text/css">
body {
	font-family: "微软雅黑";
	font-size: 12px;
}
</style>
</head>
<body class="easyui-layout">
<div data-options="region:'center'" title="摄像机数据列表">
	<div id="toolbar" style="padding-left:10px;">
		SN：<input name="kw_sn" type="text" id="kw_sn" style="width:160px;">
		时间段：<input id="search_dt_start" type="text" name="search_dt_start" class="easyui-datebox" data-options="value:'',formatter:fnFormatter_DateTime,width:100">
		  &nbsp;~&nbsp;
		  <input id="search_dt_end" type="text" name="search_dt_end" class="easyui-datebox" data-options="value:'',formatter:fnFormatter_DateTime,width:100">
		<input type="button" value="查询" onClick="fnSearch();">
		<input type="button" value="删除" onClick="fnDelete();">
		<input type="button" value="详情" onClick="fnViewRawData();">
		<input type="button" value="日统计" onClick="fnCalcuOneday();">
		<input type="button" value="导出TXT" onClick="fnOutputTxt();">
	</div>
	<table id="dg1" class="easyui-datagrid" width="100%" height="100%"
        data-options="url:'listCameraData',fitColumns:true,singleSelect:true,fit:true,
        pagination:true,rownumbers:true,toolbar:'#toolbar',pageSize:30,pageList:[10,20,30,50,100,150,200],queryParams:{fistTime:true}">
	    <thead>
	        <tr>
	        	<th data-options="field:'timeShow'">时间</th>
	            <th data-options="field:'sn',width:100,formatter:formatSnMark">SN</th>
	            <th data-options="field:'din'">In</th>
	            <th data-options="field:'dout'">Out</th>
	            <th data-options="field:'picUrl',formatter:formatUrl">图片</th>
	        </tr>
	    </thead>
	</table>
</div>

<!-- 历史数据详情的窗口 -->
<div id="dlg_data_view" class="easyui-dialog" title="原始数据详情查看" style="width:350px;height:350px;"
        data-options="iconCls:'icon-save',resizable:false,modal:true,closed:true,buttons:'#dlg_data_view_btn'">
        <table width="98%" border="0" align="center" cellspacing="0">
        	<tr>
		    	<td id="dlg_data_view_td_picTime">
			    	图片时间:
		    	</td>
		  	</tr>
		  	<tr>
		    	<td>
			    	<img id="dlg_data_view_td_picUrl" src="" width="260" height="148" />
		    	</td>
		  	</tr>
		  	<tr>
		    	<td>
			    	In / Out 数据:
		    	</td>
		  	</tr>
		  	<tr>
		    	<td id="dlg_data_view_td_rows">
			    	&nbsp;
		    	</td>
		  	</tr>
        </table>
</div>
<div id="dlg_data_view_btn">
	<a href="#" class="easyui-linkbutton" onClick="$('#dlg_data_view').dialog('close')">关闭</a>
</div>

</body>
</html>