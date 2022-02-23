package com.wu.config.security.component;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *  JwtTokenUtil工具类
 * @author cxj
 */
@Component
public class JwtTokenUtil {
    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     *  根据用户信息生成token
     * @author cxj
     */
    public  String generateToken(UserDetails userDetails){
    Map<String, Object> claims = new HashMap<>();
    claims.put(CLAIM_KEY_USERNAME,userDetails.getUsername());
    claims.put(CLAIM_KEY_CREATED,new Date());
    return generateToken(claims);

  }

    /**
     *  从token获取登陆用户名
     * @author cxj
     */
    public String getUsernameFormToken(String token){
        String username;
        try {
            Claims claims= getCliamsFormToken(token);
            username =claims.getSubject();
        } catch (Exception e) {
          username=null;
        }
   return  username;
    }



    /**
     *  从token获取荷载
     * @author cxj
     */
    private Claims getCliamsFormToken(String token) {
        Claims claims=null;
        try {
            claims=Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  claims;
    }


    /**
     *  根据荷载生成jwt token
     * @author cxj
     */
    private  String generateToken(Map<String, Object> claims){

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateeExpiration())
                .signWith(SignatureAlgorithm.HS512,secret)
                .compact();
    }
    /**
     *  生成  jwt token 失效时间
     * @author cxj
     */
    private Date generateeExpiration() {
        //
        return  new Date(System.currentTimeMillis()+expiration*1000);
    }
    /** 验证token失效，token里面的用户名和UserDetails的用户名是否一致
     *  生成  jwt token 失效时间
     * @author cxj
     */
    public Boolean validateToken(String token,UserDetails userDetails) {
        String username = getUsernameFormToken(token);
        return username.equals(userDetails.getUsername())&& !isTokenExpired(token);


    }


    /**
     * 判断 token是否 失效时间
     * @author cxj
     */
    private boolean isTokenExpired(String token) {
       Date expireedDate=  getExpiredDateFromToken(token);
       //在当前时间之前就是失效
       return  expireedDate.before(new Date());
    }
    /**
     * 从token获取失效时间
     * @author cxj
     */
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getCliamsFormToken(token);
        Date expiration = claims.getExpiration();
        return expiration;
    }
    /**
     * 判断token是否可以被刷新
     * @author cxj
     */
    public boolean canRefresh(String token){
        return  !isTokenExpired(token);
    }
    /**
     *token刷新。把失效时间改一下
     * @author cxj
     */
    public  String refreshToken(String token){
        Claims claims = getCliamsFormToken(token);
         claims.put(CLAIM_KEY_CREATED,new Date());
         return  generateToken(claims);
    }
}
