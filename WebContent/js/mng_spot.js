var formatProvince = function(value,row,index){
	return row.city.province;
};
var formatCity = function(value,row,index){
	return row.city.name;
};
var formatViewLevel = function(value,row,index){
	if(value==1)
		return '绝美';
	else if(value==2)
		return '超美';
	else
		return '美';
};
var formatMaxCapacity = function(value,row,index){
	if(value == 0)
		return '';
	return value+' 人';
};
var formatPicture = function(value,row,index){
	return '<a href="#" onClick="fnViewPic('+index+');">查看('+row.picCount+')</a>';
};

var fnViewPic = function(index){
	var row = $('#dg1').datagrid('getRows')[index];
	//调用main中的新增一个tab
	if(typeof(window.parent.fnIframeToPage) != 'function')
		return;
	window.parent.fnIframeToPage('toPageSpotPic?spotId='+row.id,'图片: '+row.city.name+'->'+row.name);
};

var formatCC = function(value,row,index){
	return '<a href="#" onClick="fnViewCC('+index+');">设置('+row.ccCount+')</a>';
};

var fnViewCC = function(index){
	var row = $('#dg1').datagrid('getRows')[index];
	//调用main中的新增一个tab
	if(typeof(window.parent.fnIframeToPage) != 'function')
		return;
	window.parent.fnIframeToPage('toPageSpotCC?spotId='+row.id,'修正: '+row.city.name+'->'+row.name);
};

//内容区域的省份选中
var fnSelectAProvince = function(record){
	$('#city').combobox('reload','comboCity');
};

//查询区域的省份选中
var fnSelectAKWProvince = function(record){
	$('#kw_city').combobox('reload','comboCity');
};

var fnSearch = function(refresh){
	$('#dg1').datagrid(refresh!=null?'reload':'load',{
		province:$('#kw_province').combobox('getText'),
		city:$('#kw_city').combobox('getText'),
		viewLevel:$('#kw_viewLevel').val(),
		name:$('#kw_name').val()
	});
};

//表格数据单击
var fnClickRow = function(rowIndex,rowData){
	preClickRow = rowData;
	//省份设置为当前省份
	if($('#province').combobox('getValue') != preClickRow.city.province){
		$('#province').combobox('setValue',preClickRow.city.province);
		fnSelectAProvince();
	}else{
		//省份一样的话，判断城市是否一样
		if($('#city').combobox('getValue') != (preClickRow.city.id+'')){
			$('#city').combobox('setValue',preClickRow.city.id+'');
		}
	}
	
	//其它属性的设置
	$('#name').val(preClickRow.name);
	$('#code').val(preClickRow.code);
	$('#wcode').val(preClickRow.wcode);
	$('#grade').val(preClickRow.grade);
	$('#viewLevel').val(preClickRow.viewLevel+'');
	$('#maxCapacity').numberspinner('setValue',preClickRow.maxCapacity);
	$('#lonX').numberbox('setValue',preClickRow.lonX);
	$('#latY').numberbox('setValue',preClickRow.latY);
	$('#mainRcmd').numberspinner('setValue',preClickRow.mainRcmd);
	
	//将当前记录设置到main中去
	fnSetTopSelSpot(preClickRow);
};

//单击列表的时候，将当前控制的景点设置到main中去
var fnSetTopSelSpot = function(record){
	if(typeof(window.parent.fnSetTopSelSpot) != 'function')
		return;
	window.parent.fnSetTopSelSpot(record);
};

