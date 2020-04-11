package com.eth.zeroxmap.api;

import android.content.Context;
import android.util.Log;

import com.eth.zeroxmap.model.foam_poi.FoamPoi;
import com.eth.zeroxmap.model.opensea.Asset;
import com.eth.zeroxmap.model.opensea.OpenSeaResponse;
import com.eth.zeroxmap.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OpenSeaApi {

    public static void fetchContractAssets(Context mContext, String addy, FutureCallback<Response<String>> callback) {
        String url = Constants.OPENSEA_API_BASE + Constants.OS_CONTRACT_BASE + addy + "&limit=" + Constants.QUERY_SIZE;
        Log.d(Constants.TAG, url);
        Ion.with(mContext)
                .load(url)
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    public static void fetchUserAssets(Context mContext, String addy, FutureCallback<Response<String>> callback){
        String url = Constants.OPENSEA_API_BASE + Constants.OS_ASSETS_BASE + addy + "&limit=" + Constants.QUERY_SIZE;
        Log.d(Constants.TAG, url);
        Ion.with(mContext)
                .load(url)
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    public static void fetchUserAssetsBlvd(Context mContext, String addy, FutureCallback<Response<String>> callback){
        String url = Constants.OPENSEA_API_BASE + Constants.OS_ASSETS_BASE + addy + "&limit="
                + Constants.QUERY_SIZE + "&asset_contract_address=" + "0xf101430f3c4295958a06b8366e7a097596f2d612";
        Log.d(Constants.TAG, url);
        Ion.with(mContext)
                .load(url)
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    public static List<Asset> parseOpenSeaResult(String result){
//        Type foamPoiListType = new TypeToken<ArrayList<Asset>>(){}.getType();
        OpenSeaResponse openSeaResponse = new Gson().fromJson(result, OpenSeaResponse.class);
        return openSeaResponse.assets;
    }
}
