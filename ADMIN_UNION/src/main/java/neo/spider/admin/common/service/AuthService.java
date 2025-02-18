package neo.spider.admin.common.service;

import java.util.List;

import org.springframework.stereotype.Service;

import neo.spider.admin.common.dto.FwkUserDTO;
import neo.spider.admin.common.mapper.AuthMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthMapper authMapper;

	public List<FwkUserDTO> findSById(String username) {
		return authMapper.findById(username);
	}
	public void updateUserSession(String username, String lastSession, String dtime) {
		authMapper.updateUserSession(username, lastSession, dtime);
	}
	public void insertUser(FwkUserDTO user) {
		authMapper.insertUser(user);
	}
	
	public void isLogin(String currentSessionId , String newSessionId) {
		
	}
	

}
