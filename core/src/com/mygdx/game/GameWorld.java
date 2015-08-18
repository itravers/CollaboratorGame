package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.Player.Ghost;
import com.mygdx.Player.Player;
import java.util.ArrayList;

import input.GameInput;
import input.InputManager;

/**
 * Created by Isaac Assegai on 8/13/2015.
 */
public class GameWorld implements ContactListener{
    public MyGdxGame parent;  /* Parent */

    private LevelManager levelManager; /* Manages Level Changes. */

    // Rendering Related Fields

    private SpriteBatch batch;
    private SpriteBatch backGroundBatch;
    private OrthographicCamera camera; //drawing game pieces
    public ParallaxCamera backgroundCamera; //drawing sprites
    private Matrix4 debugMatrix;
    public Box2DDebugRenderer debugRenderer;
    public boolean drawSprite = true;

    //UI Related Fields
    private Skin skin;
    private Stage stage; //for drawing ui
    private BitmapFont font;
    private GlyphLayout layout; /* Used to get bounds of fonts. */

    // Player Related Fields

    private String playerName;


    //Input Related Fields
    private InputManager inputManager;

    // Constant Fields
    public final float PIXELS_TO_METERS = 10f;
    public final short CATEGORY_PLAYER = -1;
    public final short CATEGORY_PLANET = -2;

    //Animation Related Fields
    private TextureAtlas shipAtlas;
    private TextureAtlas deadAtlas;
    private TextureAtlas explosionAtlas;

    private TextureRegion[] deadFrames;
    private TextureRegion[] moveForwardFrames;
    private TextureRegion[] noMovementAnimationFrames;

    public Animation deadAnimation;
    public Animation moveForwardAnimation;
    public Animation noMovementAnimation;
    public Animation explosionAnimation;


    /**
     * Creates a new game world. Sets up all needed pieces.
     * @param p The Parent wrapper of the game world.
     */
    public GameWorld(MyGdxGame p){
        parent = p;
        levelManager = new LevelManager(this);
        setupRendering();
        inputManager = new InputManager(this);
    }

    /**
     * Prepare objects for rendering.
     */
    private void setupRendering(){
        setupUI();
        batch = new SpriteBatch();
        backGroundBatch = new SpriteBatch();

        levelManager.setupBackground();
        levelManager.setupMenu(this, batch);
        setupAnimations();



        layout = new GlyphLayout();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundCamera = new ParallaxCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(camera.combined);
        backGroundBatch.setProjectionMatrix(backgroundCamera.combined);
    }

    private void setupAnimations(){
        setupMoveForwardAnimation();
        setupNoMovementAnimation();
        setupExplosionAnimation();
        setupDeadAnimation();
    }

    /**
     * Gets our forward animation from the sprite sheet.
     */
    private void setupMoveForwardAnimation(){
        shipAtlas = new TextureAtlas(Gdx.files.internal("data/shipSprite.txt"));
        moveForwardFrames = new TextureRegion[7];
        moveForwardFrames[0] = (shipAtlas.findRegion("0005"));
        moveForwardFrames[1] = (shipAtlas.findRegion("0006"));
        moveForwardFrames[2] = (shipAtlas.findRegion("0007"));
        moveForwardFrames[3] = (shipAtlas.findRegion("0008"));
        moveForwardFrames[4] = (shipAtlas.findRegion("0007"));
        moveForwardFrames[5] = (shipAtlas.findRegion("0006"));
        moveForwardFrames[6] = (shipAtlas.findRegion("0005"));
        moveForwardAnimation = new Animation(1/15f, moveForwardFrames);
    }

    private void setupNoMovementAnimation(){
        noMovementAnimationFrames = new TextureRegion[2];
        noMovementAnimationFrames[0] = (shipAtlas.findRegion("0005"));
        noMovementAnimationFrames[1] = (shipAtlas.findRegion("0005"));
        noMovementAnimation = new Animation(1/2f, noMovementAnimationFrames);
    }

