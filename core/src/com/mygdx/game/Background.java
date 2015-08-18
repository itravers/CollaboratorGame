package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;

import java.util.ArrayList;

/**
 * Represents a single levels background.
 * Created by Isaac Assegai on 8/17/2015.
 */
public class Background {
    public static int NUM_LAYERS = 3;
    private LevelManager parent;
    private TextureRegion[] layers;

    public Background(LevelManager parent, TextureRegion[] layers){
        this.parent = parent;
        this.layers = layers;
    }


    /**
     * Returns the designated background layer.
     * If layerNum is to big, it returns the last available layer.
     * If layerNum is to small, it returns the first available layer.
     * @param layerNum The layer number we are seeking to find.
     * @return The layer itself.
     */
    public TextureRegion getLayer(int layerNum){
        TextureRegion layer;
        //Check if layerNum doesn't fit specified parameters, and fix.
        if(layerNum >= NUM_LAYERS){
            //layerNum is too high, use last available layer.
            layer = layers[NUM_LAYERS-1];
        }else if(layerNum >= 0){
            //layer num is just right, return it.
            layer = layers[layerNum];
        }else{
            //layerNum is too low, return first available layer.
            layer = layers[0];
        }
        return layer;
    }

    public void render(float elapsedTime, SpriteBatch batch){
        Matrix4 temp = batch.getProjectionMatrix();
        batch.setProjectionMatrix(parent.getParent().backgroundCamera.calculateParallaxMatrix(.1f, .1f));
        batch.disableBlending();
        batch.begin();
        batch.draw(layers[0], -(int) (layers[0].getRegionWidth() / 2),
                -(int) (layers[0].getRegionHeight() / 2));
        batch.end();
        batch.enableBlending();

        batch.setProjectionMatrix(parent.getParent().backgroundCamera.calculateParallaxMatrix(.3f, .3f));
        batch.begin();
        batch.draw(layers[1], -(int) (layers[1].getRegionWidth() / 2),
                -(int) (layers[1].getRegionHeight() / 2));
        batch.end();

        batch.setProjectionMatrix(temp);
    }
}
