var fnLogout = function(){
	$.messager.confirm('Confirm','确实要退出吗？',function(r){
		if(!r)
			return;
		$.post('logout',function(){
			location.href='mng_index.jsp';
		});
	});
};

//修改密码 弹出
var fnOpenDlg_modpass = function(){
	$('#dlg_modpass').dialog('open');
};

//修改密码 执行操作
var fnSubmit_modpass = function(){
	if($('#dlg_modpass_oldPass').val() == ''){
		$.messager.alert('Information','请输入原密码');
		$('#dlg_modpass_oldPass').focus();
		return;
	}
	if($('#dlg_modpass_newPass').val() == ''){
		$.messager.alert('Information','请输入新密码');
		$('#dlg_modpass_newPass').focus();
		return;
	}
	if($('#dlg_modpass_newPass').val() != $('#dlg_modpass_newPass2').val()){
		$.messager.alert('Information','两次新密码输入不一致');
		$('#dlg_modpass_newPass2').focus();
		return;
	}
	if($('#dlg_modpass_oldPass').val() == $('#dlg_modpass_newPass').val()){
		$.messager.alert('Information','新密码与原密码一样，无需修改');
		$('#dlg_modpass_newPass').focus();
		return;
	}
	$.messager.confirm('Confirm','确实要执行密码修改操作吗？',function(r){
		if(!r)
			return;
		$.post('modifypass',{
			oldPass:$('#dlg_modpass_oldPass').val(),
			newPass:$('#dlg_modpass_newPass').val()
		},function(data){
			if(data.succ){
				alert('密码修改成功，请重新登入');
				$.post('logout',function(){
					location.href='mng_index.jsp';
				});
			}else{
				$.messager.alert('Information','密码修改失败：'+data.stmt);
			}
		});
	});
};

//当前tab的数量
var tabSize = 0;

//使用js来控制iframe的跳转
var fnIframeToPage = function(url,title){
	var curTabTitle = title!=null?title:url;
	if($('#tabs').tabs('exists',curTabTitle)){
		//已经存在，则选中
		$('#tabs').tabs('select',curTabTitle);
	}else{
		//不存在，则添加新的
		var curTabContent = '<iframe width="99%" height="99%" src="'+(url.indexOf('?')!=-1?url+'&rdm='+Math.random():url+'?rdm='+Math.random())+'"></iframe>';
		$('#tabs').tabs('add',{
			id:url,
			title:curTabTitle,
			content:curTabContent,
			closable:true
		});
	}
};

//删除全部的tab页面
var fnDelAllTabs = function(){
	if(tabSize==0)
		return;
	$.messager.confirm('Confirm','确实要关闭当前打开的全部页面吗？',function(r){
		if(!r)
			return;
		while(tabSize>0){
			$('#tabs').tabs('close',0);
		}
	});
};

//刷新当前tab的页面
var fnRefreshCurTab = function(){
	if(tabSize==0)
		return;
	var tab = $('#tabs').tabs('getSelected');
	if(tab == null)
		return;
	$.messager.confirm('Confirm','确实要重新加载当前页面吗？',function(r){
		if(!r)
			return;
		var tabOps = tab.panel('options');
		//执行更新
		var curURL = tabOps.id;
		var curTabContent = '<iframe width="99%" height="99%" src="'+(curURL.indexOf('?')!=-1?curURL+'&rdm='+Math.random():curURL+'?rdm='+Math.random())+'"></iframe>';
		$('#tabs').tabs('update',{
			tab:tab,
			options:{
				id:curURL,
				title:tabOps.title,
				content:curTabContent
			}
		});
	});
};

//获得ip地址的归属地
var fnFetchIPLocation = function(){
	//返回结果，var remote_ip_info = {"ret":1,"start":-1,"end":-1,"country":"\u4e2d\u56fd","province":"\u5317\u4eac","city":"\u5317\u4eac","district":"","isp":"","type":"","desc":""};
	$.getScript('http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js', function(response,status){  
		if(status=='success'){
			$('#iploc').text(remote_ip_info.province+'->'+remote_ip_info.city);
		}
	}); 
};

//当前选择的景点，其它几个界面都需要调用的到
var topSelSpot = null;

var fnSetTopSelSpot = function(spot){
	topSelSpot = spot;
};
var fnGetTopSelSpot = function(){
	return topSelSpot;
};

$(function(){
	fnFetchIPLocation();
	
	$('#tabs').tabs({
		onAdd:function(title,index){
			tabSize++;
		},
		onClose:function(title,index){
			tabSize--;
		}
	});
	
	//fnIframeToPage('mng_spot.jsp','景点信息');
	fnIframeToPage('mng_camera_cfg.jsp','相机配置');
	
	setInterval(function(){
		$.get('keepOnline');
	},180000);
});