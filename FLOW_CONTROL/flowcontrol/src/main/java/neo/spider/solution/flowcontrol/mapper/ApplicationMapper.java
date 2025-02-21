package neo.spider.solution.flowcontrol.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplicationMapper {
    long findIdByName(String appName);
    int count(String name);
}
