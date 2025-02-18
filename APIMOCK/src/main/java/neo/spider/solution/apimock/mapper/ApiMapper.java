package neo.spider.solution.apimock.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import neo.spider.solution.apimock.dto.ApiDTO;

@Mapper
public interface ApiMapper {
	List<ApiDTO> findAll();
	ApiDTO findById(int id);
	List<ApiDTO> findByIds(List<Integer> ids);
	void saveApi(ApiDTO apiDTO);
	void updateCheckedStatus(ApiDTO apiDTO);
	void changeResponseStatusById(ApiDTO apiDTO);
	void toggleResponseStatusById(int id);
	void toggleResponseStatusByIds(List<Integer> ids);
	
	String findWireMockIdById(int id);
	void deleteById(int id);
	void updateById(ApiDTO apiDTO);
	
}
