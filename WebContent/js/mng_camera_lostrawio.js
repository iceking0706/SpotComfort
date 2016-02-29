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

var formatLostP = function(value,row,index){
	return value+'%';
};

var formatOper = function(value,row,index){
	return '<a href="#" onclick="fnView('+index+');">详情</a>';
};

//查看某天丢失数据的详情
var fnView = function(index){
	var row = $('#dg1').datagrid('getRows')[index];
	if(row == null)
		return;
	//alert(row.oneDayStr+' , '+row.sn+' , '+row.onedayActual);
	$('#dlg_view_detail').panel('setTitle',curCmrMark+', '+row.oneDayStr);
	//去后台获取数据
	$.post('detailCmrOnedayLostIOMinutes',{
		sn:row.sn,
		oneDayStr:row.oneDayStr
	},function(data){
		if(data.succ){
			$('#dgview').datagrid('loadData',data.rows);
			$('#dlg_view_detail').dialog('open');
		}else{
			$.messager.alert('Information','失败：'+data.stmt);
		}
	});
};

var formatLostMinutes = function(value,row,index){
	return row.startStr+' ~ '+row.endStr;
};

//根据缺失的时间段去补充
var formatTianbu = function(value,row,index){
	return '<a href="#" onclick="fnTianbu('+index+');">填补</a>';
};

//去相机获取数据，并填补进去
var fnTianbu = function(index){
	var row = $('#dgview').datagrid('getRows')[index];
	if(row == null)
		return;
	
	//alert(row.startLong+' , '+row.endLong+' , '+curCmrSn);
	$.messager.confirm('Confirm','确实要去相机读取数据进行填补吗？<br/>'+row.startStr+' ~ '+row.endStr,function(r){
		if(!r)
			return;
		$.messager.progress({msg:'正在获取相机数据，请稍后...'});
		$.post('tianbuCmrOnedayLostIO',{
			sn:curCmrSn,
			startTime:row.startLong,
			endTime:row.endLong
		},function(data){
			$.messager.progress('close');
			if(data.succ){
				//填补成功，需要删除表格中的这条记录
				$('#dgview').datagrid('deleteRow',index);
			}else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
};

//每次查询之后，保存一下相机的备注
var curCmrMark = '';
var curCmrSn = '';
var fnSearch = function(){
	if($('#kw_sn').val() == ''){
		$.messager.alert('Information','请填写完整的相机SN');
		return;
	}
	if($('#search_dt_start').datetimebox('getValue') == ''){
		$.messager.alert('Information','请选择开始日期');
		return;
	}
	if($('#search_dt_end').datetimebox('getValue') == ''){
		$.messager.alert('Information','请选择结束日期');
		return;
	}
	$.messager.progress({msg:'正在统计数据，请稍后...'});
	$.post('listCameraLostRawDataInfo',{
		sn:$('#kw_sn').val(),
		startDateStr:$('#search_dt_start').datetimebox('getValue'),
		endDateStr:$('#search_dt_end').datetimebox('getValue')
	},function(data){
		$.messager.progress('close');
		if(data.succ){
			curCmrSn = data.sn;
			curCmrMark = data.cmrMark;
			$('#div_west_data').panel('setTitle',data.cmrMark);
			$('#dg1').datagrid('loadData',data.rows);
			fnChartByData(data);
		}else{
			$.messager.alert('Information','失败：'+data.stmt);
		}
	});
};

var fnChartByData = function(data){
	var xdata = new Array();
	var ydata = new Array();
	for(var i=0;i<data.rows.length;i++){
		xdata[xdata.length] = data.rows[i].dayShort;
		ydata[ydata.length] = data.rows[i].lostPercent;
	}
	$('#chart1').highcharts({
		credits:{
			enabled:false
		},
        title: {
            text: data.cmrMark,
            x: -20 //center
        },
        subtitle: {
            text: data.startDateStr+' ~ '+data.endDateStr,
            x: -20
        },
        xAxis: {
            categories:xdata
        },
        yAxis: {
            title: {
                text: '数据丢失率 (%)'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix: '%'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            borderWidth: 0,
            enabled:false
        },
        series: [{
            name: '丢失率',
            data: ydata
        }]
    });
};

/*$(function(){
	$('#kw_sn').val('VECAM-D01-LS14024063');
});*/

/*var test11 = function(){
	$('#chart1').highcharts({
        title: {
            text: 'Monthly Average Temperature',
            x: -20 //center
        },
        subtitle: {
            text: 'Source: WorldClimate.com',
            x: -20
        },
        xAxis: {
            categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
                'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
        },
        yAxis: {
            title: {
                text: 'Temperature (°C)'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix: '°C'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            borderWidth: 0
        },
        series: [{
            name: 'Tokyo',
            data: [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6]
        }, {
            name: 'New York',
            data: [-0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5]
        }, {
            name: 'Berlin',
            data: [-0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0]
        }, {
            name: 'London',
            data: [3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]
        }]
    });
};*/
