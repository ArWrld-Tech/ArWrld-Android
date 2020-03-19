package com.eth.zeroxmap.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.eth.zeroxmap.MainApp;
import com.eth.zeroxmap.R;
import com.eth.zeroxmap.activity.MainActivity;
import com.eth.zeroxmap.api.Foam;
import com.eth.zeroxmap.api.LocationApi;
import com.eth.zeroxmap.model.foam_poi.FoamPoi;
import com.eth.zeroxmap.utils.Constants;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import java.util.List;

//import io.radar.sdk.Radar;
//import io.radar.sdk.RadarReceiver;
//import io.radar.sdk.model.RadarEvent;
//import io.radar.sdk.model.RadarUser;

//public class RadarService extends RadarReceiver {
public class RadarService  {

//    private static int MIN_POI_DIST = 805; //meters
//    private static int NOTI_ID = 100;
//
//    @Override
//    public void onEventsReceived(Context context, RadarEvent[] events, RadarUser user) {
//        // do something with context, events, user
//    }
//
//    @Override
//    public void onLocationUpdated(Context context, Location location, RadarUser user) {
//        // do something with context, location, user
//        Location lastLoc = LocationApi.getLastLoc();
//        boolean shouldQuery = false;
//        if(lastLoc != null) {
//            //Only query and prompt if location change is somewhat significant
//            if (location.distanceTo(lastLoc) > MIN_POI_DIST){
//                shouldQuery = true;
//            }
//        }else{
//            shouldQuery = true;
//        }
//
//        if(shouldQuery){
//            checkForLocalPois(context, location);
//        }
//    }
//
//    @Override
//    public void onError(Context context, Radar.RadarStatus status) {
//        // do something with context, status
//    }
//
//    private void checkForLocalPois(Context mContext, Location location){
//        Foam.fetchLocalPoi(mContext, location, true, new FutureCallback<Response<String>>() {
//            @Override
//            public void onCompleted(Exception e, Response<String> result) {
//                try {
//                    if (e == null) {
//                        List<FoamPoi> foamPois = Foam.parseFoamPoiResult(result.getResult());
//                        if (foamPois.size() > 0) {
//                            localNotifyForChallenge(mContext);
//                        }
//                    }
//                } catch (Exception e1) {
//
//                }
//            }
//        });
//    }
//
//    private void localNotifyForChallenge(Context mContext){
//        try {
//            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.cancel(NOTI_ID);
//        }catch (Exception e){}
//
//        Intent intent = new Intent(mContext, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "foam_bg")
//                .setSmallIcon(R.drawable.user_puck_icon)
//                .setContentTitle("Nearby FOAM Challenges")
//                .setContentText("There is a FOAM POI challenge near you! Click to view the map and see more details. Contribute by validating metadata in person.")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                // Set the intent that will fire when the user taps the notification
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//         NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
//        notificationManager.notify(NOTI_ID, builder.build());
//    }
}