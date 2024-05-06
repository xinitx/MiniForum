package com.weibo.filter;

import com.weibo.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@Order(0)
public class globalFilter implements GlobalFilter {
    private static final List<String> white_List = Arrays.asList("/login", "/logout","/register","/all");
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {


        //获取request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        System.out.println(request.getURI());
        //白名单
        String path = request.getURI().getPath();
        boolean isWhiteUrl = white_List.stream().anyMatch(path::endsWith);
        if(isWhiteUrl) return chain.filter(exchange);

        //获取token
        String token = request.getHeaders().getFirst("token");
        //判断token是否存在
        if(StringUtils.isBlank(token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        Claims claims = JwtUtils.getClaimsBody(token);
        if(claims==null){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        String role =  (String) claims.get("role");
        //判断role是否存在
        if(StringUtils.isBlank(role)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        String roleRequire = request.getQueryParams().getFirst("roleRequire");
        if(roleRequire != null && !role.equals(roleRequire)){
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        //是否是过期
        int result = JwtUtils.verifyToken(token);
        if(result == 1 || result  == 2){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }

}
