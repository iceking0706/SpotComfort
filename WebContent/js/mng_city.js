var formatUrl = function(value,row,index){
	return '<img src="'+value+'" width="260" height="148" />';
};

var fnSearch = function(refresh){
	$('#dg1').datagrid(refresh!=null?'reload':'load',{
		province:$('#kw_province').val(),
		city:$('#kw_city').val()
	});
};

var fnClickRow = function(rowIndex,rowData){
	$('#id').val(rowData.id);
	$('#name').val(rowData.name);
	$('#wcode').val(rowData.wcode);
	$('#province').val(rowData.province);
	$('#pinyin').val(rowData.pinyin);
	$('#url').val(rowData.picUrl);
	$('#rcmd').numberspinner('setValue',rowData.rcmd);
	fnImgPreview();
};

var fnAdd = function(){
	if($('#name').val() == ''){
		$.messager.alert('Information','请填写 城市名称');
		$('#name').focus();
		return;
	}
	if($('#province').val() == ''){
		$.messager.alert('Information','请填写 所属省份');
		$('#province').focus();
		return;
	}
	$.messager.confirm('Confirm','确实要新增城市： '+$('#province').val()+'->'+$('#name').val()+' 吗？',function(r){
		if(!r)
			return;
		$.post('addCity',{
			name:$('#name').val(),
			wcode:$('#wcode').val(),
			province:$('#province').val(),
			pinyin:$('#pinyin').val(),
			picUrl:$('#url').val(),
			rcmd:$('#rcmd').numberspinner('getValue')
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
		$.messager.alert('Information','请选择要修改的城市记录');
		return;
	}
	if($('#name').val() == ''){
		$.messager.alert('Information','请填写 城市名称');
		$('#name').focus();
		return;
	}
	if($('#province').val() == ''){
		$.messager.alert('Information','请填写 所属省份');
		$('#province').focus();
		return;
	}
	$.messager.confirm('Confirm','确实要修改城市： '+$('#province').val()+'->'+$('#name').val()+' 吗？',function(r){
		if(!r)
			return;
		$.post('modifyCity',{
			name:$('#name').val(),
			wcode:$('#wcode').val(),
			province:$('#province').val(),
			pinyin:$('#pinyin').val(),
			picUrl:$('#url').val(),
			rcmd:$('#rcmd').numberspinner('getValue'),
			id:selRowData.id
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
		$.messager.alert('Information','请选择要删除的城市记录');
		return;
	}
	$.messager.confirm('Confirm','确实要删除城市： '+selRowData.province+'->'+selRowData.name+' 吗？',function(r){
		if(!r)
			return;
		$.post('deleteCity',{
			id:selRowData.id
		},function(data){
			if(data.succ)
				fnSearch('refresh');
			else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
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
	
	$('#kw_province').keypress(function(e){
		if(e.which == 13){
			fnSearch();
		}
	});
	$('#kw_city').keypress(function(e){
		if(e.which == 13){
			fnSearch();
		}
	});
	//表格单击事件
	$('#dg1').datagrid({
		onClickRow:fnClickRow
	});
	setTimeout(function(){
		fnSearch();
	},100);
	
	$('#dg1').datagrid('getPager').pagination({
		displayMsg:'{from}~{to} of {total}'
	});
});