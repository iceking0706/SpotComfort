package com.xie.spot.sys;

import java.io.File;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import com.xie.spot.sys.utils.JDBC;

/**
 * 控制系统的启动和关闭，使用filter来控制，会在servlet启动之前
 * @author IcekingT420
 *
 */
public class MyContextLoader extends ContextLoaderListener{
	private static final Logger logger = LoggerFactory.getLogger(MyContextLoader.class);
	
	/**
	 * 每次重启后，把临时文件夹中的全部文件给删除掉
	 */
	private void deleteTmpFiles(){
		try {
			if(Utils.getTmpDir() != null){
				File[] tmpFiles = Utils.getTmpDir().listFiles();
				if(tmpFiles != null && tmpFiles.length>0){
					for(File tmpFile: tmpFiles){
						tmpFile.delete();
					}
				}
			}
		} catch (Exception e) {
			logger.error("Delete tmp files error: "+e.getMessage(), e);
		}
	}
	
	/**
	 * 系统的启动
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		// TODO Auto-generated method stub
		Utils.setProjectCommonDir();
		//如果数据库不存在，则建立
		JDBC.createDataBase();
		
		//删除临时文件的内容
		deleteTmpFiles();
		super.contextInitialized(event);
	}
	
	/**
	 * 系统的停止
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
		super.contextDestroyed(event);
	}
}
