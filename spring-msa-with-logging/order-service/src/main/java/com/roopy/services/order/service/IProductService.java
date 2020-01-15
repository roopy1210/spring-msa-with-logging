package com.roopy.services.order.service;

import java.util.HashMap;

import com.roopy.services.order.domain.Order;

public interface IProductService {

	/**
	 * 상품수량 업데이트
	 * 
	 * @param order			주문정보
	 * @throws Exception
	 */
	public HashMap<String,Object> updateProductQty(Order order) throws Exception;
	
}
