//格式，yyyy-MM-dd
var fnFormatter_DateTime = function(date){
	var y = date.getFullYear();
	var m = date.getMonth()+1;
	var d = date.getDate();
	//var h = date.getHours();
	//var mi = date.getMinutes();
	//var ss = date.getSeconds();
	//return y+'-'+(m<10?'0'+m:m)+'-'+(d<10?'0'+d:d)+' '+(h<10?'0'+h:h)+':'+(mi<10?'0'+mi:mi)+':'+(ss<10?'0'+ss:ss);
	return y+'-'+(m<10?'0'+m:m)+'-'+(d<10?'0'+d:d);
};

var formatSnMark= function(value,row,index){
	return value+(row.cmrMark!=''?' ('+row.cmrMark+')':'');
};

var formatType= function(value,row,index){
	switch(value){
	case 1:
		return '长时间脱机';
	case 2:
		return '时钟偏差';
	default:
		return '未知';
	}
};

var formatProccessed = function(value,row,index){
	if(!value)
		return '<font color="#ff0000">未处理</font>';
	return '已处理: '+row.prsMark+'<br/>'+row.prsTimeShow;
};

var formatSendMailed = function(value,row,index){
	return value?'已发送':'未发送';
};

var removeAllRowsOfGrid = function(){
	var grid = $('#dg1');
	while(grid.datagrid('getRows')!=null && grid.datagrid('getRows').length>0){
		grid.datagrid('deleteRow',0);
	}
};

var fnSearch = function(refresh){
	removeAllRowsOfGrid();
	$('#dg1').datagrid(refresh!=null?'reload':'load',{
		sn:$('#kw_sn').val(),
		type:$('#kw_type').val(),
		processed:$('#kw_processed').val(),
		startTime:$('#search_dt_start').datetimebox('getValue'),
		endTime:$('#search_dt_end').datetimebox('getValue')
	});
};

var fnDelete = function(){
	var array = $('#dg1').datagrid('getSelections');
	if(array == null || array.length == 0){
		$.messager.alert('Information','请选择要操作的记录');
		return;
	}
	var ids = '';
	for(var i=0;i<array.length;i++){
		if(i>0)
			ids += '_';
		ids += array[i].id+'';
	}
	$.messager.confirm('Confirm','确实要删除数据吗？',function(r){
		if(!r)
			return;
		$.post('deleteCameraAlert',{
			ids:ids
		},function(data){
			if(data.succ)
				fnSearch('refresh');
			else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
};

//打开填写处理内容的窗口
var fnProcess = function(){
	var selRowData = $('#dg1').datagrid('getSelected');
	if(selRowData == null){
		$.messager.alert('Information','请选择一条记录');
		return;
	}
	if(selRowData.processed){
		$('#prsMark').val(selRowData.prsMark);
	}else{
		$('#prsMark').val('知道了');
	}
	
	$('#dlg_alert_process').dialog('open');
};

//处理内容填写完毕后的提交
var fnSubmitProcess = function(){
	if($('#prsMark').val() == ''){
		$.messager.alert('Information','请填写处理内容');
		return;
	}
	var array = $('#dg1').datagrid('getSelections');
	if(array == null || array.length == 0){
		$.messager.alert('Information','请选择要操作的记录');
		return;
	}
	var ids = '';
	for(var i=0;i<array.length;i++){
		if(i>0)
			ids += '_';
		ids += array[i].id+'';
	}
	
	$.post('processCameraAlert',{
		ids:ids,
		prsMark:$('#prsMark').val()
	},function(data){
		if(data.succ){
			$('#dlg_alert_process').dialog('close');
			fnSearch('refresh');
		}else
			$.messager.alert('Information','失败：'+data.stmt);
	});
};

$(function(){
	$('#kw_sn').keypress(function(e){
		if(e.which == 13){
			fnSearch();
		}
	});
	
	setTimeout(function(){
		fnSearch();
	},100);
	
	$('#dg1').datagrid('getPager').pagination({
		displayMsg:'{from}~{to} of {total}'
	});
});
