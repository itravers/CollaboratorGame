package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.Player.Ghost;
import com.mygdx.Player.Player;
import java.util.ArrayList;

import input.GameInput;
import input.InputManager;

/**
 * Created by Isaac Assegai on 8/13/2015.
 */
public class GameWorld{
    public MyGdxGame parent;  /* Parent */

    // Rendering Related Fields
    private GameMenu menu;
    private SpriteBatch batch;
    private SpriteBatch backGroundBatch;
    private OrthographicCamera camera; //drawing game pieces
    private ParallaxCamera backgroundCamera; //drawing sprites
    private Matrix4 debugMatrix;
    public Box2DDebugRenderer debugRenderer;
    public boolean drawSprite = true;
    private TextureRegion[] backgroundLayers; //Parallax background

    //UI Related Fields
    private Skin skin;
    public Label nameLabel;
    public Label elapsedTimeLabel;
    public Label midGameMsgLbl;

    private Stage stage; //for drawing ui
    private BitmapFont font;
    private GlyphLayout layout; /* Used to get bounds of fonts. */

    // Physics Related Fields
    private World world; /* box2d physics world. */

    // Player Related Fields
    private Player player;
    private String playerName;
    public Vector2 originalPlayerPosition;

    // Ghost Related Fields
    private ArrayList<Ghost> ghosts;

    //Input Related Fields
    private InputManager inputManager;

    // Planet Related Fields
    private ArrayList <Planet> planets;

    // Constant Fields
    public final float PIXELS_TO_METERS = 10f;
    public final short CATEGORY_PLAYER = -1;
    public final short CATEGORY_PLANET = -2;

    /**
     * Creates a new game world. Sets up all needed pieces.
     * @param p The Parent wrapper of the game world.
     */
    public GameWorld(MyGdxGame p){
        parent = p;
        setupRendering();
        setupPhysics();
        setupPlanets(); // before setupPlayer
        setupPlayer();
        setupGhosts();

        inputManager = new InputManager(this);
    }

    /**
     * Setup players ghosts
     */
    private void setupGhosts(){
        ghosts = new ArrayList<Ghost>();
    }

    /**
     * Setup planets in the world
     */
    private void setupPlanets(){
        planets = new ArrayList<Planet>();
        TextureAtlas planetAtlas = new TextureAtlas(Gdx.files.internal("data/planetSprites.txt"));
        TextureAtlas planetAtlas2 = new TextureAtlas(Gdx.files.internal("data/planetSprites.txt"));
        TextureAtlas planetAtlas3 = new TextureAtlas(Gdx.files.internal("data/planetSprites.txt"));
        Vector2 pPos = new Vector2(0,0);
        Vector2 p2Pos = new Vector2(0, 1000);
        Vector2 p3Pos = new Vector2(0, -1000);
        Planet p = new Planet(pPos, planetAtlas, world, 100000f, this);
        Planet p2 = new Planet(p2Pos, planetAtlas2, world, 100000f, this);
        Planet p3 = new Planet(p3Pos, planetAtlas3, world, 100000f, this);
        planets.add(p);
        planets.add(p2);
        planets.add(p3);

    }

