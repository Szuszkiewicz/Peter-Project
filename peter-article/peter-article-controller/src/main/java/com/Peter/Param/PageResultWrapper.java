package com.Peter.Param;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@JsonPropertyOrder({"success", "code", "msg", "pageIndex", "pageSize", "total", "totalPage", "data", "extendInfo"})
@Data
public class PageResultWrapper<T> implements Serializable {
    private boolean success;
    private String code;
    private String msg;
    private Integer pageIndex;
    private Integer pageSize;
    private Integer total;
    private Integer totalPage;
    private List<T> data;
    private Map extendInfo;

    public PageResultWrapper() {
    }

    public static <T> PageResultWrapper<T> absent() {
        PageResultWrapper<T> ret = new PageResultWrapper();
        ret.data = new ArrayList();
        ret.success = true;
        ret.total = 0;
        ret.totalPage = 0;
        return ret;
    }

    public static <T> PageResultWrapper<T> of(List<T> data) {
        PageResultWrapper<T> ret = new PageResultWrapper();
        ret.data = data;
        ret.success = true;
        ret.total = 0;
        ret.totalPage = 0;
        return ret;
    }

    public static <T> PageResultWrapper<T> page(List<T> data, Integer total, int pageIndex, int pageSize) {
        PageResultWrapper<T> ret = new PageResultWrapper();
        ret.data = data;
        ret.success = true;
        ret.total = total;
        ret.pageIndex = pageIndex;
        ret.pageSize = pageSize;
        return ret;
    }

    public static <T> PageResultWrapper<T> fail(String errorCode, String messsage) {
        PageResultWrapper<T> ret = new PageResultWrapper();
        ret.success = false;
        ret.code = errorCode;
        ret.msg = messsage;
        return ret;
    }

}