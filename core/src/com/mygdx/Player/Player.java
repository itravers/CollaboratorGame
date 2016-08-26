package com.mygdx.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import com.mygdx.game.AnimationManager;
import com.mygdx.game.GameWorld;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Planet;

import java.util.ArrayList;

import com.mygdx.input.GameInput;

/**
 * The Player Sprites
 */
public class Player extends Sprite {
	GameWorld parent;
	// Rendering Oriented Fields
	private Animation currentAnimation;
	private float lastFrameTime = 0; //Used by gravity to calculate the time since last frame.
	
	public float TOTAL_BOOST_TIME = 2;
	private float boostTime;

	// State Tracking Fields
	public enum STATE {FLYING, LAND_FORWARD, LAND_SIDEWAYS, EXPLOADING, DEAD, STAND_STILL_SIDEWAYS, STAND_STILL_FORWARD,
		WALK_SLOW, WALK_FAST, JUMP_FORWARD, JUMP_SIDEWAYS, RUN_SLOW, RUN_FAST, FLOAT_SIDEWAYS, WAVE}
	private STATE currentState;

	// Physics Oriented Fields
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	private Fixture fixture;
	private Body body;
	private PolygonShape shape;
	private World world;
	private float torque = 0.0f;

	private Vector2 gravityForce;
	public float MAX_VELOCITY = 35f;
	public float CRASH_VELOCITY;
	public float MAX_ANGULAR_VELOCITY = 20f;
	
	public float MAX_HEALTH = 100;
	private float health;

	//Inputs
	public boolean forwardPressed;
	public boolean backwardPressed;
	public boolean rotateRightPressed;
	public boolean rotateLeftPressed;
	public boolean boostPressed;
	public ArrayList<GameInput>inputList;
	public float stateTime;

	/**
	 * Player Constructor
	 * @param textureAtlas The spritesheet, etc
	 * @param world The physics world the player exists in.
	 */
	public Player(Vector2 pos, TextureAtlas textureAtlas, World world, GameWorld parent){
		super(textureAtlas.getRegions().first(), 0, 0, 32, 40);
		this.parent = parent;
		setCurrentState(STATE.WALK_SLOW);
		this.setPosition(pos.x, pos.y);
		boostTime = TOTAL_BOOST_TIME;
		health = MAX_HEALTH;
		setupInputs();
		setupRendering();
		setupPhysics(world);
	}

	public Player() {
	}

