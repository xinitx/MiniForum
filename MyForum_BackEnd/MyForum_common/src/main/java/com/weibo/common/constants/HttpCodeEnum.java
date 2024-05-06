package com.weibo.common.constants;


public enum HttpCodeEnum {
    // 信息端100
    INFO_REQUIRE("108","缺少必要信息"),

    //登录
    PASSWORD_ERROR("110","密码错误"),
    //注册
    USERNAME_ERROR("111","账号错误"),
    NICKNAME_ERROR("112","昵称错误"),
    NOPOSTING_ERROR("114","已经到底了"),
    REPEAT_ERROR("115","重复操作"),
    // 成功段固定为200
    SUCCESS("200","操作成功"),
    SERVER_ERROR("500","服务器内部错误");

    String code;
    String msg;
    HttpCodeEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public void getTest(){
        System.out.println(2);
    }
    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
