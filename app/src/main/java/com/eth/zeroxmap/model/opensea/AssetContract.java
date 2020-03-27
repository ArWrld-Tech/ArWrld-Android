package com.eth.zeroxmap.model.opensea;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AssetContract {

    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("asset_contract_type")
    @Expose
    public String assetContractType;
    @SerializedName("created_date")
    @Expose
    public String createdDate;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("nft_version")
    @Expose
    public String nftVersion;
//    @SerializedName("opensea_version")
//    @Expose
//    public Object openseaVersion;
    @SerializedName("owner")
    @Expose
    public Integer owner;
    @SerializedName("schema_name")
    @Expose
    public String schemaName;
    @SerializedName("symbol")
    @Expose
    public String symbol;
    @SerializedName("total_supply")
    @Expose
    public String totalSupply;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("external_link")
    @Expose
    public String externalLink;
    @SerializedName("image_url")
    @Expose
    public String imageUrl;
    @SerializedName("default_to_fiat")
    @Expose
    public Boolean defaultToFiat;
    @SerializedName("dev_buyer_fee_basis_points")
    @Expose
    public Integer devBuyerFeeBasisPoints;
    @SerializedName("dev_seller_fee_basis_points")
    @Expose
    public Integer devSellerFeeBasisPoints;
    @SerializedName("only_proxied_transfers")
    @Expose
    public Boolean onlyProxiedTransfers;
    @SerializedName("opensea_buyer_fee_basis_points")
    @Expose
    public Integer openseaBuyerFeeBasisPoints;
    @SerializedName("opensea_seller_fee_basis_points")
    @Expose
    public Integer openseaSellerFeeBasisPoints;
    @SerializedName("buyer_fee_basis_points")
    @Expose
    public Integer buyerFeeBasisPoints;
    @SerializedName("seller_fee_basis_points")
    @Expose
    public Integer sellerFeeBasisPoints;
    @SerializedName("payout_address")
    @Expose
    public String payoutAddress;
}
