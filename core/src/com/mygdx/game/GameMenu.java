package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by Isaac Assegai on 8/13/2015.
 */
public class GameMenu {
    private GameWorld parent;
    private SpriteBatch batch;
    private Skin skin;
    private Stage stage;
    private Label nameLabel;
    private TextField textField;
    private TextButton button;
    private TextButton landscapeButton;
    private TextButton portraitButton;

    public GameMenu(GameWorld p, SpriteBatch b){
        System.out.println("init gamemenu");
        parent = p;
        batch = b;
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        stage = new Stage();

        nameLabel = new Label("Your Name", skin, "default");
        nameLabel.setWidth(200f);
        nameLabel.setHeight(10f);
        nameLabel.setAlignment(1);
        nameLabel.setPosition(Gdx.graphics.getWidth() / 2 - 100f, Gdx.graphics.getHeight() / 2 + 30f);

        textField = new TextField("", skin, "default");
        textField.addListener(new InputListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("event happens");
                textField.clear();
            }
        });

        textField.setWidth(200f);
        textField.setHeight(20f);
        textField.setAlignment(1); /* Center */
        textField.getStyle().fontColor = Color.GREEN;
        textField.setPosition(Gdx.graphics.getWidth() / 2 - 100f, Gdx.graphics.getHeight() / 2 + 10f);
        button = new TextButton("Start Game", skin, "default");
        button.setWidth(200f);
        button.setHeight(20f);
        button.setPosition(Gdx.graphics.getWidth() / 2 - 100f, Gdx.graphics.getHeight() / 2 - 10f);
        button.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                button.setText("Starting Game");
                setName();
                parent.parent.setFrameNum(0);
                parent.parent.setGameState(MyGdxGame.GAME_STATE.INGAME);
                //parent.getLevelManager().setLevel(1); //start the first level
                parent.getLevelManager().nextLevel();
            }
        });

        landscapeButton = new TextButton("Landscape", skin, "default");
        portraitButton = new TextButton("Portrait", skin, "default");

        landscapeButton.setWidth(100f);
        landscapeButton.setHeight(20f);
        portraitButton.setWidth(100f);
        portraitButton.setHeight(20f);

        landscapeButton.setPosition(Gdx.graphics.getWidth() / 2 - 100f, Gdx.graphics.getHeight() / 2 - 30f);
        portraitButton.setPosition(Gdx.graphics.getWidth() / 2 - 0f, Gdx.graphics.getHeight() / 2 - 30f);

        landscapeButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                button.setText("Setting Landscape Mode");

            }
        });

        portraitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                button.setText("Setting Portrait Mode");
                setName();
                parent.parent.setGameState(MyGdxGame.GAME_STATE.INGAME);
            }
        });


        stage.addActor(nameLabel);
        stage.addActor(textField);
        stage.addActor(button);
        stage.addActor(landscapeButton);
        stage.addActor(portraitButton);
        Gdx.input.setInputProcessor(stage);
    }

    public void render(float elapsedTime){
       // Gdx.gl.glClearColor(0, 0, 0, 1);
       // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        stage.draw();
        batch.end();
    }

    private void setName(){
        String name = textField.getText();
        if(name.length() >= 1){
            parent.setPlayerName(name);
        }else{
            parent.setPlayerName(generateRandomName());
        }
    }

    private String generateRandomName(){
        int ran1 = MathUtils.random(1, 1000);
        int ran2 = MathUtils.random(5000, 9999);
        String name = "player" + ran1 + ran2;
        return name;
    }
}
