package com.eth.zeroxmap.model.opensea;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trait {

    @SerializedName("trait_type")
    @Expose
    public String traitType;
    @SerializedName("value")
    @Expose
    public Object value;
    @SerializedName("display_type")
    @Expose
    public String displayType;
    @SerializedName("max_value")
    @Expose
    public Object maxValue;
    @SerializedName("trait_count")
    @Expose
    public Integer traitCount;
    @SerializedName("order")
    @Expose
    public Object order;

}
