package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Isaac Assegai on 8/13/2015.
 */
public class Planet extends Sprite {
    // Rendering Oriented Fields
    private Animation rotateAnimation;
    private TextureAtlas textureAtlas;

    //Physics Oriented Fields
    private BodyDef bodyDef;
    private FixtureDef fixtureDef;
    private Fixture fixture;
    private Body body;
    private CircleShape shape;
    private World world;

    private GameWorld parent;

    public Planet(TextureAtlas textureAtlas, World world, GameWorld parent){
        super(textureAtlas.getRegions().first());
        this.parent = parent;
        setupRendering(textureAtlas);
        setupPhysics(world);
    }

    public void render(float elapsedTime, SpriteBatch batch){
        batch.draw(rotateAnimation.getKeyFrame(elapsedTime, true), 0, 0);
    }

    private void setupRendering(TextureAtlas textureAtlas){
        this.textureAtlas = textureAtlas;
        setupAnimations();
    }

    private void setupPhysics(World world){
        this.world = world;
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody; //Planets are not moving
        bodyDef.position.set((this.getX() + this.getWidth()  / 2) / parent.PIXELS_TO_METERS,
                             (this.getY() + this.getHeight() / 2) / parent.PIXELS_TO_METERS);
        body = world.createBody(bodyDef);
        shape = new CircleShape();
        shape.setRadius((getHeight() / 2) / parent.PIXELS_TO_METERS);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 1f;
        fixture = body.createFixture(fixtureDef);
        shape.dispose();
    }

    private void setupAnimations(){
        rotateAnimation = new Animation(1/16f, textureAtlas.getRegions());
    }
}
