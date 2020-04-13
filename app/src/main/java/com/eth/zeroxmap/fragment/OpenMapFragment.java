package com.eth.zeroxmap.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.eth.zeroxmap.R;
import com.eth.zeroxmap.activity.MainActivity;
import com.eth.zeroxmap.api.Analytics;
import com.eth.zeroxmap.api.Foam;
import com.eth.zeroxmap.api.LocationApi;
import com.eth.zeroxmap.api.OpenSeaApi;
import com.eth.zeroxmap.model.foam_poi.FoamPoi;
import com.eth.zeroxmap.model.foam_poi.details.FoamPoiMeta;
import com.eth.zeroxmap.model.opensea.Asset;
import com.eth.zeroxmap.utils.BBox;
import com.eth.zeroxmap.utils.Constants;
import com.eth.zeroxmap.utils.DistanceFormatter;
import com.eth.zeroxmap.utils.IconUtils;
import com.eth.zeroxmap.utils.MapUtils;
import com.eth.zeroxmap.utils.Utils;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.VectorSource;

import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class OpenMapFragment extends BaseFragment {

    View view;
    MapView mapView;

    private Context mContext;
    private MapboxMap map;
    private LocationComponent locationPlugin;
    private LocationEngine locationEngine;
    private LocationEngineRequest locationEngineRequest;
    private LocalizationPlugin localizationPlugin;
    private final BaseMapFragmentLocationCallback callback = new BaseMapFragmentLocationCallback(this);

    private boolean firstCamera = true;

    private Location lastLoc = null;
    private IconFactory iconFactory;
    private Icon forSaleIconF;
    private Icon forSaleIcon;
    private Icon earthIconF;
    private Icon earthIcon;
    private Icon baseIcon;
    private Icon chalIcon;
    private Icon pendIcon;
    private Icon verIcon;
    List<FoamPoi> foamPois = new ArrayList<>();
    List<Asset> earthAssets = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        iconFactory = IconFactory.getInstance(mContext);
        earthIconF = iconFactory.fromResource(R.mipmap.ic_earth_marker_f);
        forSaleIconF = iconFactory.fromResource(R.mipmap.ic_for_sale_f);
        earthIcon = iconFactory.fromResource(R.mipmap.ic_earth_marker);
        forSaleIcon = iconFactory.fromResource(R.mipmap.ic_for_sale);
        baseIcon = iconFactory.fromResource(R.mipmap.ic_marker);
        chalIcon = iconFactory.fromBitmap(IconUtils.returnBitmapDrawable(mContext, R.drawable.foam_circle_challenged));
        pendIcon = iconFactory.fromBitmap(IconUtils.returnBitmapDrawable(mContext, R.drawable.foam_circle_pending));
        verIcon = iconFactory.fromBitmap(IconUtils.returnBitmapDrawable(mContext, R.drawable.foam_circle_verified));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_open_map, container, false);
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        if (map == null) {
            setUpMap();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (locationPlugin != null) {
            locationPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (locationPlugin != null) {
            locationPlugin.onStop();
        }
    }

    private void setUpMap() {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                map = mapboxMap;
                map.getUiSettings().setAttributionEnabled(false);
                map.getUiSettings().setLogoEnabled(false);
//                map.getUiSettings().setScrollGesturesEnabled(false);

                map.setStyle(MapUtils.getMapStyle(), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        try {
                            localizationPlugin = new LocalizationPlugin(mapView, map, style);
                            localizationPlugin.matchMapLanguageWithDeviceDefault();
                            localizationPlugin.setCameraToLocaleCountry(0);
                        } catch (Exception e) {
//                            Api.logException(e);
                        }

                        try {
                            VectorSource streetSource = new VectorSource("streetsSource", "mapbox://mapbox.mapbox-streets-v8");
                            style.addSource(streetSource);
                            LineLayer streetsLayer = new LineLayer("streetsLayer", "streetsSource")
                                    .withProperties(
                                            lineWidth(20f),
                                            lineColor(Color.WHITE)
                                    )
                                    .withSourceLayer("road_label");
                            style.addLayerAt(streetsLayer, 0);
                        } catch (Exception e) {
//                            Api.logException(e);
                        }

                        BuildingPlugin buildingPlugin = new BuildingPlugin(mapView, map, style);
                        buildingPlugin.setColor(Color.parseColor(MapUtils.getMapBuildingColor()));
                        buildingPlugin.setOpacity(0.35f);
                        buildingPlugin.setMinZoomLevel(12);
                        buildingPlugin.setVisibility(true);

                        initializeLocationEngine();

                        map.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                            @Override
                            public boolean onMapClick(@NonNull LatLng point) {
                                //TODO?
                                return false;
                            }
                        });

                        map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(final @NonNull Marker marker) {
                                if(marker.getTitle().contains("FOAMPOI:")) {
                                    fetchPoiDetails(marker.getSnippet());
                                }

                                if(marker.getTitle().contains("0xEarthLAND:")) {
                                    for(Asset asset : earthAssets){
                                        if(TextUtils.equals(asset.tokenId, marker.getSnippet())){
                                            setupEarthDetailsDialog(asset);
                                        }
                                    }
                                }
                                return true;
                            }
                        });

                        map.addOnMoveListener(new MapboxMap.OnMoveListener() {
                            @Override
                            public void onMoveBegin(@NonNull MoveGestureDetector detector) {

                            }

                            @Override
                            public void onMove(@NonNull MoveGestureDetector detector) {

                            }

                            @Override
                            public void onMoveEnd(@NonNull MoveGestureDetector detector) {

                            }
                        });

                        map.addOnFlingListener(new MapboxMap.OnFlingListener() {
                            @Override
                            public void onFling() {

                            }
                        });

                        map.clear();
                        map.removeAnnotations();

                    }
                });
            }
        });
    }

    private void addNewPoi(FoamPoi foamPoi) {
        Location location = Foam.getLocationFromGeohash(foamPoi);

        Icon icon = baseIcon;
        if (TextUtils.equals(foamPoi.state.status.type, "challenged")) {
            icon = chalIcon;
        }
        if (TextUtils.equals(foamPoi.state.status.type, "pending")) {
            icon = pendIcon;
        }
        if (TextUtils.equals(foamPoi.state.status.type, "applied")) {
            icon = pendIcon;
        }
        if (TextUtils.equals(foamPoi.state.status.type, "verified")) {
            icon = verIcon;
        }
        if (TextUtils.equals(foamPoi.state.status.type, "listing")) {
            icon = verIcon;
        }

        map.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .icon(icon)
                .title("FOAMPOI:" + foamPoi.name)
                .snippet(foamPoi.listingHash));
    }

    private void addNewEarth(Asset earth) {
//        LatLng location = MapUtils.assetToLatLng(earth);
        Icon icon = earthIcon;

        int version = Utils.getEarthEditionType(earth);
        if(version == 1) {
            icon = earthIconF;
            if (earth.sellOrders.size() > 0) {
                icon = forSaleIconF;
            }
        }else{
            icon = earthIcon;
            if (earth.sellOrders.size() > 0) {
                icon = forSaleIcon;
            }
        }

        map.addMarker(new MarkerOptions()
                .position(MapUtils.assetToLatLng(earth))
                .icon(icon)
                .title("0xEarthLAND:" + earth.name)
                .snippet(earth.tokenId));

//        BBox bBox = MapUtils.assetToBbox(earth);
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        checkOrientation(newConfig);
//    }
//
//    private void checkOrientation(Configuration newConfig) {
//        // Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            isLandscape = true;
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            isLandscape = false;
//        }
//    }

    private void initializeLocationEngine() {

        locationEngineRequest = LocationApi.buildEngineRequest();

        locationEngine = LocationEngineProvider.getBestLocationEngine(mContext);
        locationEngine.requestLocationUpdates(locationEngineRequest, callback, null);

        locationPlugin = map.getLocationComponent();
        LocationComponentOptions options =
                LocationComponentOptions.builder(mContext)
                        .accuracyAnimationEnabled(true)
                        .compassAnimationEnabled(true)
                        .enableStaleState(true)
                        .elevation(Utils.dpToPx(mContext, 20))
                        .accuracyAlpha(0.65f)
                        .accuracyColor(Color.parseColor(MapUtils.getMapBuildingColor()))
                        .backgroundDrawable(R.drawable.user_stroke_icon)
                        .gpsDrawable(R.drawable.user_puck_icon)
                        .build();
        LocationComponentActivationOptions activationOptions =
                LocationComponentActivationOptions.builder(mContext, map.getStyle())
                        .useDefaultLocationEngine(false)
                        .locationEngine(locationEngine)
                        .locationEngineRequest(locationEngineRequest)
                        .locationComponentOptions(options).build();
        locationPlugin.activateLocationComponent(activationOptions);
        locationPlugin.setLocationEngine(locationEngine);
        locationPlugin.setLocationComponentEnabled(true);
        locationPlugin.setRenderMode(RenderMode.GPS);
        locationPlugin.setCameraMode(CameraMode.TRACKING_GPS);

        mapView.setMaximumFps(120);
        locationPlugin.setMaxAnimationFps(120);

        map.addOnCameraIdleListener(new MapboxMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                locationPlugin.setMaxAnimationFps(120);
            }
        });
    }

    private static class BaseMapFragmentLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<OpenMapFragment> activityWeakReference;

        BaseMapFragmentLocationCallback(OpenMapFragment activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            OpenMapFragment activity = activityWeakReference.get();
            if (activity != null) {
                Location location = result.getLastLocation();
                if (location == null) {
                    return;
                }
                activity.updateLocation(location);
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            Timber.e(exception);
        }
    }

    void updateLocation(Location location) {
        if (lastLoc == null) {
            lastLoc = location;
            initialCamera();
            fetchFoamPois();
        } else {
            lastLoc = location;
        }
    }

    private void initialCamera() {
        if (firstCamera) {
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(lastLoc.getLatitude(), lastLoc.getLongitude()))
                    .zoom(14)
                    .tilt(60)
                    .bearing(lastLoc.getBearing())
                    .build();

            map.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), 2000, new MapboxMap.CancelableCallback() {
                @Override
                public void onCancel() {
                    initialCamera();
                    Log.d("CAMERA", "CANCEL CALLED");
                }

                @Override
                public void onFinish() {
                    Log.d("CAMERA", "FINISH CALLED");
                    if (firstCamera) {
                        firstCamera = false;
                    }
                }
            });
        }
    }

    private void fetchFoamPois() {
        foamPois.clear();
        earthAssets.clear();
        map.clear();
        ((MainActivity) getActivity()).showLoading();
        Foam.fetchLocalPoi(mContext, lastLoc, 1200, false, new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                try {
                    if (e == null) {
                        foamPois = Foam.parseFoamPoiResult(result.getResult());
                        if (foamPois.size() > 0) {
                            for (FoamPoi foamPoi : foamPois) {
                                Log.d(Constants.TAG, new Gson().toJson(foamPoi));
                                addNewPoi(foamPoi);
                            }
                        } else {
//                            showNoPoiDialog();
                        }
                        ((MainActivity) getActivity()).hideLoading();
                    }
                } catch (Exception e1) {

                }
            }
        });

        OpenSeaApi.fetchContractAssets(mContext, Constants.EARTH_CONTRACT_ADDY, new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                try{
                    if(e == null){
                        Log.d(Constants.TAG, result.getResult());
                        earthAssets = OpenSeaApi.parseOpenSeaResult(result.getResult());
                        if (earthAssets.size() > 0) {
                            for (Asset earth : earthAssets) {
                                Log.d(Constants.TAG, new Gson().toJson(earth));
                                addNewEarth(earth);
                            }
                        }else{
                            Log.d(Constants.TAG, "OS: " + "Size is 0");
                        }
                    }else{
                        Log.e(Constants.TAG, "OS: " + e.toString());
                    }
                }catch (Exception e1){
                    Log.e(Constants.TAG, "OS: " + e1.toString());
                }
            }
        });
    }

    private void showNoPoiDialog() {
        Analytics.sendAnalyticEvent(mContext, "Dialog", "FOAM_None", Utils.returnId(), System.currentTimeMillis());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("FOAM POIs");
        alertDialogBuilder.setMessage("We could not find any FOAM POIs nearby, would you like to add some?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Utils.urlIntentWeb3(mContext, Constants.URL_FOAM_MAP);
                    }
                });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void fetchPoiDetails(String listingHash) {
        ((MainActivity) getActivity()).showLoading();
        Foam.fetchLocalPoiDetails(mContext, listingHash, new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                try {
                    if (e == null) {
                        setupPoiDetailsDialog(Foam.getMetaFromResult(result.getResult()));
                    }
                } catch (Exception e1) {
                }
            }
        });
    }

    private void setupPoiDetailsDialog(FoamPoiMeta foamPoiMeta) {
        Analytics.sendAnalyticEvent(mContext, "Dialog", "FOAM_Det", foamPoiMeta.meta.listingHash, System.currentTimeMillis());
        Location location = Foam.getLocationFromGeohash(foamPoiMeta);
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_foam_poi);
        dialog.setTitle("FOAM POI Details");

        TextView fStatus = dialog.findViewById(R.id.foam_status);
        fStatus.setText(foamPoiMeta.state.status.type);
        ImageView fImage = dialog.findViewById(R.id.foam_icon);
        Glide.with(mContext)
                .load(IconUtils.returnBitmapDrawable(mContext, IconUtils.getDrawableForPoiState(foamPoiMeta.state.status.type)))
                .into(fImage);

        TextView fName = dialog.findViewById(R.id.foam_name);
        fName.setText(foamPoiMeta.data.name);

        TextView fAddy = dialog.findViewById(R.id.foam_addy);
        fAddy.setText(foamPoiMeta.data.address);
        fAddy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + foamPoiMeta.data.address);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        TextView fDesc = dialog.findViewById(R.id.foam_desc);
        fDesc.setText(foamPoiMeta.data.description);

        TextView fTags = dialog.findViewById(R.id.foam_tags);
        fTags.setText(foamPoiMeta.data.tags.toString());

        TextView fGeohash = dialog.findViewById(R.id.foam_geohash);
        fGeohash.setText(foamPoiMeta.data.geohash);

        TextView fPNum = dialog.findViewById(R.id.foam_p_num);
        fPNum.setText(foamPoiMeta.data.phone);
        fPNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri u = Uri.parse("tel:" + foamPoiMeta.data.phone);

                // Create the intent and set the data for the
                // intent as the phone number.
                Intent i = new Intent(Intent.ACTION_DIAL, u);

                try {
                    // Launch the Phone app's dialer with a phone
                    // number to dial a call.
                    startActivity(i);
                } catch (SecurityException s) {
                }
            }
        });

        TextView fWeb = dialog.findViewById(R.id.foam_web);
        fWeb.setText(foamPoiMeta.data.web);
        fWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.urlIntentWeb3(mContext, foamPoiMeta.data.web);
            }
        });

        TextView fDist = dialog.findViewById(R.id.foam_dist);
        fDist.setText(DistanceFormatter.distanceFormatted(lastLoc.distanceTo(location)));

        TextView fOwner = dialog.findViewById(R.id.foam_owner);
        fOwner.setText(foamPoiMeta.meta.owner);
        fOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.urlIntentWeb3(mContext, Constants.ETHERSCAN_ADDY_BASE + foamPoiMeta.meta.owner);
            }
        });

        Button foamMap = dialog.findViewById(R.id.foam_btn_map);
        foamMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.urlIntentWeb3(mContext, Constants.URL_FOAM_MAP + "#/at/?lng=" + location.getLongitude()
                        + "&lat=" + location.getLatitude() + "&zoom=15.00");
            }
        });
        Button foamClose = dialog.findViewById(R.id.foam_btn_close);
        foamClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        ((MainActivity) getActivity()).hideLoading();
    }

    private void setupEarthDetailsDialog(Asset asset) {
        Analytics.sendAnalyticEvent(mContext, "Dialog", "EARTH_Det", asset.tokenId, System.currentTimeMillis());
        LatLng latLng = MapUtils.assetToLatLng(asset);
        Location location = new Location("0xEarth");
        location.setLatitude(latLng.getLatitude());
        location.setLongitude(latLng.getLongitude());

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_land);
        dialog.setTitle("0xEarth LAND Details");

        TextView fStatus = dialog.findViewById(R.id.foam_status);
