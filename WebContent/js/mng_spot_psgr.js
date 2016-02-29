var formatFlow = function(value,row,index){
	return value+' 人/小时';
};

var fnCdShow = function(cd){
	if(cd>=90)
		return '很多';
	else if(cd>=70)
		return '较多';
	else if(cd>=50)
		return '适中';
	else if(cd>=20)
		return '较少';
	else
		return '很少';
};

var formatCD = function(value,row,index){
	return value+'%, '+fnCdShow(value);
};

var fnSearch = function(refresh){
	$('#dg1').datagrid(refresh!=null?'reload':'load',{
		spotCity:$('#kw_city').combobox('getText'),
		spotName:$('#kw_spotname').val()
	});
};

//查询区域的省份选中
var fnSelectAKWProvince = function(record){
	$('#kw_city').combobox('reload','comboCity');
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
	$.messager.confirm('Confirm','确实要删除选择的记录吗？',function(r){
		if(!r)
			return;
		$.post('deleteSpotPassengerFlow',{
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
	
	$('#kw_province').combobox({
		onLoadSuccess:function(){
			var loadedData = $('#kw_province').combobox('getData');
			if(loadedData==null || loadedData.length==0)
				return;
			$('#kw_province').combobox('setValue',loadedData[0].value);
			fnSelectAKWProvince(loadedData[0]);
		},
		onSelect:fnSelectAKWProvince
	});
	
	$('#kw_city').combobox({
		onBeforeLoad:function(param){
			param.province=$('#kw_province').combobox('getValue');
		},
		onLoadSuccess:function(){
			var loadedData = $('#kw_city').combobox('getData');
			if(loadedData==null || loadedData.length==0)
				return;
			$('#kw_city').combobox('setValue',loadedData[0].value);
			fnSearch();
		},
		onSelect:function(record){
			fnSearch();
		}
	});
	
	$('#kw_spotname').keypress(function(e){
		if(e.which == 13){
			fnSearch();
		}
	});
	
	setTimeout(function(){
		$('#kw_province').combobox('reload','comboProvince');
	},100);
	
	$('#dg1').datagrid('getPager').pagination({
		displayMsg:'{from}~{to} of {total}'
	});
});