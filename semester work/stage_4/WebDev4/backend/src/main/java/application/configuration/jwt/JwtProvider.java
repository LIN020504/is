package application.configuration.jwt;

import application.configuration.CustomUserDetailsService;
import io.jsonwebtoken.*;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static org.springframework.util.StringUtils.hasText;

// 生成，验证，解析JWT token
@Component
@Log
public class JwtProvider {
//    注入CustomUserDetailsService实例，用于验证登录名是否存在

    @Value("$(jwt.secret)")
    private String jwtSecret;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    private static final long TOKEN_VALIDITY = 1800000; //30 min
    public static final String AUTHORIZATION = "Authorization";

//    生成 JWT 令牌
    public String generateToken(String login) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

//    解析token，验证其签名和过期时间是否正确
    public boolean validateToken(String token) {
        JwtParser jwtParser = Jwts.parser().setSigningKey(jwtSecret).setAllowedClockSkewSeconds(300); // 300 seconds = 5 minutes
        Jws<Claims> claims = jwtParser.parseClaimsJws(token);
        if (claims.getBody().getExpiration().before(new Date())) {
            return false;
        } else {
            return true;
        }
    }

//    从给定的 JWT 令牌字符串中提取登录名
    public String getLoginFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

//    从给定的 JWT 令牌字符串中提取令牌的过期时间
    public Date getExpirationDate(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getExpiration();
    }

//    从 HTTP 请求中获取 JWT 令牌字符串
    public String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

}
