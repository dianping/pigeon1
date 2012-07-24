package com.dianping.pigeon.engine;

public class UserServiceImpl implements UserService {
	public String selectUsernameById(String id) {
		return id;
	}

	public String selectEmailById(String id, String[] ids) {
		return id;
	}

	public User getUser(User u) {
		return u;
	}

	@Override
	public int getInt(int i, int[] is) {
		return i;
		
	}
	
	@Override
	public Integer getInteger(Integer i, Integer[] is) {
		return i;
		
	}
}