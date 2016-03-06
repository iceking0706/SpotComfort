var formatOnline = function(value,row,index){
	return value==1?'联机':'<font color="#f00">脱机</font>';
};

var fnSearch = function(refresh){
	$('#dg1').datagrid(refresh!=null?'reload':'load',{
		sn:$('#kw_sn').val(),
		mark:$('#kw_mark').val(),
		inUse:$('#kw_inUse').prop('checked')?1:-1,
		scene:$('#kw_scene').val()
	});
};

//将设置的参数应用到全部的相机
var fnSetParamToAll = function(){
	$.messager.confirm('Confirm','确实要将相机参数应用到全部吗？',function(r){
		if(!r)
			return;
		$.post('setCameraParamsToAll',{
			inoutInterval:$('#inoutInterval').numberspinner('getValue'),
			tkpInterval:$('#tkpInterval').numberspinner('getValue'),
			tkpHourSt:$('#tkpHourSt').numberspinner('getValue'),
			tkpHourEd:$('#tkpHourEd').numberspinner('getValue'),
			offlineTimeout:$('#offlineTimeout').numberspinner('getValue')
		},function(data){
			if(data.succ){
				$.messager.alert('Information','应用成功');
				fnSearch('refresh');
			}else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
};

//手工发起的相机时间设置
var fnSetCmrTime = function(){
	var selRowData = $('#dg1').datagrid('getSelected');
	if(selRowData!=null && selRowData.online != 1){
		$.messager.alert('Information','设备未联机. SN='+selRowData.sn+', IP='+selRowData.ip);
		return;
	}
	$.messager.confirm('Confirm','确实要设置'+(selRowData!=null?'指定':'全部联机')+'相机的时钟吗？',function(r){
		if(!r)
			return;
		$.post('setCameraTimeManually',{id:selRowData!=null?selRowData.id:0},function(data){
			if(data.succ)
				$.messager.alert('Information','命令已下发');
			else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
};

//手动发起的获取数据
var fnFetchDataImg = function(){
	var selRowData = $('#dg1').datagrid('getSelected');
	if(selRowData == null){
		$.messager.alert('Information','请选择要获取数据的摄像机设备');
		return;
	}
	if(selRowData.online != 1){
		$.messager.alert('Information','设备未联机. SN='+selRowData.sn+', IP='+selRowData.ip);
		return;
	}
	$.post('fetchCameraManually',{id:selRowData.id},function(data){
		if(data.succ)
			$.messager.alert('Information','命令已下发，稍后在摄像机数据中可查看');
		else
			$.messager.alert('Information','失败：'+data.stmt);
	});
};

//手动触发的相机拍照，和蒋涛的接口一致
var fnTakepicture = function(){
	var selRowData = $('#dg1').datagrid('getSelected');
	if(selRowData == null){
		$.messager.alert('Information','请选择要获取数据的摄像机设备');
		return;
	}
	if(selRowData.online != 1){
		$.messager.alert('Information','设备未联机. SN='+selRowData.sn+', IP='+selRowData.ip);
		return;
	}
	$.messager.confirm('Confirm','确实要抓拍相机照片吗？SN='+selRowData.sn,function(r){
		if(!r)
			return;
		$.messager.progress({msg:'正在获取照片...'});
		$.post('cmr/takepicture',{sn:selRowData.sn},function(data){
			$.messager.progress('close');
			if(data.succ){
				$('#dlg_quick_pic').dialog('setTitle','照片: '+data.time+', '+data.sn+', '+data.ip);
				$('#dlg_quick_pic_img').attr('src',data.picUrl);
				$('#dlg_quick_pic').dialog('open');
			}else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
	
};

//实时视频直播
var fnLiveStream = function(){
	var selRowData = $('#dg1').datagrid('getSelected');
	if(selRowData == null){
		$.messager.alert('Information','请选择要获取数据的摄像机设备');
		return;
	}
	/*if(selRowData.online != 1){
		$.messager.alert('Information','设备未联机. SN='+selRowData.sn+', IP='+selRowData.ip);
		return;
	}*/
	$.messager.confirm('Confirm','确实要直播相机视频吗？SN='+selRowData.sn,function(r){
		if(!r)
			return;
		$.messager.progress({msg:'正在建立直播连接...'});
		$.post('cmr/livestream',{sn:selRowData.sn,oper:'start'},function(data){
			$.messager.progress('close');
			if(data.succ){
				//alert(data.rtmpurl+' , '+data.pic);
				//直接弹出一个播放视频的小窗口
				var pageurl = 'mng_camera_livestream.jsp?sn='+data.sn+'&rtmpurl='+data.rtmpurl+'&picurl='+data.pic;
				window.open(pageurl,'','height=600,width=1000,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no, alwaysRaised=yes');
			}else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
};

//主动去触发的联机检测
var fnUpdateOnline = function(){
	$.messager.confirm('Confirm','确实要检测相机的联机状态吗？',function(r){
		if(!r)
			return;
		$.post('updateCameraOnlineManually',function(data){
			if(data.succ)
				$.messager.alert('Information','命令已下发，稍后可查询状态');
			else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
};

//主动去重启监听
var fnResetCmrListener = function(){
	$.messager.confirm('Confirm','确实要重启相机监听吗？',function(r){
		if(!r)
			return;
		$.post('resetCameraListenerManually',function(data){
			if(data.succ)
				$.messager.alert('Information','命令已下发，稍后可查询状态');
			else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
};

var fnClickRow = function(rowIndex,rowData){
	$('#id').val(rowData.id);
	$('#sn').val(rowData.sn);
	$('#sim').val(rowData.sim);
	$('#telNo').val(rowData.telNo);
	$('#mark').val(rowData.mark);
	$('#provider').val(rowData.provider);
	$('#puk').val(rowData.puk);
	$('#inoutInterval').numberspinner('setValue',rowData.inoutInterval);
	$('#tkpInterval').numberspinner('setValue',rowData.tkpInterval);
	$('#tkpHourSt').numberspinner('setValue',rowData.tkpHourSt);
	$('#tkpHourEd').numberspinner('setValue',rowData.tkpHourEd);
	$('#inUse').val(rowData.inUse);
	$('#scene').val(rowData.scene);
	$('#offlineTimeout').numberspinner('setValue',rowData.offlineTimeout);
};

var fnAdd = function(){
	if($('#sn').val() == ''){
		$.messager.alert('Information','请填写 SN');
		$('#sn').focus();
		return;
	}
	
	$.messager.confirm('Confirm','确实要新增摄像机： '+$('#sn').val()+' 吗？',function(r){
		if(!r)
			return;
		$.post('addCameraCfg',{
			sn:$('#sn').val(),
			mark:$('#mark').val(),
			provider:$('#provider').val(),
			sim:$('#sim').val(),
			puk:$('#puk').val(),
			telNo:$('#telNo').val(),
			inoutInterval:$('#inoutInterval').numberspinner('getValue'),
			tkpInterval:$('#tkpInterval').numberspinner('getValue'),
			tkpHourSt:$('#tkpHourSt').numberspinner('getValue'),
			tkpHourEd:$('#tkpHourEd').numberspinner('getValue'),
			inUse:$('#inUse').val(),
			scene:$('#scene').val(),
			offlineTimeout:$('#offlineTimeout').numberspinner('getValue')
			
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
	if($('#sn').val() == ''){
		$.messager.alert('Information','请填写 SN');
		$('#sn').focus();
		return;
	}
	
	$.messager.confirm('Confirm','确实要修改摄像机： '+$('#sn').val()+' 吗？',function(r){
		if(!r)
			return;
		$.post('modifyCameraCfg',{
			sn:$('#sn').val(),
			mark:$('#mark').val(),
			provider:$('#provider').val(),
			sim:$('#sim').val(),
			puk:$('#puk').val(),
			telNo:$('#telNo').val(),
			inoutInterval:$('#inoutInterval').numberspinner('getValue'),
			tkpInterval:$('#tkpInterval').numberspinner('getValue'),
			tkpHourSt:$('#tkpHourSt').numberspinner('getValue'),
			tkpHourEd:$('#tkpHourEd').numberspinner('getValue'),
			inUse:$('#inUse').val(),
			scene:$('#scene').val(),
			offlineTimeout:$('#offlineTimeout').numberspinner('getValue'),
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
		$.messager.alert('Information','请选择要删除的记录');
		return;
	}
	$.messager.confirm('Confirm','确实要删除摄像机： '+selRowData.sn+' 吗？',function(r){
		if(!r)
			return;
		$.post('deleteCameraCfg',{
			id:selRowData.id
		},function(data){
			if(data.succ)
				fnSearch('refresh');
			else
				$.messager.alert('Information','失败：'+data.stmt);
		});
	});
};

//下发操作命令到相机
var fnDownOper = function(){
	switch(parseInt($('#kw_oper').val())){
	case 1: //重启相机连接监听
		fnResetCmrListener();
		break;
	case 2: //检测相机联机状态
		fnUpdateOnline();
		break;
	case 3: //抓取相机I/O数据
		fnFetchDataImg();
		break;
	case 4: //设置相机时钟
		fnSetCmrTime();
		break;
	case 5: //触发相机拍照，和蒋涛的接口一样
		fnTakepicture();
		break;
	case 6: //建立实时直播流
		fnLiveStream();
		break;
	}
};

$(function(){
	
	$('#kw_sn').keypress(function(e){
		if(e.which == 13){
			fnSearch();
		}
	});
	$('#kw_mark').keypress(function(e){
		if(e.which == 13){
			fnSearch();
		}
	});
	$('#kw_scene').keypress(function(e){
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