<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="com.xie.spot.pojo.spotshow.SpotsShowCfgByJson" %>
<%@page import="com.xie.spot.pojo.spotshow.OneSpotCfg" %>
<%
	//景点编号，默认是1
	int spotNo = request.getParameter("no")!=null?Integer.parseInt(request.getParameter("no")):1;
	OneSpotCfg bean = SpotsShowCfgByJson.getInstance().getOneByNo(spotNo);
	if(bean==null || !bean.action()){
		out.println("数据获取失败");
	}else{
		//System.out.println(bean.toString());
		//相机总的数量
		int cmrCount = bean.cmrCount();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="easyui/themes/icon.css">
<script type="text/javascript" src="easyui/jquery.min.js"></script>
<script type="text/javascript" src="easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyui/highcharts4.1.5/highcharts.js"></script>
<script type="text/javascript" src="js/showOne.js"></script>
<script type="text/javascript" >
//景点的名称
var spotName = '<%= bean.getSpotName() %>';
//画图用的两个数组
var dataInArray = new Array();
var dataOutArray = new Array();
//景点各个相机的名称，画图时候使用的
var spotCmrNameArray = new Array();
<%
	for(int i=0;i<cmrCount;i++){
		String curSn = bean.cmrSN(i);
%>
dataInArray[dataInArray.length]=<%= bean.cmrSumIn(curSn) %>;
dataOutArray[dataOutArray.length]=<%= bean.cmrSumOut(curSn) %>;
spotCmrNameArray[spotCmrNameArray.length]='<%= bean.cmrName(curSn) %>';
<%
	}
%>
</script>
<title><%= com.xie.spot.sys.VersionInfo.pageTitle %>-<%= bean.getSpotName() %>-数据管理</title>
<style type="text/css">
body {
	font-family: "微软雅黑";
	font-size: 12px;
}
.p-title {
	font-size: 42px;
}
.mark1 {
	font-family: 华文彩云;
	font-size: 40px;
}
.mark3 {
	font-size: 28px;
}
.mark2 {
	font-weight: bold;
}
.color-red {
	color: #F00;
}
.font24{
	font-size: 22px;
	color: #00F;
}
</style>
</head>
<body class="easyui-layout">
<div data-options="region:'north',border:false" style="height:80px;padding-top:5px;">
<table width="95%" border="0" cellspacing="0" align="center">
	<tr>
		<td style="width:70px;"><img src="icons/smooth-drop.png" width="64" height="64"></td>
		<td class="mark1" style="width:520px;" align=left>智慧景区游客流量管理系统</td>
		<td>&nbsp;</td>
		<td class="mark3" style="width:300px;" valign=bottom align=right onclick="refresh();">杭州酥游科技有限公司</td>
	</tr>
</table>
</div>
<div data-options="region:'center'" >
<p style="padding-left:15px;">截止<%= bean.nowCurTime() %>，<span class="font24"><%= bean.getSpotName() %></span> 累计游客进入：<span class="font24"><%= bean.sumIn() %></span> 人，离开：<span class="font24"><%= bean.sumOut() %></span> 人，留存：<span class="font24"><%= bean.sumStay() %></span> 人</p>
<table width="98%" border="0" cellpadding="1">
<%
	//每行4个，
	int rows = cmrCount/4;
	if(cmrCount % 4 != 0)
		rows++;
	//现在相机的编号
	int curCmrNo = 0;
	for(int i=0;i<rows;i++){
		//每行四个相机的
		String[] snArray = new String[4];
		snArray[0] = curCmrNo<=cmrCount-1?bean.cmrSN(curCmrNo):null;
		curCmrNo++;
		snArray[1] = curCmrNo<=cmrCount-1?bean.cmrSN(curCmrNo):null;
		curCmrNo++;
		snArray[2] = curCmrNo<=cmrCount-1?bean.cmrSN(curCmrNo):null;
		curCmrNo++;
		snArray[3] = curCmrNo<=cmrCount-1?bean.cmrSN(curCmrNo):null;
		curCmrNo++;
%>
  <tr>
  	<%
  		for(int j=0;j<4;j++){
  			if(snArray[j]==null){
  				out.print("<td width=\"25%\">&nbsp;</td>");
  				continue;
  			}
  			String sn1 = snArray[j];
  	%>
    <td width="25%" align="center">
	    <table width="100%" border="0" cellpadding="1">
	      <tr>
	        <td align="center"><a href="<%= bean.cmrPicOri(sn1) %>" target="_blank"><img src="<%= bean.cmrPicUrl(sn1) %>" width="240" height="135" /></a></td>
	      </tr>
	      <tr>
	        <td align="center"><%= bean.cmrName(sn1) %> 【<a href="#" onclick="fnLiveStream('<%= sn1 %>');">播放</a>】</td>
	      </tr>
	      <tr>
	        <td align="center">累计进入：<%= bean.cmrSumIn(sn1) %>人次</td>
	      </tr>
	      <tr>
	        <td align="center">累计离开：<%= bean.cmrSumOut(sn1) %>人次</td>
	      </tr>
	    </table>
    </td>
    <%
  		}
    %>
  </tr>
<%
	}
%>
</table>
<p>&nbsp;</p>
<div align="center" id="chart1" style="width:90%;">

</div>
</div>
<div data-options="region:'south',border:false" style="height:35px;padding-top:8px;">
<table width="90%" border="0" cellspacing="0" align="center">
	<tr>
    <td align="center">版权所有：杭州酥游科技有限公司&nbsp;&nbsp;版本：<%= com.xie.spot.sys.VersionInfo.VERSION_NO %></td>
  </tr>
</table>
</div>
</body>
</html>
<%}%>