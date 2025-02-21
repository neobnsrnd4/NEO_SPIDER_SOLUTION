package neo.spider.admin.flow.mapper;

import neo.spider.admin.flow.dto.ratelimiter.RateLimiterDto;
import neo.spider.admin.flow.dto.ratelimiter.RateLimiterSearchDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RateLimiterMapper {
    List<RateLimiterSearchDto> findByApplication(@Param("applicationId") long applicationId, @Param("type") int type, @Param("url") String url);
    int create(RateLimiterDto newRateLimiter);
    int delete(long ratelimiterId);
    RateLimiterDto findById(long ratelimiterId);
    int update(RateLimiterDto rl);
}
