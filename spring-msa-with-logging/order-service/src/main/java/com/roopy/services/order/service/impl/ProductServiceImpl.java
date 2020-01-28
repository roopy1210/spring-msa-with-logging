package com.roopy.services.order.service.impl;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.roopy.services.order.domain.Order;
import com.roopy.services.order.domain.OrderDetail;
import com.roopy.services.order.service.IProductService;

@Service
public class ProductServiceImpl implements IProductService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);
	
	@Autowired
	private RestTemplate restTemplate;

	@Override
	@HystrixCommand(commandKey = "order-service.updateProductQty", commandProperties = {
		@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000"),
		@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
		@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "30"),
		@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "100000"),
		@HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "100000")
	}, fallbackMethod = "productFallBack")
	public HashMap<String,Object> updateProductQty(Order order) throws Exception {
		HashMap<String,Object> retObj = new HashMap<String,Object>();
		
		HttpEntity<Order> ordRequest = new HttpEntity<>(order);
		ResponseEntity<List<OrderDetail>> response =  null;
		
		try {
			response = restTemplate.exchange("http://product-service/products", HttpMethod.POST, ordRequest, new ParameterizedTypeReference<List<OrderDetail>>() {});
		} catch (Exception e) {
			LOGGER.error("product-service exception occured :: " + e);
		}
		
		retObj.put("code", response.getStatusCodeValue());
		retObj.put("data", response.getBody());
		
		return retObj;
	}
	
	/**
	 * 결재 오류 시 호출되는 메소드
	 * 
	 * @param order
	 * @return
	 */
	public HashMap<String,Object> productFallBack(Order order) {
		
		HashMap<String,Object> retObj = new HashMap<String,Object>();
		
		retObj.put("code", -1);
		retObj.put("data", order);
		retObj.put("msg", "[" + order.getOrderId() + "] 상품재고 수량 업데이트 중 오류가 발생하였습니다.");
		
		return retObj;
	}

}
