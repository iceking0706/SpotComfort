var formatDownload = function(value,row,index){
	return '<a href="uploadfiles/mysqlBackup/'+row.name+'">下载 (鼠标右键-目标另存为)</a>';
};

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

var fnSearch = function(){
	$('#dg1').datagrid('load',{
		limit:60
	});
};

var fnDoBackup = function(){
	$.messager.confirm('Confirm','确实要执行数据库备份操作吗？',function(r){
		if(!r)
			return;
		$.messager.progress({msg:'正在执行备份，请稍后...'});
		$.post('doMysqlDump',function(data){
			$.messager.progress('close');
			if(data.succ){
				alert('数据库备份成功. '+data.stmt);
				fnSearch();
			}else{
				$.messager.alert('Information','数据库备份失败：'+data.stmt);
			}
		});
	});
};

var fnDeleteFile = function(){
	var rec = $('#dg1').datagrid('getSelected');
	if(rec == null){
		$.messager.alert('Information','请首先选择一条备份文件记录');
		return;
	}
	
	$.messager.confirm('Confirm','确实要删除备份文件“'+rec.name+'”吗？',function(r){
		if(!r)
			return;
		$.post('deleteMysqlDumpFile',{
			fileName:rec.name
		},function(data){
			if(data.succ){
				alert('备份文件删除成功');
				fnSearch();
			}else{
				$.messager.alert('Information','备份文件删除失败：'+data.stmt);
			}
		});
	});
};

//迁移历史数据到文件
var fnAnalyTrans = function(){
	var endTime = $('#search_dt_end').datetimebox('getValue');
	if(endTime == ''){
		$.messager.alert('Information','请首先选择迁移的日期');
		return;
	}
	//先分析
	$.post('analyHisData',{
		endTime:endTime
	},function(data){
		if(data.succ){
			var html = '在 '+endTime+' 之前的相机数据有 '+data.stmt+' 条。确实要迁移到历史文件吗？';
			$.messager.confirm('Confirm',html,function(r){
				if(!r)
					return;
				$.messager.progress({msg:'正在执行迁移，请稍后...'});
				$.post('transHisData',{
					endTime:endTime
				},function(data){
					$.messager.progress('close');
					if(data.succ){
						$.messager.alert('Information','迁移成功');
						fnSearch();
					}else{
						$.messager.alert('Information','迁移失败：'+data.stmt);
					}
				});
			});
		}else{
			$.messager.alert('Information','分析失败：'+data.stmt);
		}
	});
};

$(function(){
	setTimeout(function(){
		fnSearch();
	},200);
});