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
<script type="text/javascript" src="js/mng_camera_alert.js"></script>
<title>Engin-Lock-Cloud-Platform</title>
<style type="text/css">
body {
	font-family: "微软雅黑";
	font-size: 12px;
}
</style>
</head>
<body class="easyui-layout">
<div data-options="region:'center'" title="摄像机报警信息列表">
	<div id="toolbar" style="padding-left:10px;">
		类型：<select id="kw_type" onChange="fnSearch();">
			<option value="0" selected>--全部--</option>
			<option value="1" >长时间脱机</option>
			<option value="2" >时钟偏差</option>
		</select>
		状态：<select id="kw_processed" onChange="fnSearch();">
			<option value="0" >--全部--</option>
			<option value="1" selected>未处理</option>
			<option value="2" >已处理</option>
		</select>
		SN：<input name="kw_sn" type="text" id="kw_sn" style="width:160px;">
		时间段：<input id="search_dt_start" type="text" name="search_dt_start" class="easyui-datebox" data-options="value:'',formatter:fnFormatter_DateTime,width:100">
		  &nbsp;~&nbsp;
		  <input id="search_dt_end" type="text" name="search_dt_end" class="easyui-datebox" data-options="value:'',formatter:fnFormatter_DateTime,width:100">
		<input type="button" value="查询" onClick="fnSearch();">
		<input type="button" value="删除" onClick="fnDelete();">
		<input type="button" value="处理" onClick="fnProcess();">
	</div>
	<table id="dg1" class="easyui-datagrid" width="100%" height="100%"
        data-options="url:'listCameraAlert',fitColumns:true,singleSelect:false,fit:true,
        pagination:true,rownumbers:true,toolbar:'#toolbar',pageSize:30,pageList:[10,20,30,50,100,150,200],queryParams:{fistTime:true}">
	    <thead>
	        <tr>
	        	<th data-options="field:'ck',checkbox:true">&nbsp;</th>
	        	<th data-options="field:'timeShow'">时间</th>
	        	<th data-options="field:'type',formatter:formatType">报警类型</th>
	            <th data-options="field:'sn',width:100,formatter:formatSnMark">SN</th>
	            <th data-options="field:'mark'">内容</th>
	            <th data-options="field:'processed',formatter:formatProccessed">状态</th>
	            <th data-options="field:'sendMailed',formatter:formatSendMailed">邮件</th>
	        </tr>
	    </thead>
	</table>
</div>

<!-- 处理信息的填写 -->
<div id="dlg_alert_process" class="easyui-dialog" title="报警记录处理" style="width:300px;height:150px;"
        data-options="iconCls:'icon-save',resizable:false,modal:true,closed:true,buttons:'#dlg_alert_process_btn'">
        <table width="98%" border="0" align="center" cellspacing="0">
        	<tr>
		    	<td>
			    	填写处理内容:
		    	</td>
		  	</tr>
		  	<tr>
		    	<td>
			    	<input type="text" name="prsMark" id="prsMark" style="width:95%;"/>
		    	</td>
		  	</tr>
        </table>
</div>
<div id="dlg_alert_process_btn">
	<a href="#" class="easyui-linkbutton" onClick="fnSubmitProcess()">提交</a>
	<a href="#" class="easyui-linkbutton" onClick="$('#dlg_alert_process').dialog('close')">关闭</a>
</div>

</body>
</html>