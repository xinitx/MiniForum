package com.weibo.chat;

public class Test2{

    {
        System.out.println(4);
    }
    static {
        System.out.println(2);
    }
    static Test test = new Test(1);


    Test test2 = new Test(3);


    public static void main(String[] args) {
        Test2 t = new Test2();
        System.out.println(-1%7);

    }
    Test2() {
        System.out.println(5);
    }
    // 定义一个泛型接口

}
