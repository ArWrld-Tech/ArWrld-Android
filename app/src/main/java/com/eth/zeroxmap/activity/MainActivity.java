package com.eth.zeroxmap.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.eth.zeroxmap.R;
import com.eth.zeroxmap.activity.BaseActivity;
import com.eth.zeroxmap.api.Analytics;
import com.eth.zeroxmap.fragment.ArMapFragment;
import com.eth.zeroxmap.fragment.OpenMapFragment;
import com.eth.zeroxmap.fragment.earth.EarthVisionMapFragment;
import com.eth.zeroxmap.fragment.foam.FoamVisionMapFragment;
import com.eth.zeroxmap.fragment.nft.BlvdMapStylesFragment;
import com.eth.zeroxmap.fragment.nft.WalletViewerFragment;
import com.eth.zeroxmap.fragment.nft.WorldViewerFragment;
import com.eth.zeroxmap.utils.Constants;
import com.eth.zeroxmap.utils.Utils;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;
import java.util.List;

import info.isuru.sheriff.enums.SheriffPermission;
import info.isuru.sheriff.helper.Sheriff;
import info.isuru.sheriff.interfaces.PermissionListener;
//import io.radar.sdk.Radar;
//import io.radar.sdk.RadarTrackingOptions;


public class MainActivity extends BaseActivity implements PermissionListener {

    public static int PERM_REQ_CODE = 100;

    private IProfile profile;
    private AccountHeader headerResult = null;
    public Drawer result = null;
    private Bundle savedInstanceState;
    private boolean viewSetup = false;
    private boolean hasWallet = false;

    Toolbar toolbar;
    public LottieAnimationView loading;
    FrameLayout frameLayout;
    Context mContext;

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    private Sheriff sheriffPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        loading = findViewById(R.id.loading);
        frameLayout = findViewById(R.id.frame_container);
        this.savedInstanceState = savedInstanceState;
        mContext = this;

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("0xMap");

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.disallowAddToBackStack();

        profile = new ProfileDrawerItem().withName(getString(R.string.app_name))
                .withIdentifier(100);
        profile.withEmail("");
        profile.withIcon(R.mipmap.ic_launcher_web);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withCompactStyle(true)
                .withHeaderBackground(R.mipmap.ic_blueprint)
                .addProfiles(
                        profile
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult, true)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(false)
                .build();

        sheriffPermission = Sheriff.Builder()
                .with(this)
                .requestCode(PERM_REQ_CODE)
                .setPermissionResultCallback(this)
                .askFor(SheriffPermission.CAMERA, SheriffPermission.LOCATION)
                .build();
        sheriffPermission.requestPermissions();
    }

    @Override
    public void onResume(){
        super.onResume();
//        RadarTrackingOptions trackingOptions = new RadarTrackingOptions.Builder()
//                .priority(Radar.RadarTrackingPriority.EFFICIENCY)
//                .offline(Radar.RadarTrackingOffline.REPLAY_STOPPED)
//                .sync(Radar.RadarTrackingSync.POSSIBLE_STATE_CHANGES)
//                .build();
//        Radar.startTracking(trackingOptions);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //Renable if you want to use full screen
//        if (hasFocus) {
//            // Standard Android full-screen functionality.
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        sheriffPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionsGranted(int requestCode, ArrayList<String> acceptedPermissionList) {
        Analytics.setUserProperty(mContext, "Perm_Granted", "true");
        setupDrawer();
    }

    @Override
    public void onPermissionsDenied(int requestCode, ArrayList<String> deniedPermissionList) {
        Analytics.setUserProperty(mContext, "Perm_Granted", "false");
    }

    private void setupDrawer(){
        List<IDrawerItem> drawerItems = new ArrayList<>();
        drawerItems.add(new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_local_map))
                .withIcon(R.mipmap.ic_map)
                .withIdentifier(999)
                .withSelectable(true));

        //TODO ICONS
        //divider 0xEarth
        drawerItems.add(new SectionDrawerItem().withName("0xEarth"));
        drawerItems.add(new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_oxe_v_map))
                .withIcon(R.mipmap.ic_vision)
                .withIdentifier(40)
                .withSelectable(true));
        drawerItems.add(new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_oxe_market))
                .withIcon(R.mipmap.ic_token)
                .withIdentifier(41)
                .withSelectable(false));
        drawerItems.add(new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_oxe_globe))
                .withIcon(R.mipmap.ic_map)
                .withIdentifier(45)
                .withSelectable(false));
        drawerItems.add(new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_oxe_site))
                .withIcon(R.mipmap.ic_tools)
                .withIdentifier(42)
                .withSelectable(false));
        drawerItems.add(new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_oxe_discord))
                .withIcon(R.mipmap.ic_chat)
                .withIdentifier(43)
                .withSelectable(false));