	public void dispose(){
		shape.dispose();
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
		//System.out.println("Mass: " + this.body.getMass());
		/* Change state from exploading to dead if the exploading animation is done. */
		if(getCurrentState() == STATE.EXPLOADING && currentAnimation.isAnimationFinished(stateTime)){
			setCurrentState(STATE.DEAD);
		}

		/* We only want to transition to flying if we are currently Jumping forward, or floating sideways
		 * (see state flow chart). We are considered to be flying when we are over 3.5 units (magic num) from the planet
		  * and we were previously in the mentioned states.*/
		if((getCurrentState() == STATE.JUMP_FORWARD || getCurrentState() == STATE.FLOAT_SIDEWAYS) && getDistanceFromClosestPlanet() > 3.5){
			setCurrentState(STATE.FLYING);
		}

		 //draws all sprites the same size
		batch.draw(currentAnimation.getKeyFrame(elapsedTime, true), getX(), getY(), 
				this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(),
				this.getScaleX(), this.getScaleY(), this.getRotation());

		if(forwardPressed && !boostPressed){
			//draws all sprites the same size
			//get a directly on the butt of the ship.
			Vector2 P = new Vector2(getX(), getY());
			Vector2 D = new Vector2((float)Math.cos(body.getAngle()), (float)Math.sin(body.getAngle()));
			D = D.rotate(88);
			Animation exhastAnimation = parent.getAnimationManager().getExhastAnimation();
			TextureRegion exhastFrame = exhastAnimation.getKeyFrame(elapsedTime, true);
			D = D.setLength(-this.getHeight()*1.55f);
			Vector2 B = P.cpy().sub(D);
			//System.out.println("pPos: " + P + " bPos: " + B + " pDir: " + D);
			batch.draw(parent.getAnimationManager().getExhastAnimation().getKeyFrame(elapsedTime, true), B.x, B.y,
					this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(),
					this.getScaleX()*2, this.getScaleY()*2, this.getRotation()+180);
		}
		
		if(forwardPressed && boostPressed){
			//draws all sprites the same size
			//get a directly on the butt of the ship.
			Vector2 P = new Vector2(getX(), getY());
			Vector2 D = new Vector2((float)Math.cos(body.getAngle()), (float)Math.sin(body.getAngle()));
			D = D.rotate(88);
			Animation exhastAnimation = parent.getAnimationManager().getExhastAnimation();
			TextureRegion exhastFrame = exhastAnimation.getKeyFrame(elapsedTime, true);
			D = D.setLength(-this.getHeight()*2.5f);
			Vector2 B = P.cpy().sub(D);
			//System.out.println("pPos: " + P + " bPos: " + B + " pDir: " + D);
			batch.draw(parent.getAnimationManager().getExhastAnimation().getKeyFrame(elapsedTime, true), B.x, B.y,
					this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(),
					this.getScaleX()*2, this.getScaleY()*4, this.getRotation()+180);
		}

		if(backwardPressed && !boostPressed){
			//draws all sprites the same size
			//get a directly on the butt of the ship.
			Vector2 P = new Vector2(getX(), getY());
			Vector2 D = new Vector2((float)Math.cos(body.getAngle()), (float)Math.sin(body.getAngle()));
			D = D.rotate(270);
			Animation exhastAnimation = parent.getAnimationManager().getExhastAnimation();
			TextureRegion exhastFrame = exhastAnimation.getKeyFrame(elapsedTime, true);
			D = D.setLength(-this.getHeight()*.98f);
			Vector2 B = P.cpy().sub(D);
			//System.out.println("pPos: " + P + " bPos: " + B + " pDir: " + D);
			batch.draw(parent.getAnimationManager().getExhastAnimation().getKeyFrame(elapsedTime, true), B.x, B.y,
					this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(),
					this.getScaleX()*1, this.getScaleY()*1, this.getRotation());
		}
		
		if(backwardPressed && boostPressed){
			//draws all sprites the same size
			//get a directly on the butt of the ship.
			Vector2 P = new Vector2(getX(), getY());
			Vector2 D = new Vector2((float)Math.cos(body.getAngle()), (float)Math.sin(body.getAngle()));
			D = D.rotate(270);
			Animation exhastAnimation = parent.getAnimationManager().getExhastAnimation();
			TextureRegion exhastFrame = exhastAnimation.getKeyFrame(elapsedTime, true);
			D = D.setLength(-this.getHeight()*1.55f);
			Vector2 B = P.cpy().sub(D);
			//System.out.println("pPos: " + P + " bPos: " + B + " pDir: " + D);
			batch.draw(parent.getAnimationManager().getExhastAnimation().getKeyFrame(elapsedTime, true), B.x, B.y,
					this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(),
					this.getScaleX()*1, this.getScaleY()*2, this.getRotation());
		}

		if(rotateRightPressed){ //draw an exast coming out of right side of nose
			Vector2 P = new Vector2(getX(), getY());
			Vector2 D = new Vector2((float)Math.cos(body.getAngle()), (float)Math.sin(body.getAngle()));
			D = D.rotate(-150);
			Animation exhastAnimation = parent.getAnimationManager().getExhastAnimation();
			D = D.setLength(-this.getHeight()*.5f);
			Vector2 B = P.cpy().sub(D);
			//System.out.println("pPos: " + P + " bPos: " + B + " pDir: " + D);
			batch.draw(parent.getAnimationManager().getExhastAnimation().getKeyFrame(elapsedTime, true), B.x, B.y,
					this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(),
					this.getScaleX()*.5f, this.getScaleY()*.5f, this.getRotation()-95);
		}

		if(rotateLeftPressed){ //draw an exast coming out of right side of nose
			Vector2 P = new Vector2(getX(), getY());
			Vector2 D = new Vector2((float)Math.cos(body.getAngle()), (float)Math.sin(body.getAngle()));
			D = D.rotate(325);
			//D=D.rotate(180);
			Animation exhastAnimation = parent.getAnimationManager().getExhastAnimation();
			D = D.setLength(this.getHeight()*.5f);
			Vector2 B = P.cpy().sub(D);
			//System.out.println("pPos: " + P + " bPos: " + B + " pDir: " + D);
			batch.draw(parent.getAnimationManager().getExhastAnimation().getKeyFrame(elapsedTime, true), B.x, B.y,
					this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(),
					this.getScaleX()*.5f, this.getScaleY()*.5f, this.getRotation()-275);
		}

		/* // draw different size based on fraem
		TextureRegion frame = currentAnimation.getKeyFrame(elapsedTime, true);
		float x = getX();
		float y = getY();
		float oX = getOriginX();
		float oY = getOriginY();
		float w = frame.getRegionWidth();
		float h = frame.getRegionHeight();
		float sX = this.getScaleX();
		float sY = this.getScaleY();
		batch.draw(frame, x, y, oX, oY , w, h, sX, sY, this.getRotation());
		*/

		/* Create a new GameInput to record Player states. We do this every time the player
			has input, but we also do it here, maybe twice a second? We */
		if (!(this instanceof Ghost) && parent.parent.getFrameNum() % 60 == 0){
			GameInput gInput = new GameInput(GameInput.InputType.TIMER, 0, parent.parent.getFrameNum(), parent.parent.elapsedTime, this);
			inputList.add(gInput);
		}

	}

