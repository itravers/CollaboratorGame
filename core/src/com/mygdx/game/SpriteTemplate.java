package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by slack on 8/24/2015.
 */

public class SpriteTemplate {


    private String type;
    private String atlas;
    private float xLoc;
    private float yLoc;
    private float mass;
    private float size;

    public String getType() {
        return type;
    }

    public String getAtlas() {
        return atlas;
    }

    public void setType(String type) {
        this.type = type;
    }


    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void setAtlas(String atlas) {
        this.atlas = atlas;
    }

    public float getxLoc() {
        return xLoc;
    }

    public void setxLoc(float xLoc) {
        this.xLoc = xLoc;
    }

    public float getyLoc() {
        return yLoc;
    }

    public void setyLoc(float yLoc) {
        this.yLoc = yLoc;
    }
}