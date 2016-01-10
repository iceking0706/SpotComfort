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
<script type="text/javascript" src="easyui/highcharts4.1.5/highcharts.js"></script>
<script type="text/javascript" src="js/mng_camera_lostrawio.js"></script>
<title>Engin-Lock-Cloud-Platform</title>
<style type="text/css">
body {
	font-family: "微软雅黑";
	font-size: 12px;
}
</style>
</head>
<body class="easyui-layout">
<div data-options="region:'north',border:false" style="height:35px;padding-left:10px;padding-top:3px;">
		SN：<input name="kw_sn" type="text" id="kw_sn" style="width:160px;">
		时间段：<input id="search_dt_start" type="text" name="search_dt_start" class="easyui-datebox" data-options="value:'',formatter:fnFormatter_DateTime,width:100">
		  &nbsp;~&nbsp;
		  <input id="search_dt_end" type="text" name="search_dt_end" class="easyui-datebox" data-options="value:'',formatter:fnFormatter_DateTime,width:100">
		<input type="button" value="查询" onClick="fnSearch();">
</div>

<div id="div_west_data" data-options="region:'west',collapsible:false" title="相机数据丢失情况" style="width:250px;padding:1px;">
	
	<table id="dg1" class="easyui-datagrid" width="100%" height="100%"
        data-options="url:'',fitColumns:true,singleSelect:true,fit:true,
        pagination:false,rownumbers:true,pageSize:30,pageList:[10,20,30,50,100,150,200],queryParams:{fistTime:true}">
	    <thead>
	        <tr>
	        	<th data-options="field:'dayShort',width:100">日期</th>
	            <th data-options="field:'sumIn'">进</th>
	            <th data-options="field:'sumOut'">出</th>
	            <th data-options="field:'lostPercent',formatter:formatLostP">丢失</th>
	            <th data-options="field:'a',formatter:formatOper">操作</th>
	        </tr>
	    </thead>
	</table>
</div>

<div id="chart1" data-options="region:'center'" >

</div>

<!-- 查看某一天丢失分钟数据的弹出框 -->
<div id="dlg_view_detail" class="easyui-dialog" title="原始数据详情查看" style="width:320px;height:250px;"
        data-options="iconCls:'icon-save',resizable:false,modal:true,closed:true,buttons:'#dlg_view_detail_btn'">
   <table id="dgview" class="easyui-datagrid" width="100%" height="100%"
        data-options="url:'',fitColumns:true,singleSelect:true,fit:true,
        pagination:false,rownumbers:true,pageSize:30,pageList:[10,20,30,50,100,150,200],queryParams:{fistTime:true}">
	    <thead>
	        <tr>
	        	<th data-options="field:'a',width:100,formatter:formatLostMinutes">丢失时间段</th>
	        	<th data-options="field:'b',formatter:formatTianbu">操作</th>
	        </tr>
	    </thead>
	</table>
</div>
<div id="dlg_view_detail_btn">
	<a href="#" class="easyui-linkbutton" onClick="$('#dlg_view_detail').dialog('close')">关闭</a>
</div>

</body>
</html>