	private float getDistanceFromClosestPlanet(){
		float distanceToClosestPlanet = 1010101f;
		ArrayList<Planet>planets = parent.getLevelManager().getPlanets();
		for(int i = 0; i < planets.size(); i++){
			Planet p = planets.get(i);
			float radiusPlanet = p.getBody().getFixtureList().first().getShape().getRadius();
			float dist = p.getBody().getPosition().dst(body.getPosition());
			dist -= radiusPlanet;
			if(dist < distanceToClosestPlanet){
				distanceToClosestPlanet = dist;
			}
		}
		return distanceToClosestPlanet;
	}

	public Planet getClosestPlanet(){
		float distanceToClosestPlanet = 1010101f;
		ArrayList<Planet>planets = parent.getLevelManager().getPlanets();
		Planet closest = planets.get(0);
		for(int i = 0; i < planets.size(); i++){
			Planet p = planets.get(i);
			float radiusPlanet = p.getBody().getFixtureList().first().getShape().getRadius();
			float dist = p.getBody().getPosition().dst(body.getPosition());
			dist -= radiusPlanet;
			if(dist < distanceToClosestPlanet){
				distanceToClosestPlanet = dist;
				closest = planets.get(i);
			}
		}
		return closest;
	}

	public void setWorld(World world){
		setupPhysics(world);
	}

	/**
	 * Initialize players physics.
	 * @param world The physics world the player exists in.
	 */
	public void setupPhysics(World world){
		CRASH_VELOCITY = MAX_VELOCITY/3.25f;
		gravityForce = new Vector2(0,0);
		this.world = world;
		bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set((getX() + getWidth()  / 2) / parent.PIXELS_TO_METERS,
				             (getY() + getHeight() / 2) / parent.PIXELS_TO_METERS);
		body = world.createBody(bodyDef);
		body.setLinearDamping(.4f);
		body.setAngularDamping(.5f);
		shape = new PolygonShape();
		shape.setAsBox((getWidth()  / 2) / parent.PIXELS_TO_METERS,
				       (getHeight() / 2) / parent.PIXELS_TO_METERS);
		fixtureDef  = new FixtureDef();
		fixtureDef.filter.groupIndex = parent.CATEGORY_PLAYER;
		fixtureDef.shape = shape;
		fixtureDef.density = 1.25f;
		fixtureDef.friction = .2f;
		fixture = body.createFixture(fixtureDef);
		body.setUserData(this);
		shape.dispose();
	}

	/**
	 * Initialze player rendering.
	 */
	private void setupRendering(){
		parent.getRenderManager().setDebugRenderer( new Box2DDebugRenderer());
		setupAnimations();
	}

	/**
	 * Initializes player animations.
	 */
	private void setupAnimations(){
		currentAnimation = parent.getAnimationManager().getNoMovementAnimation();
	}

