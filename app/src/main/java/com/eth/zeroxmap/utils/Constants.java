package com.eth.zeroxmap.utils;

public class Constants {
    public static final String TAG = "ArWrld";
    public static final String REF_VAL = "ArWrld";

    public static final String EARTH_MARKETPALCE = "https://opensea.io/assets/0xearth";

    public static final String EARTH_SITE = "https://0xearth.github.io";
    public static final String EARTH_DAO = "https://0xearth.github.io";
    public static final String EARTH_DISCORD = "https://discord.gg/A73P7aS";


    public static final String BLVD_MARKETPALCE = "https://opensea.io/assets/blvd?refinementList%5Basset_contract.address%5D%5B0%5D=0xf101430f3c4295958a06b8366e7a097596f2d612";

    public static final String FOAM_TOKEN_ADDRESS = "0x4946fcea7c692606e8908002e55a582af44ac121";
    public static final String URL_FOAM_TOOLS = "https://foam.tools/#/";
    //Deep Link example
    //#/at/?lng=-74.0053928&lat=40.7054488&zoom=15.00
    public static final String URL_FOAM_MAP = "https://map.foam.space/";
    public static final String URL_FOAM_TOKEN_UNISWAP = "https://uniswap.exchange/swap/" + FOAM_TOKEN_ADDRESS;
    public static final String URL_BUILT_ON_ETH = "https://ethereum.org/";

    //3rd party wallet app packages
    public static final String TRUST_WALLET_PACKAGE = "com.wallet.crypto.trustapp";
    public static final String STATUS_WALLET_PACKAGE = "im.status.ethereum";
    public static final String METAMASK_WALLET_PACKAGE = "io.metamask";

    //3rd party wallet deep links
    public static final String TRUST_URL_BASE = "https://link.trustwallet.com/open_url?coin_id=60&url=";
    public static final String STATUS_URL_BASE = "https://get.status.im/browse/";
    public static final String METAMASK_URL_BASE = "https://metamask.app.link/dapp/";

    public static final String ETHERSCAN_ADDY_BASE = "https://etherscan.io/address/";


    //max = 300
    public static final Integer QUERY_SIZE = 275;
    public static final String OPENSEA_API_BASE = "https://api.opensea.io";
    public static final String OS_CONTRACT_BASE = "/api/v1/assets?asset_contract_address=";
    public static final String OS_ASSETS_BASE = "/api/v1/assets/?owner=";
    public static final String EARTH_CONTRACT_ADDY = "0x5c1110907a0f0d39b7e3b7bf472981bc19e88a62";

    //Prefs
    public static final String PREF_LAST_LOC_LAT = "last_loc_lat";
    public static final String PREF_LAST_LOC_LON = "last_loc_lon";
    public static final String PREF_LAST_LOC_ISMOCK = "last_loc_ismock";
    public static final String PREF_LAST_LOC_ALT = "last_loc_alt";
    public static final String PREF_LAST_LOC_ACCU = "last_loc_accu";
    public static final String PREF_LAST_LOC_PROV = "last_loc_prov";
    public static final String PREF_LAST_LOC_SPEED = "last_loc_speed";
    public static final String PREF_LAST_LOC_BEAR = "last_loc_bear";
    public static final String PREF_LAST_LOC_TIME = "last_loc_time";
    public static final String PREF_LAST_LOC_ELA = "last_loc_ela";
    public static final String PREF_LAST_LOC_VER_ACCU = "last_loc_vert_accu";
    public static final String PREF_LAST_LOC_BEAR_ACCU = "last_loc_bear_accu";
    public static final String PREF_LAST_LOC_SPEED_ACCU = "last_loc_speed_accu";

    public static final String PREF_MAP_STYLE = "pref_map_style";
    public static final String PREF_MAP_B_COLOR = "pref_map_b_color";
    public static final String PREF_WALLET_ADDY = "pref_wallet_addy";
}
