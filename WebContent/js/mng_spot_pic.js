var formatUrl = function(value,row,index){
	return '<img src="'+value+'" width="260" height="148" />';
};

var fnSearch = function(refresh){
	if(curSpotId == 0)
		return;
	$('#dg1').datagrid(refresh!=null?'reload':'load',{
		spotId:curSpotId
	});
};

/*var fnGetTopSelSpot = function(){
	if(typeof(window.parent.fnGetTopSelSpot) != 'function')
		return;
	return window.parent.fnGetTopSelSpot();
};*/

//重新从main中获得当前景点的信息
/*var curSpot = null;
var fnReloadCurSpot = function(){
	curSpot = fnGetTopSelSpot();
	if(curSpot == null)
		return;
	$('#divCenter').panel('setTitle','景点图片列表 [当前景点：'+curSpot.city.province+'->'+curSpot.city.name+'：'+curSpot.name+']');
	fnSearch();
};*/

var fnAdd = function(){
	if(curSpotId == 0){
		$.messager.alert('Information','当前景点未设定');
		return;
	}
	if($('#url').val() == ''){
		$.messager.alert('Information','请设置景点图片');
		return;
	}
	
	$.messager.confirm('Confirm','确实要新增图片吗？',function(r){
		if(!r)
			return;
		$.post('addSpotPicture',{
			spotId:curSpotId,
			url:$('#url').val(),
			mainRcmd:$('#mainRcmd').numberspinner('getValue')
		},function(data){
			if(data.succ)
				fnSearch('refresh');
			else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
};

var fnModify = function(){
	var selRowData = $('#dg1').datagrid('getSelected');
	if(selRowData == null){
		$.messager.alert('Information','请选择要修改的景点图片记录');
		return;
	}
	$.messager.confirm('Confirm','确实要设置该图片的推荐值吗？',function(r){
		if(!r)
			return;
		$.post('modifySpotPicture',{
			id:selRowData.id,
			mainRcmd:$('#mainRcmd').numberspinner('getValue')
		},function(data){
			if(data.succ)
				fnSearch('refresh');
			else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
};

var fnDelete = function(){
	var array = $('#dg1').datagrid('getSelections');
	if(array == null || array.length == 0){
		$.messager.alert('Information','请选择要删除的记录');
		return;
	}
	var ids = '';
	for(var i=0;i<array.length;i++){
		if(i>0)
			ids += '_';
		ids += array[i].id+'';
	}
	$.messager.confirm('Confirm','确实要删除选择的图片吗？',function(r){
		if(!r)
			return;
		$.post('deleteSpotPicture',{
			ids:ids
		},function(data){
			if(data.succ)
				fnSearch('refresh');
			else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
};

var fnReset = function(){
	$('#url').val('');
	$('#imgPreview').attr('src','');
	$('#mainRcmd').numberspinner('setValue',0);
};

var fnImgPreview = function(){
	$('#imgPreview').attr('src',$('#url').val());
};

var fnSubmitForm = function(){
	if(curSpotId == 0){
		$.messager.alert('Information','缺少当前景点信息，请首先设置');
		return;
	}
	
	if($('#fileToUpload').val() == ''){
		$.messager.alert('Information','请选择一个要上传的数据文件');
		return;
	}
	$.messager.confirm('Confirm','确实要上传该文件吗？',function(r){
		if(!r)
			return;
		//打开进度条提示
		$.messager.progress();
		$('#form1').submit();
	});
};

$(function(){
	$('#form1').ajaxForm();
	
	$('#form1').submit(function(){
		$(this).ajaxSubmit({
			type: "post",
			url:'justFileUpload',
			dataType:'json',
			success:function(data){
				//关闭进度条
				$.messager.progress('close');
				if(data.succ){
					$('#url').val(data.stmt);
					fnImgPreview();
				}else{
					alert('上传失败: '+data.stmt);
				}
			},
			error: function(e){
				$.messager.progress('close');
				alert(e);
			}
		});
		return false;
	});
	
	setTimeout(function(){
		fnSearch();
	},100);
	
	$('#dg1').datagrid('getPager').pagination({
		displayMsg:'{from}~{to} of {total}'
	});
});