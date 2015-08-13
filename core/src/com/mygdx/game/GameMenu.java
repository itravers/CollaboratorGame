package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by Isaac Assegai on 8/13/2015.
 */
public class GameMenu {
    private GameWorld parent;
    private SpriteBatch batch;
    private Skin skin;
    private Stage stage;

    public GameMenu(GameWorld p, SpriteBatch b){
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        parent = p;
        batch = b;
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        stage = new Stage();

        final TextButton button = new TextButton("Click Me", skin, "default");
        button.setWidth(200f);
        button.setHeight(20f);
        button.setPosition(Gdx.graphics.getWidth() / 2 - 100f, Gdx.graphics.getHeight() / 2 - 10f);
        button.addListener(new ClickListener(){
           public void clicked(InputEvent event, float x, float y){
               button.setText("You clicked the button");
           }
        });
        stage.addActor(button);
        Gdx.input.setInputProcessor(stage);
    }

    public void render(float elapsedTime){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        stage.draw();
        batch.end();
    }
}
