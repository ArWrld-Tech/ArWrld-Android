package com.eth.zeroxmap.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.eth.zeroxmap.R;
import com.eth.zeroxmap.activity.ArNftViewerActivity;
import com.eth.zeroxmap.api.Analytics;
import com.eth.zeroxmap.model.opensea.Asset;
import com.eth.zeroxmap.model.styles.BlvdMap;
import com.eth.zeroxmap.utils.CircleTransform;
import com.eth.zeroxmap.utils.Constants;
import com.eth.zeroxmap.utils.HtmlUtils;
import com.eth.zeroxmap.utils.SvgSoftwareLayerSetter;
import com.eth.zeroxmap.utils.Utils;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.List;

import jnr.ffi.annotations.In;

public class OpenSeaWalletAdapter extends RecyclerView.Adapter<OpenSeaWalletAdapter.ViewHolder> {

    private List<Asset> movies;
    private Context mContext;
    private RequestOptions requestOptions;
    private boolean launchWeb3Url = false;

    private RequestBuilder<PictureDrawable> requestBuilder;
    private SvgSoftwareLayerSetter svgSoftwareLayerSetter;

    public OpenSeaWalletAdapter(Context applicationContext, List<Asset> movieArrayList, boolean launchWeb3) {
        this.mContext = applicationContext;
        this.movies = movieArrayList;
        requestOptions = new RequestOptions();
        requestOptions.apply(RequestOptions.centerCropTransform());
        requestOptions.transform(new CircleTransform(mContext));
        requestOptions.priority(Priority.HIGH);
        launchWeb3Url = launchWeb3;
        svgSoftwareLayerSetter = new SvgSoftwareLayerSetter();
//        requestBuilder = Glide.with(mContext)
//                .as(PictureDrawable.class)
//                .transition(withCrossFade())
//                .listener(new SvgSoftwareLayerSetter());
    }

