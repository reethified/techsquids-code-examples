package com.ts.redis;

import org.slf4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

public class Test {
	static Logger logger =org.slf4j.LoggerFactory.getLogger(Test.class);
	public static ClassPathXmlApplicationContext context;

	static {
		// Acquire Context
		context = new ClassPathXmlApplicationContext("redis-context.xml");
	}
	public static void main(String[] args) {
		RedisTemplate<String, String> template= (RedisTemplate<String, String>) context.getBean("redisTemplate");
		template.boundHashOps("abc").put("xyz", "pqr");
		template.boundHashOps("abc").put("lmn", "def");
		template.boundHashOps("abc").put("xyz", "lop");
		
		System.out.println(template.boundHashOps("abc").get("xyz"));
		
	}
}
