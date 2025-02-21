package neo.spider.solution.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import neo.spider.solution.dto.AdminApiDTO;

@Mapper
public interface AdminMapper {

	List<AdminApiDTO> findAll(@Param("paramDto") AdminApiDTO paramDto, @Param("size") int size, @Param("offset") int offset);
	
	int countByApiName(AdminApiDTO paramDto);
	
}
