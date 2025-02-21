package neo.spider.solution.flowcontrol.trie;

import org.springframework.stereotype.Component;

@Component
public class TrieRegistry {
    private final BulkheadTrie BulkheadTrie;
    private final RateLimiterTrie RateLimiterTrie;

    public TrieRegistry() {
        BulkheadTrie = new BulkheadTrie();
        RateLimiterTrie = new RateLimiterTrie();
    }

    public BulkheadTrie getBulkheadTrie() {
        return BulkheadTrie;
    }

    public RateLimiterTrie getRateLimiterTrie() {
        return RateLimiterTrie;
    }
}
