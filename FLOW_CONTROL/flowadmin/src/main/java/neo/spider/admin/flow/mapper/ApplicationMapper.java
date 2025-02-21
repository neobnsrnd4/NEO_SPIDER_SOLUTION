package neo.spider.admin.flow.mapper;

import neo.spider.admin.flow.dto.CreateApplicationDto;
import neo.spider.admin.flow.dto.SearchApplicationResultDto;
import neo.spider.admin.flow.dto.SearchDto;
import neo.spider.admin.flow.dto.UpdateApplicationDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApplicationMapper {
    List<SearchApplicationResultDto> findSelective(SearchDto param, @Param("size") int size, @Param("offset") int offset);
    int countSelective(SearchDto param);
    int create(CreateApplicationDto param);
    int delete(long id);
    SearchApplicationResultDto findById(long id);
    long findByApplicationCategory(CreateApplicationDto param);
    int update(UpdateApplicationDto param);
    int updateModified_date(long id);
}
