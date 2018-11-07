package org.cn.wzy.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static org.cn.wzy.util.PropertiesUtil.IntegerValue;
import static org.cn.wzy.util.PropertiesUtil.StringValue;


public class TokenUtil {

    public static String tokens(Map<String, Object> claims) {
        String SecretKey = StringValue("secretKey");
        //获取当前的时间
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(System.currentTimeMillis());
        calendar.setTime(date);
        //向后退后的秒数
        int time = IntegerValue("millisecond");
        calendar.add(Calendar.MILLISECOND, time);
        Date endTime = calendar.getTime();
        String issuer = StringValue("JWT_ISSUER");
        String aud = StringValue("JWT_AUD");
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

    public static Object tokenValueOf(String token, String key) {
        Claims claims = parse(token);
        if (claims == null || claims.get(key) == null)
            return null;
        return claims.get(key);
    }

    public static Claims parse(String jwt) {
        String secretKey = StringValue("secretKey");
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt).getBody();
        return claims;
    }
}
