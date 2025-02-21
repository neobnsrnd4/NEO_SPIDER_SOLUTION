package neo.spider.admin.e2e.service;

import java.util.List;

import org.springframework.stereotype.Service;

import neo.spider.admin.e2e.dto.LogDTO;
import neo.spider.admin.e2e.mapper.LogMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {

	private final LogMapper logMapper;

	public List<LogDTO> findByTraceId(String traceId) {
		return logMapper.findByTraceId(traceId);
	}
	
	public int countDelayRequestLogs(LogDTO paramDto) {
        return logMapper.countDelayRequestLogs(paramDto);
    }

    public List<LogDTO> findDelayRequestLogs(LogDTO paramDto, int page, int size) {
        int offset = (page - 1) * size;
        return logMapper.findDelayRequestLogs(paramDto, size, offset);
    }

    public int countDelayQueryLogs(LogDTO paramDto) {
        return logMapper.countDelayQueryLogs(paramDto);
    }

    public List<LogDTO> findDelayQueryLogs(LogDTO paramDto, int page, int size) {
        int offset = (page - 1) * size;
        return logMapper.findDelayQueryLogs(paramDto, size, offset);
    }

    public int countErrorLogs(LogDTO paramDto) {
        return logMapper.countErrorLogs(paramDto);
    }
    
    public List<LogDTO> findErrorLogs(LogDTO paramDto, int page, int size) {
        int offset = (page - 1) * size;
        return logMapper.findErrorLogs(paramDto, size, offset);
    }
    
	public List<LogDTO> findInfluenceLogs(int page, int size, String searchType, String searchKeyword) {
		int offset = (page - 1) * size;
	    return logMapper.findInfluenceLogs(size, offset, searchType, searchKeyword);
	}
	
    public int countInfluenceLogs(String searchType, String searchKeyword) {
        return logMapper.countInfluenceLogs(searchType, searchKeyword);
    }

}
