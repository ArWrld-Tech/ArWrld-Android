package com.eth.zeroxmap.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.eth.zeroxmap.R;
import com.eth.zeroxmap.api.Analytics;
import com.eth.zeroxmap.model.opensea.Asset;
import com.eth.zeroxmap.model.styles.BlvdMap;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;

import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;

public class Utils {

    public static boolean isAppInstalled(Context mContext, String packageName) {
        try {
            mContext.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void geoIntent(Context mContext, double lat, double lon) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String data = String.format("geo:%s,%s", lat, lon);
        intent.setData(Uri.parse(data));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public static void urlIntent(Context mContext, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url + "?ref=" + Constants.REF_VAL + "?utm_source=" + Constants.REF_VAL + "?from=" + Constants.REF_VAL));
        mContext.startActivity(i);
    }

    public static void urlIntentWeb3(Context mContext, String url) {
        Analytics.sendAnalyticEvent(mContext, "Url", url,
            "", System.currentTimeMillis());
        if (Utils.isAppInstalled(mContext, Constants.TRUST_WALLET_PACKAGE)){
            String fullUrl = Constants.TRUST_URL_BASE + url;
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( fullUrl ) );
            mContext.startActivity(intent);
        }else if(Utils.isAppInstalled(mContext, Constants.STATUS_WALLET_PACKAGE)){
            String fullUrl = Constants.STATUS_URL_BASE + url;
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( fullUrl ) );
            mContext.startActivity(intent);
        }else if(Utils.isAppInstalled(mContext, Constants.METAMASK_WALLET_PACKAGE)){
            url = url.replace("https://", "");
            url = url.replace("http://", "");
            url = url.replace("www.", "");
            String fullUrl = Constants.METAMASK_URL_BASE + url;
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( fullUrl ) );
            mContext.startActivity(intent);
        }else {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            mContext.startActivity(i);
        }
    }

    public static void launchAppPackage(Context mContext, String packageUrl){
        Analytics.sendAnalyticEvent(mContext, "App", packageUrl,
                "", System.currentTimeMillis());
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(packageUrl);
        if (intent != null) {
            // We found the activity now start the activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } else {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + packageUrl));
            mContext.startActivity(intent);
        }
    }

    /**
     * Creates an ARCore session. This checks for the CAMERA permission, and if granted, checks the
     * state of the ARCore installation. If there is a problem an exception is thrown. Care must be
     * taken to update the installRequested flag as needed to avoid an infinite checking loop. It
     * should be set to true if null is returned from this method, and called again when the
     * application is resumed.
     *
     * @param activity         - the activity currently active.
     * @param installRequested - the indicator for ARCore that when checking the state of ARCore, if
     *                         an installation was already requested. This is true if this method previously returned
     *                         null. and the camera permission has been granted.
     */
    public static Session createArSession(Activity activity, boolean installRequested)
            throws UnavailableException {
        Session session = null;
        // if we have the camera permission, create the session
        if (ARLocationPermissionHelper.hasPermission(activity)) {
            switch (ArCoreApk.getInstance().requestInstall(activity, !installRequested)) {
                case INSTALL_REQUESTED:
                    return null;
                case INSTALLED:
                    break;
            }
            session = new Session(activity);
            // IMPORTANT!!!  ArSceneView needs to use the non-blocking update mode.
            Config config = new Config(session);
            config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
            session.configure(config);
        }
        return session;
    }

    public static void handleSessionException(
            Activity activity, UnavailableException sessionException) {

        String message;
        if (sessionException instanceof UnavailableArcoreNotInstalledException) {
            message = "Please install ARCore";
        } else if (sessionException instanceof UnavailableApkTooOldException) {
            message = "Please update ARCore";
        } else if (sessionException instanceof UnavailableSdkTooOldException) {
            message = "Please update this app";
        } else if (sessionException instanceof UnavailableDeviceNotCompatibleException) {
            message = "This device does not support AR";
        } else {
            message = "Failed to create AR session";
            Log.e(Constants.TAG, "Exception: " + sessionException);
        }
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    public static int dpToPx(Context context, int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics()));
    }

    public static boolean isBlvdMapAsset(Context mContext, Asset asset){
        boolean isMap = false;
        String[] contracts = mContext.getResources().getStringArray(R.array.map_style_contracts);
        String[] styles = mContext.getResources().getStringArray(R.array.map_style_names);
        for(String addy : contracts){
            if(TextUtils.equals(addy, asset.assetContract.address)){
                for (String name : styles){
                    if(TextUtils.equals(name, asset.name)){
                        isMap = true;
                    }
                }
            }
        }
        return isMap;
    }

    public static BlvdMap styleMetaForAsset(Asset asset){
        BlvdMap blvdMap = new BlvdMap();
        blvdMap.bColor = MapUtils.BASE_B_COLOR;
        blvdMap.styleUrl = MapUtils.BASE_STYLE;

        if(TextUtils.equals(asset.name, "BLVD Map Digital Midnight")){
            blvdMap.bColor = "#0f4f64";
            blvdMap.styleUrl = "mapbox://styles/ktmdavid229/ck89h15w503121ikd9csbbany";
        }

        if(TextUtils.equals(asset.name, "BLVD Map Nomekop")){
            blvdMap.bColor = "#8ff0c7";
            blvdMap.styleUrl = "mapbox://styles/ktmdavid229/ck89h4v0k032l1illxgdlw5vx";
        }

        if(TextUtils.equals(asset.name, "BLVD Map Frozen Wonderland")){
            blvdMap.bColor = "#19c2fa";
            blvdMap.styleUrl = "mapbox://styles/ktmdavid229/ck89h82pd035x1iqvnmj6ka0k";
        }

        if(TextUtils.equals(asset.name, "BLVD Map Nightshade")){
            blvdMap.bColor = "#241f33";
            blvdMap.styleUrl = "mapbox://styles/ktmdavid229/ck89h990u035q1ine1oxj409a";
        }

        if(TextUtils.equals(asset.name, "BLVD Map Endless Summer")){
            blvdMap.bColor = "#982f67";
            blvdMap.styleUrl = "mapbox://styles/ktmdavid229/ck89gwm0u02um1ill9rd2py9b";
        }

        if(TextUtils.equals(asset.name, "BLVD Map North Pole")){
            blvdMap.bColor = "#9cbcc2";
            blvdMap.styleUrl = "mapbox://styles/ktmdavid229/ck89h6had034s1inzsvnoogzy";
        }

        if(TextUtils.equals(asset.name, "BLVD Map Halloween 2019")){
            blvdMap.bColor = "#000000";
            blvdMap.styleUrl = "mapbox://styles/ktmdavid229/ck89h350g031b1inz9yhxt2hh";
        }

        if(TextUtils.equals(asset.name, "BLVD Map Scribble")){
            blvdMap.bColor = "#D1D1D1";
            blvdMap.styleUrl = "mapbox://styles/ktmdavid229/ck89gz1xa02ys1ikdbyieru39";
        }

        return blvdMap;
    }
}
