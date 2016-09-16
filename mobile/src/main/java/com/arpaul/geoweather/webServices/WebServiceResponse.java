package com.arpaul.geoweather.webServices;

import java.io.Serializable;

/**
 * Created by Aritra on 01-08-2016.
 */
public class WebServiceResponse implements Serializable {

    public enum ResponseType {
        SUCCESS,
        FAILURE
    }

    private ResponseType responseCode;
    private String responseMessage;

    public ResponseType getResponseCode(){
        return responseCode;
    }

    public String getResponseMessage(){
        return responseMessage;
    }

    public void setResponseCode(ResponseType responseCode){
        this.responseCode = responseCode;
    }

    public void setResponseMessage(String responseMessage){
        this.responseMessage = responseMessage;
    }
}
