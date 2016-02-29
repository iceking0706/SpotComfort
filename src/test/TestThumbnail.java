package test;

import java.io.File;
import java.io.IOException;

import com.xie.spot.sys.utils.ThumbnailUtil;

import net.coobird.thumbnailator.Thumbnails;

public class TestThumbnail {
	public static void testthumbnailator(){
		try {
			//Thumbnails.of("E:\\临时文件\\aaaa\\7_1445158348338_261257_1920_1080.jpg").size(480, 270).toFile("E:\\临时文件\\aaaa\\11.jpg");
			Thumbnails.of("E:\\临时文件\\aaaa\\7_1445158348338_261257_1920_1080.jpg").scale(0.25d).toFile("E:\\临时文件\\aaaa\\11.jpg");
			System.out.println("succ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String url2min(String url){
		int idx = url.lastIndexOf('.');
		return url.substring(0, idx)+"_min"+url.substring(idx);
	}
	
	public static void main(String[] args) {
		//String url = "http://120.26.108.57:8888/sc/uploadfiles/cameraPics/3_1445161762250_48882_1920_1080.jpg";
		//System.out.println(url2min(url));
		File jpg = new File("D:\\IcekingDocs\\2015\\谢总-景点舒适度\\0-客户项目\\呀诺达雨林文化旅游区电子导览图.jpg");
		boolean flag = ThumbnailUtil.to25Pct(jpg);
		System.out.println(flag);
	}
}
