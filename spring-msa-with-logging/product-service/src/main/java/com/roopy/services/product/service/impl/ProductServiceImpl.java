package com.roopy.services.product.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roopy.services.product.domain.Order;
import com.roopy.services.product.domain.OrderDetail;
import com.roopy.services.product.domain.Product;
import com.roopy.services.product.repository.ProductRepository;
import com.roopy.services.product.service.IProductService;

@Service
public class ProductServiceImpl implements IProductService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

	@Autowired
	private ProductRepository productRepository;
	
	@Transactional
	@Override
	public List<OrderDetail> save(Order order) {
		
		// 총 재고수량 
		int totalProductStockQty = 0;

		if (null != order.getOrderDetails() && order.getOrderDetails().size() > 0) {
			for (int i = 0; i < order.getOrderDetails().size(); i++) {
				// 총 재고수량
				totalProductStockQty += order.getOrderDetails().get(i).getQty();
				
				/*기존상품정보조회*/
				Product product = productRepository.find(order.getOrderDetails().get(i).getProdCd());
				
				/*기존상품총수량정보업데이트*/
				product.setQty(product.getQty() - order.getOrderDetails().get(i).getQty());
				productRepository.save(product);
				
				order.getOrderDetails().get(i).setProduct(product);
			}
			
		}
		
		// 로그출력
		StringBuffer paymentSb = new StringBuffer();
		for (int i = 0; i < order.getOrderDetails().size(); i++) {
			if (i == 0) {
				paymentSb.append("상품코드("+order.getOrderDetails().get(i).getProdCd()+"): " + order.getOrderDetails().get(i).getQty() + "건");
			}
			else {
				paymentSb.append(", 상품코드("+order.getOrderDetails().get(i).getProdCd()+"): " + order.getOrderDetails().get(i).getQty() + "건");
			}
		}
		
		LOGGER.info("총 상품재고 업데이트: {}건, {}", totalProductStockQty, paymentSb.toString());
		
		return order.getOrderDetails();
	}

}