    private void setupExplosionAnimation(){
        explosionAtlas = new TextureAtlas(Gdx.files.internal("data/explosionC.txt"));
        explosionAnimation = new Animation(1/30f, explosionAtlas.getRegions());
    }



    /**
     * Creates the UI Overlay HUD. This doesn't change per level
     */
    private void setupUI(){
       // layout.setText(font, playerName);
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        stage = new Stage();
        levelManager.setupUI(skin, stage);


    }

    /**
     * Depending on the game state this will render either the pregame, ingame or postgame
     * @param elapsedTime The elapsed Time
     */
    public void render(float elapsedTime){

        /*if(parent.getGameState() == MyGdxGame.GAME_STATE.PREGAME){
            renderPreGame(elapsedTime);
        }else if(parent.getGameState() == MyGdxGame.GAME_STATE.INGAME){
            renderInGame(elapsedTime);
        }else if(parent.getGameState() == MyGdxGame.GAME_STATE.POSTGAME){
            renderPostGame(elapsedTime);
        }else if(parent.getGameState() == MyGdxGame.GAME_STATE.MIDGAME){
            renderMidGame(elapsedTime);
        }*/
        int level = levelManager.getLevel();
        switch (level){
            case 0: //Pregame menu
                renderPreGame(elapsedTime);
                break;
            case 1: //Level 1
                if(parent.getGameState() == MyGdxGame.GAME_STATE.INGAME){
                    renderInGame(elapsedTime);
                }else if(parent.getGameState() == MyGdxGame.GAME_STATE.MIDGAME){
                    renderMidGame(elapsedTime);
                }
                break;
        }


    }

    /**
     * Renders the menu's that allows the player to start the game.
     * @param elapsedTime The time passed.
     */
    private void renderPreGame(float elapsedTime){
        levelManager.getMenu().render(elapsedTime);
    }

    private void renderMidGame(float elapsedTime){
        camera.position.set(levelManager.getPlayer().getX(), levelManager.getPlayer().getY(), 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        backgroundCamera.position.set(levelManager.getPlayer().getX(), levelManager.getPlayer().getY(), 0);
        backgroundCamera.update();
        backGroundBatch.setProjectionMatrix(backgroundCamera.combined);
        levelManager.getWorld().step(1f / 60f, 6, 2);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0);
        renderBackground(elapsedTime, backGroundBatch); //Should be done before other renders
        batch.begin();
       // midGameMsgLbl = new Label("You Have Died. Press Space To Continue.");
        stage.draw();
        batch.end();
        //renderUI(elapsedTime, batch); /* this needs to be after batch.end */
        //debugRenderer.render(world, debugMatrix); /* Render box2d physics items */
    }

    private void renderBackground(float elapsedTime, SpriteBatch b){
        levelManager.getBackground().render(elapsedTime, b);
    }

    /**
     * Renders the game itself
     * @param elapsedTime The time passed
     */
    private void renderInGame(float elapsedTime) {
        camera.position.set(levelManager.getPlayer().getX(), levelManager.getPlayer().getY(), 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        backgroundCamera.position.set(levelManager.getPlayer().getX(), levelManager.getPlayer().getY(), 0);
        backgroundCamera.update();
        backGroundBatch.setProjectionMatrix(backgroundCamera.combined);
        levelManager.getWorld().step(1f / 60f, 6, 2);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0);
        renderBackground(elapsedTime, backGroundBatch); //Should be done before other renders
        batch.begin();
        if(drawSprite){ /* Draw sprites if true */
            renderPlanets(elapsedTime, batch);
            renderGhosts(elapsedTime, batch);
            renderPlayer(elapsedTime, batch);
        }
        batch.end();
        renderUI(elapsedTime, batch); /* this needs to be after batch.end */
        if(!drawSprite) debugRenderer.render(levelManager.getWorld(), debugMatrix); /* Render box2d physics items */

        //Update after rendering, this will be rendered next frame
        updatePlayer(elapsedTime);
        updatePlanets(elapsedTime);
        updateGhosts(elapsedTime);
    }



