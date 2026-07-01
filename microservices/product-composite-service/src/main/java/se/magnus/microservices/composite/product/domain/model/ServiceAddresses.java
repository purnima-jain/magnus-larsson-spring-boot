package se.magnus.microservices.composite.product.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAddresses {

	private String cmp;
	private String pro;
	private String rev;
	private String rec;

}
