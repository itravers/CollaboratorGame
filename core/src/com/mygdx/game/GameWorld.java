package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.Player.Player;
import java.util.ArrayList;

/**
 * Created by Isaac Assegai on 8/13/2015.
 */
public class GameWorld  implements InputProcessor {
    public MyGdxGame parent;  /* Parent */

    // Rendering Related Fields
    private GameMenu menu;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Matrix4 debugMatrix;
    private Matrix4 originalProjectionMatrix;
    public Box2DDebugRenderer debugRenderer;
    public boolean drawSprite = true;

    //UI Related Fields
    private Skin skin;
    private Label nameLabel;
    private Label elapsedTimeLabel;
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
        planets.add(new Planet(planetAtlas, world, this));
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
        batch = new SpriteBatch();
        menu = new GameMenu(this, batch);

        layout = new GlyphLayout();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        originalProjectionMatrix = batch.getProjectionMatrix();
        batch.setProjectionMatrix(camera.combined);
    }

    private void setupUI(){
       // layout.setText(font, playerName);
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        nameLabel = new Label("NAME", skin, "default");
        nameLabel.setPosition(0, Gdx.graphics.getHeight() - 20);
        elapsedTimeLabel = new Label("ELAPSEDTIME", skin, "default");
        elapsedTimeLabel.setPosition(nameLabel.getWidth() + 2, Gdx.graphics.getHeight() - 20);
        stage = new Stage();
        stage.addActor(nameLabel);
        stage.addActor(elapsedTimeLabel);
    }

    /**
     * Depending on the game state this will render either the pregame, ingame or postgame
     * @param elapsedTime The elapsed Time
     */
    public void render(float elapsedTime){
        if(parent.getGameState() == MyGdxGame.GAME_STATE.PREGAME){
            renderPreGame(elapsedTime);
        }else if(parent.getGameState() == MyGdxGame.GAME_STATE.INGAME){
            renderInGame(elapsedTime);
        }else if(parent.getGameState() == MyGdxGame.GAME_STATE.POSTGAME){
            renderPostGame(elapsedTime);
        }
    }

    /**
     * Renders the menu's that allows the player to start the game.
     * @param elapsedTime The time passed.
     */
    private void renderPreGame(float elapsedTime){
        menu.render(elapsedTime);
    }

    /**
     * Renders the game itself
     * @param elapsedTime The time passed
     */
    private void renderInGame(float elapsedTime) {
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        world.step(1f / 60f, 6, 2);
        updatePlayer(elapsedTime);
        updatePlanets(elapsedTime);
        updateGhosts(elapsedTime);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0);
        batch.begin();
        if(drawSprite){ /* Draw sprites if true */
            renderPlayer(elapsedTime, batch);
            renderPlanets(elapsedTime, batch);
            renderGhosts(elapsedTime, batch);
        }
        renderUI(elapsedTime, batch); /* Render UI even if sprites are invisible. */
        batch.end();
        debugRenderer.render(world, debugMatrix); /* Render box2d physics items */
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
        /*
        String msg = "ElapsedTime: " + elapsedTime;
        layout.setText(font, playerName);
        font.draw(batch, msg, -Gdx.graphics.getWidth()/2 + layout.width, Gdx.graphics.getHeight()/2);
        Color oldColor = Color.RED;
        font.setColor(Color.GREEN);
        font.draw(batch, playerName, -Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        font.setColor(oldColor);*/
    }

    /**
     * Called by menu when game starts
     * @param name
     */
    public void setPlayerName(String name){
        Gdx.input.setInputProcessor(this);
        playerName = name;
        nameLabel.setText(playerName);
        //nameLabel.setText("this is just a test");
        elapsedTimeLabel.setPosition(Gdx.graphics.getWidth()-elapsedTimeLabel.getWidth(), nameLabel.getY());
    }

    /**
     * Returns the players name.
     * @return The players name.
     */
    public String getPlayerName(){
        return playerName;
    }

    /**
     * Listens for key presses.
     * @param keycode The code of the key pressed
     * @return returns true when done. False if there is a problem.
     */
    @Override
    public boolean keyDown(int keycode) {
        Vector2 vel = player.getBody().getLinearVelocity();
        float angularVelocity = player.getBody().getAngularVelocity();
        if(keycode == Input.Keys.W && vel.dst2(vel) <= player.MAX_VELOCITY) player.forwardPressed  = true;
        if(keycode == Input.Keys.S && vel.dst2(vel) <= player.MAX_VELOCITY) player.backwardPressed = true;
        if(keycode == Input.Keys.Q && angularVelocity <= player.MAX_ANGULAR_VELOCITY) player.rotateRightPressed = true;
        if(keycode == Input.Keys.E && angularVelocity <= player.MAX_ANGULAR_VELOCITY) player.rotateLeftPressed  = true;
        if(keycode == Input.Keys.ESCAPE) drawSprite = ! drawSprite;
        return true;
    }

    /**
     * Listens for key releases
     * @param keycode The code of the key released.
     * @return Returns true if good, false if not.
     */
    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.W) player.forwardPressed = false;
        if(keycode == Input.Keys.S) player.backwardPressed = false;
        if(keycode == Input.Keys.Q) player.rotateRightPressed = false;
        if(keycode == Input.Keys.E) player.rotateLeftPressed  = false;
        return true;
    }

    /**
     * Listens for key typing, should probably use keyDown and keyUp instead.
     * @param character
     * @return
     */
    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    /**
     * Touch listener for mouse or touchscreen
     * @param screenX The X coords
     * @param screenY The Y coords
     * @param pointer
     * @param button
     * @return
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