//        fStatus.setText(foamPoiMeta.state.status.type);

        ImageView fImage = dialog.findViewById(R.id.land_icon);
        Glide.with(mContext)
                .load(asset.imageUrl)
                .into(fImage);

        TextView fName = dialog.findViewById(R.id.foam_name);
        fName.setText(asset.name);

        TextView fAddy = dialog.findViewById(R.id.foam_addy);
        fAddy.setText("LAND ID: " + asset.tokenId);
//        fAddy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + foamPoiMeta.data.address);
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                startActivity(mapIntent);
//            }
//        });

        TextView fDesc = dialog.findViewById(R.id.foam_desc);
        fDesc.setText(asset.description);

        TextView fTags = dialog.findViewById(R.id.foam_tags);
        if(asset.sellOrders.size() > 0){
            try{
                BigDecimal price = Convert.fromWei(asset.sellOrders.get(0).currentPrice, Convert.Unit.ETHER);

                fTags.setText("Price: " + price.toEngineeringString());
                Log.d(Constants.TAG, "ETHER PRICE : " + price.toString());
                Log.d(Constants.TAG, "ETHER PRICE : " + price.toPlainString());
//                Log.d(Constants.TAG, "ETHER PRICE : " + price.toEngineeringString());
            }catch (Exception e){
                Log.e(Constants.TAG, "ETHER PRICE : " + e.toString());
                fTags.setText("Price: " + asset.sellOrders.get(0).currentPrice);
            }
        }else {
            fTags.setText("Price: " + asset.currentPrice);
        }

        TextView fGeohash = dialog.findViewById(R.id.foam_geohash);
        fGeohash.setText("Owner: " + asset.owner.address);
        fGeohash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.urlIntentWeb3(mContext, Constants.ETHERSCAN_ADDY_BASE + asset.owner.address);
            }
        });

        TextView fPNum = dialog.findViewById(R.id.foam_p_num);