    @Override
    public OpenSeaWalletAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_opensea, viewGroup, false);
        return new OpenSeaWalletAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OpenSeaWalletAdapter.ViewHolder viewHolder, int i) {
        if (movies.get(i).description != null) {
            viewHolder.title.setVisibility(View.VISIBLE);
            viewHolder.title.setText(movies.get(i).description);
        } else {
            viewHolder.title.setVisibility(View.GONE);
        }

        viewHolder.userId.setVisibility(View.VISIBLE);
        viewHolder.userId.setText(movies.get(i).name);
        viewHolder.name.setVisibility(View.VISIBLE);
        viewHolder.name.setText(movies.get(i).assetContract.name);

        viewHolder.timeText.setVisibility(View.VISIBLE);
        viewHolder.timeText.setText(movies.get(i).assetContract.symbol);

        if (movies.get(i).imageUrl != null) {
            viewHolder.postImg.setVisibility(View.VISIBLE);

            if (movies.get(i).imageUrl.endsWith("svg")) {
                Glide.with(viewHolder.itemView)
                        .as(PictureDrawable.class)
                        .load(Uri.parse(movies.get(i).imageUrl))
                        .listener(svgSoftwareLayerSetter)
                        .into(viewHolder.postImg);
            } else {
                Glide.with(viewHolder.itemView)
                        .load(movies.get(i).imageUrl)
                        .thumbnail(0.1f)
                        .into(viewHolder.postImg);
            }
        } else {
            viewHolder.postImg.setVisibility(View.GONE);
        }

        if (movies.get(i).backgroundColor != null) {
            viewHolder.postImg.setBackgroundColor(Color.parseColor("#" + movies.get(i).backgroundColor));
        } else {
            viewHolder.postImg.setBackgroundColor(Color.parseColor("#fcfcfc"));
        }

//
        if (movies.get(i).assetContract.imageUrl != null) {
            Glide.with(viewHolder.itemView)
                    .load(movies.get(i).assetContract.imageUrl)
                    .thumbnail(0.1f)
                    .apply(requestOptions)
                    .into(viewHolder.logoImg);
        } else {
            Glide.with(viewHolder.itemView)
                    .load(R.mipmap.ic_launcher_web)
                    .thumbnail(0.1f)
                    .apply(requestOptions)
                    .into(viewHolder.logoImg);
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView userId;
        private TextView name;
        private ImageView postImg;
        private TextView timeText;
        private ImageView logoImg;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.post_user_msg);
            userId = view.findViewById(R.id.post_user_id);
            postImg = view.findViewById(R.id.post_img);
            timeText = view.findViewById(R.id.post_user_time);
            name = view.findViewById(R.id.post_user_name);
            logoImg = view.findViewById(R.id.post_user_img);

            postImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    routeOptions(getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    routeOptions(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    routeOptions(getAdapterPosition());
                    return true;
                }
            });
        }
    }

    private void routeOptions(int position) {
        showWhatDo(position);

    }

    private void showWhatDo(int position) {
        Analytics.sendAnalyticEvent(mContext, "NFT", movies.get(position).assetContract.address, movies.get(position).tokenId, System.currentTimeMillis());
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.colorPrimaryDark);
        alertDialog.setTitle(HtmlUtils.fromHtml("<font color='#FFFFFF'>How to use</font>"));
        alertDialog.setMessage("What would you like to do with this digital collectible?");
        alertDialog.setCancelable(true);
        if (launchWeb3Url) {
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, HtmlUtils.fromHtml("<font color='#FFFFFF'>View Asset</font>"),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (launchWeb3Url) {
                                Utils.urlIntentWeb3(mContext, movies.get(position).externalLink);
                            }
                            //TODO launch AR Viewer?
//                        fetchCryptoMotors(movies.get(position), alertDialog);
//                        if(TextUtils.equals(movies.get(position).assetContract.address.toLowerCase(), "0x30a2fa3c93fb9f93d1efeffd350c6a6bb62ba000")){
//                            fetchCryptoMotors(movies.get(position),alertDialog);
//                        }else{
//                            Prefs.putString(Constants.PREF_ASSET_ADDY, movies.get(position).assetContract.address);
//                            Prefs.putString(Constants.PREF_ASSET_IMG_URL, movies.get(position).imageUrl);
//                            Prefs.putString(Constants.PREF_ASSET_LINK, movies.get(position).permalink);
//                            Toast.makeText(mContext, "Asset set as map icon!", Toast.LENGTH_LONG).show();
//                            dialog.dismiss();
//                        }
                        }
                    });
        } else {
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, HtmlUtils.fromHtml("<font color='#FFFFFF'>View in AR</font>"),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //TODO launch AR Viewer?
                            Intent intent = new Intent(mContext, ArNftViewerActivity.class);
                            intent.putExtra("url", movies.get(position).imageUrl);
                            intent.putExtra("name", movies.get(position).name);
                            mContext.startActivity(intent);
//                        fetchCryptoMotors(movies.get(position), alertDialog);
//                        if(TextUtils.equals(movies.get(position).assetContract.address.toLowerCase(), "0x30a2fa3c93fb9f93d1efeffd350c6a6bb62ba000")){
//                            fetchCryptoMotors(movies.get(position),alertDialog);
//                        }else{
//                            Prefs.putString(Constants.PREF_ASSET_ADDY, movies.get(position).assetContract.address);
//                            Prefs.putString(Constants.PREF_ASSET_IMG_URL, movies.get(position).imageUrl);
//                            Prefs.putString(Constants.PREF_ASSET_LINK, movies.get(position).permalink);
//                            Toast.makeText(mContext, "Asset set as map icon!", Toast.LENGTH_LONG).show();
//                            dialog.dismiss();
//                        }
                        }
                    });
        }
        if (Utils.isBlvdMapAsset(mContext, movies.get(position))) {
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, HtmlUtils.fromHtml("<font color='#FFFFFF'>Set Map Style</font>"),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            BlvdMap blvdMap = Utils.styleMetaForAsset(movies.get(position));
                            Prefs.putString(Constants.PREF_MAP_B_COLOR, blvdMap.bColor);
                            Prefs.putString(Constants.PREF_MAP_STYLE, blvdMap.styleUrl);
                            Toast.makeText(mContext, "Map Style Set!", Toast.LENGTH_LONG).show();
                        }
                    });
        }else{
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, HtmlUtils.fromHtml("<font color='#FFFFFF'>View in AR</font>"),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(mContext, ArNftViewerActivity.class);
                            intent.putExtra("imgUrl", movies.get(position).imageUrl);
                            intent.putExtra("name", movies.get(position).name);
                            mContext.startActivity(intent);
                        }
                    });
        }

        alertDialog.show();
    }

}