package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;

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
    private TextureAtlas gravityWellAtlas;
    private TextureAtlas exhastAtlas;

    //tests with running figure
    private TextureAtlas runRightAtlas;
    private Array<TextureAtlas.AtlasRegion> runRightRegion;
    private Animation runRightAnimation;

    private TextureRegion[] deadFrames;
    //private TextureRegion[] moveForwardFrames;
    private TextureRegion[] noMovementAnimationFrames;
    private Array<TextureAtlas.AtlasRegion> exhastRegion;
    private Array<TextureAtlas.AtlasRegion> planetRegion;
    private Array<TextureAtlas.AtlasRegion> moveForwardRegion;


    private Animation deadAnimation;
    private Animation moveForwardAnimation;
    private Animation noMovementAnimation;
    private Animation explosionAnimation;
    private Animation exhastAnimation;

    private Animation planetRotateAnimation;

    public AnimationManager(GameWorld parent){
        this.parent = parent;
    }

    public void setupAnimations(){
        setupRunRightAnimation();
        setupMoveForwardAnimation();
        setupNoMovementAnimation();
        setupExplosionAnimation();
        setupDeadAnimation();
        setupPlanetRotateAnimation();
        setupGravityWells();
        setupExhastAnimation();

    }



    public Animation getExhastAnimation() {
        return exhastAnimation;
    }

    public void setExhastAnimation(Animation exhastAnimation) {
        this.exhastAnimation = exhastAnimation;
    }

    private void setupExhastAnimation(){
        exhastAtlas = new TextureAtlas(Gdx.files.internal("data/Exhast.pack"));
        exhastRegion = exhastAtlas.getRegions();
        exhastAnimation = new Animation(1/30f, exhastRegion);

    }

    private void setupGravityWells(){
        gravityWellAtlas = new TextureAtlas(Gdx.files.internal("data/gravity_Well.txt"));
    }


    private void setupPlanetRotateAnimation(){
        planetAtlas = new TextureAtlas((Gdx.files.internal("data/Planets.pack")));
        planetRegion = planetAtlas.findRegions("Moon");
        planetRotateAnimation = new Animation(1/2f, planetRegion);
    }

    private void setupRunRightAnimation(){
        runRightAtlas = new TextureAtlas(Gdx.files.internal("data/RunRight.pack"));
        runRightRegion = runRightAtlas.getRegions();
        runRightAnimation = new Animation(1/30f, runRightRegion);
    }

    /**
     * Gets our forward animation from the sprite sheet.
     */
    private void setupMoveForwardAnimation(){

        shipAtlas = new TextureAtlas(Gdx.files.internal("data/Ship.pack"));
        moveForwardRegion = shipAtlas.getRegions();
        moveForwardAnimation = new Animation(1/15f, moveForwardRegion);
    }

    private void setupNoMovementAnimation(){
        noMovementAnimationFrames = new TextureRegion[2];
        noMovementAnimationFrames[0] = (shipAtlas.getRegions().first());
        noMovementAnimationFrames[1] = (shipAtlas.getRegions().first());
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

    public Animation getRunRightAnimation(){return runRightAnimation;}

    public void setRunRightAnimation(Animation a){
        runRightAnimation = a;
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


    public TextureAtlas getGravityWellAtlas() {
        return gravityWellAtlas;
    }

    public void setGravityWellAtlas(TextureAtlas gravityWellAtlas) {
        this.gravityWellAtlas = gravityWellAtlas;
    }



}
