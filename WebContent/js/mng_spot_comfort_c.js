var fnSearch = function(refresh){
	if(curSpotId == 0)
		return;
	$('#dg1').datagrid(refresh!=null?'reload':'load',{
		spotId:curSpotId
	});
};

var fnReset = function(){
	$('#seasonFactor').val('');
	$('#weatherFactor').val('');
	$('#tempFactor').val('');
	$('#passengerFactor').val('');
	$('#mark').val('');
	
	$('#seasonScore').numberspinner('setValue',0);
	$('#weatherScore').numberspinner('setValue',0);
	$('#tempScore').numberspinner('setValue',0);
	$('#passengerScore').numberspinner('setValue',0);
};

//新增、修改的时候，判断是否正确
var fnAMValid = function(){
	if(curSpotId == 0){
		$.messager.alert('Information','当前景点未设定');
		return false;
	}
	if($('#seasonFactor').val()=='' && $('#weatherFactor').val()=='' && $('#tempFactor').val()=='' && $('#passengerFactor').val()==''){
		$.messager.alert('Information','至少设定一个修正因子');
		return false;
	}
	if($('#seasonFactor').val()!='' && $('#seasonScore').numberspinner('getValue')==0){
		$.messager.alert('Information','请设置 季节 因子对舒适度的调整值');
		return false;
	}
	if($('#weatherFactor').val()!='' && $('#weatherScore').numberspinner('getValue')==0){
		$.messager.alert('Information','请设置 天气 因子对舒适度的调整值');
		return false;
	}
	if($('#tempFactor').val()!='' && $('#tempScore').numberspinner('getValue')==0){
		$.messager.alert('Information','请设置 温度 因子对舒适度的调整值');
		return false;
	}
	if($('#passengerFactor').val()!='' && $('#passengerScore').numberspinner('getValue')==0){
		$.messager.alert('Information','请设置 客流 因子对舒适度的调整值');
		return false;
	}
	return true;
};

var fnAdd = function(){
	if(!fnAMValid())
		return;
	$.messager.confirm('Confirm','确实要新增该景点的修正因子吗？',function(r){
		if(!r)
			return;
		$.post('addModifySpotCC',{
			spotId:curSpotId,
			seasonFactor:$('#seasonFactor').val(),
			seasonScore:$('#seasonScore').numberspinner('getValue'),
			weatherFactor:$('#weatherFactor').val(),
			weatherScore:$('#weatherScore').numberspinner('getValue'),
			tempFactor:$('#tempFactor').val(),
			tempScore:$('#tempScore').numberspinner('getValue'),
			passengerFactor:$('#passengerFactor').val(),
			passengerScore:$('#passengerScore').numberspinner('getValue'),
			mark:$('#mark').val()
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
		$.messager.alert('Information','请选择要修改的记录');
		return;
	}
	if(!fnAMValid())
		return;
	$.messager.confirm('Confirm','确实要修改该景点的修正因子吗？',function(r){
		if(!r)
			return;
		$.post('addModifySpotCC',{
			id:selRowData.id,
			spotId:curSpotId,
			seasonFactor:$('#seasonFactor').val(),
			seasonScore:$('#seasonScore').numberspinner('getValue'),
			weatherFactor:$('#weatherFactor').val(),
			weatherScore:$('#weatherScore').numberspinner('getValue'),
			tempFactor:$('#tempFactor').val(),
			tempScore:$('#tempScore').numberspinner('getValue'),
			passengerFactor:$('#passengerFactor').val(),
			passengerScore:$('#passengerScore').numberspinner('getValue'),
			mark:$('#mark').val()
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
	$.messager.confirm('Confirm','确实要删除选择的修正因子吗？',function(r){
		if(!r)
			return;
		$.post('deleteSpotCC',{
			ids:ids
		},function(data){
			if(data.succ)
				fnSearch('refresh');
			else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
};

var fnClickRow = function(rowIndex,rowData){
	$('#seasonFactor').val(rowData.seasonFactor);
	$('#weatherFactor').val(rowData.weatherFactor);
	$('#tempFactor').val(rowData.tempFactor);
	$('#passengerFactor').val(rowData.passengerFactor);
	$('#mark').val(rowData.mark);
	
	$('#seasonScore').numberspinner('setValue',rowData.seasonScore);
	$('#weatherScore').numberspinner('setValue',rowData.weatherScore);
	$('#tempScore').numberspinner('setValue',rowData.tempScore);
	$('#passengerScore').numberspinner('setValue',rowData.passengerScore);
};

$(function(){
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