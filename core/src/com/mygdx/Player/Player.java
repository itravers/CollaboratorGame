package com.mygdx.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
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

import javax.xml.soap.Text;

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
	private TextureAtlas explosionAtlas;
	private TextureRegion[] explosionFrames;
	private Animation explosionAnimation;
	private TextureAtlas deadAtlas;
	private TextureRegion[] deadFrames;
	private Animation deadAnimation;
	public enum STATE {ALIVE, EXPLOADING, DEAD}
	AnimationController animationController;

	private STATE currentState;


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
	public Player(Vector2 pos, TextureAtlas textureAtlas, World world, GameWorld parent){
		super(textureAtlas.getRegions().first());
		this.parent = parent;
		setCurrentState(STATE.ALIVE);
		this.setPosition(pos.x, pos.y);
		setupInputs();
		setupRendering(textureAtlas);
		setupPhysics(world);
	}

	public Player() {
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
		/* Change state from exploading to dead if the exploading animation is done. */
		if(getCurrentState() == STATE.EXPLOADING && currentAnimation.isAnimationFinished(elapsedTime)){
			setCurrentState(STATE.DEAD);
		}
		//if(getCurrentState() == STATE.ALIVE){
			batch.draw(currentAnimation.getKeyFrame(elapsedTime, true), getX(), getY(),
					this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(),
					this.getScaleX(), this.getScaleY(), this.getRotation());
		/*}else if(getCurrentState() == STATE.EXPLOADING){
			if(explosionAnimation.isAnimationFinished(elapsedTime)){
				setCurrentState(STATE.DEAD);
				System.out.println("DEAD!");
			} else {
				System.out.println("EXPLOADING");
				batch.draw(explosionAnimation.getKeyFrame(elapsedTime, false), getX(), getY(),
						this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(),
						this.getScaleX(), this.getScaleY(), this.getRotation());
				//
			}

		}*/


		/* Create a new GameInput to record Player states. We do this every time the player
			has input, but we also do it here, maybe twice a second? We */
		if (!(this instanceof Ghost) && parent.parent.getFrameNum() % 30 == 0){
			GameInput gInput = new GameInput(GameInput.InputType.TIMER, 0, parent.parent.getFrameNum(), parent.parent.elapsedTime, this);
			inputList.add(gInput);
		}

	}

	public void setWorld(World world){
		setupPhysics(world);
	}

	/**
	 * Initialize players physics.
	 * @param world The physics world the player exists in.
	 */
	public void setupPhysics(World world){
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
		fixtureDef.filter.groupIndex = parent.CATEGORY_PLAYER;
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
		setupExplosionAnimation();
		setupDeadAnimation();
		currentAnimation = noMovementAnimation;
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

	private void setupExplosionAnimation(){
		explosionAtlas = new TextureAtlas(Gdx.files.internal("data/explosionC.txt"));
		explosionAnimation = new Animation(1/30f, explosionAtlas.getRegions());
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
		if(getCurrentState() == STATE.DEAD){
			//we have died, we don't want to update like normal.
		}else{
			//we are not dead, we want to update
			//i think we'll let the explosion state fall through here too
			applyInput(elapsedTime);
			applyGravity(elapsedTime);
			body.applyTorque(torque, true);
			setRotation((float) Math.toDegrees(body.getAngle()));
		}
		/* We want the position to be set reguardless of the STATE. */
		this.setPosition(body.getPosition().x * parent.PIXELS_TO_METERS - getWidth() / 2,
				body.getPosition().y * parent.PIXELS_TO_METERS - getHeight() / 2);

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
		chooseAnimation();

		if(forwardPressed) body.applyLinearImpulse(impulse, pos, true);
		if(backwardPressed){
			impulse = impulse.rotate(180f);
			body.applyLinearImpulse(impulse, pos, true);
		}
		if(rotateLeftPressed) body.applyAngularImpulse(-1f, true);
		if(rotateRightPressed) body.applyAngularImpulse(1f, true);
	}

	/**
	 * Choose animation based on current input as well as current state
	 */
	private void chooseAnimation(){
		if(getCurrentState() == STATE.ALIVE){
			if(forwardPressed || backwardPressed){
				currentAnimation = moveForwardAnimation;
			}else{
				currentAnimation = noMovementAnimation;
			}
		}else if(getCurrentState() == STATE.EXPLOADING){
			currentAnimation = explosionAnimation;
		}else if(getCurrentState() == STATE.DEAD){
			currentAnimation = deadAnimation;
		}

	}

	public void setBody(Body b){
		body = b;
	}

	public Body getBody(){
		return body;
	}

	public TextureAtlas getTextureAtlas() {
		return textureAtlas;
	}

	public void setTextureAtlas(TextureAtlas textureAtlas) {
		this.textureAtlas = textureAtlas;
	}


	public STATE getCurrentState() {
		return currentState;
	}

	/**
	 * Set the current state. Change animation sometimes
	 * @param currentState
	 */
	public void setCurrentState(STATE currentState) {
		System.out.println("Setting State to: " + currentState);
		if(currentState == STATE.EXPLOADING){
			currentAnimation = explosionAnimation;
		}else if(currentState == STATE.DEAD){
			currentAnimation = deadAnimation;
		}
		this.currentState = currentState;
	}

}
