package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.Player.Player;

/**
 * This class is resposible for all rendering calls, and fields
 * Created by Isaac Assegai on 8/17/2015.
 */
public class RenderManager {
    GameWorld parent;

    // Rendering Related Fields
    private SpriteBatch batch;
    private SpriteBatch backGroundBatch;
    private OrthographicCamera camera; //drawing game pieces
    private ShapeRenderer shapeRenderer;

    private ParallaxCamera backgroundCamera; //drawing sprites
    private Matrix4 debugMatrix;

    private Box2DDebugRenderer debugRenderer;

    private boolean drawSprite = true;

    //UI Related Fields
    private Skin skin;
    private Stage stage; //for drawing ui

    public RenderManager(GameWorld parent){
        this.parent = parent;
        setupRendering();
    }



    /**
     * Prepare objects for rendering.
     */
    private void setupRendering(){
        setupUI();

        batch = new SpriteBatch();
        backGroundBatch = new SpriteBatch();
       // parent.getLevelManager().setupBackground();
        parent.getLevelManager().setupMenu(this.parent, batch);
        parent.getAnimationManager().setupAnimations();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundCamera = new ParallaxCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(camera.combined);
        backGroundBatch.setProjectionMatrix(backgroundCamera.combined);
        shapeRenderer = new ShapeRenderer();
    }

    /**
     * Creates the UI Overlay HUD. This doesn't change per level
     */
    private void setupUI(){
        // layout.setText(font, playerName);
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        stage = new Stage();
        parent.getLevelManager().setupUI(skin, stage);
    }

