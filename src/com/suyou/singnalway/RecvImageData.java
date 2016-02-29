package com.suyou.singnalway;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import com.xie.spot.sys.utils.ThumbnailUtil;

import ssin.util.DateProcess;

/**
 * 从摄像头接收到的图片信息的保存
 * 
 * @author IcekingT420
 * 
 */
public class RecvImageData {
	/**
	 * 普通的统一保存路径
	 */
	public static File saveDir = new File(System.getProperty("user.dir"));

	private int width;
	private int height;
	private long timeMs;
	private byte[] data;
	private int len;
	private File jpgFile;

	/**
	 * 创建图片的名字
	 * 
	 * @return
	 */
	public String getImageName() {
		return timeMs + "_" + len + "_" + width + "_" + height + ".jpg";
	}
	
	public boolean isValid(){
		if (data == null || data.length == 0)
			return false;
		return true;
	}

	public String saveTo() {
		return saveTo(saveDir,null);
	}

	/**
	 * 将文本保存到指定的目录下 ok表示成功，其它错误信息
	 * 
	 * @param dir
	 * @return
	 */
	public String saveTo(File dir,String fileName) {
		if(jpgFile != null)
			return "Already has Jpg file: "+jpgFile.getPath();
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return "Save directory is not valid.";
		if (!isValid())
			return "Image data is not valid";
		try {
			String curFileName = fileName!=null?fileName:getImageName();
			jpgFile = new File(dir, curFileName);
			if (!jpgFile.exists())
				jpgFile.createNewFile();

			FileOutputStream fos = new FileOutputStream(jpgFile);
			for (int i = 0; i < data.length; i++) {
				fos.write(data[i]);
			}
			fos.flush();
			fos.close();
			
			//进行缩略图的处理
			ThumbnailUtil.to25Pct(jpgFile);

			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			return "Fail. error: " + e.getMessage();
		}
	}

	public String getTimeShow() {
		if (timeMs == 0l)
			return "";
		return DateProcess.toString(new Date(timeMs),
				DateProcess.format_yyyy_MM_dd_HH_mm_ss);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public long getTimeMs() {
		return timeMs;
	}

	public void setTimeMs(long timeMs) {
		this.timeMs = timeMs;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public File getJpgFile() {
		return jpgFile;
	}

	public void setJpgFile(File jpgFile) {
		this.jpgFile = jpgFile;
	}
}
