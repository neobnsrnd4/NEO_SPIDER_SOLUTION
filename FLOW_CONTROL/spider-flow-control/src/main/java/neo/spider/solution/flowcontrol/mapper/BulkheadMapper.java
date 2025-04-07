package neo.spider.solution.flowcontrol.mapper;

import neo.spider.solution.flowcontrol.dto.BulkheadDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BulkheadMapper {
    BulkheadDto findById(long id);
    List<BulkheadDto> findAll(long application_id);
}
