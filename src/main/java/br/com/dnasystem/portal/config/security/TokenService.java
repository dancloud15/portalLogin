package br.com.dnasystem.portal.config.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.dnasystem.portal.modelo.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {
	
	@Value("${dna.jwt.expiration}")
	private String expiration;
	
	@Value("${dna.jwt.secret}")
	private String secret;

	public String createToken(Authentication authentication) {
		User logado = (User) authentication.getPrincipal();
		Date today = new Date();
		Date expirationTime = new Date(today.getTime() + Long.parseLong(expiration));
		
		return Jwts.builder()
				.setIssuer("API SISTEMA REGISTRO DNASYSTEM")
				.setSubject(logado.getId().toString())
				.setIssuedAt(today)
				.setExpiration(expirationTime)
				.signWith(SignatureAlgorithm.HS256, secret)
				.compact();
	}
	
	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Long getUserId(String token) {
		Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
		return Long.parseLong(claims.getSubject());
	}

}
