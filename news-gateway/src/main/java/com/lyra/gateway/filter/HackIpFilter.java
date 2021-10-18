package com.lyra.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyra.result.GraceJSONResult;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.utils.JsonUtil;
import com.lyra.utils.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@Component
public class HackIpFilter implements GlobalFilter, Ordered {
    @Autowired
    private RedisOperator redisOperator;

    private Logger logger = LoggerFactory.getLogger(HackIpFilter.class);

    private static final String REDIS_VISIT_IP = "redis_visit_ip";

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        InetSocketAddress remoteAddress = request.getRemoteAddress();

        ServerHttpResponse response = exchange.getResponse();

        if (remoteAddress == null) {
            logger.warn("========{} request Ip获取为空", logger.getName());

            DataBuffer wrap = response.bufferFactory().wrap("".getBytes(StandardCharsets.UTF_8));

            return response.writeWith(Mono.just(wrap));
        }


        InetAddress address = remoteAddress.getAddress();
        String hostAddress = address.getHostAddress();

        logger.info("=======userIp: {}", hostAddress);

        // 设置key + ip 并设置一个时间段 filter获取key + ip 判断这个value > 100 则拦截

        String visitIpNumber = redisOperator.get(REDIS_VISIT_IP + ":" + hostAddress);

        if (visitIpNumber == null) {
            redisOperator.set(REDIS_VISIT_IP + ":" + hostAddress, "0", 10);
        } else if (Integer.parseInt(visitIpNumber) > 10) {
            GraceJSONResult graceJSONResult = GraceJSONResult.errorCustom(ResponseStatusEnum.OFTEN_VISIT);
            String jsonResult = JsonUtil.objectForJson(graceJSONResult);

            assert jsonResult != null;
            DataBuffer wrap = response.bufferFactory().wrap(jsonResult.getBytes(StandardCharsets.UTF_8));

            return response.writeWith(Mono.just(wrap));
        }

        if (visitIpNumber != null) {
            int visitIpNumberForInteger = Integer.parseInt(visitIpNumber) + 1;
            redisOperator.set(REDIS_VISIT_IP + ":" + hostAddress, visitIpNumberForInteger + "", 60);
        }


        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
