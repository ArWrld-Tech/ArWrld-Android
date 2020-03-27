package com.eth.zeroxmap;

import android.app.Application;
import android.content.Context;

import com.eth.zeroxmap.api.LocationApi;
import com.foursquare.api.types.geofence.GeofenceEvent;
import com.foursquare.pilgrim.LogLevel;
import com.foursquare.pilgrim.PilgrimNotificationHandler;
import com.foursquare.pilgrim.PilgrimSdk;
import com.foursquare.pilgrim.PilgrimSdkBackfillNotification;
import com.foursquare.pilgrim.PilgrimSdkGeofenceEventNotification;
import com.foursquare.pilgrim.PilgrimSdkVisitNotification;
import com.foursquare.pilgrim.Visit;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mapbox.mapboxsdk.Mapbox;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.List;

//import io.radar.sdk.Radar;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MainApp extends Application {

    private FirebaseAnalytics firebaseAnalytics;

    private final PilgrimNotificationHandler pilgrimNotificationHandler = new PilgrimNotificationHandler() {
        @Override
        public void handleVisit(Context context, PilgrimSdkVisitNotification notification) {
            Visit visit = notification.getVisit();
            LocationApi.sendPlaceEvent4sq(visit, false);
        }

        @Override
        public void handleBackfillVisit(Context context, PilgrimSdkBackfillNotification notification) {
            super.handleBackfillVisit(context, notification);
            Visit visit = notification.getVisit();
            LocationApi.sendPlaceEvent4sq(visit, true);
        }

        @Override
        public void handleGeofenceEventNotification(Context context, PilgrimSdkGeofenceEventNotification notification) {
            List<GeofenceEvent> geofenceEvents = notification.getGeofenceEvents();
            for (GeofenceEvent geofenceEvent : geofenceEvents) {
                LocationApi.sendGeofenceEvent4sq(geofenceEvent);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
//        instance = this;
//        mContext = getApplicationContext();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.font))
                .setFontAttrId(R.attr.fontPath)
                .build());

        Mapbox.getInstance(getApplicationContext(), getString(R.string.access_token));

        PilgrimSdk.Builder builder = new PilgrimSdk.Builder(this)
                .consumer("S5DKV4KTNT5LAUAKMXMGULVKG4OFRZCU0MR2LE3CJQ0LEFWC", "XA0FI3J4M1C42GWNVWTWRWYWBPKB0YABA5GTKGYR0RY1ANYR")
                .notificationHandler(pilgrimNotificationHandler)
                .logLevel(LogLevel.DEBUG)
                .enableDebugLogs();
        PilgrimSdk.with(builder);

        new Prefs.Builder()
                .setContext(this)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

//        Radar.initialize("");

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }
}
