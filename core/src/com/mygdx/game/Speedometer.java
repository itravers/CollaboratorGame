package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

    public static enum MODES{GREEN, BLUE, RED};

    public Speedometer(GuiManager parent){
        this.parent = parent;
        loc = new Vector2();
        dimensions = new Vector2();
        speedometerOrigin = new Vector2();
        textureAtlas = new TextureAtlas(Gdx.files.internal("data/speedometer.pack"));
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
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, OrthographicCamera c){
    	updateSizes(); //for debugging
    	updateSpeedometer();
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
        shapeRenderer.rectLine(speedometerOrigin.x, speedometerOrigin.y, 100, 100, 5);
        shapeRenderer.end();
    }
    
    private void updateSpeedometer(){
    	speedometerOrigin = new Vector2(getX()+getWidth()/2, getY()+getHeight());
    }
    
    private void updateSizes(){
    	region = textureAtlas.getRegions().get(0);
    	
    	this.setWidth(Gdx.graphics.getWidth()/2.5f);
    	this.setHeight(Gdx.graphics.getHeight()/8);
    	this.setX(-getWidth()/2);
        this.setY(Gdx.graphics.getHeight()/3+getHeight()/2);
        
        
        
        System.out.println("x:y " + getX() + ":" + getY() + "  hXw " + getHeight()  + "X" + getWidth());
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
