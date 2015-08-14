package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.Player.Player;
import java.util.ArrayList;

import javax.xml.soap.Text;


/**
 * Created by Isaac Assegai on 8/13/2015.
 */
public class GameWorld {
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


    public GameWorld(MyGdxGame p){
        parent = p;
        batch = new SpriteBatch();
        menu = new GameMenu(this, batch);
        font = new BitmapFont();
        font.setColor(Color.RED);
        layout = new GlyphLayout();
        camera = new OrthographicCamera(480, 800);
        world = new World(new Vector2(0, 0), false);
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("data/shipSprite.txt"));
        player = new Player(atlas, world);
        player.setPosition(0, 0);
        planets = new ArrayList<Planet>();
        TextureAtlas planetAtlas = new TextureAtlas(Gdx.files.internal("data/planetSprites.txt"));
        planets.add(new Planet(planetAtlas));

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
        batch.setProjectionMatrix(camera.combined);
        updatePlayer(elapsedTime);
        updatePlanets(elapsedTime);
        updateGhosts(elapsedTime);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        renderPlayer(elapsedTime, batch);
        renderPlanets(elapsedTime, batch);
        renderGhosts(elapsedTime, batch);
        renderUI(elapsedTime, batch);
        batch.end();
    }

    private void renderPostGame(float elapsedTime){

    }

    private void updatePlayer(float elapsedTime) {
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

    public void setPlayerName(String name){
        playerName = name;
    }

    public String getPlayerName(){
        return playerName;
    }
}
