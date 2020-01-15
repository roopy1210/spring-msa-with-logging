package com.roopy.services.order.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.roopy.services.order.domain.Order;
import com.roopy.services.order.helper.IDGeneratorHelper;
import com.roopy.services.order.service.IOrderService;
import com.roopy.services.order.service.IPaymentService;
import com.roopy.services.order.service.IProductService;

@RestController
public class OrderController {
	
	@Autowired
	private IOrderService orderService;
	
	@Autowired
	private IPaymentService paymentService;
	
	@Autowired
	private IProductService productService;
	
	@Autowired
	private IDGeneratorHelper idGenerator;

	@RequestMapping(value = "/order", method = RequestMethod.POST)
	public HashMap<String,Object> order(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Order order) throws Exception {
		
		/*주문결과*/
		HashMap<String,Object> retObj = new LinkedHashMap<String,Object>();
		
		/*주문ID생성*/
		String orderId = idGenerator.getOrderId();
		
		/*주문ID설정*/
		order.setOrderId(orderId);
		
		/*주문날짜설정*/
		order.setOrderDtm(orderId.substring(1));
		
		/*결제처리결과*/	
		HashMap<String,Object> paymentRetObj = paymentService.payment(order);
		
		/*결제처리결과코드*/
		int paymentResultCode = (int) paymentRetObj.get("code");
		
		/*주문정보저장*/
		if (paymentResultCode == 200) {
			// 상품재고수량 업데이트
			HashMap<String,Object> productRetObj = productService.updateProductQty(order);
			
			/*상품재고수량처리 결과 코드*/
			int productStockQtyUpdateResultCode = (int) productRetObj.get("code");
			
			if (productStockQtyUpdateResultCode == -1) {
				// 결제처리시 오류가 발생하더라도 이력을 남기기 위해서 주문대기상태로 저장한다.
				orderService.save(order);
				
				retObj.put("code", -1);
				retObj.put("msg", productRetObj.get("msg"));
				retObj.put("data", order);
				
				return retObj;
			}
			
			// 결제처리, 상품재고수량 업데이트 처리가 정상적으로 된경우 주문상태코드를 완료 변경 처리
			order.setOrderStatus("C");
			
			// 주문정보저장
			orderService.save(order);
		}
		else {
			// 결제처리시 오류가 발생하더라도 이력을 남기기 위해서 주문대기상태로 저장한다.
			orderService.save(order);
			
			retObj.put("code", -1);
			retObj.put("msg", paymentRetObj.get("msg"));
			retObj.put("data", order);
			
			return retObj;
		}
		
		// 최종주문정보 조회
		retObj.put("code", HttpStatus.SC_OK);
		retObj.put("msg", "[" + order.getOrderId() + "] 주문처리가 정상적으로 처리 되었습니다.");
		
		return retObj;
	}
	
	@RequestMapping(value = "/order/{orderId}", method = RequestMethod.POST)
	public Order findOrder(HttpServletRequest request, HttpServletResponse response, @PathVariable String orderId,
			@RequestParam HashMap<String, String> param) throws Exception {
		
		return orderService.find(orderId);
	}
}