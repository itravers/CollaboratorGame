package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Isaac Assegai on 8/13/2015.
 */
public class Planet extends Sprite {
    //private TextureRegion[] rotateFrames;
    private Animation rotateAnimation;
    private TextureAtlas textureAtlas;

    public Planet(TextureAtlas textureAtlas){
        super(textureAtlas.getRegions().first());
        //super.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/3+200);
        setupRendering(textureAtlas);
    }

    public void render(float elapsedTime, SpriteBatch batch){
        batch.draw(rotateAnimation.getKeyFrame(elapsedTime, true), -this.getWidth()/2, -this.getHeight()/2+200);
    }

    private void setupRendering(TextureAtlas textureAtlas){
        this.textureAtlas = textureAtlas;
        setupAnimations();
    }

    private void setupAnimations(){
        rotateAnimation = new Animation(1/16f, textureAtlas.getRegions());
    }
}
