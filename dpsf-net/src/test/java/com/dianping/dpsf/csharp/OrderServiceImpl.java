package com.dianping.dpsf.csharp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.dianping.avatar.tracker.CacheExecutionTrace;
import com.dianping.avatar.tracker.ExecutionContextHolder;
import com.dianping.avatar.tracker.SqlDetail;
import com.dianping.avatar.tracker.SqlExecutionTrace;
import com.dianping.avatar.tracker.TrackerContext;

public class OrderServiceImpl implements OrderService {
	
	private AtomicLong counter = new AtomicLong();

	@Override
	public OrderResponse getOrder(OrderRequest request) {
		ExecutionContextHolder.setTrackerContext(createTrackerContext());
		System.out.println("Recevie get order request with[id=" + request.getId() + ", remark=" + request.getRemark() 
				+ ", price=" + request.getPrice() + "].");
		OrderResponse response = new OrderResponse();
		response.setName("ProductName");
		response.setCount(333);
		response.setSets(new HashSet<String>(Arrays.asList("hello", "world")));
//		response.setPrice(BigDecimal.valueOf(100.50));
		response.setQrc(50.80f);
		response.setValid(true);
		List<Product> products = Arrays.asList(new Product("apple", 3), new Product("intel", 2));
		response.setProducts(products);
		response.setPrivilege(Privilege.High);
		response.setCreatedTime(new Date());
		return response;
	}

	@Override
	public boolean createOrder(OrderCreateRequest request, long count) {
		ExecutionContextHolder.setTrackerContext(createTrackerContext());
		if (count == 100) {
			throw new RuntimeException("Illegal count value[" + count + "].");
		}
		System.out.println("CreateOrder: [id=" + request.getId() + ", name=" + request.getName() 
				+ ", count=" + count + "].");
		return false;
	}

	private TrackerContext createTrackerContext() {
		TrackerContext trackerContext = new TrackerContext();
		trackerContext.setToken("Token-001-333");
		trackerContext.setLocation("location-sdc");
		trackerContext.addExtension("t1", "abc");
		trackerContext.addExtension("t2", 300);
		TrackerContext remoteContext = new TrackerContext();
		remoteContext.setToken("Token-remote-002-400");
		trackerContext.addRemoteContext(remoteContext);
		CacheExecutionTrace cacheTrace = new CacheExecutionTrace();
		Map<String, Integer> details = new HashMap<String, Integer>();
		details.put("detail-0", 100);
		details.put("detail-1", 200);
		cacheTrace.setRelatedKeys(details);
		cacheTrace.setTimeConsumed(2000);
		trackerContext.setCacheExecutionTrace(cacheTrace);
		SqlExecutionTrace sqlTrace = new SqlExecutionTrace();
		List<SqlDetail> sqlDetails = new ArrayList<SqlDetail>();
		SqlDetail sqlDetail = new SqlDetail();
		sqlDetail.setContent("select * from orderitem");
		sqlDetails.add(sqlDetail);
		sqlTrace.setDetails(sqlDetails);
		trackerContext.setSqlExecutionTrace(sqlTrace);
		return trackerContext;
	}

}
