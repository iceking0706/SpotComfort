var formatterSucc = function(value,row,index){
	if(value)
		return '<img src="icons/icon16/saved.png" width=16 height=16>';
	else
		return '<img src="icons/icon16/quit_16.png" width=16 height=16>';
};

var removeAllRowsOfGrid = function(gridId){
	var grid = $('#'+gridId);
	while(grid.datagrid('getRows')!=null && grid.datagrid('getRows').length>0){
		grid.datagrid('deleteRow',0);
	}
};

var fnSubmitForm = function(){
	if($('#fileToUpload').val() == ''){
		$.messager.alert('Information','请选择一个要上传的数据文件');
		return;
	}
	$.messager.confirm('Confirm','确实要上传该文件吗？',function(r){
		if(!r)
			return;
		//删除表格中原来的数据
		removeAllRowsOfGrid('dg1');
		$('#dg1').datagrid('appendRow',{succ:true,result:'开始导入: '+$('#fileToUpload').val()});
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
				url:'excelInput',
				dataType:'json',
				success:function(data){
					//关闭进度条
					$.messager.progress('close');
					if(data.rows!=null && data.rows.length>0){
						var curRows = data.rows;
						for(var i=0;i<curRows.length;i++){
							$('#dg1').datagrid('appendRow',{succ:curRows[i].succ,result:curRows[i].result});
						}
					}
				},
				error: function(e){
					$.messager.progress('close');
					alert(e);
				}
			});
			return false;
		});
});