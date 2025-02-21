package neo.spider.admin.flow.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import neo.spider.admin.flow.dto.ratelimiter.RateLimiterDto;
import neo.spider.admin.flow.dto.ratelimiter.RateLimiterSearchDto;
import neo.spider.admin.flow.dto.redisPub.UpdateConfigDto;
import neo.spider.admin.flow.mapper.ApplicationMapper;
import neo.spider.admin.flow.mapper.RateLimiterMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RateLimiterService {

    private static final int TYPE = 1; //rateLimiter
    private final ObjectMapper objectMapper;

    private final RateLimiterMapper rateLimiterMapper;
    private final ApplicationMapper applicationMapper;
    private final MessagePublisher messagePublisher;

    public RateLimiterService(ApplicationMapper applicationMapper , RateLimiterMapper rateLimiterMapper, ObjectMapper objectMapper, MessagePublisher messagePublisher) {
        this.rateLimiterMapper = rateLimiterMapper;
        this.objectMapper = objectMapper;
        this.applicationMapper = applicationMapper;
        this.messagePublisher = messagePublisher;
    }

    public List<RateLimiterSearchDto> findByApplication(long applicationId, int type, String url) {
        return rateLimiterMapper.findByApplication(applicationId, type, url);
    }

    public boolean create(RateLimiterDto newRateLimiter){
        if (newRateLimiter.getType() == 0){
            newRateLimiter.setUrl("global");
        } else if (newRateLimiter.getType() == 2){
            newRateLimiter.setUrl("personal");
        }
        int result = rateLimiterMapper.create(newRateLimiter);
        if (result > 0){
            applicationMapper.updateModified_date(newRateLimiter.getApplicationId());
            UpdateConfigDto updateConfigDto = new UpdateConfigDto();
            updateConfigDto.setType(TYPE);
            updateConfigDto.setId(newRateLimiter.getRatelimiterId());
            updateConfigDto.setDoing(0);
            updateConfigDto.setName(newRateLimiter.getUrl());
            updateConfigDto.setRateLimiter(newRateLimiter);
            try{
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

    public boolean delete(long ratelimiterId, String application_name){
        RateLimiterDto rl = rateLimiterMapper.findById(ratelimiterId);
        int result = rateLimiterMapper.delete(ratelimiterId);
        if (result > 0) {
            applicationMapper.updateModified_date(rl.getApplicationId());
            UpdateConfigDto updateConfigDto = new UpdateConfigDto();
            updateConfigDto.setId(ratelimiterId);
            updateConfigDto.setType(TYPE);
            updateConfigDto.setDoing(1); // delete
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

    public boolean update(RateLimiterDto rl){
        int result = rateLimiterMapper.update(rl);
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

    public RateLimiterDto findById(long rateLimiterId){
        return rateLimiterMapper.findById(rateLimiterId);
    }
}
