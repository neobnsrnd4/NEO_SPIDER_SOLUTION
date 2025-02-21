package neo.spider.admin.flow.mapper;

import neo.spider.admin.flow.dto.bulkhead.BulkheadDto;
import neo.spider.admin.flow.dto.bulkhead.BulkheadSearchDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BulkheadMapper {
    List<BulkheadSearchDto> findByApplication(@Param("applicationId") long applicationId, @Param("url") String url);
    int create(BulkheadDto newBulkhead);
    int delete(long bulkheadId);
    int update(BulkheadDto dto);
    BulkheadDto findById(long bulkheadId);
}
