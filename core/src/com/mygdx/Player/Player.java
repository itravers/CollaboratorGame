package com.mygdx.Player;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Player extends Sprite {
	 BodyDef bodyDef;
	 FixtureDef fixtureDef;
	 Fixture fixture;
	 public Body body;
	 PolygonShape shape;
	 World world;
	 
	public Player(Texture img, World world){
		super(img);
		this.world = world;
		setPosition(Gdx.graphics.getWidth() / 2 - getWidth() / 2,
                Gdx.graphics.getHeight() / 2);
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

}
