package com.Peter.utils;

import com.Peter.Param.BaseResult;

//相应工具类
public class BaseResultUtils {
    //成功的
    public static <T> BaseResult<T> generateSuccess(T t){
        BaseResult<T> baseResult = new BaseResult<>();
        baseResult.setCode(0);
        baseResult.setSuccess(true);
        baseResult.setMessage("操作成功");
        baseResult.setData(t);
        return baseResult;
    }
    //失败的 @return
    public static <T> BaseResult<T> generateError(Integer code, String message){
        BaseResult baseResult = new BaseResult<>();
        baseResult.setCode(code);
        baseResult.setSuccess(false);
        baseResult.setMessage(message);
        baseResult.setData( null);
        return baseResult;
    }
    //失败的@param @ return @param
    public static <T> BaseResult<T> generateError( String message){
        BaseResult baseResult = new BaseResult<>();
        baseResult.setCode(-1);
        baseResult.setSuccess(false);
        baseResult.setMessage(message);
        baseResult.setData( null);
        return baseResult;
    }
    public static BaseResult success(){
        BaseResult baseResult = new BaseResult<>();
        baseResult.setCode(0);
        baseResult.setSuccess(true);
        baseResult.setMessage("操作成功");
        return baseResult;
    }


}
