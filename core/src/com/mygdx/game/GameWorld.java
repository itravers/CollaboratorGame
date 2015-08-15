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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.Player.Player;
import java.util.ArrayList;

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

    // Ghost Related Fields
    private ArrayList<Player> ghosts;

    //Input Related Fields
    private InputManager inputManager;
    public float inputTime = 0;

    // Planet Related Fields
    private ArrayList <Planet> planets;

    // Constant Fields
    public final float PIXELS_TO_METERS = 10f;

    /**
     * Creates a new game world. Sets up all needed pieces.
     * @param p The Parent wrapper of the game world.
     */
    public GameWorld(MyGdxGame p){
        parent = p;
        setupRendering();
        setupPhysics();
        setupPlayer();
        setupGhosts();
        setupPlanets();
        inputManager = new InputManager(this);
    }

    /**
     * Setup players ghosts
     */
    private void setupGhosts(){
        //setup ghosts here
    }

    /**
     * Setup planets in the world
     */
    private void setupPlanets(){
        planets = new ArrayList<Planet>();
        TextureAtlas planetAtlas = new TextureAtlas(Gdx.files.internal("data/planetSprites.txt"));
        planets.add(new Planet(planetAtlas, world, 100000f, this));
    }

    /**
     * Setup the player
     */
    private void setupPlayer(){
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("data/shipSprite.txt"));
        player = new Player(atlas, world, this);
        player.setPosition(0, 0);
    }

    /**
     * Setup the worlds physics.
     */
    private void setupPhysics(){
        world = new World(new Vector2(0, 0), false);
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
        inputTime = elapsedTime; /* Gives InputManager access to elapsedTime. */
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
        // Update Planets here
    }

    /**
     * Update the ghosts
     * @param elapsedTime The time passed
     */
    private void updateGhosts(float elapsedTime){
        //Update Ghosts here.
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
        //Render Ghosts here.
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
        elapsedTimeLabel.setPosition(Gdx.graphics.getWidth()-elapsedTimeLabel.getWidth(), nameLabel.getY());
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

}
