package neo.spider.solution.apimock.dto;

import java.util.List;

import lombok.Data;

@Data
public class ChangeModeRequest {
	
	private List<Integer> ids; 
	private boolean targetMode;
	
}
