package test;

import java.util.List;

import com.xie.spot.sys.utils.ChinesePinyin;

public class TestPinyin {
	public static void test1(){
		String city = "杭州";
		String py1 = ChinesePinyin.getPinyin1(city);
		System.out.println(py1+" , "+ChinesePinyin.getSzm(py1));
	}
	
	public static void main(String[] args) {
//		test1();
		String str = "aaa";
		System.out.println(str.equals(null));
	}
}
