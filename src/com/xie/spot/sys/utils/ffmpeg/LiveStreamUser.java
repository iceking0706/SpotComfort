package com.xie.spot.sys.utils.ffmpeg;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 调用相机必须指定用户名和密码，
 * 在此使用数值进行配置
 * @author iceking
 *
 */
public class LiveStreamUser {
	
	/**
	 * 从json文件中读取出来用户和sn的关系
	 */
	private static List<UserSN> userList;
	
	private static List<UserSN> getUserList() {
		if(userList == null){
			try {
				//读取usersn.json文件
				InputStream is = LiveStreamUser.class.getResourceAsStream("/com/xie/spot/sys/utils/ffmpeg/usersn.json");
				BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
				String json = "";
				String line = null;
				while((line=br.readLine())!=null){
					json += line.trim();
				}
				br.close();
				is.close();
				
				//将json转换为对象
				JSONArray jsonArray = JSONArray.fromObject(json);
				if(jsonArray == null || jsonArray.isEmpty())
					return null;
				int size = jsonArray.size();
				
				//将内容填写到userList中去
				userList = new ArrayList<UserSN>();
				for(int i=0;i<size;i++){
					JSONObject obj = jsonArray.getJSONObject(i);
					UserSN user = new UserSN();
					user.setUser(obj.getString("user"));
					user.setPass(obj.getString("pass"));
					user.setMark(obj.getString("mark"));
					user.setAll(obj.getBoolean("all"));
					user.setSns((List<String>)obj.get("sns"));
					
					userList.add(user);
				}
				showInfo();
			} catch (Exception e) {
				e.printStackTrace();
				userList = null;
			}
		}
		return userList;
	}
	
	public static void showInfo(){
		if(userList==null || userList.size()==0){
			System.out.println("No user info found.");
			return;
		}
		System.out.println("=======User define for Live stream===========");
		int i=0;
		for(UserSN user: userList){
			i++;
			System.out.println("No."+i+"->"+user.toString());
		}
	}
	
	/**
	 * 根据用户名和密码找到用户定义信息
	 * @param username
	 * @param password
	 * @return
	 */
	public static UserSN findUserBy(String username,String password){
		if(getUserList() == null)
			return null;
		if(username==null || username.equals(""))
			return null;
		if(password == null || password.equals(""))
			return null;
		for(UserSN user: userList){
			if(user.getUser().equals(username) && user.getPass().equals(password))
				return user;
		}
		return null;
	}

	/**
	 * 判断用户名和密码是否存在
	 * @param username
	 * @param password
	 * @return
	 */
	public static boolean isUserValid(String username,String password){
		return findUserBy(username,password)!=null;
	}
}
