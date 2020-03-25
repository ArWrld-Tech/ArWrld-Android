package com.eth.zeroxmap.api;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.eth.zeroxmap.BuildConfig;
import com.eth.zeroxmap.model.foam_poi.FoamPoi;
import com.eth.zeroxmap.model.foam_poi.details.FoamPoiMeta;
import com.eth.zeroxmap.utils.Constants;
import com.eth.zeroxmap.utils.GeoHash;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Foam {

    //https://map-api-direct.foam.space/poi/filtered
    // ?limit=101&neLat=36.914355624468264&neLng=-76.23272863779059
    // &offset=0&sort=most_value&status=application&status=challenged&status=listing
    // &swLat=36.79861899009666&swLng=-76.40721922600498
    private static final String BASE_URL = BuildConfig.FOAM_API;
    private static final String ENDPOINT_BEACON_BBOX = "beacon?lat_min=%1$s&lon_min=%2$s&lat_max=%3$s&lon_max=%4$s";
    private static final String ENDPOINT_BEACON_DATA = "beacon/%1$s";
    private static final String ENDPOINT_BEACON_TOKEN_TRANSFERS = "beacon/%1$s/token_transfers";
    private static final String ENDPOINT_BEACON_SHAREHOLDERS = "beacon/%1$s/shareholders";
    private static final String ENDPOINT_CSC_BBOX = "csc?lat_min=%1$s&lon_min=-%2$s&lat_max=%3$s&lon_max=%4$s";
    private static final String ENDPOINT_CSC_DATA = "csc/%1$s";
    private static final String TAG = "beta-06-11";
//    private static final String BASE_URL_PROD = "https://map-api-direct.foam.space";
    //    private static final String BASE_URL_PROD = "https://map-api.foam.space/";
    private static final String ENDPOINT_POI = "/poi/map?swLng=%1$s&swLat=%2$s&neLng=%3$s&neLat=%4$s";
    //    private static final String ENDPOINT_POI = "/poi/filtered?limit=101&swLng=%1$s&swLat=%2$s&neLat=%3$s&neLng=%4$s&offset=0&status=application&status=challenged&status=listing";
    private static final String ENDPOINT_POI_DETAILS = "/poi/details/";
    private static final String ENDPOINT_SIGNAL = "/signal/map?swLng=%1$s&swLat=%2$s&neLng=%3$s&neLat=%4$s";

    public static double R1 = 6371;
    //TODO adjust as needed (or ideally make user setting)
    public static double radius = 5;

    public static void fetchLocalBeacons(Context mContext, Location lastLocation, FutureCallback<Response<String>> callback) {
        double lonLeft = lastLocation.getLongitude() - Math.toDegrees(radius / R1 / Math.cos(Math.toRadians(lastLocation.getLatitude())));
        double lonRight = lastLocation.getLongitude() + Math.toDegrees(radius / R1 / Math.cos(Math.toRadians(lastLocation.getLatitude())));
        double latTop = lastLocation.getLatitude() + Math.toDegrees(radius / R1);
        double latBottom = lastLocation.getLatitude() - Math.toDegrees(radius / R1);

        String url = BASE_URL + String.format(ENDPOINT_BEACON_BBOX, lonLeft, latBottom, lonRight, latTop);
//        Log.d(Constants.TAG, url);
//                lonLeft + "&right=" + lonRight
//                + "&bottom=" + latBottom + "&top=" + latTop + "&types=alerts,traffic&_=" + System.currentTimeMillis();
        Ion.with(mContext)
                .load(url)
                .addHeader("Accept", "application/json;charset=utf-8")
                .addHeader("authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJkYXQiOiIxZWFlNTllZDEwMTJkOGI3YzZiMzc5MTBiNGRmMjAxNjNjYWZkMWFjIn0.6oPNMmLNEit19XYjMv3ld_zsvvaTNvbKg4n5no7HnV1SiTdYMDr1QfPbcGp7D9ocWgfxdB2fJPpqXz4___qb8Q")
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    public static void fetchLocalCsc(Context mContext, Location lastLocation, FutureCallback<Response<String>> callback) {
        double lonLeft = lastLocation.getLongitude() - Math.toDegrees(radius / R1 / Math.cos(Math.toRadians(lastLocation.getLatitude())));
        double lonRight = lastLocation.getLongitude() + Math.toDegrees(radius / R1 / Math.cos(Math.toRadians(lastLocation.getLatitude())));
        double latTop = lastLocation.getLatitude() + Math.toDegrees(radius / R1);
        double latBottom = lastLocation.getLatitude() - Math.toDegrees(radius / R1);

        String url = BASE_URL + String.format(ENDPOINT_CSC_BBOX, lonLeft, lonRight, latBottom, latTop);
        Log.d(Constants.TAG, url);
        Ion.with(mContext)
                .load(url)
                .addHeader("Accept", "application/json;charset=utf-8")
                .addHeader("authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJkYXQiOiIxZWFlNTllZDEwMTJkOGI3YzZiMzc5MTBiNGRmMjAxNjNjYWZkMWFjIn0.6oPNMmLNEit19XYjMv3ld_zsvvaTNvbKg4n5no7HnV1SiTdYMDr1QfPbcGp7D9ocWgfxdB2fJPpqXz4___qb8Q")
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    public static void fetchLocalPoi(Context mContext, Location lastLocation, double customRadius, boolean challengeOnly, FutureCallback<Response<String>> callback) {
        double lonLeft = lastLocation.getLongitude() - Math.toDegrees(customRadius / R1 / Math.cos(Math.toRadians(lastLocation.getLatitude())));
        double lonRight = lastLocation.getLongitude() + Math.toDegrees(customRadius / R1 / Math.cos(Math.toRadians(lastLocation.getLatitude())));
        double latTop = lastLocation.getLatitude() + Math.toDegrees(customRadius / R1);
        double latBottom = lastLocation.getLatitude() - Math.toDegrees(customRadius / R1);

        String url = BASE_URL + String.format(ENDPOINT_POI, lonLeft, latBottom, lonRight, latTop);
        if(challengeOnly){
            url = url + "&status=challenged";
        }
        Log.d(Constants.TAG, url);
        Ion.with(mContext)
                .load(url)
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    public static void fetchLocalPoiDetails(Context mContext, String listingHash, FutureCallback<Response<String>> callback) {
        String url = BASE_URL + ENDPOINT_POI_DETAILS + listingHash;
        Log.d(Constants.TAG, url);
        Ion.with(mContext)
                .load(url)
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    public static void fetchLocalSignal(Context mContext, Location lastLocation, double customRadius, FutureCallback<Response<String>> callback) {
        double lonLeft = lastLocation.getLongitude() - Math.toDegrees(customRadius / R1 / Math.cos(Math.toRadians(lastLocation.getLatitude())));
        double lonRight = lastLocation.getLongitude() + Math.toDegrees(customRadius / R1 / Math.cos(Math.toRadians(lastLocation.getLatitude())));
        double latTop = lastLocation.getLatitude() + Math.toDegrees(customRadius / R1);
        double latBottom = lastLocation.getLatitude() - Math.toDegrees(customRadius / R1);

        String url = BASE_URL + String.format(ENDPOINT_SIGNAL, lonLeft, latBottom, lonRight, latTop);
        Log.d(Constants.TAG, url);
        Ion.with(mContext)
                .load(url)
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    public static Location getLocationFromGeohash(FoamPoi foamPoi){
        //Create geohash object
        GeoHash geoHash = GeoHash.fromString(foamPoi.geohash);
        //get location from center point of geohash bounding box
        return geoHash.getBoundingBox().getCenterPoint();
    }

    public static Location getLocationFromGeohash(FoamPoiMeta foamPoi){
        //Create geohash object
        GeoHash geoHash = GeoHash.fromString(foamPoi.data.geohash);
        //get location from center point of geohash bounding box
        return geoHash.getBoundingBox().getCenterPoint();
    }

    public static List<FoamPoi> parseFoamPoiResult(String result){
        Type foamPoiListType = new TypeToken<ArrayList<FoamPoi>>(){}.getType();
        return new Gson().fromJson(result, foamPoiListType);
    }

    public static FoamPoiMeta getMetaFromResult(String result){
        return new Gson().fromJson(result, FoamPoiMeta.class);
    }
}
