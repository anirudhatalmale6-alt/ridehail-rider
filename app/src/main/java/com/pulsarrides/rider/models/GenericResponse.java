package com.pulsarrides.rider.models;

import com.google.gson.annotations.SerializedName;

public class GenericResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private boolean success;

    @SerializedName("error")
    private String error;

    public GenericResponse() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
