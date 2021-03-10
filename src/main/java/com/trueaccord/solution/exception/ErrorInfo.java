package com.trueaccord.solution.exception;

public class ErrorInfo {

    private String url;
    private String errorMessage;
    private String exceptionMessage;

    public ErrorInfo() {

    }

    public ErrorInfo(String url, String errorMessage, String exceptionMessage) {
        super();
        this.url = url;
        this.errorMessage = errorMessage;
        this.exceptionMessage = exceptionMessage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
