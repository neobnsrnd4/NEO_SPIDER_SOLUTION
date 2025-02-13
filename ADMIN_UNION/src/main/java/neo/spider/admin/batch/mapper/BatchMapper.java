package neo.spider.admin.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import neo.spider.admin.batch.dto.BatchJobExecutionDTO;
import neo.spider.admin.batch.dto.BatchJobInstanceDTO;
import neo.spider.admin.batch.dto.BatchStepExecutionDTO;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BatchMapper {
	int countJobs(BatchJobInstanceDTO paramDto);

	List<BatchJobInstanceDTO> findJobs(@Param("paramDto") BatchJobInstanceDTO paramDto, @Param("limit") int limit, @Param("offset") int offset);

	BatchJobInstanceDTO findJobById(int instanceId, int executionId);

	BatchJobExecutionDTO findStepById(int id);

	List<BatchStepExecutionDTO> findStepsByJobId(int id);
}
