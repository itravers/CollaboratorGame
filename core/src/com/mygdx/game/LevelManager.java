package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.mygdx.Player.Ghost;
import com.mygdx.Player.Player;
import com.mygdx.input.GameInput;

import java.util.ArrayList;

/**
 * Manages the level progression.
 * Allows us to load a specific level, go to the
 * next level, or replay the current level. Takes
 * care of all the assets, including spawning and deleting.
 * Created by Isaac Assegai on 8/17/2015.
 */
public class LevelManager {
    public static final int NUM_LEVELS = 4;

    private GameWorld parent; /* The Main Game Class. */
    private GameMenu menu; /* The Main Menu. */
    private Background[] backgrounds; /* Array of level backgrounds. */
    private int level; /* Current Level */

    /* Mid Game Fields. */
    private Background midGameBackground;
    private Label midGameMessage;

    private boolean midGameVisible;

    /* Labels Used by the UI. */
    private boolean uiVisible;
    private Label nameLabel;
    private Label elapsedTimeLabel;

    private Label playerStateLabel;
    private Label playerSpeedLabel;

    //Physics related fields
    private World world;

    // Planet Related Fields
    private ArrayList<Planet> planets;

    // Player related Fields
    private Player player;
    private Vector2 originalPlayerPosition;

    // Ghost Related Fields
    private ArrayList<Ghost> ghosts;

    private Sprite goal; /* Each level has a goal. */

    private Boolean levelGoalCompleted;

