package neo.spider.demo.E2E.dto;

import lombok.Data;

@Data
public class CardsDto {

	private String mobileNumber;
	
	private String cardNumber;
	
	private String cardType;
	
	private int totalLimit;
	
	private int amountUsed;
	
	private int availableAmount;
}
