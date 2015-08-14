package com.mygdx.Player;

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

	// Physics Oriented Fields
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	private Fixture fixture;
	private Body body;
	private PolygonShape shape;
	private World world;
	private float torque = 0.0f;
	public float MAX_VELOCITY = 40f;
	public float MAX_ANGULAR_VELOCITY = 10f;

	//Inputs
	public boolean forwardPressed = false;
	public boolean backwardPressed = false;
	public boolean rotateRightPressed = false;
	public boolean rotateLeftPressed = false;

	/**
	 * Player Constructor
	 * @param textureAtlas The spritesheet, etc
	 * @param world The physics world the player exists in.
	 */
	public Player(TextureAtlas textureAtlas, World world, GameWorld parent){
		super(textureAtlas.getRegions().first());
		this.parent = parent;
		setupRendering(textureAtlas);
		setupPhysics(world);
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
		fixtureDef.density = 1f;
		fixtureDef.friction = 1f;
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
	private void applyGravity(){
		ArrayList<Planet> planets = parent.getPlanets();
		for(int i = 0; i < planets.size(); i++){
			
		}
	}

	private void applyInput(){
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
