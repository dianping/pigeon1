package com.dianping.dpsf.csharp;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class InnerRequest implements Serializable {

	private String method;
	private Object[] favors;
	private Map<String, Double> counters;
	private List<Product> products;
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Object[] getFavors() {
		return favors;
	}
	public void setFavors(Object[] favors) {
		this.favors = favors;
	}
	public Map<String, Double> getCounters() {
		return counters;
	}
	public void setCounters(Map<String, Double> counters) {
		this.counters = counters;
	}
	public List<Product> getProducts() {
		return products;
	}
	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
}
