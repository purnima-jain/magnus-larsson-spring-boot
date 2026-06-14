package se.magnus.microservices.composite.product.domain.model;

import lombok.Value;

@Value
public class ServiceAddresses {

	private final String cmp;
	private final String pro;
	private final String rev;
	private final String rec;

}