    private void renderMidGame(float elapsedTime){
        camera.position.set(parent.getLevelManager().getPlayer().getX(), parent.getLevelManager().getPlayer().getY(), 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        backgroundCamera.position.set(parent.getLevelManager().getPlayer().getX(), parent.getLevelManager().getPlayer().getY(), 0);
        backgroundCamera.update();
        backGroundBatch.setProjectionMatrix(backgroundCamera.combined);
        parent.getLevelManager().getWorld().step(1f / 60f, 6, 2);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(parent.PIXELS_TO_METERS, parent.PIXELS_TO_METERS, 0);
        renderBackground(elapsedTime, backGroundBatch); //Should be done before other renders
        batch.begin();
        stage.draw();
        batch.end();
    }

    /**
     * Renders the game itself
     * @param elapsedTime The time passed
     */
    private void renderInGame(float elapsedTime) {
        camera.position.set(parent.getLevelManager().getPlayer().getX(), parent.getLevelManager().getPlayer().getY(), 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        backgroundCamera.position.set(parent.getLevelManager().getPlayer().getX(), parent.getLevelManager().getPlayer().getY(), 0);
        backgroundCamera.update();
        backGroundBatch.setProjectionMatrix(backgroundCamera.combined);
        parent.getLevelManager().getWorld().step(1f / 60f, 6, 2);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(parent.PIXELS_TO_METERS, parent.PIXELS_TO_METERS, 0);
        renderBackground(elapsedTime, backGroundBatch); //Should be done before other renders
        renderGoal(elapsedTime, batch);
        batch.begin();
        if(drawSprite){ /* Draw sprites if true */

            renderPlanets(elapsedTime, batch);

            renderGhosts(elapsedTime, batch);
            renderPlayer(elapsedTime, batch);


        }
        batch.end();

        renderUI(elapsedTime, batch); /* this needs to be after batch.end */
        if(!drawSprite) debugRenderer.render(parent.getLevelManager().getWorld(), debugMatrix); /* Render box2d physics items */

        //Update after rendering, this will be rendered next frame
        parent.updatePlayer(elapsedTime);
        parent.updatePlanets(elapsedTime);
        parent.updateGhosts(elapsedTime);
    }

    private void renderGoal(float elapsedTime, SpriteBatch batch){
        Sprite g = parent.getLevelManager().getGoal(); /* This object is our goal. */
        Player p = parent.getLevelManager().getPlayer();
        if(g instanceof Planet){
            Planet goal = (Planet)g;
            float goalRadius = goal.getRadiusFromMass(goal.getMass());
            Vector2 startPos = new Vector2(p.getX()+p.getWidth()/2, p.getY()+p.getHeight()/2);
            Vector2 goalPos = new Vector2(goal.getX()+goalRadius/2, goal.getY()+goalRadius/2);
            Vector2 endLine = goalPos.cpy().sub(startPos); //get difference vector
            Vector2 perpLine1 = endLine.cpy().rotate(135f); //get rotated difference vector
            Vector2 perpLine2 = endLine.cpy().rotate(-135f); //get rotated difference vector


            endLine.setLength(35f); // set length of distance vector
            perpLine1.setLength(10f); //set length of perpLineVector
            perpLine2.setLength(10f); //set length of perpLineVector

            endLine = startPos.cpy().add(endLine); //convert back to point
            perpLine1 = endLine.cpy().add(perpLine1); // convert back to point
            perpLine2 = endLine.cpy().add(perpLine2); // convert back to point


            if(g != null){ /* The goal can be null, make sure it isn't here. */
                //shapeRenderer.setProjectionMatrix(camera.combined);
                shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
                shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
                shapeRenderer.setColor(Color.RED);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.line(startPos, endLine);
                shapeRenderer.line(endLine, perpLine1);
                shapeRenderer.line(endLine, perpLine2);
               // shapeRenderer.li
                shapeRenderer.circle((goalPos.x), (goalPos.y), goalRadius/2);
                shapeRenderer.end();
            }
        }

    }


    /**
     * Depending on the game state this will render either the pregame, ingame or postgame
     * @param elapsedTime The elapsed Time
     */
    public void render(float elapsedTime){
        int level = parent.getLevelManager().getLevel();
        switch (level){
            case 0: //Pregame menu
                renderPreGame(elapsedTime);
                break;
            case 1: //Level 1
                if(parent.parent.getGameState() == MyGdxGame.GAME_STATE.INGAME){
                    renderInGame(elapsedTime);
                }else if(parent.parent.getGameState() == MyGdxGame.GAME_STATE.MIDGAME){
                    renderMidGame(elapsedTime);
                }
                break;
            case 2: //Level 2
                if(parent.parent.getGameState() == MyGdxGame.GAME_STATE.INGAME){
                    renderInGame(elapsedTime);
                }else if(parent.parent.getGameState() == MyGdxGame.GAME_STATE.MIDGAME){
                    renderMidGame(elapsedTime);
                }
                break;
        }
    }

    /**
     * Renders the menu's that allows the player to start the game.
     * @param elapsedTime The time passed.
     */
    private void renderPreGame(float elapsedTime){
        //camera.position.set(parent.getLevelManager().getPlayer().getX(), parent.getLevelManager().getPlayer().getY(), 0);
        camera.position.set(0, 0, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        backgroundCamera.position.set(0, 0, 0);
        backgroundCamera.update();
        backGroundBatch.setProjectionMatrix(backgroundCamera.combined);
        renderBackground(elapsedTime, backGroundBatch);
        parent.getLevelManager().getMenu().render(elapsedTime);

    }

    private void renderBackground(float elapsedTime, SpriteBatch b){
        parent.getLevelManager().getBackground().render(elapsedTime, b);
    }

    /**
     * Renders the player
     * @param elapsedTime The time passed
     * @param batch The SpriteBatch we render with.
     */
    private void renderPlayer(float elapsedTime, SpriteBatch batch){
        parent.getLevelManager().getPlayer().render(elapsedTime, batch);
    }

    /**
     * Render the planets
     * @param elapsedTime The time passed
     * @param batch The SpriteBatch we render with
     */
    private void renderPlanets(float elapsedTime, SpriteBatch batch){
        for(int i = 0; i < parent.getLevelManager().getPlanets().size(); i++) {
            parent.getLevelManager().getPlanets().get(i).render(elapsedTime, batch);
        }
    }

    /**
     * Render the players ghosts
     * @param elapsedTime The time passed
     * @param batch The SpriteBatch we render with
     */
    private void renderGhosts(float elapsedTime, SpriteBatch batch){
        for(int i = 0; i < parent.getLevelManager().getGhosts().size(); i++){
            parent.getLevelManager().getGhosts().get(i).render(elapsedTime, batch);
        }
    }

    /**
     * Render the UI Overlay
     * @param elapsedTime The time passed.
     * @param batch The SpriteBatch we render with.
     */
    private void renderUI(float elapsedTime, SpriteBatch batch){
        parent.getLevelManager().getElapsedTimeLabel().setText(new Float(elapsedTime).toString());
        parent.getLevelManager().getPlayerStateLabel().setText(parent.getLevelManager().getPlayer().getCurrentState().toString());
        parent.getLevelManager().getPlayerSpeedLabel().setText(new Float(parent.getLevelManager().getPlayer().getBody().getLinearVelocity().len()).toString());
        stage.draw();
    }

    /**
     * Renders the score sheet, etc.
     * @param elapsedTime The time elapsed
     */
    private void renderPostGame(float elapsedTime){

    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public ParallaxCamera getBackgroundCamera() {
        return backgroundCamera;
    }

    public void setBackgroundCamera(ParallaxCamera backgroundCamera) {
        this.backgroundCamera = backgroundCamera;
    }

    public Box2DDebugRenderer getDebugRenderer() {
        return debugRenderer;
    }

    public void setDebugRenderer(Box2DDebugRenderer debugRenderer) {
        this.debugRenderer = debugRenderer;
    }

    public boolean isDrawSprite() {
        return drawSprite;
    }

    public void setDrawSprite(boolean drawSprite) {
        this.drawSprite = drawSprite;
    }



}
