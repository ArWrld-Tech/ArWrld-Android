package com.eth.zeroxmap.model.opensea;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Asset {
    @SerializedName("token_id")
    @Expose
    public String tokenId;
    @SerializedName("num_sales")
    @Expose
    public Integer numSales;
    @SerializedName("background_color")
    @Expose
    public String backgroundColor;
    @SerializedName("image_url")
    @Expose
    public String imageUrl;
    @SerializedName("image_preview_url")
    @Expose
    public String imagePreviewUrl;
    @SerializedName("image_thumbnail_url")
    @Expose
    public String imageThumbnailUrl;
    @SerializedName("image_original_url")
    @Expose
    public String imageOriginalUrl;
    @SerializedName("animation_url")
    @Expose
    public Object animationUrl;
//    @SerializedName("animation_original_url")
//    @Expose
//    public Object animationOriginalUrl;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("external_link")
    @Expose
    public String externalLink;
//    @SerializedName("asset_contract")
//    @Expose
//    public AssetContract assetContract;
    @SerializedName("owner")
    @Expose
    public Owner owner;
    @SerializedName("permalink")
    @Expose
    public String permalink;
//    @SerializedName("collection")
//    @Expose
//    public Collection collection;
    @SerializedName("decimals")
    @Expose
    public Integer decimals;
//    @SerializedName("auctions")
//    @Expose
//    public Object auctions;
    @SerializedName("sell_orders")
    @Expose
    public List<SellOrder> sellOrders = new ArrayList<SellOrder>();
    @SerializedName("traits")
    @Expose
    public List<Trait> traits = new ArrayList<Trait>();
//    @SerializedName("last_sale")
//    @Expose
//    public Object lastSale;
//    @SerializedName("top_bid")
//    @Expose
//    public Object topBid;
    @SerializedName("current_price")
    @Expose
    public String currentPrice;
//    @SerializedName("current_escrow_price")
//    @Expose
//    public Object currentEscrowPrice;
    @SerializedName("listing_date")
    @Expose
    public Object listingDate;
    @SerializedName("is_presale")
    @Expose
    public Boolean isPresale;
    @SerializedName("transfer_fee_payment_token")
    @Expose
    public Object transferFeePaymentToken;
//    @SerializedName("transfer_fee")
//    @Expose
//    public Object transferFee;

}
