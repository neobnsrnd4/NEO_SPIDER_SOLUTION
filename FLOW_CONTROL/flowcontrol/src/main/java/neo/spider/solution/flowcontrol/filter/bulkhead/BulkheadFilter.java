package neo.spider.solution.flowcontrol.filter.bulkhead;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import neo.spider.solution.flowcontrol.trie.TrieRegistry;

import java.io.IOException;

public class BulkheadFilter implements Filter {

    private final BulkheadRegistry bulkheadRegistry;
    private final TrieRegistry trieRegistry;

    public BulkheadFilter(BulkheadRegistry bulkheadRegistry,
                          TrieRegistry trieRegistry) {
        this.bulkheadRegistry = bulkheadRegistry;
        this.trieRegistry = trieRegistry;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String uri = request.getRequestURI();

        String name = trieRegistry.getBulkheadTrie().search(uri);

        if (name == null){
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            Bulkhead bulkhead = bulkheadRegistry.bulkhead(name);
            if (bulkhead.tryAcquirePermission()){
                try{
                    filterChain.doFilter(servletRequest, servletResponse);
                } finally {
                    bulkhead.releasePermission();
                }
            } else {
                HttpServletResponse resp = (HttpServletResponse) servletResponse;
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write("{ \"error\": \"Too many concurrent requests. Please try again later.\" }");
                resp.getWriter().flush();
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
