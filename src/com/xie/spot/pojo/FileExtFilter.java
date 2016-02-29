package com.xie.spot.pojo;

import java.io.File;
import java.io.FileFilter;

/**
 * 文件扩展名的过滤器
 * @author IcekingT420
 *
 */
public class FileExtFilter implements FileFilter{
	/**
	 * 扩展名
	 */
	private String[] extensions;
	
	public FileExtFilter(String[] extensions) {
		this.extensions = extensions;
	}

	@Override
	public boolean accept(File pathname) {
		if(extensions==null && extensions.length==0)
			return true;
		String fileName = pathname.getName();
		//有一个相同就可以了
		for(String ext: extensions){
			if(fileName.endsWith(ext))
				return true;
		}
		return false;
	}
}
