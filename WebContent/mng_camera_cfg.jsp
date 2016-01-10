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
<script type="text/javascript" src="js/mng_camera_cfg.js"></script>
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
<div data-options="region:'east',collapsible:false" title="相机配置信息" style="width:280px;">

<table width="99%" border="0" align="center" cellspacing="0">
	<tr>
	    <td colspan="2" class="info1">相机信息</td>
	</tr>
	<tr>
	    <td width="25%" align="right">SN：</td>
	    <td><input type="hidden" name="id" id="id" value="0" />
	    <input type="text" name="sn" id="sn" style="width:95%;"/></td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">备注：</td>
	    <td><input type="text" name="mark" id="mark" style="width:95%;"/></td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">场景：</td>
	    <td><input type="text" name="scene" id="scene" style="width:95%;"/></td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">启用：</td>
	    <td>
	    	<select id="inUse" name="inUse" style="width:95%;">
			<option value="0">禁用</option>
			<option value="1" selected>启用</option>
		</select>
	    </td>
	  </tr>
	  <tr>
	    <td colspan="2" class="info1">SIM卡信息</td>
	</tr>
	<tr>
	    <td width="25%" align="right">运营商：</td>
	    <td><input type="text" name="provider" id="provider" value="联通3G-WCDMA" style="width:95%;"/></td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">ICCID：</td>
	    <td><input type="text" name="sim" id="sim" style="width:95%;"/></td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">PUK：</td>
	    <td><input type="text" name="puk" id="puk" style="width:95%;"/></td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">手机号码：</td>
	    <td><input type="text" name="telNo" id="telNo" style="width:95%;"/></td>
	  </tr>
	  <tr>
	    <td colspan="2" class="info1">相机参数设置</td>
	</tr>
	<tr>
	    <td width="25%" align="right">数据间隔：</td>
	    <td><input class="easyui-numberspinner" data-options="min:1,max:1440,editable:true,increment:5,value:10" name="inoutInterval" id="inoutInterval" style="width:165px;"/> 分钟</td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">拍照间隔：</td>
	    <td><input class="easyui-numberspinner" data-options="min:1,max:1440,editable:true,increment:10,value:60" name="tkpInterval" id="tkpInterval" style="width:165px;"/> 分钟</td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">脱机报警：</td>
	    <td><input class="easyui-numberspinner" data-options="min:1,max:1440,editable:true,increment:10,value:30" name="offlineTimeout" id="offlineTimeout" style="width:165px;"/> 分钟</td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">开始小时：</td>
	    <td><input class="easyui-numberspinner" data-options="min:0,max:23,editable:true,increment:1,value:8" name="tkpHourSt" id="tkpHourSt" style="width:165px;"/> 时</td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">结束小时：</td>
	    <td><input class="easyui-numberspinner" data-options="min:0,max:23,editable:true,increment:1,value:20" name="tkpHourEd" id="tkpHourEd" style="width:165px;"/> 时</td>
	  </tr>
	  <tr>
	    <td colspan="2" align="center"><input type="button" value="应用到全部相机" onClick="fnSetParamToAll();"></td>
	</tr>
	  <tr>
	    <td colspan="2" ><hr /></td>
	</tr>
	  <tr>
	    <td colspan="2" align="center">
		    <input type="button" value="新增" onClick="fnAdd();">&nbsp;
		    <input type="button" value="修改" onClick="fnModify();">&nbsp;
		    <input type="button" value="删除" onClick="fnDelete();"></td>
		</tr>
</table>

</div>
<div data-options="region:'center'" title="相机列表">
	<div id="toolbar" style="padding-left:10px;">
		<input type="checkbox" name="kw_inUse" id="kw_inUse" checked="checked" onClick="fnSearch();" /><label for="kw_inUse">仅启用</label>
		, SN：<input name="kw_sn" type="text" id="kw_sn" style="width:160px;">
		备注：<input name="kw_mark" type="text" id="kw_mark" style="width:100px;">
		场景：<input name="kw_scene" type="text" id="kw_scene" style="width:100px;">
		<input type="button" value="查询" onClick="fnSearch();">
		操作：
		<select id="kw_oper">
			<option value="0" selected>--选择相机操作--</option>
			<option value="1">重启相机连接监听</option>
			<option value="2">检测相机联机状态</option>
			<option value="3">抓取相机I/O数据</option>
			<option value="4">设置相机时钟</option>
			<option value="5">实时照片抓拍</option>
		</select>
		<input type="button" value="下发" onClick="fnDownOper();">
	</div>
	<table id="dg1" class="easyui-datagrid" width="100%" height="100%"
        data-options="url:'listCameraCfg',fitColumns:true,singleSelect:true,fit:true,
        pagination:true,rownumbers:true,toolbar:'#toolbar',pageSize:30,pageList:[10,20,30,50,100,150,200],queryParams:{fistTime:true}">
	    <thead>
	        <tr>
	            <th data-options="field:'sn'">SN</th>
	            <th data-options="field:'mark',width:100">备注</th>
	            <th data-options="field:'ip'">IP</th>
	            <th data-options="field:'online',formatter:formatOnline">状态</th>
	        </tr>
	    </thead>
	</table>
</div>

<!-- 快速拍照的弹出窗口 -->
<div id="dlg_quick_pic" class="easyui-dialog" title="相机照片" style="width:520px;height:354px;padding-top:5px;"
        data-options="iconCls:'icon-save',resizable:false,modal:true,closed:true,buttons:'#dlg_quick_pic_btn'">
        <center><img id="dlg_quick_pic_img" src="" width="480" height="270" /></center>
</div>
<div id="dlg_quick_pic_btn">
	<a href="#" class="easyui-linkbutton" onClick="$('#dlg_quick_pic').dialog('close')">关闭</a>
</div>
</body>
</html>