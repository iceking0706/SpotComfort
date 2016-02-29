package com.xie.spot.service;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Service;

import com.xie.spot.sys.CameraManager;
import com.xie.spot.sys.FetchWeatherOperator;
import com.xie.spot.sys.VersionInfo;


/**
 * 系统启动项，在MyContextLoader之后的
 * @author IcekingT420
 *
 */
@Service
public class ProjectInitService implements ApplicationListener<ApplicationEvent>{
	private static final Logger logger = LoggerFactory.getLogger(ProjectInitService.class);
	
	@Autowired
	private DataInitService dataInitService;
	
	@Autowired
	private CameraManager cameraManager;
	
	/**
	 * 定期去获得天气信息的定时器
	 */
	private Timer timerWeather;
	
	private void init(){
		logger.info("=================Project SpotComfort (Version: "+VersionInfo.VERSION_NO+", "+VersionInfo.VERSION_DATE+") init oper=========================");
		dataInitService.initData();
//		if(timerWeather == null){
//			timerWeather = new Timer();
//		}
//		
//		timerWeather.schedule(new TimerTask() {
//			
//			@Override
//			public void run() {
//				FetchWeatherOperator weatherOperator = new FetchWeatherOperator();
//				weatherOperator.doIt();
//			}
//		}, 5000, 1800000);
		
		//cameraManager.start();
	}
	
	private void destroy(){
//		if(timerWeather != null)
//			timerWeather.cancel();
		//cameraManager.stop();
		logger.info("=================Project SpotComfort destroy oper=========================");
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if(event instanceof ContextRefreshedEvent){
			logger.info("=================ContextRefreshedEvent=========================");
			init();
		}else if(event instanceof ContextStartedEvent){
			logger.info("=================ContextStartedEvent=========================");
		}else if(event instanceof ContextStoppedEvent){
			logger.info("=================ContextStoppedEvent=========================");
		}else if(event instanceof ContextClosedEvent){
			logger.info("=================ContextClosedEvent=========================");
			destroy();
		}
	}
	
}
