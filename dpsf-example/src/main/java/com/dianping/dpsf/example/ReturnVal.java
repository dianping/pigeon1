/**
 * 
 */
package com.dianping.dpsf.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;


/**    
 * <p>    
 * Title: ReturnValue.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-8 下午05:46:26   
 */

// Modified by YuJun on Nov 12

public class ReturnVal implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3238983551018995244L;
	/**
	 * 
	 */
	private String value;
	private ReturnObj returnObj;
	
	public ReturnVal(String value, Integer picId){
		this.value = value;
		this.returnObj = new ReturnObj(picId);
	}
	
	public ReturnVal(String value){
		this.value = value;
		this.returnObj = new ReturnObj(123);
	}
	
	public String getValue(){
		return returnObj.getTitle();
	}
	/**
	public String getValue(){
		
	}
	*/

}

class ReturnObj implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5471540203672697301L;

	/**
	 * 
	 */
	
	private Integer picId;
	
	/**
	 * 图片所属商户id,关联shop对象
	 */
	private Integer shopId;
	
	/**
	 * 图片所属用户id,关联user对象
	 */
	private Integer userId;
	
	/**
	 * 图片标题
	 */
	private String title;
	
	/**
	 * 图片评论
	 */
	private String comments;
	
	/**
	 * 图片所在server名
	 */
	private String server;
	
	/**
	 * 图片路径
	 */
	private String filePath;
	
	/**
	 * 图片类型
	 */
	private String fileType;
	
	/**
	 * 点击量
	 */
	private Integer hits;
	
	/**
	 * 加入时间
	 */
	private Date addTime;
	
	/**
	 * 最后修改时间
	 */
	private Date lastTime;
	
	/**
	 * 最后修改人ip地址
	 */
	private String lastIp;
	
	/**
	 * 图片状态
	 */
	private Integer power;
	
	/**
	 * 图片所属商户的城市id.关联city对象
	 */
	private Short cityId;
	
	/**
	 * 鲜花总数
	 */
	private Short rateTotal;
	
	/**
	 * 未知
	 */
	private Short rateGoodTotal;
	
	/**
	 * 未知
	 */
	private Short rateScore;
	
	/**
	 * 回应数量
	 */
	private Short followNoteNo;
	
	/**
	 * 图片所在商户类别
	 */
	private Short shopType;
	
	/**
	 * 图片所在商户连锁店id
	 */
	private Integer shopGroupId;
	
	/**
	 * 图片类别,关联PicClassType
	 */
	private Short shopPicType;
	
	/**
	 * 未知
	 */
	private Integer picType;
	
	/**
	 * 未知
	 */
	private Boolean isTop;
	
	/**
	 * 未知
	 */
	private Short orderNo;
	
	/**
	 * 备忘1
	 */
	private String picMemo1;
	
	/**
	 * 备忘2
	 */
	private String picMemo2;
	
	/**
	 * 备忘3
	 */
	private String picMemo3;
	
	public ReturnObj(Integer newPicId){
		picId = newPicId;
		shopId = 123123123;
		userId = 123123123;
		title = "TestTestTestTest";
		comments = "TESTTESTTESTTEST";
		server = "TestTestTestTest";
		filePath = "TestTestTestTest";
		fileType = "TestTestTestTestTest";
		hits = 123123123;
		addTime = new Date();
		lastTime = new Date();
		lastIp = "TestTestTestTestTest";
		power = 1;
		cityId = 1;
		rateTotal = 111;
		rateScore = 123;
		followNoteNo = 123;
		shopType = 12;
		shopGroupId = 123;
		shopPicType = 123;
		picType = 12;
		isTop = true;
		orderNo = 123;
		picMemo1 = "TestTestTestTestTest";
		picMemo2 = "TestTestTestTestTest";
		picMemo3 = "TestTestTestTestTest";	
	}
	
	public String getTitle(){
		return title;
	}
	
	
}


