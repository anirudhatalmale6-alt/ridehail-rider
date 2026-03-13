package com.pulsarrides.rider.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RideHistoryResponse {

    @SerializedName("rides")
    private List<Ride> rides;

    @SerializedName("page")
    private int page;

    @SerializedName("limit")
    private int limit;

    public RideHistoryResponse() {}

    public List<Ride> getRides() { return rides; }
    public void setRides(List<Ride> rides) { this.rides = rides; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
}
