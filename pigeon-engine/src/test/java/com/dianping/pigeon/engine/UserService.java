package com.dianping.pigeon.engine;

public interface UserService {
	public String selectUsernameById(String id) ;

	public String selectEmailById(String id, String[] ids) ;

	public User getUser(User u) ;
	
	public int getInt(int i, int[] is) ;
	
	public Integer getInteger(Integer i, Integer[] is);
	
}
