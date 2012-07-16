package com.dianping.pigeon.engine;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dianping.pigeon.engine.jetty.JettyInit;

public class InitSample {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("http://service.dianping.com/prj/itemService_1.0.0", new ItemService());
		maps.put("http://service.dianping.com/prj/userService_1.0.0", new UserService());
		new JettyInit().init(maps, 2121, 8080);
	}

}

class ItemService {
	public void updateItem(String id) {

	}
}

class UserService {
	public String selectUsernameById(String id) {
		return null;
	}
	
	public String selectEmailById(String id, String[] ids) {
		return null;
	}
}
