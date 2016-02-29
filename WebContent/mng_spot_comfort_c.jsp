<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
	long curSpotId = request.getAttribute("curSpotId")!=null?(Long)request.getAttribute("curSpotId"):0l;
	String curSpotFullName = request.getAttribute("curSpotFullName")!=null?(String)request.getAttribute("curSpotFullName"):"";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="easyui/themes/icon.css">
<script type="text/javascript" src="easyui/jquery.min.js"></script>
<script type="text/javascript" src="easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="js/mng_spot_comfort_c.js"></script>
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
<script type="text/javascript">
var curSpotId = <%= curSpotId %>;
var curSpotFullName = '<%= curSpotFullName %>';
</script>
</head>
<body class="easyui-layout">
<div data-options="region:'east',collapsible:false" title="舒适度修正因子设置" style="width:280px;">
<table width="99%" border="0" align="center" cellspacing="0">
			<tr>
        		<td colspan="4" style="color:#f00;">(各因子对舒适度的调整值范围为-100~100)
        		<input type="hidden" id="id" name="id" value="0">
        		</td>
        	</tr>
			<tr>
		    	<td align="right" style="width:65px;">季节因子：</td>
		    	<td style="width:80px;">
				    	<select id="seasonFactor" name="seasonFactor" style="width:75px;">
							<option value="" selected>-未设置-</option>
						    <option value="春">春</option>
						    <option value="夏">夏</option>
						    <option value="秋">秋</option>
						    <option value="冬">冬</option>
					    </select>
		    	</td>
		    	<td align="right" style="width:55px;">调整值：</td>
		    	<td><input class="easyui-numberspinner" data-options="min:-100,max:100,editable:true,increment:1,value:0" name="seasonScore" id="seasonScore" style="width:55px;"/></td>
		  	</tr>
		  	
		  	<tr>
		    	<td align="right" style="width:65px;">天气因子：</td>
		    	<td style="width:80px;">
				    	<select id="weatherFactor" name="weatherFactor" style="width:75px;">
							<option value="" selected>-未设置-</option>
						    <option value="晴">晴</option>
						    <option value="阴">阴</option>
						    <option value="雨">雨</option>
						    <option value="雪">雪</option>
					    </select>
		    	</td>
		    	<td align="right" style="width:55px;">调整值：</td>
		    	<td><input class="easyui-numberspinner" data-options="min:-100,max:100,editable:true,increment:1,value:0" name="weatherScore" id="weatherScore" style="width:55px;"/></td>
		  	</tr>
		  	
		  	<tr>
		    	<td align="right" style="width:65px;">温度因子：</td>
		    	<td style="width:80px;">
				    	<select id="tempFactor" name="tempFactor" style="width:75px;">
							<option value="" selected>-未设置-</option>
						    <option value="高">高</option>
						    <option value="低">低</option>
					    </select>
		    	</td>
		    	<td align="right" style="width:55px;">调整值：</td>
		    	<td><input class="easyui-numberspinner" data-options="min:-100,max:100,editable:true,increment:1,value:0" name="tempScore" id="tempScore" style="width:55px;"/></td>
		  	</tr>
		  	
		  	<tr>
		    	<td align="right" style="width:65px;">客流因子：</td>
		    	<td style="width:80px;">
				    	<select id="passengerFactor" name="passengerFactor" style="width:75px;">
							<option value="" selected>-未设置-</option>
						    <option value="很多">很多</option>
						    <option value="较多">较多</option>
						    <option value="适中">适中</option>
						    <option value="较少">较少</option>
						    <option value="很少">很少</option>
					    </select>
		    	</td>
		    	<td align="right" style="width:55px;">调整值：</td>
		    	<td><input class="easyui-numberspinner" data-options="min:-100,max:100,editable:true,increment:1,value:0" name="passengerScore" id="passengerScore" style="width:55px;"/></td>
		  	</tr>
		  	
		  	<tr>
		  		<td align="right" style="width:65px;">备注：</td>
		  		<td colspan="3"><input type="text" name="mark" id="mark" style="width:95%;"/></td>
		  	</tr>
		  	
		  	<tr>
		    <td colspan="4" align="center">
			    <input type="button" value="新增" onClick="fnAdd();">&nbsp;
			    <input type="button" value="修改" onClick="fnModify();">&nbsp;
			    <input type="button" value="删除" onClick="fnDelete();">&nbsp;
			    <input type="button" value="重置" onClick="fnReset();">
			    </td>
			</tr>
</table>
</div>
<div id="divCenter" data-options="region:'center'" title="景点图片列表 [当前景点：<%= curSpotFullName %>]">
<table id="dg1" class="easyui-datagrid" width="100%" height="100%"
        data-options="url:'listSpotCC',fitColumns:true,singleSelect:true,fit:true,
        pagination:true,rownumbers:true,pageSize:30,pageList:[10,20,30,50,100,150,200],queryParams:{fistTime:true}">
	    <thead>
	        <tr>
	            <th data-options="field:'showStr',width:100">舒适度修正情况</th>
	        </tr>
	    </thead>
	</table>
</div>
</body>
</html>