	public void update(float elapsedTime){
		stateTime += Gdx.graphics.getDeltaTime();
		//updateBodyBasedOnFrame(elapsedTime);
		if(getCurrentState() == STATE.DEAD || getCurrentState() == STATE.EXPLOADING){
			//we have died, we don't want to update like normal.
			body.setLinearVelocity(0,0); // stay where we die at.
			body.setAngularVelocity(0);
		}else{
			//we are not dead, we want to update
			//i think we'll let the explosion state fall through here too
			applyInput(elapsedTime);
			applyGravity(elapsedTime);
			body.applyTorque(torque, true);
			setRotation((float) Math.toDegrees(body.getAngle()));
		}
		boostTime += Gdx.graphics.getDeltaTime()/100;
		//if(boostTime < 0)boostTime = 0;
		//System.out.println("boostTime:" + boostTime);
		if(boostTime > TOTAL_BOOST_TIME) boostTime = TOTAL_BOOST_TIME;
		
		/* We want the position to be set reguardless of the STATE. */
		this.setPosition(body.getPosition().x * parent.PIXELS_TO_METERS - getWidth() / 2,
				body.getPosition().y * parent.PIXELS_TO_METERS - getHeight() / 2);


	}

	public void updateBodyBasedOnFrame(float elapsedTime){
		TextureRegion frame = currentAnimation.getKeyFrame(elapsedTime, true);
		fixture.getShape().setRadius(frame.getRegionWidth());

	}

