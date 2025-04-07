package neo.spider.admin.flow.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import neo.spider.admin.flow.dto.bulkhead.BulkheadDto;
import neo.spider.admin.flow.dto.bulkhead.BulkheadSearchDto;
import neo.spider.admin.flow.dto.redisPub.UpdateConfigDto;
import neo.spider.admin.flow.mapper.ApplicationMapper;
import neo.spider.admin.flow.mapper.BulkheadMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BulkheadService {

    private static final int TYPE = 0;

    private final BulkheadMapper bulkheadMapper;
    private final ApplicationMapper applicationMapper;
    private final ObjectMapper objectMapper;
    private final MessagePublisher publisher;

    public BulkheadService(BulkheadMapper bulkheadMapper, ObjectMapper objectMapper, MessagePublisher publisher, ApplicationMapper applicationMapper) {
        this.bulkheadMapper = bulkheadMapper;
        this.objectMapper = objectMapper;
        this.publisher = publisher;
        this.applicationMapper = applicationMapper;
    }

    public List<BulkheadSearchDto> findByApplication(long applicationId, String url){
        return bulkheadMapper.findByApplication(applicationId, url);
    }

    public boolean create(BulkheadDto newBulkhead){
        int result = bulkheadMapper.create(newBulkhead);
        if (result > 0) {
            applicationMapper.updateModified_date(newBulkhead.getApplicationId());
            UpdateConfigDto updateConfigDto = new UpdateConfigDto();
            updateConfigDto.setId(newBulkhead.getBulkheadId());
            updateConfigDto.setType(TYPE);
            updateConfigDto.setDoing(0); // create, update
            updateConfigDto.setName(newBulkhead.getUrl());
            updateConfigDto.setBulkhead(newBulkhead);
            try{
                String json = objectMapper.writeValueAsString(updateConfigDto);
                String name = applicationMapper.findById(newBulkhead.getApplicationId()).getApplicationName();
                publisher.publish(name, json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean delete(long bulkheadId, String application_name){
        BulkheadDto bh = bulkheadMapper.findById(bulkheadId);
        int result = bulkheadMapper.delete(bulkheadId);
        if (result > 0) {
            applicationMapper.updateModified_date(bh.getApplicationId());
            UpdateConfigDto updateConfigDto = new UpdateConfigDto();
            updateConfigDto.setId(bulkheadId);
            updateConfigDto.setType(TYPE);
            updateConfigDto.setDoing(1); // delete
            updateConfigDto.setName(bh.getUrl());
            try {
                String json = objectMapper.writeValueAsString(updateConfigDto);
                publisher.publish(application_name, json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("전송 실패");
            }

        } else {
            return false;
        }

        return true;
    }

    public boolean update(BulkheadDto dto){
        int result = bulkheadMapper.update(dto);
        if (result > 0) {
            applicationMapper.updateModified_date(dto.getApplicationId());
            UpdateConfigDto updateConfigDto = new UpdateConfigDto();
            updateConfigDto.setId(dto.getBulkheadId());
            updateConfigDto.setType(TYPE);
            updateConfigDto.setDoing(0);
            updateConfigDto.setName(dto.getUrl());
            updateConfigDto.setBulkhead(dto);
            try {
                String json = objectMapper.writeValueAsString(updateConfigDto);
                String name = applicationMapper.findById(dto.getApplicationId()).getApplicationName();
                publisher.publish(name, json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return false;
        }
        return true;
    }

    public BulkheadDto findById(long bulkheadId){
        return bulkheadMapper.findById(bulkheadId);
    }
}
