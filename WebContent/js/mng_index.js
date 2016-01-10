var fnTest1 = function(){
	$.post('wechat',{
		keyword:'山'
	},function(data){
		var str = JSON.stringify(data);
		alert(str);
		$('#txt_username').val(str);
	});
};

var fnLogin = function(){
	if($('#txt_username').val() == ''){
		alert('请输入管理员帐号');
		$('#txt_username').focus();
		return;
	}
	if($('#txt_password').val() == ''){
		alert('请输入管理密码号');
		$('#txt_password').focus();
		return;
	}
	$.post('login',{username:$('#txt_username').val(),
					password:$('#txt_password').val()},
			function(data){
				if(data.succ){
					location.href='mng_main.jsp';
				}else{
					alert(data.stmt);
					$('#txt_password').select();
				}
	});
};

$(function(){
	$('#txt_username').focus();
	$('#txt_username').keypress(function(e){
		if(e.which == 13){
			$('#txt_password').focus();
		}
	});
	$('#txt_password').keypress(function(e){
		if(e.which == 13){
			fnLogin();
		}
	});
});