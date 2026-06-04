package com.Peter.rpc;

import com.Peter.Param.BaseResult;
import com.Peter.api.UserFeignService;
import com.Peter.dto.UserInfoDto;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserRpcService {
    @Resource
    private UserFeignService userFeinService;

    public UserInfoDto getUserInfo(Long id){
        if(id==null){
            return null;
        }
        BaseResult<UserInfoDto> userInfoDtoBaseResult = userFeinService.queryUserInfoById(id + "");
        if(!userInfoDtoBaseResult.getSuccess()){
            return null;
        }
        return userInfoDtoBaseResult.getData();
    }
}
