package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by Isaac Assegai on 9/26/2015.
 * This class manages the gui and hud.
 */
public class GuiManager {
    GameWorld parent;
    Stage stage; // The stage the gui will be printed out too.
    
    TextureAtlas meterAtlas;

    Speedometer speedometer;
    SquareMeter healthmeter;
    SquareMeter boostmeter;

    public GuiManager(GameWorld p){
        parent = p;
        initGui();
    }

    public void addStage(Stage s){
        this.stage = s;
    }

    private void initGui(){
    	meterAtlas = new TextureAtlas(Gdx.files.internal("data/speedometer.pack"));
        speedometer = new Speedometer(this, meterAtlas);
        healthmeter = new SquareMeter(this, meterAtlas, SquareMeter.TYPE.HEALTH);
        boostmeter = new SquareMeter(this, meterAtlas, SquareMeter.TYPE.BOOST);
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer){
    	healthmeter.render(batch, shapeRenderer);
    	 boostmeter.render(batch, shapeRenderer);
        speedometer.render(batch, shapeRenderer);
       
    }
}
