package com.xie.spot.sys.utils;

import java.io.File;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 使用图片进行缩放
 * @author IcekingT420
 *
 */
public class ThumbnailUtil {
	/***
	 * 将原图缩放成为特定比列
	 * @param jpgFile 原图
	 * @param scale 比列
	 * @param suffix 后缀，默认是min
	 * @return
	 */
	public static boolean toPercent(File jpgFile,double scale,String suffix){
		try {
			//缩放图的名称为原图后面加_min
			String path = jpgFile.getPath();
			int idx = path.lastIndexOf('.');
			if(idx == -1)
				return false;
			String outPath = path.substring(0, idx)+"_"+(suffix!=null?suffix:"min")+path.substring(idx);
			//进行缩放
			Thumbnails.of(jpgFile).scale(scale).toFile(outPath);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 将原图缩放为25%，默认后缀为 min
	 * @param jpgFile
	 * @return
	 */
	public static boolean to25Pct(File jpgFile){
		return toPercent(jpgFile, 0.25d, null);
	}
}
