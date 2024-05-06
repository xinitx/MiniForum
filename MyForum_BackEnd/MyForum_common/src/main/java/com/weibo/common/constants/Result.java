package com.weibo.common.constants;

import java.io.Serializable;


public class Result<T> implements Serializable {

    private String code;
    private String msg;
    private T data;
    public Result(){
        this.code=HttpCodeEnum.SUCCESS.getCode();
        this.msg =HttpCodeEnum.SUCCESS.getMsg();
    }
    public Result(String code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public Result(String code, String msg, T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public static Result ok(){
        return new Result();
    }
    public static Result ok(String code, String msg){
        return new Result(code, msg);
    }
    public static Result ok(String code, String msg, Object data){
        return new Result(code, msg, data);
    }
    public static Result ok(HttpCodeEnum codeEnum){
        return new Result(codeEnum.getCode(),codeEnum.getMsg());
    }
    public static Result ok(HttpCodeEnum codeEnum, Object data){
        return new Result(codeEnum.getCode(),codeEnum.getMsg(), data);
    }
    public static Result error(String code, String msg){
        return new Result(code, msg);
    }
    public static Result error(HttpCodeEnum codeEnum){
        return new Result(codeEnum.getCode(),codeEnum.getMsg());
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }
}
