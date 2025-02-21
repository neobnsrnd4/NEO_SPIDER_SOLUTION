package neo.spider.solution.flowcontrol.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import neo.spider.solution.flowcontrol.dto.BulkheadDto;
import neo.spider.solution.flowcontrol.dto.RateLimiterDto;
import neo.spider.solution.flowcontrol.dto.UpdateConfigDto;
import neo.spider.solution.flowcontrol.mapper.BulkheadMapper;
import neo.spider.solution.flowcontrol.mapper.RateLimiterMapper;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.stereotype.Service;
import neo.spider.solution.flowcontrol.trie.TrieRegistry;

import java.time.Duration;

@Service
public class FlowConfigUpdateService {
    private final BulkheadRegistry bulkheadRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;
    private final ObjectMapper objectMapper;
    private final TrieRegistry trieRegistry;


    public FlowConfigUpdateService(BulkheadRegistry bulkheadRegistry,
                                   RateLimiterRegistry rateLimiterRegistry,
                                   ObjectMapper objectMapper,
                                   BulkheadMapper bulkheadMapper,
                                   RateLimiterMapper rateLimiterMapper,
                                   TrieRegistry trieRegistry) {
        this.bulkheadRegistry = bulkheadRegistry;
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.objectMapper = objectMapper;
        this.trieRegistry = trieRegistry;
    }

    public void updateConfig(String message) {
        try{
            UpdateConfigDto updateConfigDto = objectMapper.readValue(message, UpdateConfigDto.class);
            int doing = updateConfigDto.getDoing();
            if (updateConfigDto.getType() == 0){
                //bulkhead
                String url = updateConfigDto.getName();
                BulkheadDto bulkheadDto = updateConfigDto.getBulkhead();
                if (doing == 0){
//                    BulkheadDto configDto = bulkheadMapper.findById(id);
                    BulkheadConfig newConfig = BulkheadConfig.custom()
                            .maxConcurrentCalls(bulkheadDto.getMaxConcurrentCalls())
                            .maxWaitDuration(Duration.ofSeconds(bulkheadDto.getMaxWaitDuration()))
                            .build();

                    if (bulkheadRegistry.find(url).isPresent()){
                        //update
                        bulkheadRegistry.bulkhead(url).changeConfig(newConfig);
                    } else {
                        //create
                        bulkheadRegistry.bulkhead(url, newConfig);
                        trieRegistry.getBulkheadTrie().insert(url);
                    }
                } else {
                    //delete
                    if(bulkheadRegistry.find(url).isPresent()){
                        bulkheadRegistry.remove(url);
                        trieRegistry.getBulkheadTrie().delete(url);
                    }
                }
            } else if (updateConfigDto.getType() == 1){
                //rateLimiter
                String url = updateConfigDto.getName();
                RateLimiterDto rateLimiterDto = updateConfigDto.getRateLimiter();
                if (doing == 0){
                    RateLimiterConfig newConfig = RateLimiterConfig.custom()
                            .limitForPeriod(rateLimiterDto.getLimitForPeriod())
                            .limitRefreshPeriod(java.time.Duration.ofSeconds(rateLimiterDto.getLimitRefreshPeriod()))
                            .timeoutDuration(java.time.Duration.ofSeconds(rateLimiterDto.getTimeoutDuration()))
                            .build();


                    if (rateLimiterRegistry.find(url).isPresent()){
                        //update
                        RateLimiter newRateLimiter = RateLimiter.of(url, newConfig);
                        rateLimiterRegistry.replace(url, newRateLimiter);
                    } else {
                        //create
                        rateLimiterRegistry.rateLimiter(url, newConfig);
                        trieRegistry.getRateLimiterTrie().insert(url);
                    }
                } else {
                    //delete
                    if (rateLimiterRegistry.find(url).isPresent()){
                        rateLimiterRegistry.remove(url);
                        trieRegistry.getRateLimiterTrie().delete(url);
                    }
                }
            }

        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

}