    public LevelManager(GameWorld parent){
        this.parent = parent;
        backgrounds = new Background[NUM_LEVELS];
        setupMidgame();
        setupBackground();
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

    private void setupMidgame(){
        /* Midgame background. */
        Texture background = new Texture(Gdx.files.internal("data/background.png"));
        Texture starscape1 = new Texture(Gdx.files.internal("data/starscape1.png"));
        Texture starscape2 = new Texture(Gdx.files.internal("data/starscape2.png"));
        Texture starscape3 = new Texture(Gdx.files.internal("data/starscape3.png"));
        starscape1.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        starscape2.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        starscape3.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        TextureRegion[] backgroundLayers = new TextureRegion[4];
        backgroundLayers[0] = new TextureRegion(background, 0, 0, 2732, 1536);
        backgroundLayers[1] = new TextureRegion(starscape1, 0, 0, 8000, 8000);
        backgroundLayers[2] = new TextureRegion(starscape2, 0, 0, 8000, 8000);
        backgroundLayers[3] = new TextureRegion(starscape2, 0, 0, 8000, 8000);
        midGameBackground = new Background(this, backgroundLayers);
    }

    /**
     * Sets the level to the designated level.
     * it will unload all resources used for the previous level
     * and load all the resources used for the next level
     * @param currentLevel The level to set to.
     */
    public void setLevel(int currentLevel) {
        this.level = currentLevel;
        uiVisible = true;
        midGameVisible = false;
        setupBackground();
        setupPhysics();
        setupPlanets(); // before setupPlayer
        setupPlayer();
        setupGhosts();
        setupGoal();
    }

    /**
     * Reads the levelx.json file for the current level and designates the appropriate
     * planet as the goal.
     */
    private void setupGoal(){
        int level = getLevel();
        if(level != 0){ //level 0 doesn't have a goal as it is the menu
            levelGoalCompleted = false;
            Json json = new Json();
            ArrayList<SpriteTemplate> levelItems = json.fromJson(ArrayList.class, SpriteTemplate.class,
                    getLevelFile(level));
            //read list of levelItems, creating a planet for every planet in the list.
            for(int i = 0; i < levelItems.size(); i++){
                SpriteTemplate item = levelItems.get(i);
                if(item.getType().equals("goal")){
                    int index = Integer.valueOf(item.getExtraInfo()); //The index of the goal planet defined in the levelx.json file
                    Planet p = planets.get(index);
                    goal = p;
                }
            }
        }
    }

    /**
     * Checks if we have completed the levels goal.
     * We have completed the goal if we are landed, and the
     * nearest planet is the goal planet.
     */
    public void checkGoal(Planet planet, Player p){
        //we only do this if the levelGoal has not been completed
        if(!levelGoalCompleted){

            boolean stateGood = p.getCurrentState() == Player.STATE.LANDED;
            boolean goalGood = (goal.equals(planet));
            boolean playerTypeGood = (!(p instanceof Ghost));
            //we know the goal is completed if the player has landed on the goal planet and he is not a ghost.
            if(stateGood && goalGood && playerTypeGood){

                setLevelGoalCompleted(true);//levelGoalCompleted = true;
                /*we completed the level goal, lets mark it, set a few labels
                  and transition into midgame mode to tell the player how much
                  they don't suck.
                 */
                getMidGameMessage().setColor(Color.GREEN);
                getMidGameMessage().setText("You Completed The Level. Good Job!");
                parent.parent.setGameState(MyGdxGame.GAME_STATE.MIDGAME); //must be after levelGoalCompleted = true;

                System.out.println("Completed Level Goal " + level);
            }
        }
    }

    /**
     * This will set level to the next level,
     */
    public void nextLevel(){
        int level = getLevel();
        level++;
        if(level >= NUM_LEVELS){ //reset to first level.
            level = 1;
        }
        //We are changing levels, we don't want anwwwwwwwy ghosts.
       // resetGhostsToZero();
        setLevel(level);
    }

    private void resetGhostsToZero(){
        //for(int i = 0; i < ghosts.size(); i++)ghosts.get(i).dispose();
        ghosts = new ArrayList<Ghost>();
    }

    /**
     * Sets us up to reset this level.
     * Copies player to a ghost.
     * Resets world pieces to origin.
     */
    public void resetLevel(){
        resetPhysics();
        addGhost(getPlayer());
        resetWorld();
    }

    private void resetWorld(){
        parent.parent.elapsedTime = 0;
        parent.parent.resetFrameNum(); //reset frame counter for accurate replays
        setupGoal();
        resetPlanets();
        resetGhosts();
        setupPlayer();
    }

    private void resetPlanets(){
        for(int i = 0; i < getPlanets().size(); i++){
            Planet p = getPlanets().get(i);
            Planet newPlanet = new Planet(new Vector2(p.getX(), p.getY()),
                    p.getTextureAtlas(), getWorld(), p.getRadius(), p.getGravityRadius(), p.getMass(), this.parent);
            getPlanets().set(i, newPlanet);
        }
        setupGoal();
    }

    public void setupBackground(){
        int level = getLevel();
       // System.out.println("setupBackground lvl: " + level);
        if(level == 0){
             /* Level 0 Background. MAIN MENU */
            Texture background = new Texture(Gdx.files.internal("data/background.png"));
            Texture starscape1 = new Texture(Gdx.files.internal("data/starscape1.png"));
            Texture starscape2 = new Texture(Gdx.files.internal("data/starscape2.png"));
            Texture starscape3 = new Texture(Gdx.files.internal("data/starscape3.png"));
            starscape1.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            starscape2.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            starscape3.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            TextureRegion[] backgroundLayers = new TextureRegion[4];
            backgroundLayers[0] = new TextureRegion(background, 0, 0, 2732, 1536);
            backgroundLayers[1] = new TextureRegion(starscape1, 0, 0, 8000, 8000);
            backgroundLayers[2] = new TextureRegion(starscape2, 0, 0, 8000, 8000);
            backgroundLayers[3] = new TextureRegion(starscape2, 0, 0, 8000, 8000);
            backgrounds[0] = new Background(this, backgroundLayers);
        }else if(level == 1) {
            /* Level 1 Background. */
            Texture background = new Texture(Gdx.files.internal("data/background.png"));
            Texture starscape1 = new Texture(Gdx.files.internal("data/starscape1.png"));
            Texture starscape2 = new Texture(Gdx.files.internal("data/starscape2.png"));
            Texture starscape3 = new Texture(Gdx.files.internal("data/starscape3.png"));
            starscape1.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            starscape2.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            starscape3.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            TextureRegion[] backgroundLayers = new TextureRegion[4];
            backgroundLayers[0] = new TextureRegion(background, 0, 0, 2732, 1536);
            backgroundLayers[1] = new TextureRegion(starscape1, 0, 0, 8000, 8000);
            backgroundLayers[2] = new TextureRegion(starscape2, 0, 0, 8000, 8000);
            backgroundLayers[3] = new TextureRegion(starscape2, 0, 0, 8000, 8000);
            backgrounds[1] = new Background(this, backgroundLayers);
        }else if(level == 2){
            /* Level 2 Background. */
            Texture background = new Texture(Gdx.files.internal("data/background.png"));
            Texture starscape1 = new Texture(Gdx.files.internal("data/starscape1.png"));
            Texture starscape2 = new Texture(Gdx.files.internal("data/starscape2.png"));
            Texture starscape3 = new Texture(Gdx.files.internal("data/starscape3.png"));
            starscape1.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            starscape2.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            starscape3.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            TextureRegion[] backgroundLayers = new TextureRegion[4];
            backgroundLayers[0] = new TextureRegion(background, 0, 0, 2732, 1536);
            backgroundLayers[1] = new TextureRegion(starscape1, 0, 0, 8000, 8000);
            backgroundLayers[2] = new TextureRegion(starscape2, 0, 0, 8000, 8000);
            backgroundLayers[3] = new TextureRegion(starscape2, 0, 0, 8000, 8000);
            backgrounds[2] = new Background(this, backgroundLayers);
        }else if(level == 3){
            /* Level 2 Background. */
           // backgrounds[3] = backgrounds[2];
            /* Level 2 Background. */
            Texture background = new Texture(Gdx.files.internal("data/background.png"));
            Texture starscape1 = new Texture(Gdx.files.internal("data/starscape1.png"));
            Texture starscape2 = new Texture(Gdx.files.internal("data/starscape2.png"));
            Texture starscape3 = new Texture(Gdx.files.internal("data/starscape3.png"));
            starscape1.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

            starscape2.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            starscape3.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            TextureRegion[] backgroundLayers = new TextureRegion[4];
            backgroundLayers[0] = new TextureRegion(background, 0, 0, 2732, 1536);
            backgroundLayers[1] = new TextureRegion(starscape1, 0, 0, 8000, 8000);
            backgroundLayers[2] = new TextureRegion(starscape2, 0, 0, 8000, 8000);
            backgroundLayers[3] = new TextureRegion(starscape2, 0, 0, 8000, 8000);
            backgrounds[3] = new Background(this, backgroundLayers);
        }
    }

    /**
     * Setup planets in the world
     */
    private void setupPlanets(){
        int level = getLevel();
        if(level != 0){ //don't setup planets on the menu
            planets = new ArrayList<Planet>();
            Json json = new Json();
            ArrayList<SpriteTemplate> levelItems = json.fromJson(ArrayList.class, SpriteTemplate.class,
                    getLevelFile(level));
            //read list of levelItems, creating a planet for every planet in the list.
            for(int i = 0; i < levelItems.size(); i++){
                SpriteTemplate item = levelItems.get(i);
                if(item.getType().equals("planet")){
                    TextureAtlas atlas = getTextureAtlasFromString(item.getAtlas());
                    Planet p = new Planet(new Vector2(item.getxLoc(), item.getyLoc()),
                            atlas, world, item.getSize(), item.getGravityRadius(), item.getMass(), this.parent);
                    planets.add(p);
                }
            }
        }
    }

    private TextureAtlas getTextureAtlasFromString(String atlasName){
        TextureAtlas returnVal = null;
        if(atlasName.equals("planetAtlas")){
           returnVal = parent.getAnimationManager().getPlanetAtlas();
        }else if(atlasName.equals("shipAtlas")){
            returnVal = parent.getAnimationManager().getShipAtlas();
        }
        return returnVal;
    }

    /**
     * Setup the player
     */
    private void setupPlayer(){
        int level  = getLevel();
        if(level != 0){ //Don't setup player on menu level.
            Json json = new Json();
            ArrayList<SpriteTemplate> levelItems = json.fromJson(ArrayList.class, SpriteTemplate.class,
                    getLevelFile(level));
           // Gdx.files.internal("levels/level"+level+".json")
            //        Gdx.files.in
            for(int i = 0; i < levelItems.size(); i++) {
                SpriteTemplate item = levelItems.get(i);
                if(item.getType().equals("player")) {
                    float xLoc = item.getxLoc();
                    float yLoc = item.getyLoc();
                    TextureAtlas atlas = getTextureAtlasFromString(item.getAtlas());
                    originalPlayerPosition = new Vector2(xLoc, yLoc);
                    player = new Player(originalPlayerPosition, atlas, getWorld(), this.parent);
                    player.setPosition(originalPlayerPosition.x, originalPlayerPosition.y);
                }
            }
        }
        /*
        TextureAtlas shipAtlas = parent.getAnimationManager().getShipAtlas();
        //System.out.println("levelManager.setupPlayer lvl: " + level);
        if(level == 1){
            originalPlayerPosition = new Vector2((getPlanets().get(0).getWidth()/2)-12,getPlanets().get(0).getHeight()+500);
            player = new Player(originalPlayerPosition, shipAtlas, getWorld(), this.parent);
            player.setPosition(originalPlayerPosition.x, originalPlayerPosition.y);
        }else if(level == 2){
            originalPlayerPosition = new Vector2((getPlanets().get(0).getWidth()/2)-12,
                    getPlanets().get(0).getHeight());
            player = new Player(originalPlayerPosition, shipAtlas, getWorld(), this.parent);
            player.setPosition(originalPlayerPosition.x, originalPlayerPosition.y);
        }else if(level == 3){
            originalPlayerPosition = new Vector2((getPlanets().get(0).getWidth()/2)-12,
                    getPlanets().get(0).getHeight());
            player = new Player(originalPlayerPosition, shipAtlas, getWorld(), this.parent);
            player.setPosition(originalPlayerPosition.x, originalPlayerPosition.y);
        }
        */
    }

    /**
     * Returns the level file handle for this level.
     * First checks externally, to check if a mod of the level exists.
     * if not, it looks for the level file internally, in android assets.
     * @param lvl The level we want to find.
     * @return A fileHandler for the  level we want.
     */
    private FileHandle getLevelFile(int lvl){
        FileHandle fileHandle = null;
        String fileName = "levels/level"+lvl+".json";
        if(Gdx.files.classpath(fileName).exists()){
            fileHandle = Gdx.files.local(fileName);
            //System.out.println("using external file.");
        }else{
            fileHandle = Gdx.files.internal(fileName);
            //System.out.println("using internal file.");
        }
        return fileHandle;
    }

    /**
     * Setup players ghosts, should only be used if a level is reset.
     */
    private void setupGhosts(){
        ghosts = new ArrayList<Ghost>();
    }

    /**
     * Creates a set of new ghosts from the set of old ones.
     */
    public void resetGhosts(){
        for(int i = 0; i < ghosts.size(); i++){
            Ghost g = ghosts.get(i);
            TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("data/Ship.pack"));

            ArrayList<GameInput>inputList = new ArrayList<GameInput>();
            inputList.addAll(g.inputList);
            com.badlogic.gdx.utils.Array<Body> bodies = new com.badlogic.gdx.utils.Array<Body>();
            getWorld().getBodies(bodies);
            if(bodies.contains(g.getBody(), true)){
                getWorld().destroyBody(g.getBody());
            }

            Ghost newGhost = new Ghost(getOriginalPlayerPosition(), textureAtlas,
                    getWorld(), this.parent, inputList, i);
            newGhost.setPosition(getOriginalPlayerPosition().x, getOriginalPlayerPosition().y);
            ghosts.set(i, newGhost);
            // g.dispose();
        }
    }

