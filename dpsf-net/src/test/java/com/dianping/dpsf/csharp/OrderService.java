package com.dianping.dpsf.csharp;

public interface OrderService {
	
	OrderResponse getOrder(OrderRequest request);
	
	boolean createOrder(OrderCreateRequest request, long count);

}
