package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Keeps track of all animations and animation type data.
 * Created by Isaac Assegai on 8/17/2015.
 */
public class AnimationManager {
    GameWorld parent;

    //Animation Related Fields
    private TextureAtlas shipAtlas;
    private TextureAtlas deadAtlas;
    private TextureAtlas explosionAtlas;
    private TextureAtlas planetAtlas;

    private TextureRegion[] deadFrames;
    private TextureRegion[] moveForwardFrames;
    private TextureRegion[] noMovementAnimationFrames;

    private Animation deadAnimation;
    private Animation moveForwardAnimation;
    private Animation noMovementAnimation;
    private Animation explosionAnimation;


    private Animation planetRotateAnimation;

    public AnimationManager(GameWorld parent){
        this.parent = parent;
    }

    public void setupAnimations(){
        setupMoveForwardAnimation();
        setupNoMovementAnimation();
        setupExplosionAnimation();
        setupDeadAnimation();
        setupPlanetRotateAnimation();
    }

    private void setupPlanetRotateAnimation(){
        planetAtlas = new TextureAtlas(Gdx.files.internal("data/planetSprites.txt"));
        planetRotateAnimation = new Animation(1/16f, planetAtlas.getRegions());
    }

    /**
     * Gets our forward animation from the sprite sheet.
     */
    private void setupMoveForwardAnimation(){
        shipAtlas = new TextureAtlas(Gdx.files.internal("data/shipSprite.txt"));
        moveForwardFrames = new TextureRegion[7];
        moveForwardFrames[0] = (shipAtlas.findRegion("0005"));
        moveForwardFrames[1] = (shipAtlas.findRegion("0006"));
        moveForwardFrames[2] = (shipAtlas.findRegion("0007"));
        moveForwardFrames[3] = (shipAtlas.findRegion("0008"));
        moveForwardFrames[4] = (shipAtlas.findRegion("0007"));
        moveForwardFrames[5] = (shipAtlas.findRegion("0006"));
        moveForwardFrames[6] = (shipAtlas.findRegion("0005"));
        moveForwardAnimation = new Animation(1/15f, moveForwardFrames);
    }

    private void setupNoMovementAnimation(){
        noMovementAnimationFrames = new TextureRegion[2];
        noMovementAnimationFrames[0] = (shipAtlas.findRegion("0005"));
        noMovementAnimationFrames[1] = (shipAtlas.findRegion("0005"));
        noMovementAnimation = new Animation(1/2f, noMovementAnimationFrames);
    }

    private void setupExplosionAnimation(){
        explosionAtlas = new TextureAtlas(Gdx.files.internal("data/explosionC.txt"));
        explosionAnimation = new Animation(1/30f, explosionAtlas.getRegions());
    }

    private void setupDeadAnimation(){
        deadAtlas = new TextureAtlas(Gdx.files.internal("data/coin.txt"));
        deadFrames = new TextureRegion[27];
        for(int i = 0; i < 27; i++){/* 27 frames in the rotate and flip animation. */
            if(i < 10){
                deadFrames[i] = (deadAtlas.findRegion("flipAndRotateAnimation00"+i+"0"));
            }else{
                deadFrames[i] = (deadAtlas.findRegion("flipAndRotateAnimation0"+i+"0"));
            }

        }
        deadAnimation = new Animation(1/8f, deadFrames);
    }


    public Animation getNoMovementAnimation() {
        return noMovementAnimation;
    }

    public void setNoMovementAnimation(Animation noMovementAnimation) {
        this.noMovementAnimation = noMovementAnimation;
    }

    public Animation getExplosionAnimation() {
        return explosionAnimation;
    }

    public void setExplosionAnimation(Animation explosionAnimation) {
        this.explosionAnimation = explosionAnimation;
    }

    public Animation getMoveForwardAnimation() {
        return moveForwardAnimation;
    }

    public void setMoveForwardAnimation(Animation moveForwardAnimation) {
        this.moveForwardAnimation = moveForwardAnimation;
    }

    public Animation getDeadAnimation() {
        return deadAnimation;
    }

    public void setDeadAnimation(Animation deadAnimation) {
        this.deadAnimation = deadAnimation;
    }


    public TextureAtlas getShipAtlas() {
        return shipAtlas;
    }

    public void setShipAtlas(TextureAtlas shipAtlas) {
        this.shipAtlas = shipAtlas;
    }


    public TextureAtlas getPlanetAtlas() {
        return planetAtlas;
    }

    public void setPlanetAtlas(TextureAtlas planetAtlas) {
        this.planetAtlas = planetAtlas;
    }


    public Animation getPlanetRotateAnimation() {
        return planetRotateAnimation;
    }

    public void setPlanetRotateAnimation(Animation planetRotateAnimation) {
        this.planetRotateAnimation = planetRotateAnimation;
    }


}