	/**
	 * We will apply orbital gravity, we will need to loop through all planets to add gravity.
	 */
	private void applyGravity(float elapsedTime){
		ArrayList<Planet> planets = parent.getLevelManager().getPlanets();
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

			//only add the force if the distance is less than the gravity radius of the planet
			if(distance* parent.PIXELS_TO_METERS*2 <= p.getGravityRadius()){
				//System.out.println("dist: " + (distance* parent.PIXELS_TO_METERS * 2)  + " gravRadius: " + p.getGravityRadius());
				force = force.add(preForce);
			}

		}
		float elapsedTimeInLastFrame = elapsedTime - lastFrameTime;
		lastFrameTime = elapsedTime;
		this.gravityForce = force.cpy();
		force = force.scl(elapsedTimeInLastFrame);
		//System.out.println("force = " + force.x + ":" + force.y);
		this.getBody().applyForce(force, body.getPosition(), true);

	}

	private void applyInput(float elapsedTime){
		float deltaTime = Gdx.graphics.getDeltaTime();
		Vector2 impulse;
		//System.out.println("boostTime: " + boostTime);
		//System.out.println("elapsedTime " + deltaTime);
		if(boostPressed && boostTime > 0){
			impulse = new Vector2(-(float)Math.sin(body.getAngle()), (float)Math.cos(body.getAngle())).scl(4f);
			//update boost time
			boostTime -= deltaTime;
		}else if(boostPressed && boostTime <= 0){
			boostPressed = false;
			impulse = new Vector2(-(float)Math.sin(body.getAngle()), (float)Math.cos(body.getAngle())).scl(2f);

		}else{
			impulse = new Vector2(-(float)Math.sin(body.getAngle()), (float)Math.cos(body.getAngle())).scl(2f);
		}
		
		Vector2 pos = body.getPosition();
		chooseAnimation();

		if(forwardPressed) body.applyLinearImpulse(impulse, pos, true);
		if(backwardPressed){
			impulse = impulse.rotate(180f);
			impulse = impulse.scl(.5f);
			body.applyLinearImpulse(impulse, pos, true);
		}

		/* If we are on the planet we want to apply a sideways linear impulse on our body.
		   If we are off the planet we want to apply an angular impulse.
		 */
		if(onPlanet()){
			if(rotateLeftPressed){
				System.out.println("impulse: " + impulse.len());
				body.applyLinearImpulse(impulse.rotate(-90), pos, true);
				body.applyLinearImpulse(impulse.rotate(180).limit(impulse.len()/4), pos, true);
			}
			if(rotateRightPressed){
				System.out.println("impulse: " + impulse.len());
				body.applyLinearImpulse(impulse.rotate(90), pos, true);
				body.applyLinearImpulse(impulse.rotate(180).limit(impulse.len()/4), pos, true);
			}
		}else{
			if(rotateLeftPressed) body.applyAngularImpulse(-1f, true);
			if(rotateRightPressed) body.applyAngularImpulse(1f, true);
		}

	}

	/**
	 * Examines the current state we are in and decides if that state means we are on
	 * a planet, or are we off a planet.
	 * @return True if on a planet. False if off a Planet.
     */
	public boolean onPlanet(){
		boolean returnVal = false;
		STATE s = getCurrentState();
//
		if(s == STATE.FLYING){
			returnVal = false;
		}else{
			returnVal = true;
		}
		return returnVal;
	}

	/**
	 * Choose animation based on current input as well as current state.
	 *
	 */
	private void chooseAnimation(){
		STATE s = getCurrentState();
		AnimationManager a = parent.getAnimationManager();

		if(s == STATE.FLYING){
			currentAnimation = a.getFlyingAnimation();
		}else if(s == STATE.WAVE){
			currentAnimation = a.getWaveAnimation();
		}else if(s == STATE.STAND_STILL_SIDEWAYS){
			currentAnimation = a.getStandingStillSidewaysAnimation();
		}else if(s == STATE.STAND_STILL_FORWARD){
			currentAnimation = a.getStandingStillForwardsAnimation();
		}else if(s == STATE.DEAD){
			currentAnimation = a.getDeadAnimation();
		}else if(s == STATE.EXPLOADING){
			currentAnimation = a.getExplosionAnimation();
		}else if(s == STATE.FLOAT_SIDEWAYS){
			currentAnimation = a.getFloatSidewaysAnimation();
		}else if(s == STATE.JUMP_FORWARD){
			currentAnimation = a.getJumpForwardsAnimation();
		}else if(s == STATE.JUMP_SIDEWAYS){
			currentAnimation = a.getJumpSidewaysAnimation();
		}else if(s == STATE.LAND_FORWARD){
			currentAnimation = a.getLandForwardsAnimation();
		}else if(s == STATE.LAND_SIDEWAYS){
			currentAnimation = a.getLandSidewaysAnimation();
		}else if(s == STATE.RUN_FAST){
			currentAnimation = a.getRunFastAnimation();
		}else if(s == STATE.RUN_SLOW){
			currentAnimation = a.getRunSlowAnimation();
		}else if(s == STATE.WALK_SLOW){
			currentAnimation = a.getWalkSlowAnimation();
		}else if(s == STATE.WALK_FAST){
			currentAnimation = a.getWalkFastAnimation();
		}
		/*if(getCurrentState() == STATE.FLYING || getCurrentState() == STATE.LANDED){
			if(forwardPressed || backwardPressed){
				currentAnimation = parent.getAnimationManager().getMoveForwardAnimation();
			}else{
				//currentAnimation = parent.getAnimationManager().getNoMovementAnimation();
				currentAnimation = parent.getAnimationManager().getRunRightAnimation();
			}
		}else if(getCurrentState() == STATE.EXPLOADING){
			currentAnimation = parent.getAnimationManager().getExplosionAnimation();
		}else if(getCurrentState() == STATE.DEAD){
			currentAnimation = parent.getAnimationManager().getDeadAnimation();
		}*/

	}

	public void setBody(Body b){
		body = b;
	}

	public Body getBody(){
		return body;
	}

	public STATE getCurrentState() {
		return currentState;
	}

	/**
	 * Set the current state. Change animation sometimes
	 * @param currentState
	 */
	public void setCurrentState(STATE currentState) {
		//System.out.println("Setting State to: " + currentState);
		if(currentState == STATE.EXPLOADING){
			currentAnimation = parent.getAnimationManager().getExplosionAnimation();
		}else if(currentState == STATE.DEAD){
			/* Player or Ghost has died. Play the dead animation,
			   if this is an instance of Ghost we don't do anything else.
			   if it is not, it means this is the real player, we want to
			   transition the game state to midgame, and set the labels, etc
			 */
			currentAnimation = parent.getAnimationManager().getDeadAnimation();
			if(this instanceof Ghost){
				//do nothing else if we are a ghost
			}else{
				//we must be the actual player, lets transition game states etc
				parent.getLevelManager().getMidGameMessage().setColor(Color.RED);
				parent.getLevelManager().getMidGameMessage().setText("You Have Died. You suck!");
				parent.parent.setGameState(MyGdxGame.GAME_STATE.MIDGAME);
			}
		}
		//System.out.println("STATECHANGE: + " + currentState);
		this.currentState = currentState;
		stateTime = 0;
	}

	public Vector2 getGravityForce() {
		return gravityForce;
	}
	
	public boolean getBoostPressed(){
		return boostPressed;
	}
	
	public void setBoostPressed(boolean pressed){
		boostPressed = pressed;
	}

	public float getBoostTime(){
		return boostTime;
	}
	
	public float getHealth(){
		return health;
	}
	
	public void setHealth(float health){
		this.health = health;
	}
}
