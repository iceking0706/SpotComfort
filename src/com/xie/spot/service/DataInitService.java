package com.xie.spot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ssin.security.MD5;

import com.xie.spot.entity.User;
import com.xie.spot.repository.CameraCfgRepository;
import com.xie.spot.repository.UserRepository;

/**
 * 系统启动时候的一些初始化数据
 * @author IcekingT420
 *
 */
@Service
public class DataInitService {
	private static final Logger logger = LoggerFactory.getLogger(DataInitService.class);
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CameraCfgRepository cameraCfgRepository;
	
	@Transactional
	public void initData(){
		if(userRepository.count() == 0){
			//插入超级管理数据
			User entity = new User();
			entity.setIdNo("1234567890");
			entity.setName("SuperAdmin");
			entity.setEmail("iceking0706@mail.zjgsu.edu.cn");
			entity.setTellNo("13515712723");
			entity.setType(99);
			entity.setUsername("admin");
			entity.setPassword(MD5.encode("admin"));
			
			userRepository.save(entity);
			logger.info("insert superadmin into table TUser");
		}
		
		//所有摄像机设置为脱机
		cameraCfgRepository.meSetAllCameraOffline();
	}
	
	private boolean firstKeepConn = true;
	/**
	 * 1小时连接一次mysql，保持数据库连接
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	@Scheduled(fixedDelay=10800000)
	public void keepMysqlConn(){
		if(firstKeepConn){
			firstKeepConn = false;
			return;
		}
		logger.info("keepMysqlConn");
		userRepository.count();
	}
	
}
