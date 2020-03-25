package com.eth.zeroxmap.model.opensea;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OpenSeaResponse {
    @SerializedName("assets")
    @Expose
    public List<Asset> assets = new ArrayList<>();
}
