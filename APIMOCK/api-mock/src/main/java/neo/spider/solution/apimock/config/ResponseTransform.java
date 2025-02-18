package neo.spider.solution.apimock.config;

import org.springframework.stereotype.Component;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;

@Component
public class ResponseTransform extends ResponseTransformer{

	@Override
    public Response transform(Request request, Response response, FileSource files, Parameters parameters) {
		HttpHeaders existingHeaders = response.getHeaders();

        // 기존 CORS 헤더가 있는지 개별적으로 확인
        boolean hasCorsOrigin = existingHeaders.getHeader("Access-Control-Allow-Origin").isPresent();
        boolean hasCorsMethods = existingHeaders.getHeader("Access-Control-Allow-Methods").isPresent();
        boolean hasCorsHeaders = existingHeaders.getHeader("Access-Control-Allow-Headers").isPresent();
        
        HttpHeaders newHeaders = existingHeaders;

        if (!hasCorsOrigin) {
        	newHeaders = newHeaders.plus(new HttpHeader("Access-Control-Allow-Origin", "*"));
        }
        if (!hasCorsMethods) {
        	newHeaders = newHeaders.plus(new HttpHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"));
        }
        if (!hasCorsHeaders) {
        	newHeaders = newHeaders.plus(new HttpHeader("Access-Control-Allow-Headers", "Content-Type, Authorization"));
        }

        return Response.Builder.like(response)
                .but()
                .headers(newHeaders) // 중복 방지 후 새 헤더 적용
                .build();
    }

    @Override
    public String getName() {
        return "header-transform";
    }

    @Override
    public boolean applyGlobally() {
        return true;	// 모든 응답에 적용하기 위해서, stub 저장시에도 적용됨
    }
	
}
