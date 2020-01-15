package com.roopy.services.order.exception;

public class PaymentException extends Exception {
	private static final long serialVersionUID = -6975868331190967929L;

	public PaymentException(){
        super();
    }
	public PaymentException(String message){
        super(message);
    }
}
