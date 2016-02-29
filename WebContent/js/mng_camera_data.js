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

//得到缩略图的jpg路径
var fnJpgMin = function(url){
	if(url==null || url=='')
		return '';
	if(url.substring(url.length-4)!='.jpg')
		return url;
	return url.substring(0,url.length-4)+'_min.jpg';
};

var formatUrl = function(value,row,index){
	return '<a href="'+value+'" target="_blank"><img src="'+fnJpgMin(value)+'" width="260" height="148" /></a>';
};

var fnSearch = function(refresh){
	$('#dg1').datagrid(refresh!=null?'reload':'load',{
		sn:$('#kw_sn').val(),
		startTime:$('#search_dt_start').datetimebox('getValue'),
		endTime:$('#search_dt_end').datetimebox('getValue')
	});
};

var fnDelete = function(){
	var selRowData = $('#dg1').datagrid('getSelected');
	if(selRowData == null){
		$.messager.alert('Information','请选择一条记录');
		return;
	}
	$.messager.confirm('Confirm','确实要删除数据吗？',function(r){
		if(!r)
			return;
		$.post('deleteCameraData',{
			id:selRowData.id
		},function(data){
			if(data.succ)
				fnSearch('refresh');
			else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
};

//查看详情，开业看到原始数据记录
var fnViewRawData = function(){
	var selRowData = $('#dg1').datagrid('getSelected');
	if(selRowData == null){
		$.messager.alert('Information','请选择一条记录');
		return;
	}
	
	$.post('viewCameraDataInfo',{
		id:selRowData.id
	},function(data){
		if(data.succ){
			$('#dlg_data_view_td_picTime').html('图片时间：'+data.picTime);
			$('#dlg_data_view_td_picUrl').attr('src',data.picUrl);
			if(data.rows != null && data.rows.length>0){
				var tmphtml = '';
				for(var i=0;i<data.rows.length;i++){
					tmphtml+='<br />'+data.rows[i].timeShow+', In: '+data.rows[i].inData+', Out: '+data.rows[i].outData;
				}
				$('#dlg_data_view_td_rows').html(tmphtml);
			}else{
				$('#dlg_data_view_td_rows').html('&nbsp;');
			}
			$('#dlg_data_view').dialog('open');
		}else
			$.messager.alert('Information','失败：'+data.stmt);
	});
};

var fnViewBig = function(){
	var selRowData = $('#dg1').datagrid('getSelected');
	if(selRowData == null){
		$.messager.alert('Information','请选择一条记录');
		return;
	}
	alert(selRowData.picUrl);
};

//日客流的统计，使用查询区域的sn和开始日期作为当日的统计
var fnCalcuOneday = function(){
	//sn使用查询框内的，如果没有指定，那么使用选中记录的
	var sn = $('#kw_sn').val();
	if(sn == ''){
		//如果现在有选择的，那么也可以使用选择记录的sn
		var selRowData = $('#dg1').datagrid('getSelected');
		if(selRowData != null){
			sn = selRowData.sn;
		}
	}
	if(sn == ''){
		$.messager.alert('Information','失败：请指定相机的SN');
		return;
	}
	var date = $('#search_dt_start').datetimebox('getValue');
	if(date == ''){
		$.messager.alert('Information','失败：请指定统计的日期（使用开始日期）');
		return;
	}
	
	//发送请求
	$.post('calcuOnedayInOut',{
		sn:sn,
		date:date
	},function(data){
		if(data.succ){
			var html = '相机：'+data.sn;
			html += '<br/>日期：'+data.date;
			html += '<br/>进：'+data.sumIn;
			html += '<br/>出：'+data.sumOut;
			$.messager.alert('Information',html);
		}else
			$.messager.alert('Information','失败：'+data.stmt);
	});
};

//相机数据导出txt文件
var fnOutputTxt = function(){
	var startTime = $('#search_dt_start').datetimebox('getValue');
	var endTime = $('#search_dt_end').datetimebox('getValue');
	var html = '确实要导出 '+startTime+" 到 "+endTime+" 之间的数据吗？";
	$.messager.confirm('Confirm',html,function(r){
		if(!r)
			return;
		$.messager.progress({msg:'正在执行导出，请稍后...'});
		$.post('outputCmrData',{
			startTime:startTime,
			endTime:endTime
		},function(data){
			$.messager.progress('close');
			if(data.succ){
				$.messager.alert('Information','导出成功, 共 '+data.stmt+' 条');
			}else{
				$.messager.alert('Information','迁移失败：'+data.stmt);
			}
		});
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