//        fPNum.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri u = Uri.parse("tel:" + foamPoiMeta.data.phone);
//
//                // Create the intent and set the data for the
//                // intent as the phone number.
//                Intent i = new Intent(Intent.ACTION_DIAL, u);
//
//                try {
//                    // Launch the Phone app's dialer with a phone
//                    // number to dial a call.
//                    startActivity(i);
//                } catch (SecurityException s) {
//                }
//            }
//        });

        TextView fWeb = dialog.findViewById(R.id.foam_web);
        fWeb.setText(asset.externalLink);
        fWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.urlIntentWeb3(mContext, asset.externalLink);
            }
        });

        TextView fDist = dialog.findViewById(R.id.foam_dist);
        fDist.setText(DistanceFormatter.distanceFormatted(lastLoc.distanceTo(location)));

        TextView fOwner = dialog.findViewById(R.id.foam_owner);
//        fOwner.setText(asset.);
//        fOwner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Utils.urlIntentWeb3(mContext, Constants.ETHERSCAN_ADDY_BASE + foamPoiMeta.meta.owner);
//            }
//        });

        Button foamMap = dialog.findViewById(R.id.foam_btn_map);
        foamMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.urlIntentWeb3(mContext, "https://opensea.io/assets/" + Constants.EARTH_CONTRACT_ADDY + "/" + asset.tokenId);
            }
        });
        Button foamClose = dialog.findViewById(R.id.foam_btn_close);
        foamClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        ((MainActivity) getActivity()).hideLoading();
    }

}
