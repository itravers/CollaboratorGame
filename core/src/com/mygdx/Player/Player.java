package com.mygdx.Player;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.mygdx.game.GameWorld;
import com.mygdx.game.Planet;

import java.util.ArrayList;

import input.GameInput;

/**
 * The Player Sprites
 */
public class Player extends Sprite {
	GameWorld parent;
	// Rendering Oriented Fields
	private TextureRegion[] moveForwardFrames;
	private TextureAtlas textureAtlas;
	private Animation animation;
	private Animation moveForwardAnimation;
	private Animation noMovementAnimation;
	private TextureRegion[] noMovementAnimationFrames;
	private Animation currentAnimation;
	private float lastFrameTime = 0; //Used by gravity to calculate the time since last frame.

	// Physics Oriented Fields
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	private Fixture fixture;
	private Body body;
	private PolygonShape shape;
	private World world;
	private float torque = 0.0f;
	public float MAX_VELOCITY = 80f;
	public float MAX_ANGULAR_VELOCITY = 20f;

	//Inputs
	public boolean forwardPressed;
	public boolean backwardPressed;
	public boolean rotateRightPressed;
	public boolean rotateLeftPressed;
	public ArrayList<GameInput>inputList;

	/**
	 * Player Constructor
	 * @param textureAtlas The spritesheet, etc
	 * @param world The physics world the player exists in.
	 */
	public Player(TextureAtlas textureAtlas, World world, GameWorld parent){
		super(textureAtlas.getRegions().first());
		this.parent = parent;
		setupInputs();
		setupRendering(textureAtlas);
		setupPhysics(world);
	}

	private void setupInputs(){
		forwardPressed = false;
		backwardPressed = false;
		rotateLeftPressed = false;
		rotateRightPressed = false;
		inputList = new ArrayList<GameInput>();
	}

	/**
	 * Render the player with it's current animation.
	 * @param elapsedTime Time passed since start of simulation.
	 * @param batch The GL batch renderer.
	 */
	public void render(float elapsedTime, SpriteBatch batch){
		batch.draw(currentAnimation.getKeyFrame(elapsedTime, true), getX(), getY(),
				this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(),
				this.getScaleX(), this.getScaleY(), this.getRotation());
	}

	/**
	 * Initialize players physics.
	 * @param world The physics world the player exists in.
	 */
	private void setupPhysics(World world){
		this.world = world;
		bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set((getX() + getWidth()  / 2) / parent.PIXELS_TO_METERS,
				             (getY() + getHeight() / 2) / parent.PIXELS_TO_METERS);
		body = world.createBody(bodyDef);
		body.setLinearDamping(.5f);
		body.setAngularDamping(.5f);
		shape = new PolygonShape();
		shape.setAsBox((getWidth()  / 2) / parent.PIXELS_TO_METERS,
				       (getHeight() / 2) / parent.PIXELS_TO_METERS);
		fixtureDef  = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1.25f;
		fixtureDef.friction = .5f;
		fixture = body.createFixture(fixtureDef);
		shape.dispose();
	}

	/**
	 * Initialze player rendering.
	 * @param textureAtlas The spritesheet for the player.
	 */
	private void setupRendering(TextureAtlas textureAtlas){
		parent.debugRenderer = new Box2DDebugRenderer();
		this.textureAtlas = textureAtlas;
		setupAnimations();
	}

	/**
	 * Initializes player animations.
	 */
	private void setupAnimations(){
		animation = new Animation(1/15f, textureAtlas.getRegions());
		setupMoveForwardAnimation();
		setupNoMovementAnimation();
		currentAnimation = noMovementAnimation;
	}

	/**
	 * Gets our forward animation from the sprite sheet.
	 */
	private void setupMoveForwardAnimation(){
		moveForwardFrames = new TextureRegion[7];
		moveForwardFrames[0] = (textureAtlas.findRegion("0005"));
		moveForwardFrames[1] = (textureAtlas.findRegion("0006"));
		moveForwardFrames[2] = (textureAtlas.findRegion("0007"));
		moveForwardFrames[3] = (textureAtlas.findRegion("0008"));
		moveForwardFrames[4] = (textureAtlas.findRegion("0007"));
		moveForwardFrames[5] = (textureAtlas.findRegion("0006"));
		moveForwardFrames[6] = (textureAtlas.findRegion("0005"));
		moveForwardAnimation = new Animation(1/15f, moveForwardFrames);
	}

	private void setupNoMovementAnimation(){
		noMovementAnimationFrames = new TextureRegion[2];
		noMovementAnimationFrames[0] = (textureAtlas.findRegion("0005"));
		noMovementAnimationFrames[1] = (textureAtlas.findRegion("0005"));
		noMovementAnimation = new Animation(1/2f, noMovementAnimationFrames);
	}

	public void update(float elapsedTime){
		applyInput(elapsedTime);
		applyGravity(elapsedTime);
		body.applyTorque(torque, true);
		this.setPosition(body.getPosition().x * parent.PIXELS_TO_METERS - getWidth() / 2,
				body.getPosition().y * parent.PIXELS_TO_METERS - getHeight() / 2);
		setRotation((float)Math.toDegrees(body.getAngle()));
	}

	/**
	 * We will apply orbital gravity, we will need to loop through all planets to add gravity.
	 */
	private void applyGravity(float elapsedTime){
		ArrayList<Planet> planets = parent.getPlanets();
		Vector2 force = new Vector2(0, 0);
		Vector2 preForce = new Vector2(0, 0);
		for(int i = 0; i < planets.size(); i++){
			Planet p = planets.get(i);
			float g = .08f;
			float pMass = p.getMass();
			float sMass = this.getBody().getMass();
			Vector2 pCenter = p.getBody().getPosition();
			Vector2 sCenter = this.getBody().getPosition();
			float distanceSQ = sCenter.dst2(pCenter); // Offset the position so we orbit around center of planet
			float distance = sCenter.dst(pCenter);
			float pRadius = p.getBody().getFixtureList().first().getShape().getRadius();
			//calculate force here
			preForce.set(0, 0);
			preForce = (pCenter.sub(sCenter));
			preForce = preForce.scl(g * pMass * sMass);
			preForce.set(preForce.x / distanceSQ, preForce.y / distanceSQ); //Divide by a scalar.
			force = force.add(preForce);
		}
		float elapsedTimeInLastFrame = elapsedTime - lastFrameTime;
		lastFrameTime = elapsedTime;
		force = force.scl(elapsedTimeInLastFrame);
		//System.out.println("force = " + force.x + ":" + force.y);
		this.getBody().applyForce(force, body.getPosition(), true);
	}

	private void applyInput(float elapsedTime){
		Vector2 impulse = new Vector2(-(float)Math.sin(body.getAngle()), (float)Math.cos(body.getAngle())).scl(2f);
		Vector2 pos = body.getPosition();

		if(forwardPressed || backwardPressed){
			currentAnimation = moveForwardAnimation;
		}else{
			currentAnimation = noMovementAnimation;
		}
		if(forwardPressed) body.applyLinearImpulse(impulse, pos, true);
		if(backwardPressed){
			impulse = impulse.rotate(180f);
			body.applyLinearImpulse(impulse, pos, true);
		}
		if(rotateLeftPressed) body.applyAngularImpulse(-1f, true);
		if(rotateRightPressed) body.applyAngularImpulse(1f, true);
	}

	public void setBody(Body b){
		body = b;
	}

	public Body getBody(){
		return body;
	}
}
