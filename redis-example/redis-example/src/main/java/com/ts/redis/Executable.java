package com.ts.redis;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Executable {
	public static RegistrationBean registrationBean;
	public static ClassPathXmlApplicationContext context;

	static {
		// Acquire Context
		context = new ClassPathXmlApplicationContext("redis-context.xml");
	}

	public static void main(String [] args) throws Exception{
		// Create User
		createUser();
	}

	public static void createUser(){
		User user = new User();
		user.setId("20011202");
		user.setFullName("Susa Richard");
		user.setStatus("A");
		user.setAge("30");
		Address address = new Address();
		address.setAddressValue("UK/Manchester");
		user.setAddress(address);
		RegistrationBean bean = (RegistrationBean)context.getBean("registrationBean");
		// Persisting Inside the Hash User object
		bean.getRedisTemplate().opsForHash().put("UserA", user.hashCode(),user);
		// Retrieving the User object from the Redis by using the suggested key
		User x = (User)bean.getRedisTemplate().opsForHash().get("UserA", user.hashCode());
		System.out.println(x);
	}
}