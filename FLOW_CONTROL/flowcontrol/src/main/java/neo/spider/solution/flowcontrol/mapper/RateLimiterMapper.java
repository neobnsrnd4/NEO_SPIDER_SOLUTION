package neo.spider.solution.flowcontrol.mapper;

import neo.spider.solution.flowcontrol.dto.RateLimiterDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface RateLimiterMapper {
    RateLimiterDto findById(long id);
    List<RateLimiterDto> findAll(long application_id);
}
