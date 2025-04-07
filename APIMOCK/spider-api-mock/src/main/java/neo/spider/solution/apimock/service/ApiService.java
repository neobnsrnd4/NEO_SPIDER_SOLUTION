package neo.spider.solution.apimock.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import neo.spider.solution.apimock.dto.ApiDTO;

public interface ApiService {
	List<ApiDTO> getAllApis();
	ApiDTO getApi(int id);
	List<ApiDTO> getApis(List<Integer> ids);
	void saveNewApi(ApiDTO apiDto);
	void updateCheckedApiInfo(int id, LocalDateTime checkedTime, Integer checkedStatus);
	void toggleResponseStatusById(int id);
	void changeModeById(int id, boolean targetMode/*1:대응답ON,0:대응답OFF(실서버)*/);
	void changeModeByIds(List<Integer> ids, boolean targetMode/*1:대응답ON,0:대응답OFF(실서버)*/);
	ApiDTO performHealthCheck(int id);
	void checkAllApiHealthCheck();
	void deleteApi(int id);
	
	void updateApi(int id, ApiDTO apiDto);
	Map<String, Object> getMockData(int id);
	void updateMockData(int id);

}
