package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Isaac Assegai on 9/26/2015.
 * Manages the behavior of the gui speedometer.
 */
public class Speedometer{
    private GuiManager parent;
    private TextureAtlas textureAtlas;
    private TextureRegion region;
    private MODES mode;
    private Vector2 loc;
    private Vector2 dimensions;
    private Vector2 speedometerOrigin;
    private Vector2 speedometerEnd;
    private Vector2 seperatorOrigin;
    private Vector2 seperatorEnd;
    private float baseRotation = 25f;
    private Color speedLineColor;

    public static enum MODES{GREEN, BLUE, RED};

    public Speedometer(GuiManager parent, TextureAtlas textureAtlas){
        this.parent = parent;
        loc = new Vector2();
        dimensions = new Vector2();
        speedometerOrigin = new Vector2();
        speedometerEnd = new Vector2();
        seperatorOrigin = new Vector2();
        seperatorEnd = new Vector2();
        this.textureAtlas = textureAtlas;
        region = textureAtlas.getRegions().get(1);
        setMode(MODES.GREEN);
        this.setX(-Gdx.graphics.getWidth()/2);
        this.setY(0);
    }

    /**
     * Sets the Speedometers current mode, and sets the speedometers sprite to reflect that mode.
     * @param m The mode we are setting the speedometer to.
     */
    public void setMode(MODES m){
        mode = m;
    }

    public MODES getMode(){
        return mode;
    }

    /**
     * Renders the speedometer
     */
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer){
    	float scale = parent.parent.getRenderManager().scale;
    	updateSizes(); //for debugging
    	updateSpeedometer(parent.parent.getLevelManager().getPlayer().getBody().getLinearVelocity().len());
    	OrthographicCamera d = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Matrix4 oldMatrix  = batch.getProjectionMatrix();
        batch.setProjectionMatrix(d.combined);
        batch.begin();
        //batch.draw(textureAtlas.findRegion("speedometer_green").getTexture(), getX(), getY());
        batch.draw(region, getX(), getY(), 0, 0, getWidth(), getHeight(), 1, 1, 0);
        batch.end();
        batch.setProjectionMatrix(oldMatrix);
        
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(d.combined);
        
        shapeRenderer.begin();
        shapeRenderer.set(ShapeType.Filled);
        shapeRenderer.setColor(speedLineColor);
        shapeRenderer.rectLine(speedometerOrigin.x, speedometerOrigin.y, speedometerEnd.x, speedometerEnd.y, 2*scale);
        
        //draw seperator 
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rectLine(seperatorOrigin.x, seperatorOrigin.y, seperatorEnd.x, seperatorEnd.y, 2*scale);
        
        shapeRenderer.end();
        
        //draw the stroke
        oldMatrix  = batch.getProjectionMatrix();
        batch.setProjectionMatrix(d.combined);
        batch.begin();
        batch.draw(textureAtlas.findRegion("speedometer_stroke"), getX(), getY(), 0, 0, getWidth(), getHeight(), 1, 1, 0);
        batch.end();
        batch.setProjectionMatrix(oldMatrix);
    }
    
    private void updateSpeedometer(float speed){
    	speedLineColor = parent.parent.getRenderManager().getSeperatingLineColor(speed);
    	setSpeedometerRegionBySpeed(speed);
    	float scale = parent.parent.getRenderManager().scale;
    	float rotation;
    	float maxV = parent.parent.getLevelManager().getPlayer().MAX_VELOCITY;
    	float crashV = parent.parent.getLevelManager().getPlayer().CRASH_VELOCITY;
    	speedometerOrigin = new Vector2(getX()+getWidth()/2, getY()+getHeight()+30*scale);
    	speedometerEnd = new Vector2(speedometerOrigin.x-getWidth()/2-5*scale, getY()+getHeight()+30*scale);
    	Vector2 line = speedometerEnd.cpy().sub(speedometerOrigin.cpy());
    	//calculate speedometer line
    	baseRotation = 30f;
    	rotation = baseRotation;
        rotation += (speed * 180-baseRotation) / maxV;
        line = line.cpy().rotate(rotation);
        speedometerEnd = speedometerOrigin.cpy().add(line);
        line = line.setLength(getWidth()/15.5f);
        speedometerOrigin = speedometerEnd.cpy().sub(line);
        
        //Draw the Seperating Line
        seperatorOrigin = new Vector2(getX()+getWidth()/2, getY()+getHeight()+30*scale);
        seperatorEnd = new Vector2(seperatorOrigin.x-getWidth()/2-5*scale, getY()+getHeight()+30*scale);
    	line = seperatorEnd.cpy().sub(seperatorOrigin.cpy());
    	//calculate speedometer line
    	baseRotation = 30f;
    	rotation = baseRotation;
        rotation += (crashV * 180-baseRotation) / maxV;
        line = line.cpy().rotate(rotation);
        seperatorEnd = seperatorOrigin.cpy().add(line);
        line = line.setLength(getWidth()/15.5f);
        seperatorOrigin = seperatorEnd.cpy().sub(line);
    }
    
    private void updateSizes(){
    	float scale = parent.parent.getRenderManager().scale;
    	region = textureAtlas.getRegions().get(0);
    	
    	this.setWidth(region.getRegionWidth()*scale/6);
    	this.setHeight(50*scale);
    	this.setX(-getWidth()/2);
        this.setY(0+Gdx.graphics.getHeight()/2-getHeight());
    	//this.setY(0);
        
        
        
      //  System.out.println("x:y " + getX() + ":" + getY() + "  hXw " + getHeight()  + "X" + getWidth());
    }
    
    /**
     * The Speedometer color depends on the relationship of the players current speed
     * to it's crash velocity. If the player is more than 1m/s less than crash velocity
     * the speedometer will be green, if the player is within +-1m/s of crash velocity
     * than the speedometer will be yellow. If the player is +1m/s to crash velocity
     * than the meter is red.
     * @return
     */
    private void setSpeedometerRegionBySpeed(float speed){
        float crashSpeed = parent.parent.getLevelManager().getPlayer().CRASH_VELOCITY;
        if(speed < crashSpeed - 1){
        	region = textureAtlas.findRegion("speedometer_green");
        }else if(speed > crashSpeed + 1){
        	region = textureAtlas.findRegion("speedometer_red");
        }else{
        	region = textureAtlas.findRegion("speedometer_blue");
        }
    }
    
    public void setX(float x){
    	loc.x = x;
    }
    
    public void setY(float y){
    	loc.y = y;
    }
    
    public float getX(){
    	return loc.x;
    }
    
    public float getY(){
    	return loc.y;
    }
    
    public void setWidth(float w){
    	dimensions.x = w;
    }
    
    public void setHeight(float h){
    	dimensions.y = h;
    }
    
    public float getWidth(){
    	return dimensions.x;
    }
    
    public float getHeight(){
    	return dimensions.y;
    }
}
