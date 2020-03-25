package com.eth.zeroxmap.utils;


import android.text.TextUtils;
import android.util.Log;

import com.airbnb.lottie.L;
import com.eth.zeroxmap.model.opensea.Asset;
import com.eth.zeroxmap.model.opensea.Trait;
import com.mapbox.mapboxsdk.geometry.LatLng;

public class MapUtils {
    public static final String BASE_STYLE = "mapbox://styles/ktmdavid229/ck6ihr3100e0r1ipi4nle1ubp";
    public static final String BASE_B_COLOR = "#5D3A63";

    public static String getMapStyle() {
        String style = BASE_STYLE;

        return style;
    }

    public static String getMapBuildingColor() {
        String style = BASE_B_COLOR;
        return style;
    }

    public static String getTileNumber(final double lat, final double lon, final int zoom) {
        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int ytile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
        if (xtile < 0)
            xtile = 0;
        if (xtile >= (1 << zoom))
            xtile = ((1 << zoom) - 1);
        if (ytile < 0)
            ytile = 0;
        if (ytile >= (1 << zoom))
            ytile = ((1 << zoom) - 1);
        return ("" + zoom + "/" + xtile + "/" + ytile);
    }

//    class BoundingBox {
//        double northLat;
//        double southLat;
//        double eastLon;
//        double westLon;
//    }

    public static BBox tile2boundingBox(final int x, final int y, final int zoom) {
        BBox bb = new BBox();
        bb.northLat = tile2lat(y, zoom);
        bb.southLat = tile2lat(y + 1, zoom);
        bb.westLon = tile2lon(x, zoom);
        bb.eastLon = tile2lon(x + 1, zoom);
        return bb;
    }

    public static BBox assetToBbox(Asset asset) {
        try {
            int z = 17;
            int x = 0;
            int y = 0;
            if (asset.traits.size() > 0) {
                for (Trait trait : asset.traits) {
//                    if (TextUtils.equals("number", trait.displayType)) {
                    if (TextUtils.equals("x", trait.traitType)) {
                        x = ((Double) trait.value).intValue();
                    }
                    if (TextUtils.equals("y", trait.traitType)) {
                        y = ((Double) trait.value).intValue();
                    }
//                    }
                }
            }else{
                Log.e(Constants.TAG, "ASSET-BBOX: " + "NO TRAITS");
            }

            return tile2boundingBox(x,y,z);
        } catch (Exception e) {
            Log.e(Constants.TAG, "ASSET-BBOX: " + e.toString());
        }
        return new BBox();
    }

    public static double tile2lon(int x, int z) {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    public static double tile2lat(int y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }

    public static LatLng zxyToLatLng(int z, int x, int y) {
        double lat = tile2lat(y, z);
        double lon = tile2lon(x, z);
        return new LatLng(lat, lon);
    }

    public static LatLng assetToLatLng(Asset asset) {
        try {
            int z = 17;
            int x = 0;
            int y = 0;
            if (asset.traits.size() > 0) {
                for (Trait trait : asset.traits) {
//                    if (TextUtils.equals("number", trait.displayType)) {
                        if (TextUtils.equals("x", trait.traitType)) {
                            x = ((Double) trait.value).intValue();
                        }
                        if (TextUtils.equals("y", trait.traitType)) {
                            y = ((Double) trait.value).intValue();
                        }
//                    }
                }
            }else{
                Log.e(Constants.TAG, "ASSET-LATLNG: " + "NO TRAITS");
            }

            return zxyToLatLng(z, x, y);
        } catch (Exception e) {
            Log.e(Constants.TAG, "ASSET-LATLNG: " + e.toString());
        }
        return new LatLng(0, 0);
    }
}
