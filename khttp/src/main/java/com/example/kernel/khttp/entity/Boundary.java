package com.example.kernel.khttp.entity;


import com.example.kernel.khttp.utils.MultityEntityUtil;

import java.util.Random;

/**
 * Created by kernel on 15/3/1.
 */
public class Boundary {
    public static final char[] BOUNDARY_CHARS =
            {'0','1','2','3','4','5','6','7','8','9',
            'a','b','c','d','e','f','g','h','i','j','k','l','m',
            'n','o','p','q','r','s','t','u','v','w','x','y','z',
            'A','B','C','D','E','F','G','H','I','J','k','L','M',
            'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};

    /** 边界字符串*/
    public static String boundary;

    public Boundary() {
        if(boundary == null) {
            generateBoundary();
        }
    }

    /** 获取request body开始的边界字符串*/
    public String getStartBoundary() {
        synchronized (boundary) {
            return MultityEntityUtil.DOUBLE_MINUS + boundary;
        }
    }

    public  byte[] getStartBoundaryByte() {
        synchronized (boundary) {
            return  (MultityEntityUtil.DOUBLE_MINUS + boundary).getBytes();
        }
    }

    /** 获取所有request body结束的边界字符串*/
    public String getEndBoundary() {
        synchronized (boundary) {
            return MultityEntityUtil.DOUBLE_MINUS + boundary + MultityEntityUtil.DOUBLE_MINUS;
        }
    }

    public byte[] getEndBoundaryByte() {
        synchronized (boundary) {
            return (MultityEntityUtil.DOUBLE_MINUS + boundary + MultityEntityUtil.DOUBLE_MINUS).getBytes();
        }
    }

    /** 随机生成一个新的边界字符串*/
    public void generateBoundary() {
        StringBuilder newBoundary = new StringBuilder();
        Random random = new Random();
        for(int i= 0,len = BOUNDARY_CHARS.length ; i < len; i ++) {
            int charPos = random.nextInt() / len;
            char boundaryChar = BOUNDARY_CHARS[charPos];
            newBoundary.append(boundaryChar);
        }
        synchronized (boundary) {
            Boundary.boundary = newBoundary.toString();
        }
    }

    /** 自定义边界字符串*/
    public void setBoundary(String boundary) {
        synchronized (boundary) {
            Boundary.boundary = boundary;
        }
    }

    public String getBoundary() {
        synchronized (boundary) {
            return Boundary.boundary;
        }
    }

}
