var formatTemp = function(value,row,index){
	return value+'℃';
};
var formatHum = function(value,row,index){
	return value+'%';
};
var formatAqi = function(value,row,index){
	return value+' '+row.aqiShow;
};

var fnSearch = function(refresh){
	$('#dg1').datagrid(refresh!=null?'reload':'load',{
		wcode:$('#kw_wcode').val()
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
	$.messager.confirm('Confirm','确实要删除选择的天气记录吗？',function(r){
		if(!r)
			return;
		$.post('deleteCodeWeather',{
			ids:ids
		},function(data){
			if(data.succ)
				fnSearch('refresh');
			else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
};

$(function(){
	
	$('#kw_wcode').keypress(function(e){
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