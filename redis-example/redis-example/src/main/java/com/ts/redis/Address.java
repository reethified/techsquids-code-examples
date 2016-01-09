package com.ts.redis;

import java.io.Serializable;

public class Address implements Serializable{

	private static final long serialVersionUID = 1L;

	private Long addressId;

	private String addressValue;

	public Long getAddressId() {
		return addressId;
	}
	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}
	public String getAddressValue() {
		return addressValue;
	}
	public void setAddressValue(String addressValue) {
		this.addressValue = addressValue;
	}
}