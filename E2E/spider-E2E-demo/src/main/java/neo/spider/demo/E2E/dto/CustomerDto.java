package neo.spider.demo.E2E.dto;

import lombok.Data;

@Data
public class CustomerDto {
private String name;
	
	private String email;
	
	private String mobileNumber;
	
	private AccountsDto accountsDto;
	
	private LoansDto loansDto;
	
	private CardsDto cardsDto;
}
