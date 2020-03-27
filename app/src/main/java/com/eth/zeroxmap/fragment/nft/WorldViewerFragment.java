package com.eth.zeroxmap.fragment.nft;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.eth.zeroxmap.R;
import com.eth.zeroxmap.fragment.BaseFragment;
import com.eth.zeroxmap.utils.CardNode;
import com.eth.zeroxmap.utils.IconUtils;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.maps.MapView;

public class WorldViewerFragment extends BaseFragment {

    View view;
    Context mContext;

    private ArFragment arFragment;
    private ViewRenderable card;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ar_map, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        arFragment = (ArFragment) getChildFragmentManager().findFragmentById(R.id.ux_fragment);

        ViewRenderable.builder()
                .setView(mContext, R.layout.item_card)
                .build()
                .thenAccept(
                        (renderable) -> card = renderable)
                .exceptionally(
                        (throwable) -> {
                            throw new AssertionError("Could not load plane card view.", throwable);
                        });

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (card == null) {
                        return;
                    }

                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());


                    // Create CardNode and attach to the Anchor.
                    CardNode cardNode = new CardNode(anchorNode);
                    cardNode.setLocalRotation(new Quaternion(new Vector3(-90f,0f,0f)));
                    cardNode.setRenderable(card);
                });

    }
}
