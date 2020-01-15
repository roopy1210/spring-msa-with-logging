package com.roopy.services.payment.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.roopy.services.payment.domain.Order;
import com.roopy.services.payment.service.IPaymentService;

@RestController
public class PaymentController {

	@Autowired
	private IPaymentService paymentService;

	@RequestMapping(value = "/payment", method = RequestMethod.POST)
	public String payment(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Order order) throws Exception {
		
		return paymentService.save(order);
	}
	
}
