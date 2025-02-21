package neo.spider.admin.flow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import neo.spider.admin.flow.dto.CreateApplicationDto;
import neo.spider.admin.flow.dto.SearchApplicationResultDto;
import neo.spider.admin.flow.dto.SearchDto;
import neo.spider.admin.flow.dto.UpdateApplicationDto;
import neo.spider.admin.flow.mapper.ApplicationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ControlService {

    private final ApplicationMapper applicationMapper;
    private final MessagePublisher publisher;
    private final ObjectMapper objectMapper;

    public ControlService(ApplicationMapper applicationMapper, MessagePublisher publisher, ObjectMapper objectMapper) {
        this.applicationMapper = applicationMapper;
        this.publisher = publisher;
        this.objectMapper = objectMapper;
    }

    public List<SearchApplicationResultDto> find(SearchDto dto, int page, int size){
        int offset = (page - 1) * size;
        return applicationMapper.findSelective(dto, size, offset);
    }

    public int count(SearchDto dto){
        return applicationMapper.countSelective(dto);
    }

    public int create(CreateApplicationDto dto){
        return applicationMapper.create(dto);
    }

    public int delete(long id){
        return applicationMapper.delete(id);
    }

    public SearchApplicationResultDto findById(long id){
        return applicationMapper.findById(id);
    }

    public int update(UpdateApplicationDto dto){
        return applicationMapper.update(dto);
    }

//    public int create(ConfigDto dto){
//        try {
//            int result = configMapper.create(dto);
//            if (result > 0) {
//                try {
//                    String json = objectMapper.writeValueAsString(dto);
//                    publisher.publish(dto.getAppName(), json);
//                } catch (JsonProcessingException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            return result;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public int deleteByIds(List<Long> ids){
//        try{
//            int result = 0;
//            for (Long id : ids){
//                result += configMapper.delete(id);
//            }
//            return result;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public int update(ConfigDto dto){
//        try {
//            int result =  configMapper.update(dto);
//            if (result > 0) {
//                String json = objectMapper.writeValueAsString(dto);
//                System.out.println(json);
//                publisher.publish(dto.getAppName(), json);
//            }
//            return result;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}
