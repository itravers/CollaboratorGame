package com.mygdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by Isaac Assegai on 9/26/2015.
 * This class manages the gui and hud.
 */
public class GuiManager {
    GameWorld parent;
    Stage stage; // The stage the gui will be printed out too.

    Speedometer speedometer;

    public GuiManager(GameWorld p){
        parent = p;
        initGui();
    }

    public void addStage(Stage s){
        this.stage = s;
    }

    private void initGui(){
        speedometer = new Speedometer(this);
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, OrthographicCamera c){
        speedometer.render(batch, shapeRenderer, c);
    }
}
