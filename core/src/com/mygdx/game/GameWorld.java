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
import com.mygdx.Player.Player;
import java.util.ArrayList;



/**
 * Created by Isaac Assegai on 8/13/2015.
 */
public class GameWorld  implements InputProcessor {
    public MyGdxGame parent;  /* Parent */
    private GameMenu menu;
    private Player player;
    private String playerName;
    private ArrayList <Planet> planets;
    private ArrayList<Player> ghosts;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout; /* Used to get bounds of fonts. */
    private OrthographicCamera camera;
    private World world; /* box2d physics world. */
    public Box2DDebugRenderer debugRenderer;
    private Matrix4 debugMatrix;

    public final float PIXELS_TO_METERS = 10f;
    public boolean drawSprite = true;

    public GameWorld(MyGdxGame p){
        parent = p;
        batch = new SpriteBatch();
        menu = new GameMenu(this, batch);
        font = new BitmapFont();
        font.setColor(Color.RED);
        layout = new GlyphLayout();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(camera.combined);
        world = new World(new Vector2(0, 0), false);
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("data/shipSprite.txt"));
        player = new Player(atlas, world, this);
        player.setPosition(0, 0);
        planets = new ArrayList<Planet>();
        TextureAtlas planetAtlas = new TextureAtlas(Gdx.files.internal("data/planetSprites.txt"));
        planets.add(new Planet(planetAtlas, world, this));
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

    private void renderInGame(float elapsedTime){
        camera.update();
        world.step(1f / 60f, 6, 2);
        updatePlayer(elapsedTime);
        updatePlanets(elapsedTime);
        updateGhosts(elapsedTime);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0);
        batch.begin();
        if(drawSprite){
            renderPlayer(elapsedTime, batch);
            renderPlanets(elapsedTime, batch);
            renderGhosts(elapsedTime, batch);
        }
        renderUI(elapsedTime, batch);
        batch.end();
        debugRenderer.render(world, debugMatrix);
    }

    private void renderPostGame(float elapsedTime){

    }

    private void updatePlayer(float elapsedTime) {
        player.update(elapsedTime);
    }

    private void updatePlanets(float elapsedTime){

    }

    private void updateGhosts(float elapsedTime){

    }

    private void renderPlayer(float elapsedTime, SpriteBatch batch){
        player.render(elapsedTime, batch);
    }

    private void renderPlanets(float elapsedTime, SpriteBatch batch){
        for(int i = 0; i < planets.size(); i++) planets.get(i).render(elapsedTime, batch);
    }

    private void renderGhosts(float elapsedTime, SpriteBatch batch){

    }

    private void renderUI(float elapsedTime, SpriteBatch batch){
        String msg = "ElapsedTime: " + elapsedTime;
        layout.setText(font, playerName);
        font.draw(batch, msg, -Gdx.graphics.getWidth()/2 + layout.width, Gdx.graphics.getHeight()/2);
        Color oldColor = Color.RED;
        font.setColor(Color.GREEN);
        font.draw(batch, playerName, -Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        font.setColor(oldColor);
       // font.draw(batch, "test", -50, 0);
    }

    /**
     * Called by menu when game starts
     * @param name
     */
    public void setPlayerName(String name){
        Gdx.input.setInputProcessor(this);
        playerName = name;
    }

    public String getPlayerName(){
        return playerName;
    }

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

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.W) player.forwardPressed = false;
        if(keycode == Input.Keys.S) player.backwardPressed = false;
        if(keycode == Input.Keys.Q) player.rotateRightPressed = false;
        if(keycode == Input.Keys.E) player.rotateLeftPressed  = false;
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

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
