package neo.spider.solution.flowcontrol.init;

import neo.spider.solution.flowcontrol.ConfigurationProp;
import neo.spider.solution.flowcontrol.dto.BulkheadDto;
import neo.spider.solution.flowcontrol.mapper.ApplicationMapper;
import neo.spider.solution.flowcontrol.mapper.BulkheadMapper;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import neo.spider.solution.flowcontrol.trie.TrieRegistry;

import java.time.Duration;
import java.util.List;

@Component
public class BulkheadInitializer {

    private final BulkheadRegistry bulkheadRegistry;
    private final BulkheadMapper mapper;
    private final ConfigurationProp prop;
    private final ApplicationMapper applicationMapper;
    private final TrieRegistry trieRegistry;

    public BulkheadInitializer(BulkheadRegistry bulkheadRegistry,
                               BulkheadMapper mapper,
                               ConfigurationProp prop,
                               ApplicationMapper applicationMapper,
                               TrieRegistry trieRegistry) {
        this.bulkheadRegistry = bulkheadRegistry;
        this.mapper = mapper;
        this.prop = prop;
        this.applicationMapper = applicationMapper;
        this.trieRegistry = trieRegistry;
    }

    @PostConstruct
    public void init(){
        if (applicationMapper.count(prop.getName()) == 0){
            return;
        }
        long id = applicationMapper.findIdByName(prop.getName());
        List<BulkheadDto> bulkheads = mapper.findAll(id);
        for (BulkheadDto bulkhead : bulkheads) {
            BulkheadConfig bulkheadConfig = BulkheadConfig.custom()
                    .maxConcurrentCalls(bulkhead.getMaxConcurrentCalls())
                    .maxWaitDuration(Duration.ofSeconds(bulkhead.getMaxWaitDuration()))
                    .build();
            bulkheadRegistry.bulkhead(bulkhead.getUrl(), bulkheadConfig);
            trieRegistry.getBulkheadTrie().insert(bulkhead.getUrl());
        }
    }
}
