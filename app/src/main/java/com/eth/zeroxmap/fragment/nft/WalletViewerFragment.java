package com.eth.zeroxmap.fragment.nft;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.eth.zeroxmap.R;
import com.eth.zeroxmap.adapter.OpenSeaWalletAdapter;
import com.eth.zeroxmap.api.OpenSeaApi;
import com.eth.zeroxmap.fragment.BaseFragment;
import com.eth.zeroxmap.model.opensea.OpenSeaResponse;
import com.eth.zeroxmap.utils.Constants;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.pixplicity.easyprefs.library.Prefs;

public class WalletViewerFragment extends BaseFragment {

    View view;
    Context mContext;

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    LottieAnimationView loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wallet_viewer, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeContainer);
        loading = view.findViewById(R.id.loading);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                runQuery();
                loading.setVisibility(View.GONE);
            }
        });
        runQuery();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void runQuery() {
        if(Prefs.getString(Constants.PREF_WALLET_ADDY, null) != null) {
//        Glide.get(mContext).clearMemory();
//            loading.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(true);
            OpenSeaApi.fetchUserAssets(mContext, Prefs.getString(Constants.PREF_WALLET_ADDY, null), new FutureCallback<Response<String>>() {
                @Override
                public void onCompleted(Exception e, Response<String> result) {
                    try {
                        if (e == null) {
                            OpenSeaResponse openSeaResponse = new Gson().fromJson(result.getResult(), OpenSeaResponse.class);
                            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                            recyclerView.setAdapter(new OpenSeaWalletAdapter(mContext, openSeaResponse.assets, true));
                            recyclerView.smoothScrollToPosition(0);
                        }
                    } catch (Exception e1) {

                    }
                    loading.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }else{
            addWalletAddy();
        }
//        Api.sendAnalyticEvent("ERC721", "Wallet View", parseId, System.currentTimeMillis());
    }

    private void addWalletAddy() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.dialog_add_wallet, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                if(userInput.getText().toString().startsWith("0x")) {
                                    Prefs.putString(Constants.PREF_WALLET_ADDY, userInput.getText().toString());
                                    runQuery();
                                }else{
                                    Toast.makeText(mContext, "Please enter a proper wallet address", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
