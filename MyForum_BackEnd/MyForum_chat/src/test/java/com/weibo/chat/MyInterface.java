package com.weibo.chat;

interface MyInterface {
    int CONSTANT_VALUE = 100; // 接口中的常量

    void myMethod();
}

class MyClass implements MyInterface {


    @Override
    public void myMethod() {
        System.out.println("Implementation of myMethod()");
    }

    public void printConstant() {
        System.out.println("Constant value: " + CONSTANT_VALUE); // 访问隐藏的常量
    }
    public static void main(String[] args) {
        MyClass obj = new MyClass();
        obj.myMethod(); // 实现了接口中的方法
        obj.printConstant(); // 访问隐藏的常量
    }
}

