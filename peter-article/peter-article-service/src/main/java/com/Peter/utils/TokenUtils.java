package com.Peter.utils;

import com.Peter.common.Constants;
import com.Peter.dto.UserInfoDto;
import com.Peter.dto.UserTokenInfoDto;
import com.Peter.rpc.UserRpcService;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSONObject;
import com.auth0.jwt.JWT;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;

@Component
@Slf4j
public class TokenUtils {
    private static UserRpcService staticUserService;

    @Resource
    UserRpcService userRpcService;

    @Resource
    Constants constants;



    @PostConstruct
    public void setUserService(){staticUserService = userRpcService;}
    /**
     * 获取当前登录的用户信息
     */
    public static UserInfoDto getCurrentUser() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if(requestAttributes==null){
                return new UserInfoDto();
            }
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader(Constants.TOKEN);
            if (StringUtils.isNotEmpty(token)) {
                UserTokenInfoDto userTokenInfoDto = JSONObject.parseObject(JWT.decode(token).getAudience().get(0), UserTokenInfoDto.class);
                UserInfoDto result = new UserInfoDto();
                BeanUtils.copyProperties(userTokenInfoDto, result);
                return result;
            }
        } catch (Exception e) {
            log.error("获取当前用户信息出错", e);
        }
        return new UserInfoDto();  // 返回空的账号对象
    }

public static String getUsername() {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    String username = request.getHeader(Constants.USERNAME);//前端把USERNAME放在请求头中，这里取出来
    if (StringUtils.isNotBlank(username)) {
        try {
            //解码url
            username = URLDecoder.decode(username, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return username;
    }
    UserInfoDto currentUser = getCurrentUser();
    if(currentUser != null){
        return currentUser.getUsername();
    }
    return null;
}
    public static String getAvatar() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String avatar = request.getHeader(Constants.AVATAR);
        if (StringUtils.isNotBlank(avatar)) {
            try {
                avatar = URLDecoder.decode(avatar, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return avatar;
        }
        UserInfoDto currentUser = getCurrentUser();
        if(currentUser != null){
            return currentUser.getAvatar();
        }
        return null;
    }
    public static Long getUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userIdStr = request.getHeader(Constants.USERID);
        if(StringUtils.isNotBlank(userIdStr)) {
            try {
                userIdStr = URLDecoder.decode(userIdStr, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Long userId=Long.valueOf(userIdStr);
            return userId;
        }
        UserInfoDto currentUser= getCurrentUser();
        if(currentUser!=null){
            return currentUser.getId();
        }
        return null;
    }
}
