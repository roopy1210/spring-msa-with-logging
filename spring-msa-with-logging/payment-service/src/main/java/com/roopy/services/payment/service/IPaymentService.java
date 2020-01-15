package com.roopy.services.payment.service;

import com.roopy.services.payment.domain.Order;

public interface IPaymentService {
	
	public String save(Order order) throws Exception;
	
}
