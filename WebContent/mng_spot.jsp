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
<script type="text/javascript" src="js/mng_spot.js"></script>
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
<div data-options="region:'east',collapsible:false" title="景点详细信息" style="width:280px;">
<table width="99%" border="0" align="center" cellspacing="0">
	<tr>
	    <td colspan="2" class="info1">景点所属城市</td>
	</tr>
	<tr>
	    <td width="25%" align="right">所属省份：</td>
	    <td><input type="hidden" name="id" id="id" value="0" />
	    <input class="easyui-combobox" data-options="editable:false" name="province" id="province" style="width:198px;"/></td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">所属城市：</td>
	    <td><input class="easyui-combobox" data-options="editable:false"  name="city" id="city" style="width:198px;"/></td>
	  </tr>
	  <tr>
	    <td colspan="2" class="info1">景点基本信息</td>
	</tr>
	<tr>
	    <td width="25%" align="right">景点名称：</td>
	    <td><input type="text" name="name" id="name" style="width:95%;"/></td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">景点编号：</td>
	    <td><input type="text" name="code" id="code" style="width:95%;"/></td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">天气代码：</td>
	    <td><input type="text" name="wcode" id="wcode" style="width:95%;"/></td>
	  </tr>
	 <tr>
	    <td width="25%" align="right">景点评级：</td>
	    <td><input type="text" name="grade" id="grade" style="width:95%;"/></td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">景观等级：</td>
	    <td><select id="viewLevel" name="viewLevel" style="width:198px;">
	    <option value="1">绝美</option>
	    <option value="2" selected>超美</option>
	    <option value="3">美</option>
	    </select></td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">最大承载：</td>
	    <td><input class="easyui-numberspinner" data-options="min:0,max:100000,editable:true,increment:100,value:0" name="maxCapacity" id="maxCapacity" style="width:198px;"/></td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">坐标X：</td>
	    <td><input class="easyui-numberbox" data-options="min:0,value:0,precision:8" name="lonX" id="lonX" style="width:198px;"/></td>
	  </tr>
	  <tr>
	    <td width="25%" align="right">坐标Y：</td>
	    <td><input class="easyui-numberbox" data-options="min:0,value:0,precision:8" name="latY" id="latY" style="width:198px;"/></td>
	  </tr>
	  <tr>
	    <td colspan="2" class="info1">其它设置</td>
	</tr>
	<tr>
	    <td width="25%" align="right">推荐值：</td>
	    <td><input class="easyui-numberspinner" data-options="min:0,max:1000,editable:true,increment:1,value:0" name="mainRcmd" id="mainRcmd" style="width:198px;"/></td>
	  </tr>
	  <tr>
	    <td colspan="2" align="center">
		    <input type="button" value="新增" onClick="fnAdd();">&nbsp;
		    <input type="button" value="修改" onClick="fnModify();">&nbsp;
		    <input type="button" value="删除" onClick="fnDelete();"></td>
		</tr>
	<tr><td colspan="2"><hr /></td></tr>
	<tr>
	    <td width="25%" align="right">模拟客流：</td>
	    <td><input class="easyui-numberspinner" data-options="min:0,max:100000,editable:true,increment:100,value:0" name="simulateFlow" id="simulateFlow" style="width:100px;"/>
	    &nbsp;人/小时&nbsp;[<a href="#" onClick="fnSimulateFlow();">提交</a>]
	    </td>
	  </tr>
	  
	  <tr><td colspan="2" align="left">
	  	[<a href="#" onClick="fnCalcuSpotComfort();">计算景点舒适度指数</a>]
	  </td></tr>
	
</table>
</div>
<div data-options="region:'center'" title="景点列表">
<div id="toolbar" style="padding-left:10px;">
		省份：<input class="easyui-combobox" data-options="editable:false" name="kw_province" id="kw_province" style="width:70px;"/>
		城市：<input class="easyui-combobox" data-options="editable:false"  name="kw_city" id="kw_city" style="width:100px;"/>
		景观：<select id="kw_viewLevel" name="kw_viewLevel" style="width:60px;">
		<option value="0" selected>----</option>
	    <option value="1">绝美</option>
	    <option value="2">超美</option>
	    <option value="3">美</option>
	    </select>
	    景点名称：<input type="text" name="kw_name" id="kw_name" style="width:100px;"/>
		<input type="button" value="查询" onClick="fnSearch();">
	</div>
	<table id="dg1" class="easyui-datagrid" width="100%" height="100%"
        data-options="url:'listSpot',fitColumns:true,singleSelect:true,fit:true,
        pagination:true,rownumbers:true,toolbar:'#toolbar',pageSize:30,pageList:[10,20,30,50,100,150,200],queryParams:{fistTime:true}">
	    <thead>
	        <tr>
	            <th data-options="field:'a',formatter:formatProvince">省份</th>
	            <th data-options="field:'b',formatter:formatCity">城市</th>
	            <th data-options="field:'name',width:100">景点名称</th>
	            <th data-options="field:'viewLevel',formatter:formatViewLevel">景观等级</th>
	            <th data-options="field:'grade'">景点评级</th>
	            <th data-options="field:'maxCapacity',formatter:formatMaxCapacity">最大承载</th>
	            <th data-options="field:'mainRcmd'">推荐值</th>
	            <th data-options="field:'c',formatter:formatPicture">图片</th>
	            <th data-options="field:'d',formatter:formatCC">修正</th>
	        </tr>
	    </thead>
	</table>
</div>


</body>
</html>