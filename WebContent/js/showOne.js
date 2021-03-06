//启动视频直播
var fnLiveStream = function(sn){
	$.messager.confirm('Confirm','确实要直播相机视频吗？SN='+sn,function(r){
		if(!r)
			return;
		$.messager.progress({msg:'正在建立直播连接...'});
		//这个界面，默认是hls方式打开的，弹出video的播放器控件
		$.post('cmr/livestream',{sn:sn,oper:'start',username:'admin',password:'adminsuyou360',hls:false},function(data){
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

//刷新数据，每个1分钟自动刷新一次
var refresh = function(){
	location.reload(true);
};

var fnChart1 = function(){
	$('#chart1').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: spotName+'各监控点客流数据'
        },
        xAxis: {
            categories: spotCmrNameArray
        },
        yAxis: {
            min: 0,
            title: {
                text: '客流进出人数 (单位：人)'
            }
        },
        tooltip: {
        	enabled:false,
        	shared: true,
            useHTML: true
        },
        plotOptions: {
            column: {
                pointPadding: 0.2,
                borderWidth: 0
            }
        },
        series: [{
            name: '累计进入',
            data: dataInArray,
            dataLabels: {
                enabled: true,
                rotation: 0,
                color: '#0000FF',
                align: 'right',
                x: 5,
                y: 5,
                style: {
                    fontSize: '13px'
                    //fontFamily: 'Verdana, sans-serif',
                    //textShadow: '0 0 3px black'
                }
            }
        }, {
            name: '累计离开',
            data: dataOutArray,
            dataLabels: {
                enabled: true,
                rotation: 0,
                color: '#000000',
                align: 'right',
                x: 5,
                y: 5,
                style: {
                    fontSize: '13px'
                    //fontFamily: 'Verdana, sans-serif',
                    //textShadow: '0 0 3px black'
                }
            }
        }]
    });
};

$(function(){
	fnChart1();
	setTimeout(function(){
		refresh();
	},60000);
});