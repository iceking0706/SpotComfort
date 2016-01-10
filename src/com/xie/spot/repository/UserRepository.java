package com.xie.spot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xie.spot.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
	/**
	 * 根据用户名和密码查询，判断是否合法用户
	 * @param username
	 * @param password
	 * @return
	 */
	public User findByUsernameAndPassword(String username,String password);
}
