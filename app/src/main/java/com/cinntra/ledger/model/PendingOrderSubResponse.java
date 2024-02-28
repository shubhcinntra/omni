package com.cinntra.ledger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PendingOrderSubResponse implements Serializable
{

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private PendingOrderHeaderOrderWiseData PendingOrderData = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PendingOrderHeaderOrderWiseData getPendingOrderData() {
        return PendingOrderData;
    }

    public void setPendingOrderData(PendingOrderHeaderOrderWiseData pendingOrderData) {
        PendingOrderData = pendingOrderData;
    }
}
