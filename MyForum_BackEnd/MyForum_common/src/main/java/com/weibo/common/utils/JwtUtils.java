package com.weibo.common.utils;

import io.jsonwebtoken.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JwtUtils {
    // TOKEN的有效期半天（S）
    private static final int TOKEN_TIME_OUT = 1_800;
    // 加密KEY
    private static final String TOKEN_ENCRY_KEY = "ustc";
    // 最小刷新间隔(S)
    private static final int REFRESH_TIME = 300;
    public static String getToken(int id, String role){
        Map<String, Object> claimMaps = new HashMap<>();
        claimMaps.put("id",id);
        claimMaps.put("role",role);
        long currentTime = System.currentTimeMillis();
        System.out.println(new Date(currentTime));
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(currentTime))  //签发时间
                .setSubject("system")  //说明
                .setIssuer("weibo") //签发者信息
                .setAudience("user")  //接收用户
                .compressWith(CompressionCodecs.GZIP)  //数据压缩方式
                .signWith(SignatureAlgorithm.HS512, TOKEN_ENCRY_KEY) //加密方式
                .setExpiration(new Date(currentTime + TOKEN_TIME_OUT * 1000))  //过期时间戳
                .addClaims(claimMaps) //cla信息
                .compact();
    }
    //获取token中的claims信息
    private static Jws<Claims> getJws(String token) {
        return Jwts.parser()
                .setSigningKey(TOKEN_ENCRY_KEY)
                .parseClaimsJws(token);
    }
    //获取payload body信息
    public static Claims getClaimsBody(String token) {
        try {
            return getJws(token).getBody();
        }catch (ExpiredJwtException e){
            return null;
        }
    }
    //获取hearder body信息
    public static JwsHeader getHeaderBody(String token) {
        return getJws(token).getHeader();
    }
    //是否过期
    public static int verifyToken(String token) {
        Claims claims = getClaimsBody(token);
        if(claims==null){
            return 1;
        }
        try {
            claims.getExpiration()
                    .before(new Date());
            // 需要自动刷新TOKEN
            if((claims.getExpiration().getTime()-System.currentTimeMillis())>REFRESH_TIME*1000){
                return -1;
            }else {
                return 0;
            }
        } catch (ExpiredJwtException ex) {
            return 1;
        }catch (Exception e){
            return 2;
        }
    }
}
