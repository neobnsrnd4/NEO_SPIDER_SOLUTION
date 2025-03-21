package neo.spider.solution.flowcontrol.service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;

import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import neo.spider.solution.flowcontrol.ConfigurationProp;

@Service
public class JwtProviderService {

	private final int respirationTime = 1000 * 60 * 60 * 10; // 10분
	private final ConfigurationProp prop;
	private final Key secretKey;

	public JwtProviderService(ConfigurationProp prop) {
		this.prop = prop;
		this.secretKey = getKey();
	}

	public Key getKey() {
		String secretKeyString = prop.getJwt().getSecret();
		System.out.println(secretKeyString);
		Key key = new SecretKeySpec(secretKeyString.getBytes(), "HmacSHA256");
		System.out.println(key.toString());
		return key;
	}

	public String generateToken() {

		return Jwts.builder().setSubject(UUID.randomUUID().toString()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + respirationTime)).signWith(secretKey).compact();
	}

	public Claims parseToken(String token) {
		return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
	}

}
