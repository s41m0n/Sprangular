package it.polito.ai.lab2.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

  public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

  @Value("${jwt.secret}")
  private String secret;

  //Retrieve username from JWT Token
  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  //Retrieve expiration date from JWT Token
  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimResolver) {
    return claimResolver.apply(getAllClaimsFromToken(token));
  }

  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
  }

  //Method to check whether the token is expired or not
  private Boolean isTokenExpired(String token) {
    return getExpirationDateFromToken(token).before(new Date());
  }

  //Generate token for user
  public String generateToken(UserDetails userDetails) {
    HashMap<String, Object> roles = new HashMap<>();
    roles.put("roles", userDetails.getAuthorities());
    return Jwts.builder()
        .setClaims(roles)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    return getUsernameFromToken(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
  }
}
