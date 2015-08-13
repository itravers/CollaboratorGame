package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.Player.Player;
import java.util.ArrayList;

/**
 * Created by Isaac Assegai on 8/13/2015.
 */
public class GameWorld {
    /* Parent */
    MyGdxGame parent;
    Player player;
    ArrayList <Planet> planets;
    ArrayList<Player> ghosts;
    SpriteBatch batch;

    public GameWorld(MyGdxGame p){
        parent = p;
        batch = new SpriteBatch();
    }

    public void render(float elapsedTime){
        updatePlayer(elapsedTime);
        updatePlanets(elapsedTime);
        updateGhosts(elapsedTime);

        renderPlayer(elapsedTime);
        renderPlanets(elapsedTime);
        renderGhosts(elapsedTime);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    }
}
