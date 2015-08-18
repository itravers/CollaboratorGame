package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Manages the level progression.
 * Allows us to load a specific level, go to the
 * next level, or replay the current level. Takes
 * care of all the assets, including spawning and deleting.
 * Created by Isaac Assegai on 8/17/2015.
 */
public class LevelManager {
    public static final int NUM_LEVELS = 2;

    private GameWorld parent;
    private GameMenu menu;
    private Background[] backgrounds;
    private int level;

    /* Labels Used by the UI. */
    private Label nameLabel;
    private Label elapsedTimeLabel;
    private Label midGameMsgLbl;
    private Label playerStateLabel;
    private Label playerSpeedLabel;

    public LevelManager(GameWorld parent){
        this.parent = parent;
        backgrounds = new Background[NUM_LEVELS];
        setLevel(0);
    }
    public GameWorld getParent() {
        return parent;
    }

    public void setParent(GameWorld parent) {
        this.parent = parent;
    }

    public int getLevel() {
        return level;
    }


    /**
     * Sets the level to the designated level.
     * it will unload all resources used for the previous level
     * and load all the resources used for the next level
     * @param currentLevel The level to set to.
     */
    public void setLevel(int currentLevel) {
        System.out.println("levelManager.setLevel("+currentLevel+")");
        this.level = currentLevel;
    }

    /**
     * This will set level to the next level,
     */
    public void nextLevel(){
        setLevel(getLevel()+1);
    }

    public void resetLevel(){

    }

    public void setupBackground(){
        int level = getLevel();
        System.out.println("setupBackground lvl: " + level);
        if(level == 0){
             /* Level 0 Background. MAIN MENU */
            Texture background = new Texture(Gdx.files.internal("data/background.png"));
            Texture background2 = new Texture(Gdx.files.internal("data/background2.png"));
            background2.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            TextureRegion[] backgroundLayers = new TextureRegion[3];
            backgroundLayers[0] = new TextureRegion(background, 0, 0, 2732, 1536);
            backgroundLayers[1] = new TextureRegion(background2, 0, 0, 5320, 4440);
            backgrounds[1] = new Background(this, backgroundLayers);
        }
        if(level == 1) {
            /* Level 1 Background. */
            Texture background = new Texture(Gdx.files.internal("data/background.png"));
            Texture background2 = new Texture(Gdx.files.internal("data/background2.png"));
            background2.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            TextureRegion[] backgroundLayers = new TextureRegion[3];
            backgroundLayers[0] = new TextureRegion(background, 0, 0, 2732, 1536);
            backgroundLayers[1] = new TextureRegion(background2, 0, 0, 5320, 4440);
            backgrounds[1] = new Background(this, backgroundLayers);
        }
    }

    public void setupMenu(GameWorld w, SpriteBatch b){
        menu = new GameMenu(w, b);
    }

    public void setupUI(Skin skin, Stage stage){
        nameLabel = new Label("NAME", skin, "default");
        nameLabel.setPosition(0, Gdx.graphics.getHeight() - 20);
        elapsedTimeLabel = new Label("ELAPSEDTIME", skin, "default");
        elapsedTimeLabel.setPosition(nameLabel.getWidth() + 2, Gdx.graphics.getHeight() - 20);
        playerStateLabel = new Label("STATE", skin, "default");
        playerStateLabel.setPosition(0, 20);
        playerSpeedLabel = new Label("SPEED", skin, "default");
        playerSpeedLabel.setPosition(playerStateLabel.getWidth() + 10, 20);
        nameLabel.setColor(Color.GREEN);
        elapsedTimeLabel.setColor(Color.RED);
        playerStateLabel.setColor(Color.RED);
        playerSpeedLabel.setColor(Color.RED);

        //MidGameMsgSEtup
        midGameMsgLbl = new Label("You have died. Press Space to Continue", skin, "default");
        midGameMsgLbl.setColor(Color.RED);
        midGameMsgLbl.setPosition(0, Gdx.graphics.getHeight() / 2 - 20);
        midGameMsgLbl.setVisible(false);
        stage.addActor(nameLabel);
        stage.addActor(elapsedTimeLabel);
        stage.addActor(playerStateLabel);
        stage.addActor(playerSpeedLabel);
        stage.addActor(midGameMsgLbl);
    }

    public Background getBackground(){
        return getBackground(level);
    }

    /**
     * Returns the background for the current level, or 0 or last if levelNum is wrong.
     * @param levelNum The number of the level we are getting a background for.
     * @return The background itself.
     */
    public Background getBackground(int levelNum){
        Background background;
        //First check if the levelNum is possible.
        if(levelNum >= NUM_LEVELS){
            //levelNum is too high, assign last available level background.
            background = backgrounds[NUM_LEVELS-1];
        }else if(levelNum >= 0){
            //levelNum is just right, assign it.
            background = backgrounds[levelNum];
        }else{
            //level num is negative, assign first level num
            background = backgrounds[0];
        }
        return background;
    }


    public GameMenu getMenu() {
        return menu;
    }

    public void setMenu(GameMenu menu) {
        this.menu = menu;
    }


    public Label getNameLabel() {
        return nameLabel;
    }

    public void setNameLabel(Label nameLabel) {
        this.nameLabel = nameLabel;
    }

    public Label getElapsedTimeLabel() {
        return elapsedTimeLabel;
    }

    public void setElapsedTimeLabel(Label elapsedTimeLabel) {
        this.elapsedTimeLabel = elapsedTimeLabel;
    }

    public Label getMidGameMsgLbl() {
        return midGameMsgLbl;
    }

    public void setMidGameMsgLbl(Label midGameMsgLbl) {
        this.midGameMsgLbl = midGameMsgLbl;
    }

    public Label getPlayerStateLabel() {
        return playerStateLabel;
    }

    public void setPlayerStateLabel(Label playerStateLabel) {
        this.playerStateLabel = playerStateLabel;
    }

    public Label getPlayerSpeedLabel() {
        return playerSpeedLabel;
    }

    public void setPlayerSpeedLabel(Label playerSpeedLabel) {
        this.playerSpeedLabel = playerSpeedLabel;
    }


}
