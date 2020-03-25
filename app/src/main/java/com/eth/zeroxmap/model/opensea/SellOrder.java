package com.eth.zeroxmap.model.opensea;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SellOrder {
    @SerializedName("created_date")
    @Expose
    public String createdDate;
    @SerializedName("closing_date")
    @Expose
    public String closingDate;
    @SerializedName("closing_extendable")
    @Expose
    public Boolean closingExtendable;
    @SerializedName("expiration_time")
    @Expose
    public Integer expirationTime;
    @SerializedName("listing_time")
    @Expose
    public Integer listingTime;
    @SerializedName("order_hash")
    @Expose
    public String orderHash;
//    @SerializedName("metadata")
//    @Expose
//    public Metadata metadata;
    @SerializedName("exchange")
    @Expose
    public String exchange;
//    @SerializedName("maker")
//    @Expose
//    public Maker maker;
//    @SerializedName("taker")
//    @Expose
//    public Taker taker;
    @SerializedName("current_price")
    @Expose
    public String currentPrice;
    @SerializedName("current_bounty")
    @Expose
    public String currentBounty;
    @SerializedName("bounty_multiple")
    @Expose
    public String bountyMultiple;
    @SerializedName("maker_relayer_fee")
    @Expose
    public String makerRelayerFee;
    @SerializedName("taker_relayer_fee")
    @Expose
    public String takerRelayerFee;
    @SerializedName("maker_protocol_fee")
    @Expose
    public String makerProtocolFee;
    @SerializedName("taker_protocol_fee")
    @Expose
    public String takerProtocolFee;
    @SerializedName("maker_referrer_fee")
    @Expose
    public String makerReferrerFee;
//    @SerializedName("fee_recipient")
//    @Expose
//    public FeeRecipient feeRecipient;
    @SerializedName("fee_method")
    @Expose
    public Integer feeMethod;
    @SerializedName("side")
    @Expose
    public Integer side;
    @SerializedName("sale_kind")
    @Expose
    public Integer saleKind;
    @SerializedName("target")
    @Expose
    public String target;
    @SerializedName("how_to_call")
    @Expose
    public Integer howToCall;
    @SerializedName("calldata")
    @Expose
    public String calldata;
    @SerializedName("replacement_pattern")
    @Expose
    public String replacementPattern;
    @SerializedName("static_target")
    @Expose
    public String staticTarget;
    @SerializedName("static_extradata")
    @Expose
    public String staticExtradata;
    @SerializedName("payment_token")
    @Expose
    public String paymentToken;
//    @SerializedName("payment_token_contract")
//    @Expose
//    public PaymentTokenContract paymentTokenContract;
    @SerializedName("base_price")
    @Expose
    public String basePrice;
    @SerializedName("extra")
    @Expose
    public String extra;
    @SerializedName("quantity")
    @Expose
    public String quantity;
    @SerializedName("salt")
    @Expose
    public String salt;
    @SerializedName("v")
    @Expose
    public Integer v;
    @SerializedName("r")
    @Expose
    public String r;
    @SerializedName("s")
    @Expose
    public String s;
    @SerializedName("approved_on_chain")
    @Expose
    public Boolean approvedOnChain;
    @SerializedName("cancelled")
    @Expose
    public Boolean cancelled;
    @SerializedName("finalized")
    @Expose
    public Boolean finalized;
    @SerializedName("marked_invalid")
    @Expose
    public Boolean markedInvalid;
    @SerializedName("prefixed_hash")
    @Expose
    public String prefixedHash;
}
