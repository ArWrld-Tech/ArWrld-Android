package com.eth.zeroxmap.model.opensea;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Owner {

    @SerializedName("profile_img_url")
    @Expose
    public String profileImgUrl;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("config")
    @Expose
    public String config;
    @SerializedName("discord_id")
    @Expose
    public String discordId;
}
