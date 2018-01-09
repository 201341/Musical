package com.swj.musical.util;
/*  Author: swj
 *  Date: 17-12-14 
 */

public class Response {
    private String message;
    private Object result;

    public Response() {

    }

    public Response(Object result) {
        this.result = result;
    }



    public Response(int state, String message, Object data) {
        this.message = message;
        this.result = data;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return result;
    }

    public void setData(Object data) {
        this.result = data;
    }
}
