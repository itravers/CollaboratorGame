package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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

    public GameMenu(GameWorld p, SpriteBatch b){
        float devWidth = p.parent.developmentWidth;
        float scale = Gdx.graphics.getWidth()/devWidth;
        System.out.println("init gamemenu at scale: " + scale);
        float labelWidth = Gdx.graphics.getWidth()/2.5f;
        float labelHeight = Gdx.graphics.getHeight()/30;
        parent = p;
        batch = b;
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        stage = new Stage();

        //skin.getFont().
        nameLabel = new Label("Your Name", skin, "default");
        nameLabel.setWidth(labelWidth);
        nameLabel.setHeight(labelHeight);
        nameLabel.setAlignment(1);
        nameLabel.setPosition(Gdx.graphics.getWidth() / 2 - labelWidth / 2, Gdx.graphics.getHeight() / 2 + labelHeight * 4);

        textField = new TextField("", skin, "default");
        textField.addListener(new InputListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("event happens");
                textField.clear();
            }
        });

        textField.setWidth(labelWidth);
        textField.setHeight(labelHeight*2);
        textField.setAlignment(1); /* Center */
        textField.getStyle().fontColor = Color.GREEN;
        //textField.getStyle().font.getData().setScale(scale, scale);
        textField.setPosition(Gdx.graphics.getWidth() / 2 - labelWidth/2, Gdx.graphics.getHeight() / 2 + labelHeight*2);
        button = new TextButton("Start Game", skin, "default");
        button.setWidth(labelWidth);
        button.setHeight(labelHeight);
        button.setPosition(Gdx.graphics.getWidth() / 2 - labelWidth/2, Gdx.graphics.getHeight() / 2 + labelHeight*1);
        button.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                button.setText("Starting Game");
                setName();
                parent.parent.setFrameNum(0);
                parent.parent.setGameState(MyGdxGame.GAME_STATE.INGAME);
                parent.getLevelManager().nextLevel();
            }
        });


        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);

        //change font size
        nameLabel.getStyle().font.getData().setScale(.5f, .5f);


        stage.addActor(nameLabel);
        stage.addActor(textField);
        stage.addActor(button);
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
