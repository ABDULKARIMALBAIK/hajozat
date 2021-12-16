package com.abdulkarimalbaik.dev.hajozat.Model;

public class ReCaptchaResponse {

    private boolean success;
    private String message;

    public ReCaptchaResponse() {
    }

    public ReCaptchaResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