    /**
     * Creates a ghost based on a player.
     * @param player The player we are basing the ghost on.
     */
    public void addGhost(Player player){
        TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("data/Ship.pack"));
        //ArrayList<GameInput>inputList = (ArrayList<GameInput>) player.inputList.clone();//new ArrayList<GameInput>(player.inputList);//(ArrayList<GameInput>) player.inputList.clone();
        ArrayList<com.mygdx.input.GameInput>inputList = new ArrayList<com.mygdx.input.GameInput>();
        inputList.addAll(player.inputList);
        int index = ghosts.size();
        Ghost g = new Ghost(getOriginalPlayerPosition(), textureAtlas, getWorld(), this.parent, inputList, index);
        g.setPosition(getOriginalPlayerPosition().x, getOriginalPlayerPosition().y);
        ghosts.add(g);
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
        midGameMessage = new Label("You have died. Press Space to Continue", skin, "default");
        midGameMessage.setColor(Color.RED);
        midGameMessage.setPosition(0, Gdx.graphics.getHeight() / 2 - 20);
        midGameMessage.setVisible(false);
        stage.addActor(nameLabel);
        stage.addActor(elapsedTimeLabel);
        stage.addActor(playerStateLabel);
        stage.addActor(playerSpeedLabel);
        stage.addActor(midGameMessage);
    }

    /**
     * Setup the worlds physics.
     */
    public void setupPhysics(){
        parent.parent.elapsedTime = 0;
        parent.parent.resetFrameNum(); //reset frame counter for accurate replays
        world = new World(new Vector2(0, 0),true);
        world.setContactListener(this.parent);
    }

    public void resetPhysics(){
        setupPhysics();
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

    public Label getMidGameMessage() {
        return midGameMessage;
    }

    public void setMidGameMessage(Label midGameMessage) {
        this.midGameMessage = midGameMessage;
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


    public ArrayList<Planet> getPlanets() {
        return planets;
    }

    public void setPlanets(ArrayList<Planet> planets) {
        this.planets = planets;
    }


    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }


    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Vector2 getOriginalPlayerPosition() {
        return originalPlayerPosition;
    }

    public void setOriginalPlayerPosition(Vector2 originalPlayerPosition) {
        this.originalPlayerPosition = originalPlayerPosition;
    }


    public ArrayList<Ghost> getGhosts() {
        return ghosts;
    }

    public void setGhosts(ArrayList<Ghost> ghosts) {
        this.ghosts = ghosts;
    }


    public Sprite getGoal() {
        return goal;
    }

    public void setGoal(Sprite goal) {
        this.goal = goal;
    }


    public void setMidGameVisible(boolean isVisible){
        midGameVisible = isVisible  ;
        midGameMessage.setVisible(isVisible);
    }

    public boolean isMidGameVisible() {
        return midGameVisible;
    }

    public boolean isUiVisible() {
        return uiVisible;
    }

    public void setUiVisible(boolean uiVisible) {
        this.uiVisible = uiVisible;
        elapsedTimeLabel.setVisible(uiVisible);
        playerSpeedLabel.setVisible(uiVisible);
        nameLabel.setVisible(uiVisible);
    }


    public Background getMidGameBackground() {
        return midGameBackground;
    }

    public void setMidGameBackground(Background midGameBackground) {
        this.midGameBackground = midGameBackground;
    }


    public Boolean getLevelGoalCompleted() {
        return this.levelGoalCompleted;
    }

    public void setLevelGoalCompleted(Boolean levelGoalCompleted) {
        this.levelGoalCompleted = levelGoalCompleted;
    }



}
