package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

/**
 * SquareMeter is a meter class that can be updated and used for multiple types of meters
 * its behavior and all is contained. We are going to use this class for our health and 
 * boost meters. 9/28/15
 * @author Isaac Assegai 
 *
 */
public class SquareMeter {
	static enum TYPE{HEALTH, BOOST};
	private TYPE type;
	private GuiManager parent;
	private TextureAtlas meterAtlas;
	private Vector2 loc;
	private Vector2 dimensions;
	
	public SquareMeter(GuiManager parent, TextureAtlas meterAtlas, TYPE type){
		this.parent = parent;
		this.meterAtlas = meterAtlas;
		this.type = type;
		init();
	}
	
	public void render(SpriteBatch batch, ShapeRenderer shapeRenderer){
		OrthographicCamera d = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		if(type == TYPE.HEALTH){
			renderHealthMeter(batch, shapeRenderer, d);
		}else if(type == TYPE.BOOST){
			renderBoostMeter(batch, shapeRenderer, d);
		}
	}
	
	public float getX(){
		return loc.x;
	}
	
	public void setX(float x){
		loc.x = x;
	}
	
	public float getY(){
		return loc.y;
	}
	
	public void setY(float y){
		loc.y = y;
	}
	
	public float getWidth(){
		return dimensions.x;
	}
	
	public void setWidth(float w){
		dimensions.x = w;
	}
	
	public float getHeight(){
		return dimensions.y;
	}
	
	public void setHeight(float h){
		dimensions.y = h;
	}
	
	private void renderHealthMeter(SpriteBatch batch, ShapeRenderer shapeRenderer, OrthographicCamera d){
		 updateHealthMeterSizes();
		 float i = parent.parent.getLevelManager().getPlayer().getBody().getLinearVelocity().len();
		 float iMax = parent.parent.getLevelManager().getPlayer().MAX_VELOCITY;
		 float meterWidthBasedOnValue = getMeterWidthBasedOnValue(i, iMax);
		 Matrix4 oldMatrix  = batch.getProjectionMatrix();
		 batch.setProjectionMatrix(d.combined);
		 batch.begin();
	     batch.draw(meterAtlas.findRegion("health_fill"), getX(), getY(), 0, 0, meterWidthBasedOnValue, getHeight(), 1, 1, 0);
	     batch.draw(meterAtlas.findRegion("health_stroke"), getX(), getY(), 0, 0, getWidth(), getHeight(), 1, 1, 0);
	     batch.end();
	     batch.setProjectionMatrix(oldMatrix);
	}
	
	private float getMeterWidthBasedOnValue(float i, float iMax){
		float returnValue = 0;
		returnValue = (i * getWidth()) / iMax;
		System.out.println("width: " + returnValue);
		return returnValue;
	}
	
	private void renderBoostMeter(SpriteBatch batch, ShapeRenderer shapeRenderer, OrthographicCamera d){
		
	}
	
	private void init(){
		if(type == TYPE.HEALTH) initHealthMeter();
		if(type == TYPE.BOOST)  initBoostMeter();
	}
	
	private void initHealthMeter(){
		loc = new Vector2(0,0);
		dimensions = new Vector2(0,0);
	}
	
	private void initBoostMeter(){
		
	}
	
	private void updateHealthMeterSizes(){
		float scale = parent.parent.getRenderManager().scale;
		setWidth(Gdx.graphics.getWidth()/2-(50*scale));
		setHeight(Gdx.graphics.getHeight()/20);
		
		setX(-Gdx.graphics.getWidth()/2);
		setY(0+Gdx.graphics.getHeight()/2 - getHeight());
	}
	

}
