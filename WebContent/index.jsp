<%@page import="ssin.swing.SysOutFrame"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="com.xie.spot.entity.AdvtPicture" %>
<%@page import="java.util.List" %>
<%
List<AdvtPicture> advtPicList = (List<AdvtPicture>)request.getAttribute("advtPicList");
if(advtPicList == null){
	//out.println("无效地址，请点击进入 <a href=\"home\">酥油空间</a>");
%>
<script type="text/javascript">
location.href="home";
</script>
<%
}else{
	
%>
<%
//默认的城市是：浙江->杭州
	String dftCityName = "杭州";

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title><%= com.xie.spot.sys.VersionInfo.pageTitle %></title>
	<link rel="stylesheet" href="css/reset.css" />
	<link rel="stylesheet" href="css/index.css" />
	<script src="easyui/jquery.min.js" type="text/javascript"></script>
	<script type="text/javascript" src="js/slide.js"></script>
	<!--  <script type="text/javascript" src="js/myLv2.js"></script>-->
	<script src="js/index.js" type="text/javascript"></script>

<script type="text/javascript">
var dftCityName = '<%= dftCityName %>';
</script>
</head>
<body>


<div class="topBar">
	<div class="w clearfix">
		<div class="fr">
			<b>
				［IP地址归属：<span id="iploc">浙江-><%= dftCityName %></span>］
			</b>
			<!--  <a href="####">[选城市］</a> -->
		</div>
		<span class="fl">
			欢迎来到酥游空间！ <!--  <a href="####">请登录</a> | <a href="####">注册</a> -->
		</span>
	</div>
</div>



<div class="header">
	<div class="w clearfix pr">
		<a class="logo fl" href="####">公司名称</a>
		<div class="txt"></div>
		<div class="ma">
			<img src="images/ma.jpg" alt=""/>
			<p>
				扫一扫添加APP
			</p>
		</div>
	</div>
</div>




<div class="searchBar">
	<div class="w clearfix">
		<div class="tianqi fr">
			<a id="tq_cityname" class="city" href="####"><%= dftCityName %></a>
			<span class="icon-tq">
				<img id="tq_wthpic" src="icons/icon128/overcast.png" width="48" height="48" alt=""/>
			</span>
			<span id="tq_wendu" class="wendu">
				--℃
			</span>
		</div>
		<div class="inputBox fl">
			<div class="area fl">
				<h3><strong id="area" data-id="11"><%= dftCityName %></strong></h3>
				<div class="areaAll">
					<div class="inner">
						<div class="sc">
							<h4>收藏城市</h4>
							<p>
								<a href="####" data-id="1">北京</a> <a href="####" data-id="2">杭州</a>  

							</p>
						</div>
						<!--  
						<div class="other">
							<p>

								<span class="t">
									A-G
								</span>
								<a href="####" data-id="1">全国</a> <a href="####" data-id="2">全国</a>  <a href="####" data-id="1">全国</a>  <a href="####" data-id="1">全国</a> <a href="####" data-id="1">全国</a>
								<a href="####" data-id="1">全国</a> <a href="####" data-id="2">全国</a>  <a href="####" data-id="1">全国</a>  <a href="####" data-id="1">全国</a> <a href="####" data-id="1">全国</a>
								<a href="####" data-id="1">全国</a> <a href="####" data-id="2">全国</a>  <a href="####" data-id="1">全国</a> <a href="####" data-id="1">全国</a>

							</p>

							<p>

								<span class="t">
									H-N
								</span>
								<a href="####" data-id="1">全国</a> <a href="####" data-id="2">全国</a>  <a href="####" data-id="1">全国</a>  <a href="####" data-id="1">全国</a> <a href="####" data-id="1">全国</a>
								<a href="####" data-id="1">全国</a> <a href="####" data-id="2">全国</a>  <a href="####" data-id="1">全国</a>  <a href="####" data-id="1">全国</a> <a href="####" data-id="1">全国</a>
								<a href="####" data-id="1">全国</a> <a href="####" data-id="2">全国</a>  <a href="####" data-id="1">全国</a> <a href="####" data-id="1">全国</a>

							</p>

							<p>

								<span class="t">
									P-T
								</span>
								<a href="####" data-id="1">全国</a> <a href="####" data-id="2">全国</a>  <a href="####" data-id="1">全国</a>  <a href="####" data-id="1">全国</a> <a href="####" data-id="1">全国</a>
								<a href="####" data-id="1">全国</a> <a href="####" data-id="2">全国</a>  <a href="####" data-id="1">全国</a>  <a href="####" data-id="1">全国</a> <a href="####" data-id="1">全国</a>
								<a href="####" data-id="1">全国</a> <a href="####" data-id="2">全国</a>  <a href="####" data-id="1">全国</a> <a href="####" data-id="1">全国</a>

							</p>

							<p>

								<span class="t">
									W-Z
								</span>
								<a href="####" data-id="1">全国</a> <a href="####" data-id="2">全国</a>  <a href="####" data-id="1">全国</a>  <a href="####" data-id="1">全国</a> <a href="####" data-id="1">全国</a>
								<a href="####" data-id="1">全国</a> <a href="####" data-id="2">全国</a>  <a href="####" data-id="1">全国</a>  <a href="####" data-id="1">全国</a> <a href="####" data-id="1">全国</a>
								<a href="####" data-id="1">全国</a> <a href="####" data-id="2">全国</a>  <a href="####" data-id="1">全国</a> <a href="####" data-id="1">全国</a>

							</p>
						</div>
						-->
					</div>
				</div>
			</div>
			<input id="keyword_spotname" class="txt fl" type="text" value="请输入景区名字"  onfocus="if(this.value=='请输入景区名字') this.value=''" onblur="if(this.value=='') this.value='请输入景区名字'" />
		</div>
		<input type="button" class="submit fl" value="" onClick="schIsRcmd=false;schPage=1;fnSearchSpot();" />
	</div>
</div>








<div class="w banner">
	<a class="prev btn" href="javasript:;"></a>
	<a class="next btn" href="javasript:;"></a>
	<div class="bd">
		<ul>
		<%
			for(int i=0;i<5;i++){
				AdvtPicture apo = advtPicList.get(i);
		%>
			<li>
				<a href="<%= apo.getLinkUrl() %>" <%= apo.getLinkUrl().equals("####")?"":"target=\"_blank\"" %>>
					<img src="<%= apo.getUrl() %>" alt="" width="1000" height="400"/>
				</a>
			</li>
		<%
			}
		%>
		</ul>
	</div>
	<div class="hd">
		<ul>
			
		</ul>
	</div>
</div>




<div id="div_spotmain" class="main w">
	<div id="div_spotview" class="listMain clearfix">
		
	</div>

	<div class="page">
			每页数量
		<select name="" id="sch_size">
			<option value="9">9</option>
			<option value="12" selected>12</option>
			<option value="15">15</option>
			<option value="18">18</option>
			<option value="21">21</option>
			<option value="24">24</option>
		</select>
		<!-- <a href="####">1</a><a href="####">2</a><a href="####">3</a> -->
		<a href="####" onClick="fnToFirstPage();">首页</a><a href="####" onClick="fnToPrePage();">上一页</a><span id="sch_pageinfo">[ 1 / 1 ]</span><a href="####" onClick="fnToNextPage();">下一页</a><a href="####" onClick="fnToLastPage();">末页</a>
	</div>
</div>



<div class="footer">
	<div class="w">
		<p>
			<a href="####">关于我们</a> | <a href="####">联系我们</a> ｜ <span>2015 @ 版权所有XXXXXX</span> ｜ <a href="mng_index.jsp" target="_blank">[管理入口]</a>
		</p>
	</div>
</div>





<div class="smallPics">
	<span class="leftTop">
		<img src="images/car.png" alt="" width="133" height="124"/>
	</span>
	<span class="rightTop">
		<img src="images/huoche.png" alt="" width="135" height="128"/>
	</span>
	<span class="leftBtm">
		<img src="images/chuan.png" alt="" width="126" height="122"/>
	</span>
	<span class="rightBtm">
		<img src="images/air.png" alt="" width="135" height="124"/>
	</span>
</div>


</body>
</html>
<%
}
%>