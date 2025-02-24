package neo.spider.solution.batch.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
public class AccountDTO {
	private long id;
	private String accountNumber;
	private String name;
	private long money;
}
