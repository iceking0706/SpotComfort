var formatUrl = function(value,row,index){
	return '<img src="'+value+'" width="260" height="148" />';
};

var formatLinkUrl = function(value,row,index){
	if(value=='####')
		return '----';
	return '<a href="'+value+'" target="_blank">预览</a>';
};

var fnSearch = function(refresh){
	$('#dg1').datagrid(refresh!=null?'reload':'load',{});
};

var fnClickRow = function(rowIndex,rowData){
	$('#url').val(rowData.url);
	$('#mark').val(rowData.mark);
	$('#linkUrl').val(rowData.linkUrl);
	$('#mainRcmd').numberspinner('setValue',rowData.mainRcmd);
	fnImgPreview();
};

var fnAdd = function(){
	if($('#url').val() == ''){
		$.messager.alert('Information','请上传或指定图片地址');
		return;
	}
	if($('#mark').val() == ''){
		$.messager.alert('Information','请图片备注');
		return;
	}
	
	$.messager.confirm('Confirm','确实要新增图片吗？',function(r){
		if(!r)
			return;
		$.post('addAdvtPicture',{
			url:$('#url').val(),
			mainRcmd:$('#mainRcmd').numberspinner('getValue'),
			mark:$('#mark').val(),
			linkUrl:$('#linkUrl').val()
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
		$.messager.alert('Information','请选择要修改的图片记录');
		return;
	}
	$.messager.confirm('Confirm','确实要修改该图片吗？',function(r){
		if(!r)
			return;
		$.post('modifyAdvtPicture',{
			id:selRowData.id,
			url:$('#url').val(),
			mainRcmd:$('#mainRcmd').numberspinner('getValue'),
			mark:$('#mark').val(),
			linkUrl:$('#linkUrl').val()
		},function(data){
			if(data.succ)
				fnSearch('refresh');
			else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
};

var fnDelete = function(){
	var selRowData = $('#dg1').datagrid('getSelected');
	if(selRowData == null){
		$.messager.alert('Information','请选择要删除的图片记录');
		return;
	}
	
	$.messager.confirm('Confirm','确实要删除选择的图片吗？',function(r){
		if(!r)
			return;
		$.post('deleteAdvtPicture',{
			id:selRowData.id
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
	$('#mark').val('');
	$('#linkUrl').val('');
};

var fnImgPreview = function(){
	$('#imgPreview').attr('src',$('#url').val());
};

var fnSubmitForm = function(){
	
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
	
	//表格单击事件
	$('#dg1').datagrid({
		onClickRow:fnClickRow,
	});
	
	setTimeout(function(){
		fnSearch();
	},100);
	
	$('#dg1').datagrid('getPager').pagination({
		displayMsg:'{from}~{to} of {total}'
	});
});