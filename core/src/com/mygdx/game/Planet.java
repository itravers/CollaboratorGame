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
import com.badlogic.gdx.physics.box2d.MassData;
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
    private float mass; /* A Static body doesn't have a mass of it's own, so we need this. */

    private GameWorld parent;

    public Planet(TextureAtlas textureAtlas, World world, float mass, GameWorld parent){
        super(textureAtlas.getRegions().first());
        this.parent = parent;
        this.mass = mass;
        setupRendering(textureAtlas);
        setupPhysics(world);
    }

    public void render(float elapsedTime, SpriteBatch batch){
       // batch.draw(rotateAnimation.getKeyFrame(elapsedTime, true), 0, 0);
        batch.draw(rotateAnimation.getKeyFrame(elapsedTime, true), getX(), getY(),
                this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(),
                this.getScaleX(), this.getScaleY(), this.getRotation());
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
       // MassData massData = new MassData();
       // massData.center.set(body.getPosition());
       // massData.I = 1f;
       // massData.mass = 100000f;
        //body.setMassData(massData);

        shape = new CircleShape();
        shape.setRadius((getHeight() / 2) / parent.PIXELS_TO_METERS);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 10f;
        fixtureDef.friction = .5f;
        fixture = body.createFixture(fixtureDef);
        shape.dispose();
    }

    private void setupAnimations(){
        rotateAnimation = new Animation(1/16f, textureAtlas.getRegions());
    }


    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }
}
