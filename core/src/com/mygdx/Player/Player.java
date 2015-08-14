package com.mygdx.Player;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.graphics.g2d.Animation;

public class Player extends Sprite {
	// Rendering Oriented Fields
	private TextureRegion[] moveForwardFrames;
	private TextureAtlas textureAtlas;
	private Animation animation;
	private Animation moveForwardAnimation;
	private Animation currentAnimation;

	// Physics Oriented Fields
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	private Fixture fixture;
	private Body body;
	private PolygonShape shape;
	private World world;

	 
	public Player(TextureAtlas textureAtlas, World world){
		super(textureAtlas.getRegions().first());
		setupRendering(textureAtlas);
		setupPhysics(world);

	}

	public void render(float elapsedTime, SpriteBatch batch){
		batch.draw(moveForwardAnimation.getKeyFrame(elapsedTime, true), -this.getWidth()/2, -this.getHeight()/2);
	}

	private void setupPhysics(World world){
		this.world = world;
		bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(getX(), getY());
		body = world.createBody(bodyDef);
		shape = new PolygonShape();
		shape.setAsBox(50, 50);
		fixtureDef  = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;
		fixture = body.createFixture(fixtureDef);
	}

	private void setupRendering(TextureAtlas textureAtlas){
		this.textureAtlas = textureAtlas;

		setupAnimations();
	}

	private void setupAnimations(){
		animation = new Animation(1/15f, textureAtlas.getRegions());
		setupMoveForwardAnimation();
		currentAnimation = moveForwardAnimation;
	}

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

}
