package neo.spider.admin.flow.service;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import neo.spider.admin.flow.dto.SearchApplicationResultDto;
import neo.spider.admin.flow.dto.ratelimiter.RateLimiterDto;
import neo.spider.admin.flow.dto.ratelimiter.RateLimiterSearchDto;
import neo.spider.admin.flow.dto.redisPub.UpdateConfigDto;
import neo.spider.admin.flow.mapper.ApplicationMapper;
import neo.spider.admin.flow.mapper.RateLimiterRedisMapper;

@ConditionalOnProperty(name = "flow.control.enabled", havingValue = "true")
@Service
public class RateLimiterRedisService {

	private static final int TYPE = 3; // redis rateLimiter
	private final ObjectMapper objectMapper;

	private final RateLimiterRedisMapper rateLimiterRedisMapper;
	private final ApplicationMapper applicationMapper;
	private final MessagePublisher messagePublisher;

	public RateLimiterRedisService(ApplicationMapper applicationMapper, RateLimiterRedisMapper rateLimiterRedisMapper,
			ObjectMapper objectMapper, MessagePublisher messagePublisher) {
		this.rateLimiterRedisMapper = rateLimiterRedisMapper;
		this.objectMapper = objectMapper;
		this.applicationMapper = applicationMapper;
		this.messagePublisher = messagePublisher;
	}

	public List<RateLimiterSearchDto> findByApplication(long applicationId, int type, String url) {
		return rateLimiterRedisMapper.findByApplication(applicationId, type, url);
	}

	public boolean create(RateLimiterDto newRateLimiter) {
		if (newRateLimiter.getType() == 0) {
			newRateLimiter.setUrl("global");
		} else if (newRateLimiter.getType() == 2) {
			newRateLimiter.setUrl("personal");
		}
		int result = rateLimiterRedisMapper.create(newRateLimiter);
		if (result > 0) {
			applicationMapper.updateModified_date(newRateLimiter.getApplicationId());
			UpdateConfigDto updateConfigDto = new UpdateConfigDto();
			updateConfigDto.setType(TYPE);
			updateConfigDto.setId(newRateLimiter.getRatelimiterId());
			updateConfigDto.setDoing(0);
			updateConfigDto.setName(newRateLimiter.getUrl());
			updateConfigDto.setRateLimiter(newRateLimiter);
			try {
				String json = objectMapper.writeValueAsString(updateConfigDto);
				String name = applicationMapper.findById(newRateLimiter.getApplicationId()).getApplicationName();
				messagePublisher.publish(name, json);
			} catch (JsonProcessingException e) {
				throw new RuntimeException("전송 실패");
			}
		} else {
			return false;
		}
		return true;
	}

	public boolean delete(long ratelimiterId, String application_name) {
		RateLimiterDto rl = rateLimiterRedisMapper.findById(ratelimiterId);
		int result = rateLimiterRedisMapper.delete(ratelimiterId);
		if (result > 0) {
			applicationMapper.updateModified_date(rl.getApplicationId());
			UpdateConfigDto updateConfigDto = new UpdateConfigDto();
			updateConfigDto.setRateLimiter(rl);
			updateConfigDto.setId(ratelimiterId);
			updateConfigDto.setType(TYPE);
			updateConfigDto.setDoing(1); 
			updateConfigDto.setName(rl.getUrl());
			
			try {
				String json = objectMapper.writeValueAsString(updateConfigDto);
				messagePublisher.publish(application_name, json);
			} catch (JsonProcessingException e) {
				throw new RuntimeException("전송 실패");
			}
		} else {
			return false;
		}

		return true;
	}

	public boolean update(RateLimiterDto rl) {
		int result = rateLimiterRedisMapper.update(rl);
		if (result > 0) {
			applicationMapper.updateModified_date(rl.getApplicationId());
			UpdateConfigDto updateConfigDto = new UpdateConfigDto();
			updateConfigDto.setId(rl.getRatelimiterId());
			updateConfigDto.setType(TYPE);
			updateConfigDto.setDoing(0);
			updateConfigDto.setName(rl.getUrl());
			updateConfigDto.setRateLimiter(rl);
			try {
				String json = objectMapper.writeValueAsString(updateConfigDto);
				messagePublisher.publish(applicationMapper.findById(rl.getApplicationId()).getApplicationName(), json);
			} catch (JsonProcessingException e) {
				throw new RuntimeException("전송 실패");
			}
		} else {
			return false;
		}

		return true;
	}

	public RateLimiterDto findById(long rateLimiterId) {
		return rateLimiterRedisMapper.findById(rateLimiterId);
	}

	public boolean toggleRatelimiter(SearchApplicationResultDto dto) {

		try {
			UpdateConfigDto updateConfigDto = new UpdateConfigDto();
			RateLimiterDto rateLimiterDto = new RateLimiterDto();
			rateLimiterDto.setRatelimiterMode(dto.getRatelimiterMode());
			updateConfigDto.setName(dto.getApplicationName());
			updateConfigDto.setRateLimiter(rateLimiterDto);
			String json = objectMapper.writeValueAsString(updateConfigDto);
			String name = dto.getApplicationName();
			messagePublisher.publish(name, json);

			applicationMapper.updateRatelimiter_mode(dto);

			return true;
		} catch (JsonProcessingException e) {
			throw new RuntimeException("전송 실패");
		}
	}
}
