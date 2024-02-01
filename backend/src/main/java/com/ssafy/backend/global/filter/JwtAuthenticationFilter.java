package com.ssafy.backend.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.backend.global.error.WTException;
import com.ssafy.backend.global.util.JwtProvider;
import com.ssafy.backend.global.util.RedisDao;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RedisDao redisDao;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        try {
            if ("/api/member/reissue".equals(requestURI)) { // 토큰 재발급 요청
                String rtk = getToken(request.getHeader("Authorization"));

                try {
                    if (rtk != null && jwtProvider.validateToken(rtk)) {
                        Long seq = jwtProvider.getSeq(rtk);
                        request.setAttribute("seq", seq);

                        String token = (String) redisDao.readFromRedis("rtk:" + seq);

                        if (token == null) {
                            throw new WTException("세션이 만료되었습니다.");
                        }
                    } else {
                        throw new WTException("세션이 만료되었습니다.");
                    }
                } catch (Exception e) {
                    throw new WTException("세션이 만료되었습니다.");
                }
            } else { // 그 외의 모든 경우는 atk으로 사용자 검증
                String atk = getToken(request.getHeader("Authorization"));

                try {
                    if (atk != null && jwtProvider.validateToken(atk)) {
                        Long seq = jwtProvider.getSeq(atk);
                        request.setAttribute("seq", seq);

                        String token = (String) redisDao.readFromRedis("atk:" + seq);

                        if (token == null) {
                            throw new WTException("세션이 만료되었습니다.");
                        }

                    } else {
                        throw new WTException("세션이 만료되었습니다.");
                    }
                } catch (Exception e) {
                    throw new WTException("세션이 만료되었습니다.");
                }
            }
            filterChain.doFilter(request, response);
        } catch (WTException e) {
            ObjectMapper mapper = new ObjectMapper();

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding("utf-8");
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("message", e.getMessage());

            mapper.writeValue(response.getWriter(), resultMap);
        }

    }

    private String getToken(String header) {
        if (header != null && header.startsWith("Bearer")) {
            return header.substring("Bearer ".length());
        }
        return null;
    }
}






