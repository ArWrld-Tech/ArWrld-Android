package com.eth.zeroxmap.fragment.earth;

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
import com.eth.zeroxmap.fragment.BaseFragment;
import com.eth.zeroxmap.fragment.foam.FoamVisionMapFragment;
import com.eth.zeroxmap.model.foam_poi.FoamPoi;
import com.eth.zeroxmap.model.foam_poi.details.FoamPoiMeta;
import com.eth.zeroxmap.model.opensea.Asset;
import com.eth.zeroxmap.utils.Constants;
import com.eth.zeroxmap.utils.DistanceFormatter;
import com.eth.zeroxmap.utils.IconUtils;
import com.eth.zeroxmap.utils.MapUtils;
import com.eth.zeroxmap.utils.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ViewRenderable;
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

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;
import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.rendering.LocationNode;
import uk.co.appoly.arcorelocation.rendering.LocationNodeRender;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class EarthVisionMapFragment extends BaseFragment {
    View view;
    MapView mapView;

    private Context mContext;
    private MapboxMap map;
    private LocationComponent locationPlugin;
    private LocationEngine locationEngine;
    private LocationEngineRequest locationEngineRequest;
    private LocalizationPlugin localizationPlugin;
    private final EarthVisionMapFragment.ArMapFragmentLocationCallback callback = new EarthVisionMapFragment.ArMapFragmentLocationCallback(this);

    private ArSceneView arSceneView;
    private ViewRenderable exampleLayoutRenderable;
    CompletableFuture<ViewRenderable>[] exampleLayouts;
    private ViewRenderable[] exampleLayoutRenderables;
    private boolean hasFinishedLoading = false;
    private Snackbar loadingMessageSnackbar = null;
    private boolean installRequested;
    private boolean isLandscape = false;
    private boolean firstCamera = true;

    // Our ARCore-Location scene
    private LocationScene locationScene;
    private static int RENDER_DIST = 400;
    private Location lastLoc = null;
    private IconFactory iconFactory;
    private Icon forSaleIcon;
    private Icon earthIcon;
    private Icon baseIcon;
    private Icon chalIcon;
    private Icon pendIcon;
    private Icon verIcon;
    List<Asset> earthAssets = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        iconFactory = IconFactory.getInstance(mContext);
        baseIcon = iconFactory.fromResource(R.mipmap.ic_marker);
        earthIcon = iconFactory.fromResource(R.mipmap.ic_earth_marker);
        forSaleIcon = iconFactory.fromResource(R.mipmap.ic_for_sale);
        chalIcon = iconFactory.fromBitmap(IconUtils.returnBitmapDrawable(mContext, R.drawable.foam_circle_challenged));
        pendIcon = iconFactory.fromBitmap(IconUtils.returnBitmapDrawable(mContext, R.drawable.foam_circle_pending));
        verIcon = iconFactory.fromBitmap(IconUtils.returnBitmapDrawable(mContext, R.drawable.foam_circle_verified));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ar_map, container, false);
        mapView = view.findViewById(R.id.map);
        arSceneView = view.findViewById(R.id.ar_scene_view);
        mapView.onCreate(savedInstanceState);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CompletableFuture<ViewRenderable> exampleLayout =
                ViewRenderable.builder()
                        .setView(mContext, R.layout.view_ar_blob)
                        .build();

        CompletableFuture.allOf(
                exampleLayout)
                .handle(
                        (notUsed, throwable) -> {

                            if (throwable != null) {
//                                DemoUtils.displayError(this, "Unable to load renderables", throwable);
                                return null;
                            }

                            try {
                                exampleLayoutRenderable = exampleLayout.get();
                                hasFinishedLoading = true;
                            } catch (InterruptedException | ExecutionException ex) {
//                                DemoUtils.displayError(this, "Unable to load renderables", ex);
                            }

                            return null;
                        });

        arSceneView
                .getScene()
                .addOnUpdateListener(
                        frameTime -> {
                            if (!hasFinishedLoading) {
                                return;
                            }

                            if (locationScene == null) {
                                // If our locationScene object hasn't been setup yet, this is a good time to do it
                                // We know that here, the AR components have been initiated.
                                locationScene = new LocationScene(getActivity(), arSceneView);
                                locationScene.setOffsetOverlapping(true);
                                locationScene.setRemoveOverlapping(true);
                            }

                            Frame frame = arSceneView.getArFrame();
                            if (frame == null) {
                                return;
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                            if (locationScene != null) {
                                locationScene.processFrame(frame);
                            }

                            if (loadingMessageSnackbar != null) {
                                for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                                    if (plane.getTrackingState() == TrackingState.TRACKING) {
//                                        hideLoadingMessage();
                                    }
                                }
                            }
                        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (locationScene != null) {
            locationScene.resume();
        }
        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = Utils.createArSession(getActivity(), installRequested);
                if (session == null) {
//                    installRequested = ARLocationPermissionHelper.hasPermission(this);
                    return;
                } else {
                    arSceneView.setupSession(session);
//                    arSceneView.setCameraStreamRenderPriority(CameraStreamRe);
                }
            } catch (UnavailableException e) {
                Utils.handleSessionException(getActivity(), e);
            }
        }

        try {
            arSceneView.resume();
        } catch (Exception ex) {
            Toast.makeText(mContext, "Unable to fetch camera", Toast.LENGTH_LONG).show();
            getActivity().finish();
            return;
        }

        if (arSceneView.getSession() != null) {
//            showLoadingMessage();
        }

        if (map == null) {
            setUpMap();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationScene != null) {
            locationScene.pause();
        }
        arSceneView.pause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        arSceneView.destroy();
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
                map.getUiSettings().setScrollGesturesEnabled(false);

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
                                fetchPoiDetails(marker.getSnippet());
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

    private void addNewPoi(Asset earth) {
//        Location location = Foam.getLocationFromGeohash(foamPoi);

        Icon icon = earthIcon;
        if(earth.sellOrders.size() > 0){
            icon = forSaleIcon;
        }

        map.addMarker(new MarkerOptions()
                .position(MapUtils.assetToLatLng(earth))
                .icon(icon)
                .title(earth.name)
                .snippet(earth.tokenId));

    }

    private void checkIfFoundAndReplace(LocationScene locationScene, LocationMarker locationMarker) {
        locationScene.mLocationMarkers.add(locationMarker);
        locationScene.refreshAnchors();
    }

    @TargetApi(Build.VERSION_CODES.N)
    private Node getExampleView(Asset foamPoi, ViewRenderable viewRenderable) {
        Node base = new Node();
        base.setName(foamPoi.tokenId);
        base.setRenderable(viewRenderable);
        base.setEnabled(true);
        String val = "" + foamPoi.name;
        View eView = viewRenderable.getView();
        ImageView reportImg = eView.findViewById(R.id.report_icon);
        Glide.with(reportImg)
                .load(foamPoi.imageUrl)
                .into(reportImg);
        TextView reportType = eView.findViewById(R.id.report_type);
        reportType.setText(val);
        eView.setOnTouchListener((v, event) -> {
            fetchPoiDetails(foamPoi.tokenId);
            return false;
        });
        return base;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void prepareReportAr() {
        if (earthAssets == null) {
            return;
        }
        if (earthAssets.size() == 0) {
            return;
        }
        try {
            exampleLayoutRenderables = new ViewRenderable[earthAssets.size() - 1];
            exampleLayouts = new CompletableFuture[earthAssets.size() - 1];
            for (int i = 0; i < exampleLayouts.length; i++) {
                exampleLayouts[i] = ViewRenderable.builder()
                        .setView(mContext, R.layout.view_ar_blob)
                        .build();
            }
            CompletableFuture.allOf(exampleLayouts).handle((notUsed, throwable) -> {

                if (throwable != null) {
//                    Utils.displayError(this, "Unable to load renderables", throwable);
                    return null;
                }

                try {

                    for (int i = 0; i < exampleLayoutRenderables.length; i++) {
                        exampleLayoutRenderables[i] = exampleLayouts[i].get();
                    }

                    for (int i = 0; i < earthAssets.size(); i++) {
                        Location location = MapUtils.locationFromLatLng(MapUtils.assetToLatLng(earthAssets.get(i)));
                        ViewRenderable viewRenderable = exampleLayoutRenderables[i];
                        LocationMarker layoutLocationMarker = new LocationMarker(
                                location.getLongitude(),
                                location.getLatitude(),
                                getExampleView(earthAssets.get(i), viewRenderable)
                        );
                        //Turn back on the change how far away we render
                        layoutLocationMarker.setOnlyRenderWhenWithin(RENDER_DIST);
                        layoutLocationMarker.setScalingMode(LocationMarker.ScalingMode.GRADUAL_TO_MAX_RENDER_DISTANCE);
                        layoutLocationMarker.setRenderEvent(new LocationNodeRender() {
                            @Override
                            public void render(LocationNode node) {
                                View eView = viewRenderable.getView();
                                TextView distanceTextView = eView.findViewById(R.id.report_dist);
                                distanceTextView.setText(DistanceFormatter.distanceFormatted(location.distanceTo(lastLoc)));
                            }
                        });
                        checkIfFoundAndReplace(locationScene, layoutLocationMarker);
                    }
                } catch (InterruptedException | ExecutionException ex) {
//                    Utils.displayError(this, "Unable to load renderables", ex);
                }

                return null;
            });
        } catch (Exception e) {
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        checkOrientation(newConfig);
    }

    private void checkOrientation(Configuration newConfig) {
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandscape = true;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            isLandscape = false;
        }
    }

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

    private static class ArMapFragmentLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<EarthVisionMapFragment> activityWeakReference;

        ArMapFragmentLocationCallback(EarthVisionMapFragment activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            EarthVisionMapFragment activity = activityWeakReference.get();
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
                    .zoom(15)
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
        ((MainActivity) getActivity()).showLoading();

        OpenSeaApi.fetchContractAssets(mContext, Constants.EARTH_CONTRACT_ADDY, new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                try{
                    if(e == null){
                        Log.d(Constants.TAG, result.getResult());
                        earthAssets = OpenSeaApi.parseOpenSeaResult(result.getResult());
                        if (earthAssets.size() > 0) {
                            for (Asset earth : earthAssets) {
                                addNewPoi(earth);
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
        for(Asset asset : earthAssets){
            if(TextUtils.equals(asset.tokenId, listingHash)){
                setupEarthDetailsDialog(asset);
            }
        }
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
