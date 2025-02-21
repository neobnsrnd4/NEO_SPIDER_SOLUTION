package neo.spider.admin.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import neo.spider.admin.common.dto.FwkUserDTO;

@Mapper
public interface AuthMapper {
	
	List<FwkUserDTO> findById(@Param("username") String username);
	void updateUserSession(@Param("username") String username, @Param("sessionid") String sessionid, @Param("dtime") String dtime);
	void insertUser(FwkUserDTO user);
}
