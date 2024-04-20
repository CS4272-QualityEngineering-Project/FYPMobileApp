package com.example.lungsoundclassification;

public class  RetrofitResponseWrapper {
    private final boolean isSuccess;
    private final ResponseObject responseBody;

    public RetrofitResponseWrapper(boolean isSuccess, ResponseObject responseBody) {
        this.isSuccess = isSuccess;
        this.responseBody = responseBody;
    }

    public boolean isSuccessful() {
        return isSuccess;
    }

    public ResponseObject body() {
        return responseBody;
    }
}
