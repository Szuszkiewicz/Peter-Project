package com.Peter.utils;

import com.Peter.common.Constants;
import com.Peter.dto.UserInfoDto;
import com.Peter.dto.UserTokenInfoDto;
import com.alibaba.fastjson2.JSONObject;
import com.auth0.jwt.JWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class TokenUtils {

    public static Long getUserId() {
        return getCurrentUser().getId();
    }

    public static UserInfoDto getCurrentUser() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();// 获取当前请求
            if (attributes == null) {
                return new UserInfoDto();
            }

            HttpServletRequest request = attributes.getRequest();// 获取请求头中的token
            String token = request.getHeader(Constants.TOKEN);// 解析token

            if (StringUtils.isNotBlank(token)) {
                UserTokenInfoDto tokenInfo =
                        JSONObject.parseObject(JWT.decode(token).getAudience().getFirst(), UserTokenInfoDto.class);
// 将token中的信息转换成UserInfoDto
                UserInfoDto result = new UserInfoDto();
                BeanUtils.copyProperties(tokenInfo, result);
                return result;
            }
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
        }

        return new UserInfoDto();
    }
}
