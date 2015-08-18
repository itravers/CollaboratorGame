package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
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

    public Planet(Vector2 pos, TextureAtlas textureAtlas, World world, float mass, GameWorld parent){
        super(textureAtlas.getRegions().first());
        this.parent = parent;
        this.mass = mass;
        float radius = getRadiusFromMass(mass);
        this.setSize(radius, radius);
        this.setPosition(pos.x, pos.y);
        setupRendering(textureAtlas);
        System.out.println("Width: " + this.getWidth() + " height: " + this.getHeight());
        setupPhysics(world);
    }

    public void render(float elapsedTime, SpriteBatch batch){
       // System.out.println("Planet: " + getX() + " " + getY() + " Body: " + body.getPosition().x + " " + body.getPosition().y);
       // batch.draw(rotateAnimation.getKeyFrame(elapsedTime, true), 0, 0);
        batch.draw(rotateAnimation.getKeyFrame(elapsedTime, true), getX(), getY(),
                this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(),
                this.getScaleX(), this.getScaleY(), this.getRotation());
    }

    private void setupRendering(TextureAtlas textureAtlas){
        this.textureAtlas = textureAtlas;
        setupAnimations();
    }

    public float getRadiusFromMass(float m){
        float radius = 0;
        float sM = 10000f;
        float bM = 400000f;
        float sP = 50f;
        float bP = 850f;
        radius = (((m - sM) * (bP - sP)) / (bM - sM))+sP;

        return radius;
    }

    private void setupPhysics(World world){
        this.world = world;
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody; //Planets are not moving
        bodyDef.position.set((getX() + getRadiusFromMass(mass)  / 2) / parent.PIXELS_TO_METERS,
                             (getY() + getRadiusFromMass(mass) / 2) / parent.PIXELS_TO_METERS);
        body = world.createBody(bodyDef);

        shape = new CircleShape();
        shape.setRadius((getHeight() / 2) / parent.PIXELS_TO_METERS);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.groupIndex = parent.CATEGORY_PLANET;
        fixtureDef.density = 10f;
        fixtureDef.friction = .5f;
        fixture = body.createFixture(fixtureDef);
        body.setUserData(this); //used by body to tell who it's parent is... basically
        shape.dispose();
    }

    private void setupAnimations(){
        rotateAnimation = new Animation(1/16f, textureAtlas.getRegions());
    }

    public void update(float elapsedTime){
        this.setPosition(body.getPosition().x * parent.PIXELS_TO_METERS - getWidth() / 2,
                body.getPosition().y * parent.PIXELS_TO_METERS - getHeight() / 2);
    }


    public void dispose(){
        shape.dispose();
        textureAtlas.dispose();
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

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }

    public void setTextureAtlas(TextureAtlas textureAtlas) {
        this.textureAtlas = textureAtlas;
    }
}
