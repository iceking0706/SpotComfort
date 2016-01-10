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
            text: '盛世莲花观光园各监控点客流数据'
        },
        xAxis: {
            categories: [
                '大门一',
                '大门二'
            ]
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
	},300000);
});