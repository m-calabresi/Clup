package com.android.clup.api;

public class InvalidResponseException extends Exception {
    public InvalidResponseException(String s) {
        super(s);
    }

    public InvalidResponseException(Exception e) {
        super(e);
    }
}
