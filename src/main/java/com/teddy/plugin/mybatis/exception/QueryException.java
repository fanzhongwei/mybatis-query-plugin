package com.teddy.plugin.mybatis.exception;

/**
 * The type Query exception.
 *
 * @author teddy
 * @Package com.teddy.plugin.mybatis.exception
 * @Description: 自定义查询组件异常
 * @date 2018 -5-9 10:59
 */
public class QueryException extends RuntimeException {

    /**
     * Instantiates a new Query exception.
     */
    public QueryException() {

    }

    /**
     * Instantiates a new Query exception.
     *
     * @param message the message
     */
    public QueryException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Query exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Query exception.
     *
     * @param cause the cause
     */
    public QueryException(Throwable cause) {
        super(cause);
    }
}
