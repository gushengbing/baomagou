package com.bmg.mall.common;

public class bmgException extends RuntimeException {

    public bmgException(String message) {
        super(message);
    }

    /**
     * 丢出一个异常
     *
     * @param message
     */
    public static void fail(String message) {
        throw new bmgException(message);
    }

}