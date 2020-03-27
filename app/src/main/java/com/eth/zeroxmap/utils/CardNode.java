package com.eth.zeroxmap.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.QuaternionEvaluator;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.math.Vector3Evaluator;

public class CardNode extends Node implements Node.OnTapListener {

    private boolean isFocused;
    private AnchorNode anchorNode;
    private ObjectAnimator posePositionAnimator;
    private ObjectAnimator poseOrientationAnimator;


    public CardNode(AnchorNode anchorNode){
        this.anchorNode = anchorNode;
        isFocused = false;
        setOnTapListener(this);
        this.setParent(anchorNode);
    }


    @Override
    public void onUpdate(FrameTime frameTime) {
        super.onUpdate(frameTime);
        if(posePositionAnimator != null){
            this.setLocalPosition((Vector3) posePositionAnimator.getAnimatedValue());
        }
        if(poseOrientationAnimator != null){
            this.setLocalRotation((Quaternion) poseOrientationAnimator.getAnimatedValue());
        }
    }

    @Override
    public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
        if (isFocused){
            prepareAnimationsToSendCardToAnchor();
        }else{
            prepareAnimationsToBringCardToFront();
        }
        posePositionAnimator.start();
        poseOrientationAnimator.start();
        this.isFocused = !this.isFocused;
    }

    private void prepareAnimationsToBringCardToFront() {
        Vector3 worldPositionAnchor = this.getWorldPosition();
        Quaternion worldRotation = this.getWorldRotation();
        //compute destination
        Node destination = new Node();
        setNode1MeterInFrontCamera(destination);
        //Attach to camera and restore World position and orientation
        this.setParent(this.getScene().getCamera());
        this.setWorldPosition(worldPositionAnchor);
        this.setWorldRotation(worldRotation);

        buildPoseAnimators(this, destination);
        poseOrientationAnimator.addListener(createEndOfAnimationListener());
    }

    private void prepareAnimationsToSendCardToAnchor() {
        Vector3 worldPosition = this.getWorldPosition();
        Quaternion worldRotation = this.getWorldRotation();

        Node destination = new Node();
        setNodeAtAnchor(destination);
        this.setParent(destination);
        this.setWorldRotation( worldRotation);
        this.setWorldPosition(worldPosition);

        buildPoseAnimators(this, destination);
        poseOrientationAnimator.addListener(createEndOfAnimationListener());
    }

    private AnimatorListenerAdapter createEndOfAnimationListener(){
        return new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                poseOrientationAnimator = null;
                posePositionAnimator = null;
            }
        };
    }

    private void setNodeAtAnchor(Node temporaryDestinationNode) {
        temporaryDestinationNode.setParent(anchorNode);
        temporaryDestinationNode.setLocalPosition(new Vector3(0f, 0f, 0f));
        temporaryDestinationNode.setLocalRotation(new Quaternion(new Vector3(-45f, 0f, 0f)));
    }

    private void setNode1MeterInFrontCamera(Node temporaryDestinationNode) {
        temporaryDestinationNode.setParent(getScene().getCamera());
        temporaryDestinationNode.setLocalPosition(new Vector3(0.0f, 0.0f, -1.0f));
        temporaryDestinationNode.setLocalRotation(new Quaternion(new Vector3(0f, 0f, 0f)));
    }

    private void buildPoseAnimators(Node origin, Node destination){
        Vector3 originPosition = origin.getLocalPosition();
        Quaternion originRotation = origin.getLocalRotation();
        Vector3 destinationPosition = destination.getLocalPosition();
        Quaternion destinationRotation = destination.getLocalRotation();

        buildOrientationAnimator(originRotation, destinationRotation);
        buildPositionAnimator(originPosition, destinationPosition);
    }

    private void buildPositionAnimator(Vector3 startWorldPosition, Vector3 finalWorldPosition) {
        posePositionAnimator = new ObjectAnimator();
        posePositionAnimator.setObjectValues((Object[]) new Vector3[]{startWorldPosition, finalWorldPosition});
        posePositionAnimator.setEvaluator(new Vector3Evaluator());
        posePositionAnimator.setDuration(1000);
        posePositionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    private void buildOrientationAnimator(Quaternion startWorldRotation, Quaternion finalWorldRotation) {
        poseOrientationAnimator = new ObjectAnimator();
        poseOrientationAnimator.setObjectValues((Object[]) new Quaternion[]{startWorldRotation, finalWorldRotation});
        poseOrientationAnimator.setEvaluator(new QuaternionEvaluator());
        poseOrientationAnimator.setDuration(1000);
        poseOrientationAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    }
}