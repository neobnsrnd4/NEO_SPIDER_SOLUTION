package neo.spider.solution.flowcontrol;

import neo.spider.solution.flowcontrol.trie.RateLimiterTrie;

public class Test {
    public static void main(String[] args) {
        RateLimiterTrie trie = new RateLimiterTrie();

        trie.insert("/product/item/detail");
        trie.insert("/product/item/detail/more");
        trie.insert("/product/*");

        System.out.println("🔍 Searching for: /product/item/detail");
        System.out.println(trie.search("/product/item/detail"));
        System.out.println();

        System.out.println("🔍 Searching for: /product/item/detail/more");
        System.out.println(trie.search("/product/item/detail/more"));
        System.out.println();

        System.out.println("🔍 Searching for: /product/item/detail/extra");
        System.out.println(trie.search("/product/item/detail/extra"));
        System.out.println();

        System.out.println("🔍 Searching for: /human/number/detail");
        System.out.println(trie.search("/human/number/detail"));
        System.out.println();

        System.out.println("Deleting: /product/item/detail");
        trie.delete("/product/item/detail");
        System.out.println();

        System.out.println("🔍 Searching for: /product/item/detail");
        System.out.println(trie.search("/product/item/detail"));
        System.out.println();
    }
}