//        drawerItems.add(new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_oxe_dao))
//                .withIcon(R.mipmap.ic_map)
//                .withIdentifier(43)
//                .withSelectable(false));

        //divider FOAM
        drawerItems.add(new SectionDrawerItem().withName("FOAM"));
        drawerItems.add(new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_foam_v_map))
                .withIcon(R.mipmap.ic_vision)
                .withIdentifier(1)
                .withSelectable(true));
        drawerItems.add(new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_foam_tool))
                .withIcon(R.mipmap.ic_tools)
                .withIdentifier(2)
                .withSelectable(false));
        drawerItems.add(new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_foam_map))
                .withIcon(R.mipmap.ic_map)
                .withIdentifier(3)
                .withSelectable(false));
        drawerItems.add(new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_foam_tokens))
                .withIcon(R.mipmap.ic_token)
                .withIdentifier(4)
                .withSelectable(false));

        drawerItems.add(new SectionDrawerItem().withName("NFT / Digital Collectibles"));
//        drawerItems.add(new PrimaryDrawerItem().withName("Add to the World")
//                .withIcon(R.mipmap.ic_vision)
//                .withIdentifier(50)
//                .withSelectable(true));
        drawerItems.add(new PrimaryDrawerItem().withName("Collectibles")
                .withIcon(R.mipmap.ic_vision)
                .withIdentifier(51)
                .withSelectable(true));
        drawerItems.add(new PrimaryDrawerItem().withName("BLVD Map Styles")
                .withIcon(R.mipmap.ic_vision)
                .withIdentifier(52)
                .withSelectable(true));
        drawerItems.add(new PrimaryDrawerItem().withName("Map Styles Marketplace")
                .withIcon(R.mipmap.ic_token)
                .withIdentifier(61)
                .withSelectable(false));

//        drawerItems.add(new SectionDrawerItem().withName("BLVD Map Styles"));
//        drawerItems.add(new PrimaryDrawerItem().withName("View Your Styles")
//                .withIcon(R.mipmap.ic_map)
//                .withIdentifier(60)
//                .withSelectable(true));
//        drawerItems.add(new PrimaryDrawerItem().withName("BLVD Marketplace")
//                .withIcon(R.mipmap.ic_token)
//                .withIdentifier(61)
//                .withSelectable(false));

        drawerItems.add(new SectionDrawerItem().withName("Extras"));
        //Wallets for easy user access
        if(Utils.isAppInstalled(mContext, Constants.TRUST_WALLET_PACKAGE)){
            drawerItems.add(new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_trust_wallet))
                    .withIcon(R.mipmap.ic_wallet)
                    .withIdentifier(10)
                    .withSelectable(false));
            hasWallet = true;
        }
        if(Utils.isAppInstalled(mContext, Constants.METAMASK_WALLET_PACKAGE)){
            drawerItems.add(new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_meta_wallet))
                    .withIcon(R.mipmap.ic_wallet)
                    .withIdentifier(11)
                    .withSelectable(false));
            hasWallet = true;
        }
        if(Utils.isAppInstalled(mContext, Constants.STATUS_WALLET_PACKAGE)){
            drawerItems.add(new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_status_wallet))
                    .withIcon(R.mipmap.ic_wallet)
                    .withIdentifier(12)
                    .withSelectable(false));
            hasWallet = true;
        }

        Analytics.sendAnalyticEvent(mContext, "Has_Wallet", Boolean.toString(hasWallet),
                Utils.returnId(), System.currentTimeMillis());

        if(!hasWallet){
            drawerItems.add(new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_get_wallet))
                    .withIcon(R.mipmap.ic_wallet)
                    .withIdentifier(20)
                    .withSelectable(false));
        }

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult)
                .withDrawerItems(drawerItems)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Analytics.sendAnalyticEvent(mContext, "Nav_Click", "" + position,
                                "", System.currentTimeMillis());
                        //Base AR / Map Open
                        if (drawerItem.getIdentifier() == 999) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "0x_Map", Utils.returnId(), System.currentTimeMillis());
