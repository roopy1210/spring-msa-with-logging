package com.roopy.services.order.service;

import java.util.HashMap;

import com.roopy.services.order.domain.Order;

public interface IPaymentService {

	public HashMap<String,Object> payment(Order order) throws Exception;
	
}