    /**
     * Renders the score sheet, etc.
     * @param elapsedTime The time elapsed
     */
    private void renderPostGame(float elapsedTime){

    }

    /**
     * Updates the player
     * @param elapsedTime The time passed
     */
    private void updatePlayer(float elapsedTime) {
        levelManager.getPlayer().update(elapsedTime);
    }

    /**
     * Updates the planets
     * @param elapsedTime The time passed
     */
    private void updatePlanets(float elapsedTime){
        for(int i = 0; i < levelManager.getPlanets().size(); i++){
            levelManager.getPlanets().get(i).update(elapsedTime);
        }
    }

    /**
     * Update the ghosts
     * @param elapsedTime The time passed
     */
    private void updateGhosts(float elapsedTime){
        //System.out.println("numGhosts: " + ghosts.size());
        for(int i = 0; i < levelManager.getGhosts().size(); i++){
            levelManager.getGhosts().get(i).update(elapsedTime);
        }
    }

    /**
     * Renders the player
     * @param elapsedTime The time passed
     * @param batch The SpriteBatch we render with.
     */
    private void renderPlayer(float elapsedTime, SpriteBatch batch){
        levelManager.getPlayer().render(elapsedTime, batch);
    }

    /**
     * Render the planets
     * @param elapsedTime The time passed
     * @param batch The SpriteBatch we render with
     */
    private void renderPlanets(float elapsedTime, SpriteBatch batch){
        for(int i = 0; i < levelManager.getPlanets().size(); i++) {
            levelManager.getPlanets().get(i).render(elapsedTime, batch);
        }
    }

    /**
     * Render the players ghosts
     * @param elapsedTime The time passed
     * @param batch The SpriteBatch we render with
     */
    private void renderGhosts(float elapsedTime, SpriteBatch batch){
        for(int i = 0; i < levelManager.getGhosts().size(); i++){
            levelManager.getGhosts().get(i).render(elapsedTime, batch);
        }
    }

    /**
     * Render the UI Overlay
     * @param elapsedTime The time passed.
     * @param batch The SpriteBatch we render with.
     */
    private void renderUI(float elapsedTime, SpriteBatch batch){
        levelManager.getElapsedTimeLabel().setText(new Float(elapsedTime).toString());
        levelManager.getPlayerStateLabel().setText(levelManager.getPlayer().getCurrentState().toString());
        levelManager.getPlayerSpeedLabel().setText(new Float(levelManager.getPlayer().getBody().getLinearVelocity().len()).toString());
        stage.draw();
    }

    /**
     * Called by menu when game starts
     * @param name
     */
    public void setPlayerName(String name){
        Gdx.input.setInputProcessor(inputManager);
        playerName = name;
        levelManager.getNameLabel().setText(playerName);
        levelManager.getElapsedTimeLabel().setPosition(
                Gdx.graphics.getWidth() - levelManager.getElapsedTimeLabel().getWidth(),
                levelManager.getNameLabel().getY());
    }

