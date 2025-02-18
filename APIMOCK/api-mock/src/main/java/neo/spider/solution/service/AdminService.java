package neo.spider.solution.service;

import org.springframework.stereotype.Service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import neo.spider.solution.dto.AdminApiDTO;
import neo.spider.solution.mapper.AdminMapper;

@Service
@RequiredArgsConstructor
public class AdminService {
	
	private final AdminMapper adminMapper;

	
	// 검색 + 페이징 처리된 API 목록 가져오기
    public List<AdminApiDTO> findApisByPage(int page, int size, AdminApiDTO paramDto) {
        int offset = (page - 1) * size; // 페이지네이션을 위한 오프셋 계산
        return adminMapper.findAll(paramDto, size, offset);
    }

    // 검색된 API 개수 가져오기
    public int countApis(AdminApiDTO paramDto) {
        return adminMapper.countByApiName(paramDto);
    }
}
