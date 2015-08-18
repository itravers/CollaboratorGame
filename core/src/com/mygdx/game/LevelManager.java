package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
    private Background[] backgrounds;
    private int level;

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

    public void setLevel(int currentLevel) {
        System.out.println("levelManager.setLevel("+currentLevel+")");
        this.level = currentLevel;
    }

    public void nextLevel(){

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
}
