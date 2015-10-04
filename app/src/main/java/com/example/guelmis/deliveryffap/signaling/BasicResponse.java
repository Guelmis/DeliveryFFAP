package com.example.guelmis.deliveryffap.signaling;

/**
 * Created by mario on 09/27/15.
 */
public class BasicResponse {
    private boolean success;
    private String message;
    private String status;

    protected BasicResponse(boolean _success, String _message, String _status){
        success = _success;
        message = _message;
        status = _status;
    }

    public boolean success() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}