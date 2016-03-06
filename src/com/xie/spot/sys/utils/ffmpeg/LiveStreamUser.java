package com.xie.spot.sys.utils.ffmpeg;

/**
 * 调用相机必须指定用户名和密码，
 * 在此使用数值进行配置
 * @author iceking
 *
 */
public class LiveStreamUser {
	/**
	 * 用户名，密码，备注
	 */
	public static final String[][] userArrayForLiveStream = new String[][]{
		{"admin","adminsuyou360","酥游科技的管理员"},
		{"pfcounter","pfcounter123456","蒋涛软件对接使用的"}
	};
	
	/**
	 * 判断用户名和密码是否存在
	 * @param username
	 * @param password
	 * @return
	 */
	public static boolean isUserValid(String username,String password){
		if(username==null || username.equals(""))
			return false;
		if(password == null || password.equals(""))
			return false;
		for(String[] array: userArrayForLiveStream){
			if(array[0].equals(username) && array[1].equals(password))
				return true;
		}
		return false;
	}
}
