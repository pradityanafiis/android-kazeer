package com.praditya.kazeer.api.response;

import com.praditya.kazeer.model.Error;

public class SingleResponse<T> {
    private boolean error;
    private String message;
    private T data;
//    private Error errors;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

//    public Error getErrors() {
//        return errors;
//    }
//
//    public void setErrors(Error errors) {
//        this.errors = errors;
//    }
}
