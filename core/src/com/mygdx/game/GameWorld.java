package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.Player.Player;
import java.util.ArrayList;

/**
 * Created by Isaac Assegai on 8/13/2015.
 */
public class GameWorld {
    public MyGdxGame parent;  /* Parent */
    private GameMenu menu;
    private Player player;
    //private ArrayList <Planet> planets;
    private ArrayList<Player> ghosts;
    private SpriteBatch batch;
    private BitmapFont font;


    public GameWorld(MyGdxGame p){
        parent = p;
        batch = new SpriteBatch();
        menu = new GameMenu(this, batch);
        font = new BitmapFont();
        font.setColor(Color.RED);
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

    }

    private void renderPlanets(float elapsedTime, SpriteBatch batch){

    }

    private void renderGhosts(float elapsedTime, SpriteBatch batch){

    }

    private void renderUI(float elapsedTime, SpriteBatch batch){
        font.draw(batch, "ElapsedTime: " + elapsedTime, 10, Gdx.graphics.getHeight());
    }
}
