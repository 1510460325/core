package org.cn.wzy.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.*;

/**
 * @author wzy
 * @Date 2018/4/6 14:00
 */
public class TokenUtil {
    private static final Properties properties = new Properties();
    private static final ResourceBundle resource = ResourceBundle.getBundle("database");

    public static Object getValue(String key) {
        return resource.getString(key);
    }

    public static String tokens(Map<String, Object> claims) {
        String SecretKey = (String) getValue("secretKey");
        //获取当前的时间
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(System.currentTimeMillis());
        calendar.setTime(date);
        //向后退后的秒数
        int time = Integer.parseInt((String) getValue("millisecond"));
        calendar.add(Calendar.MILLISECOND, time);
        Date endTime = calendar.getTime();
        String issuer = (String) getValue("JWT_ISSUER");
        String aud = (String) getValue("JWT_AUD");
        JwtBuilder builder = Jwts.builder().setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, SecretKey)
                .setClaims(claims)
                .setSubject((String) claims.get("username"))
                .setIssuedAt(new Date())
                .setExpiration(endTime)
                .setIssuer(issuer)
                .setAudience(aud);
        return builder.compact();
    }

    /**
     * 从jwt中获取用户信息集合
     *
     * @param jsonWebToken
     * @return
     */
    private static Claims parseJWT(String jsonWebToken) {
        String secretKey = (String) getValue("secretKey");
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jsonWebToken).getBody();
            return claims;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 通过key从jwt中获取信息
     *
     * @param token
     * @param key
     * @return
     */
    public static Object tokenValueOf(String token, String key) {
        Claims claims = parseJWT(token);
        if (claims == null || claims.get(key) == null)
            return null;
        return claims.get(key);
    }
}