    /**
     * Returns the players name.
     * @return The players name.
     */
    public String getPlayerName(){
        return playerName;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    private void setupDeadAnimation(){
        deadAtlas = new TextureAtlas(Gdx.files.internal("data/coin.txt"));
        deadFrames = new TextureRegion[27];
        for(int i = 0; i < 27; i++){/* 27 frames in the rotate and flip animation. */
            if(i < 10){
                deadFrames[i] = (deadAtlas.findRegion("flipAndRotateAnimation00"+i+"0"));
            }else{
                deadFrames[i] = (deadAtlas.findRegion("flipAndRotateAnimation0"+i+"0"));
            }

        }
        deadAnimation = new Animation(1/8f, deadFrames);
    }

    @Override
    public void beginContact(Contact contact) {
        Object a = contact.getFixtureA().getBody().getUserData();
        Object b = contact.getFixtureB().getBody().getUserData();
        if((a instanceof Planet && b instanceof Player) || (b instanceof Planet && a instanceof Player)){
            //First assign player to s
            Player s; //s for ship
            if(a instanceof Player){
                s = (Player)a;
            }else{
                s = (Player)b;
            }

            //Then assign the planet to p
            Planet p; //p for planet
            if(a instanceof Planet){
                p = (Planet)a;
            }else{
                p = (Planet)b;
            }

            //then decide what to do based on the Players state.
            /* We were flying, now we've hit a planet, so we are either landing or crashing.
             * In order to land the player must be faced generally away from the planet
             * and we must stay under a certain speed*/
            if(s.getCurrentState() == Player.STATE.FLYING){
                if(didPlayerCrashIntoPlanet(s, p)){
                    s.setCurrentState(Player.STATE.EXPLOADING);
                }else{
                    s.setCurrentState(Player.STATE.LANDED);
                }
            }else if(s.getCurrentState() == Player.STATE.LANDED){

            }else if(s.getCurrentState() == Player.STATE.EXPLOADING){
                //we don't need to do anything here, we transition elsewhere
            }else if(s.getCurrentState() == Player.STATE.DEAD){
                //we don't need to do anything here, we transition elsewhere
            }
        }
    }

    /**
     * Checks to see if the given player - s crashed into the given
     * planet - p during the collision that called this function.
     * If s is facing the opposite direction as p and s' velocity
     * is slow enough
     * @param s The Player that we are checking
     * @param p The Planet that we are checking
     * @return True if the player crashed, false if not.
     */
    private boolean didPlayerCrashIntoPlanet(Player s, Planet p){
        boolean returnVal = false;
        Vector2 v_planetToPlayer = s.getBody().getPosition().sub(p.getBody().getPosition());
        float f_planetToPlayer = v_planetToPlayer.angle();
        float f_playerDir = (float)Math.toDegrees(s.getBody().getAngle());
        f_planetToPlayer = f_planetToPlayer % 360; //limit to 360 degrees
        f_playerDir = f_playerDir % 360; //limit to 360 degrees.
        float angleDif = Math.abs(f_planetToPlayer - f_playerDir);
        angleDif = angleDif % 360;

        //System.out.println("planetToPlayer: " + f_planetToPlayer + " playerDir: " + f_playerDir + " angleDif: " + angleDif);

        if(45 >= angleDif || 135 <= angleDif){
           // System.out.print(" angle is bad ");
            //the player hit the planet while facing the planet, it crashed
            //s.setCurrentState(Player.STATE.EXPLOADING);
            returnVal = true;
        }else{
            //System.out.print(" angle is good ");
            /*the player hit the planet while facing the opposite direction.
              We now need to check if the player was going slow enough */
            float speed = levelManager.getPlayer().getBody().getLinearVelocity().len();
            //System.out.println("speed: " +speed);
            if(speed > levelManager.getPlayer().MAX_VELOCITY/3){
                //System.out.print(" speed is bad ");
                //s.setCurrentState(Player.STATE.EXPLOADING);
                returnVal = true;
            }else{
               // System.out.print(" speed is good ");
                returnVal = false;
            }

        }

        return returnVal;
    }

    @Override
    public void endContact(Contact contact) {
        Object a = contact.getFixtureA().getBody().getUserData();
        Object b = contact.getFixtureB().getBody().getUserData();
        if((a instanceof Planet && b instanceof Player) || (b instanceof Planet && a instanceof Player)){
            //System.out.println("Ended collision with planet");
            //First assign player to s
            Player s; //s for ship
            if(a instanceof Player){
                s = (Player)a;
            }else{
                s = (Player)b;
            }

            //Then assign the planet to p
            Planet p; //p for planet
            if(a instanceof Planet){
                p = (Planet)a;
            }else{
                p = (Planet)b;
            }

            //we could transition states here from landed to flying, but we want to
            // that in the player.render itself.
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }


    public LevelManager getLevelManager() {
        return levelManager;
    }

    public void setLevelManager(LevelManager levelManager) {
        this.levelManager = levelManager;
    }


    public InputManager getInputManager() {
        return inputManager;
    }

    public void setInputManager(InputManager inputManager) {
        this.inputManager = inputManager;
    }


}