    /**
     * Setup the player
     */
    private void setupPlayer(){
        originalPlayerPosition = new Vector2((planets.get(0).getWidth()/2)-12, 100);
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("data/shipSprite.txt"));
        player = new Player(originalPlayerPosition, atlas, world, this);
        player.setPosition(originalPlayerPosition.x, originalPlayerPosition.y);
    }

    /**
     * Setup the worlds physics.
     */
    private void setupPhysics(){
        world = new World(new Vector2(0, 0),true);
    }

    private void resetPhysics(){
        //world.destroyBody(player.getBody());
        world = new World(new Vector2(0, 0), false);
       // player.setWorld(world);
       // for(int i = 0; i < ghosts.size(); i++){
       //     ghosts.get(i).setWorld(world);
       // }
    }

    /**
     * Prepare objects for rendering.
     */
    private void setupRendering(){
        setupUI();
        setupBackground();
        batch = new SpriteBatch();
        backGroundBatch = new SpriteBatch();
        menu = new GameMenu(this, batch);

        layout = new GlyphLayout();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundCamera = new ParallaxCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(camera.combined);
        backGroundBatch.setProjectionMatrix(backgroundCamera.combined);
    }

    private void setupBackground(){
        Texture background = new Texture(Gdx.files.internal("data/background.png"));
        Texture background2 = new Texture(Gdx.files.internal("data/background2.png"));
        background2.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        backgroundLayers = new TextureRegion[3];
        backgroundLayers[0] = new TextureRegion(background, 0, 0, 2732, 1536);
        backgroundLayers[1] = new TextureRegion(background2, 0, 0, 5320, 4440);
        //backgroundLayers[1].
    }
    private void setupUI(){
       // layout.setText(font, playerName);
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        nameLabel = new Label("NAME", skin, "default");
        nameLabel.setPosition(0, Gdx.graphics.getHeight() - 20);
        elapsedTimeLabel = new Label("ELAPSEDTIME", skin, "default");
        elapsedTimeLabel.setPosition(nameLabel.getWidth() + 2, Gdx.graphics.getHeight() - 20);
        nameLabel.setColor(Color.GREEN);
        elapsedTimeLabel.setColor(Color.RED);
        stage = new Stage();
        stage.addActor(nameLabel);
        stage.addActor(elapsedTimeLabel);

        //MidGameMsgSEtup
        midGameMsgLbl = new Label("You have died. Press Space to Continue", skin, "default");
        midGameMsgLbl.setColor(Color.RED);
        midGameMsgLbl.setPosition(0, Gdx.graphics.getHeight()/2 - 20);
        midGameMsgLbl.setVisible(false);
        stage.addActor(midGameMsgLbl);

    }

    /**
     * Depending on the game state this will render either the pregame, ingame or postgame
     * @param elapsedTime The elapsed Time
     */
    public void render(float elapsedTime){
        int frameNum = parent.getFrameNum();

        if(parent.getGameState() == MyGdxGame.GAME_STATE.PREGAME){
            renderPreGame(elapsedTime);
        }else if(parent.getGameState() == MyGdxGame.GAME_STATE.INGAME){
            renderInGame(elapsedTime);
        }else if(parent.getGameState() == MyGdxGame.GAME_STATE.POSTGAME){
            renderPostGame(elapsedTime);
        }else if(parent.getGameState() == MyGdxGame.GAME_STATE.MIDGAME){
            renderMidGame(elapsedTime);
        }
    }

    /**
     * Renders the menu's that allows the player to start the game.
     * @param elapsedTime The time passed.
     */
    private void renderPreGame(float elapsedTime){
        menu.render(elapsedTime);
    }

    private void renderMidGame(float elapsedTime){
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        backgroundCamera.position.set(player.getX(), player.getY(), 0);
        backgroundCamera.update();
        backGroundBatch.setProjectionMatrix(backgroundCamera.combined);
        world.step(1f / 60f, 6, 2);

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

    /**
     * Renders the game itself
     * @param elapsedTime The time passed
     */
    private void renderInGame(float elapsedTime) {
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        backgroundCamera.position.set(player.getX(), player.getY(), 0);
        backgroundCamera.update();
        backGroundBatch.setProjectionMatrix(backgroundCamera.combined);
        world.step(1f / 60f, 6, 2);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0);
        renderBackground(elapsedTime, backGroundBatch); //Should be done before other renders
        batch.begin();
        if(drawSprite){ /* Draw sprites if true */
            renderPlanets(elapsedTime, batch);
            renderPlayer(elapsedTime, batch);
            renderGhosts(elapsedTime, batch);
        }
        batch.end();
        renderUI(elapsedTime, batch); /* this needs to be after batch.end */
        debugRenderer.render(world, debugMatrix); /* Render box2d physics items */

        //Update after rendering, this will be rendered next frame
        updatePlayer(elapsedTime);
        updatePlanets(elapsedTime);
        updateGhosts(elapsedTime);
    }

    private void renderBackground(float elapsedTime, SpriteBatch batch){
        Matrix4 temp = batch.getProjectionMatrix();
        backGroundBatch.setProjectionMatrix(backgroundCamera.calculateParallaxMatrix(.1f, .1f));
        backGroundBatch.disableBlending();
        backGroundBatch.begin();
        backGroundBatch.draw(backgroundLayers[0], -(int) (backgroundLayers[0].getRegionWidth() / 2),
                -(int) (backgroundLayers[0].getRegionHeight() / 2));
        backGroundBatch.end();
        backGroundBatch.enableBlending();

        backGroundBatch.setProjectionMatrix(backgroundCamera.calculateParallaxMatrix(.3f, .3f));
        backGroundBatch.begin();
        backGroundBatch.draw(backgroundLayers[1], -(int) (backgroundLayers[1].getRegionWidth() / 2),
                -(int) (backgroundLayers[1].getRegionHeight() / 2));
        backGroundBatch.end();

        backGroundBatch.setProjectionMatrix(temp);
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
        player.update(elapsedTime);
    }

    /**
     * Updates the planets
     * @param elapsedTime The time passed
     */
    private void updatePlanets(float elapsedTime){
        for(int i = 0; i < planets.size(); i++){
            planets.get(i).update(elapsedTime);
        }
    }

    /**
     * Update the ghosts
     * @param elapsedTime The time passed
     */
    private void updateGhosts(float elapsedTime){
        //System.out.println("numGhosts: " + ghosts.size());
        for(int i = 0; i < ghosts.size(); i++){
            ghosts.get(i).update(elapsedTime);
        }
    }

    /**
     * Renders the player
     * @param elapsedTime The time passed
     * @param batch The SpriteBatch we render with.
     */
    private void renderPlayer(float elapsedTime, SpriteBatch batch){
        player.render(elapsedTime, batch);
    }

    /**
     * Render the planets
     * @param elapsedTime The time passed
     * @param batch The SpriteBatch we render with
     */
    private void renderPlanets(float elapsedTime, SpriteBatch batch){
        for(int i = 0; i < planets.size(); i++) planets.get(i).render(elapsedTime, batch);
    }

    /**
     * Render the players ghosts
     * @param elapsedTime The time passed
     * @param batch The SpriteBatch we render with
     */
    private void renderGhosts(float elapsedTime, SpriteBatch batch){
        for(int i = 0; i < ghosts.size(); i++){
            ghosts.get(i).render(elapsedTime, batch);
        }
    }

    /**
     * Render the UI Overlay
     * @param elapsedTime The time passed.
     * @param batch The SpriteBatch we render with.
     */
    private void renderUI(float elapsedTime, SpriteBatch batch){
        elapsedTimeLabel.setText(new Float(elapsedTime).toString());
        stage.draw();
    }

    /**
     * Called by menu when game starts
     * @param name
     */
    public void setPlayerName(String name){
        Gdx.input.setInputProcessor(inputManager);
        playerName = name;
        nameLabel.setText(playerName);
        elapsedTimeLabel.setPosition(Gdx.graphics.getWidth() - elapsedTimeLabel.getWidth(), nameLabel.getY());
    }

    /**
     * Returns the players name.
     * @return The players name.
     */
    public String getPlayerName(){
        return playerName;
    }

    public ArrayList<Planet> getPlanets() {
        return planets;
    }

    public void setPlanets(ArrayList<Planet> planets) {
        this.planets = planets;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }


    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets us up for the next level.
     * Copies player to a ghost.
     * Resets world pieces to origin.
     */
    public void nextLevel(){
        resetPhysics();
        addGhost(player);
        resetWorld();
    }

    /**
     * Creates a set of new ghosts from the set of old ones.
     */
    private void resetGhosts(){

        for(int i = 0; i < ghosts.size(); i++){
            Ghost g = ghosts.get(i);
            TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("data/shipSprite.txt"));

            ArrayList<GameInput>inputList = new ArrayList<GameInput>();
            inputList.addAll(g.inputList);
            com.badlogic.gdx.utils.Array<Body> bodies = new com.badlogic.gdx.utils.Array<Body>();
            world.getBodies(bodies);
            if(bodies.contains(g.getBody(), true)){
                world.destroyBody(g.getBody());
            }

            Ghost newGhost = new Ghost(originalPlayerPosition, textureAtlas, world, this, inputList, i);
            newGhost.setPosition(originalPlayerPosition.x, originalPlayerPosition.y);
            ghosts.set(i, newGhost);
        }
    }

    private void resetPlanets(){
        for(int i = 0; i < planets.size(); i++){
            Planet p = planets.get(i);
            Planet newPlanet = new Planet(new Vector2(p.getX(), p.getY()), p.getTextureAtlas(), world, p.getMass(), this);
            planets.set(i, newPlanet);
        }
    }

    /**
     * Creates a ghost based on a player.
     * @param player The player we are basing the ghost on.
     */
    private void addGhost(Player player){
        TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("data/shipSprite.txt"));
        //ArrayList<GameInput>inputList = (ArrayList<GameInput>) player.inputList.clone();//new ArrayList<GameInput>(player.inputList);//(ArrayList<GameInput>) player.inputList.clone();
        ArrayList<GameInput>inputList = new ArrayList<GameInput>();
        inputList.addAll(player.inputList);
        int index = ghosts.size();
        Ghost g = new Ghost(originalPlayerPosition, textureAtlas, world, this, inputList, index);
        g.setPosition(originalPlayerPosition.x, originalPlayerPosition.y);
        ghosts.add(g);
    }

    private void resetWorld(){
        parent.elapsedTime = 0;
        parent.resetFrameNum(); //reset frame counter for accurate replays
        resetPlanets();
        resetGhosts();
        setupPlayer();
        inputManager.reset();
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
