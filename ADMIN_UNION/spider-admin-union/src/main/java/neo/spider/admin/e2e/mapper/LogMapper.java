package neo.spider.admin.e2e.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import neo.spider.admin.e2e.dto.LogDTO;

@Mapper
public interface LogMapper {
	
	List<LogDTO> findByTraceId(String traceId);
	
	int countDelayRequestLogs(LogDTO paramDto);

	List<LogDTO> findDelayRequestLogs(@Param("paramDto") LogDTO paramDto, @Param("limit") int limit, @Param("offset") int offset);

	int countDelayQueryLogs(LogDTO paramDto);

	List<LogDTO> findDelayQueryLogs(@Param("paramDto") LogDTO paramDto, @Param("limit") int limit, @Param("offset") int offset);

    int countErrorLogs(LogDTO paramDto);

	List<LogDTO> findErrorLogs(@Param("paramDto") LogDTO paramDto, @Param("limit") int limit, @Param("offset") int offset);

	List<LogDTO> findInfluenceLogs(@Param("limit") int limit, @Param("offset") int offset, @Param("searchType") String searchType, @Param("searchKeyword") String searchKeyword);
	
	int countInfluenceLogs(@Param("searchType") String searchType, @Param("searchKeyword") String searchKeyword);

}