var fnAdd = function(){
	if($('#city').combobox('getValue') == ''){
		$.messager.alert('Information','请选择 所属城市');
		return;
	}
	if($('#name').val() == ''){
		$.messager.alert('Information','请填写 景点名称');
		$('#name').focus();
		return;
	}
	$.messager.confirm('Confirm','确实要新增景点： '+$('#province').combobox('getText')+'->'+$('#city').combobox('getText')+'：'+$('#name').val()+' 吗？',function(r){
		if(!r)
			return;
		$.post('addSpotBasic',{
			cityId:$('#city').combobox('getValue'),
			name:$('#name').val(),
			code:$('#code').val(),
			wcode:$('#wcode').val(),
			grade:$('#grade').val(),
			viewLevel:$('#viewLevel').val(),
			maxCapacity:$('#maxCapacity').numberspinner('getValue'),
			lonX:$('#lonX').numberbox('getValue'),
			latY:$('#latY').numberbox('getValue'),
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
		$.messager.alert('Information','请选择要修改的景点记录');
		return;
	}
	if($('#city').combobox('getValue') == ''){
		$.messager.alert('Information','请选择 所属城市');
		return;
	}
	if($('#name').val() == ''){
		$.messager.alert('Information','请填写 景点名称');
		$('#name').focus();
		return;
	}
	$.messager.confirm('Confirm','确实要修改景点： '+selRowData.city.province+'->'+selRowData.city.name+'：'+selRowData.name+' 吗？',function(r){
		if(!r)
			return;
		$.post('modifySpotBasic',{
			id:selRowData.id,
			cityId:$('#city').combobox('getValue'),
			name:$('#name').val(),
			code:$('#code').val(),
			wcode:$('#wcode').val(),
			grade:$('#grade').val(),
			viewLevel:$('#viewLevel').val(),
			maxCapacity:$('#maxCapacity').numberspinner('getValue'),
			lonX:$('#lonX').numberbox('getValue'),
			latY:$('#latY').numberbox('getValue'),
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
	var selRowData = $('#dg1').datagrid('getSelected');
	if(selRowData == null){
		$.messager.alert('Information','请选择要修改的景点记录');
		return;
	}
	$.messager.confirm('Confirm','确实要删除景点： '+selRowData.city.province+'->'+selRowData.city.name+'：'+selRowData.name+' 吗？',function(r){
		if(!r)
			return;
		$.post('deleteSpotBasic',{
			id:selRowData.id
		},function(data){
			if(data.succ)
				fnSearch('refresh');
			else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
};

//上一次点击的表格记录
var preClickRow = null;


//模拟客流数据的提交
var fnSimulateFlow = function(){
	var selRowData = $('#dg1').datagrid('getSelected');
	if(selRowData == null){
		$.messager.alert('Information','请选择景点记录');
		return;
	}
	if($('#maxCapacity').numberspinner('getValue')==0){
		$.messager.alert('Information','该景点的最大承载量未设置');
		return;
	}
	if($('#simulateFlow').numberspinner('getValue')==0){
		$.messager.alert('Information','请设置模拟客流量');
		return;
	}
	$.messager.confirm('Confirm','确实要提交景点： '+selRowData.city.province+'->'+selRowData.city.name+'：'+selRowData.name+' 的模拟客流吗？',function(r){
		if(!r)
			return;
		$.post('simulateSpotFlow',{
			spotId:selRowData.id,
			simulateFlow:$('#simulateFlow').numberspinner('getValue')
		},function(data){
			if(data.succ)
				$.messager.alert('Information','模拟数据提交成功');
			else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
};

//根据计算得到舒适度，返回几个笑脸
var fnGnrSCDImg = function(spotCD){
	var html1 = '';
	var count = 0;
	if(spotCD == 100)
		count = 5;
	else if(spotCD == 0)
		count = 1;
	else
		count = parseInt(spotCD/20+1);
	for(var i=0;i<count;i++){
		html1 += '<img src="icons/icon16/smiley.png" width="16" height="16">';
	}
	return html1;
};

//获得某个景点的舒适度指数
var fnCalcuSpotComfort = function(){
	var selRowData = $('#dg1').datagrid('getSelected');
	if(selRowData == null){
		$.messager.alert('Information','请选择景点记录');
		return;
	}
	$.post('calcuSpotComfort',{
		spotId:selRowData.id
	},function(data){
		if(data.succ){
			var html = '景点：'+data.result.spotFullName;
			html += '<br />客流评分：'+data.result.psgrScore;
			html += '<br />景观评分：'+data.result.viewScore;
			html += '<br />天气评分：'+data.result.weatherScore;
			html += '<br />舒适度指数：'+data.result.comfortDegree;
			html += '<br />'+fnGnrSCDImg(data.result.comfortDegree);
			$.messager.alert('Information',html);
		}else
			$.messager.alert('Information','失败：'+data.stmt);
	});
};

$(function(){
	$('#province').combobox({
		onLoadSuccess:function(){
			var loadedData = $('#province').combobox('getData');
			if(loadedData==null || loadedData.length==0)
				return;
			$('#province').combobox('setValue',loadedData[0].value);
			fnSelectAProvince(loadedData[0]);
		},
		onSelect:fnSelectAProvince
	});
	
	$('#city').combobox({
		onBeforeLoad:function(param){
			param.province=$('#province').combobox('getValue');
		},
		onLoadSuccess:function(){
			var loadedData = $('#city').combobox('getData');
			if(loadedData==null || loadedData.length==0)
				return;
			var selIndex = 0;
			//如果有点击的记录，那么设置为这个城市的id号
			if(preClickRow != null){
				for(var i=0;i<loadedData.length;i++){
					if((preClickRow.city.id+'') == loadedData[i].value){
						selIndex = i;
						break;
					}
				}
			}
			$('#city').combobox('setValue',loadedData[selIndex].value);
		}
	});
	
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
	
	$('#kw_name').keypress(function(e){
		if(e.which == 13){
			fnSearch();
		}
	});
	
	//表格单击事件
	$('#dg1').datagrid({
		onClickRow:fnClickRow,
		onLoadSuccess:function(data){
			//表格数据刷之后，把preClickRow设置为null
			preClickRow = null;
		}
	});
	
	setTimeout(function(){
		$('#province').combobox('reload','comboProvince');
		$('#kw_province').combobox('reload','comboProvince');
	},100);
	
	$('#dg1').datagrid('getPager').pagination({
		displayMsg:'{from}~{to} of {total}'
	});
});