//                            Analytics.sendAnalyticEvent());
                            //Load AR
                            getSupportActionBar().setTitle(getResources().getString(R.string.nav_local_map));
                            swapFragment(new OpenMapFragment());
                        }

                        if (drawerItem.getIdentifier() == 1) {
                            //Load AR
                            Analytics.sendAnalyticEvent(mContext, "Nav", "FOAM_Viz", Utils.returnId(), System.currentTimeMillis());
                            getSupportActionBar().setTitle(getResources().getString(R.string.nav_foam_v_map));
                            swapFragment(new FoamVisionMapFragment());
                        }
                        if (drawerItem.getIdentifier() == 2) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "FOAM_Tools", Utils.returnId(), System.currentTimeMillis());
                            Utils.urlIntentWeb3(mContext, Constants.URL_FOAM_TOOLS);
                        }
                        if (drawerItem.getIdentifier() == 3) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "FOAM_Map", Utils.returnId(), System.currentTimeMillis());
                            Utils.urlIntentWeb3(mContext, Constants.URL_FOAM_MAP);
                        }
                        if (drawerItem.getIdentifier() == 4) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "FOAM_Uni", Utils.returnId(), System.currentTimeMillis());
                            Utils.urlIntentWeb3(mContext, Constants.URL_FOAM_TOKEN_UNISWAP);
                        }

                        if (drawerItem.getIdentifier() == 40) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "EARTH_Viz", Utils.returnId(), System.currentTimeMillis());
                            getSupportActionBar().setTitle(getResources().getString(R.string.nav_oxe_v_map));
                            swapFragment(new EarthVisionMapFragment());
                        }
                        if (drawerItem.getIdentifier() == 41) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "EARTH_Mkt", Utils.returnId(), System.currentTimeMillis());
                            Utils.urlIntentWeb3(mContext, Constants.EARTH_MARKETPALCE);
                        }
                        if (drawerItem.getIdentifier() == 42) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "EARTH_Site", Utils.returnId(), System.currentTimeMillis());
                            Utils.urlIntentWeb3(mContext, Constants.EARTH_SITE);
                        }
                        if (drawerItem.getIdentifier() == 43) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "EARTH_Discord", Utils.returnId(), System.currentTimeMillis());
                            Utils.urlIntentWeb3(mContext, Constants.EARTH_DISCORD);
                        }
                        if (drawerItem.getIdentifier() == 44) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "Earth_Dao", Utils.returnId(), System.currentTimeMillis());
                            Utils.urlIntentWeb3(mContext, Constants.EARTH_DAO);
                        }
                        if (drawerItem.getIdentifier() == 45) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "Earth_Globe", Utils.returnId(), System.currentTimeMillis());
                            Utils.urlIntentWeb3(mContext, Constants.EARTH_GLOBE);
                        }


                        if (drawerItem.getIdentifier() == 50) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "NFT_Viz", Utils.returnId(), System.currentTimeMillis());
                            getSupportActionBar().setTitle("NFT Visuals");
                            swapFragment(new WorldViewerFragment());
                        }
                        if (drawerItem.getIdentifier() == 51) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "NFT_Wallet", Utils.returnId(), System.currentTimeMillis());
                            getSupportActionBar().setTitle("NFT Wallet");
                            swapFragment(new WalletViewerFragment());
                        }
                        if (drawerItem.getIdentifier() == 52) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "BLVD_Maps", Utils.returnId(), System.currentTimeMillis());
                            getSupportActionBar().setTitle("BLVD Map Styles");
                            swapFragment(new BlvdMapStylesFragment());
                        }

                        if (drawerItem.getIdentifier() == 60) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "BLVD_Maps", Utils.returnId(), System.currentTimeMillis());
                            getSupportActionBar().setTitle("BLVD Map Styles");
                            swapFragment(new WorldViewerFragment());
                        }

                        if (drawerItem.getIdentifier() == 61) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "BLVD_Mkt", Utils.returnId(), System.currentTimeMillis());
                            Utils.urlIntentWeb3(mContext, Constants.BLVD_MARKETPALCE);
                        }

                        //Open given wallets
                        if (drawerItem.getIdentifier() == 10) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "WAL_Trust", Utils.returnId(), System.currentTimeMillis());
                            Utils.launchAppPackage(mContext, Constants.TRUST_WALLET_PACKAGE);
                        }
                        if (drawerItem.getIdentifier() == 11) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "WAL_Meta", Utils.returnId(), System.currentTimeMillis());
                            Utils.launchAppPackage(mContext, Constants.METAMASK_WALLET_PACKAGE);
                        }
                        if (drawerItem.getIdentifier() == 12) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "WAL_Status", Utils.returnId(), System.currentTimeMillis());
                            Utils.launchAppPackage(mContext, Constants.STATUS_WALLET_PACKAGE);
                        }

                        if (drawerItem.getIdentifier() == 20) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "WAL_New", Utils.returnId(), System.currentTimeMillis());
                            Utils.launchAppPackage(mContext, Constants.TRUST_WALLET_PACKAGE);
                        }

                        //Footer click
                        if (drawerItem.getIdentifier() == 100) {
                            Analytics.sendAnalyticEvent(mContext, "Nav", "ETH", Utils.returnId(), System.currentTimeMillis());
                            Utils.urlIntentWeb3(mContext, Constants.URL_BUILT_ON_ETH);
                        }
                        return false;
                    }
                })
                .withDelayOnDrawerClose(500)
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .withSliderBackgroundColor(getResources().getColor(R.color.colorPrimaryDark))
                .build();

        result.addStickyFooterItem(new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_on_eth))
                .withIcon(R.mipmap.ic_eth)
                .withIdentifier(100)
                .withSelectable(false));

        if (savedInstanceState == null) {
            result.setSelection(999, true);
            headerResult.setActiveProfile(profile);
        }

        viewSetup = true;
        loading.setVisibility(View.GONE);
    }

    private void swapFragment(Fragment fragment) {
        try {
            fragmentManager.popBackStack();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {
            Log.e(Constants.TAG, "SwapFragment: " + e.toString());
        }
    }

    public void showLoading(){
        loading.setVisibility(View.VISIBLE);
    }

    public void hideLoading(){
        loading.setVisibility(View.GONE);
    }
}
