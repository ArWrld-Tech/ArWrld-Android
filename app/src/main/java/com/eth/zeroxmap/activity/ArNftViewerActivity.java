package com.eth.zeroxmap.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eth.zeroxmap.R;
import com.eth.zeroxmap.model.opensea.Asset;
import com.eth.zeroxmap.utils.CardNode;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ArNftViewerActivity extends BaseActivity {

    private ArFragment arFragment;
    private ViewRenderable card;

    private ViewRenderable exampleLayoutRenderable;
    CompletableFuture<ViewRenderable>[] exampleLayouts;
    private ViewRenderable[] exampleLayoutRenderables;

    String name = "";
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_world_viewer);

        Bundle extras = getIntent().getExtras();
        name = extras.getString("name");
        url = extras.getString("url");

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        CompletableFuture<ViewRenderable> exampleLayout =
                ViewRenderable.builder()
                        .setView(this, R.layout.item_card)
                        .build();
//
//        ViewRenderable.builder()
//                .setView(this, R.layout.item_card)
//                .build()
//                .thenAccept(
//                        (renderable) -> card = renderable)
//                .exceptionally(
//                        (throwable) -> {
//                            throw new AssertionError("Could not load plane card view.", throwable);
//                        });

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
                            } catch (InterruptedException | ExecutionException ex) {
//                                DemoUtils.displayError(this, "Unable to load renderables", ex);
                            }

                            return null;
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
//                    Node node = getExampleView(name, url, exampleLayoutRenderable);
//                    node.setParent(anchorNode);
//                    node.setRenderable(anchorNode);
                    cardNode.setLocalRotation(new Quaternion(new Vector3(0f,0f,0f)));
                    View eView = exampleLayoutRenderable.getView();
                    ImageView reportImg = eView.findViewById(R.id.report_icon);
                    Glide.with(reportImg)
                            .load(url)
                            .into(reportImg);
                    TextView reportType = eView.findViewById(R.id.report_type);
                    reportType.setText(name);
                    cardNode.setRenderable(exampleLayoutRenderable);
                });
    }

    @TargetApi(Build.VERSION_CODES.N)
    private Node getExampleView(String name, String url, ViewRenderable viewRenderable) {
        Node base = new Node();
        base.setName(name);
        base.setRenderable(viewRenderable);
        base.setEnabled(true);
        String val = "" + name;
        View eView = viewRenderable.getView();
        ImageView reportImg = eView.findViewById(R.id.report_icon);
        Glide.with(reportImg)
                .load(url)
                .into(reportImg);
        TextView reportType = eView.findViewById(R.id.report_type);
        reportType.setText(val);
        eView.setOnTouchListener((v, event) -> {

            return false;
        });
        return base;
    }
}