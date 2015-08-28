package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.Player.Player;
import com.sun.prism.j2d.paint.RadialGradientPaint;

import java.awt.Point;

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

    private float cameraZoom;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera shapeCamera; // need this because other cameras zoom

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
        shapeCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraZoom = 1;
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
        Player p = parent.getLevelManager().getPlayer();
        shapeCamera.position.set(p.getX() + p.getWidth(),
                p.getY() + p.getHeight(), 0);
        shapeCamera.update();
        camera.position.set(p.getX() + p.getWidth(),
                p.getY() + p.getHeight(), 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        backgroundCamera.position.set(p.getX() + p.getWidth(),
                p.getY() + p.getHeight(), 0);
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
        Player p = parent.getLevelManager().getPlayer();
        shapeCamera.position.set(p.getX() + p.getWidth(),
                p.getY() + p.getHeight(), 0);
        shapeCamera.update();
        camera.position.set(p.getX() + p.getWidth(),
                p.getY() + p.getHeight(), 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        backgroundCamera.position.set(p.getX() + p.getWidth(),
                p.getY() + p.getHeight(), 0);
        backgroundCamera.update();
        backGroundBatch.setProjectionMatrix(backgroundCamera.combined);
        parent.getLevelManager().getWorld().step(1f / 60f, 6, 2);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(parent.PIXELS_TO_METERS, parent.PIXELS_TO_METERS, 0);
        renderBackground(elapsedTime, backGroundBatch); //Should be done before other renders

        batch.begin();
        if(drawSprite){ /* Draw sprites if true */
            renderPlanets(elapsedTime, batch);
            renderGhosts(elapsedTime, batch);
            renderPlayer(elapsedTime, batch);
        }
        batch.end();

        renderGoal(elapsedTime, batch);
        renderClosest(elapsedTime, batch);
        renderGravityIndicator(elapsedTime, batch);
        renderUI(elapsedTime, batch); /* this needs to be after batch.end */
        if(!drawSprite) debugRenderer.render(parent.getLevelManager().getWorld(), debugMatrix); /* Render box2d physics items */

        //Update after rendering, this will be rendered next frame
        parent.updatePlayer(elapsedTime);
        parent.updatePlanets(elapsedTime);
        parent.updateGhosts(elapsedTime);
    }

    private void renderGravityIndicator(float elapsedTime, SpriteBatch batch){
        Player p = parent.getLevelManager().getPlayer();
        Vector2 gravityForce = p.getGravityForce();
        Vector2 startPos = new Vector2(p.getX() + p.getWidth() / 2, p.getY() + p.getHeight() / 2);
        Vector2 endPos = gravityForce.cpy().sub(startPos);

        Vector2 perpLine1 = endPos.cpy().rotate(135f); //get rotated difference vector
        Vector2 perpLine2 = endPos.cpy().rotate(-135f); //get rotated difference vector

        endPos.setLength(125f); // set length of distance vector
        perpLine1.setLength(30f); //set length of perpLineVector
        perpLine2.setLength(30f); //set length of perpLineVector

        endPos = startPos.cpy().add(endPos); //convert back to point
        perpLine1 = endPos.cpy().add(perpLine1); // convert back to point
        perpLine2 = endPos.cpy().add(perpLine2); // convert back to point

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.setProjectionMatrix(shapeCamera.combined);
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.line(startPos, endPos);
       // shapeRenderer.line
        shapeRenderer.rectLine(endPos, perpLine1, 3);
        shapeRenderer.rectLine(endPos, perpLine2, 3);
        shapeRenderer.end();
    }

    /**
     * Renders an arrow pointing to the closest planet, and a counter
     * telling how many meters to it's surface.
     */
    private void renderClosest(float elapsedTime, SpriteBatch batch){
        Player p = parent.getLevelManager().getPlayer();
        Planet closestPlanet = p.getClosestPlanet();
        if(closestPlanet != null){
            Vector2 startPos = new Vector2(p.getX() + p.getWidth() / 2, p.getY() + p.getHeight() / 2);
            Vector2 goalPos = new Vector2(closestPlanet.getX() + closestPlanet.getRadius()/2, closestPlanet.getY() + closestPlanet.getRadius() /2);
            Vector2 endLine = goalPos.cpy().sub(startPos); //get difference vector
            Vector2 perpLine1 = endLine.cpy().rotate(135f); //get rotated difference vector
            Vector2 perpLine2 = endLine.cpy().rotate(-135f); //get rotated difference vector

            endLine.setLength(90f); // set length of distance vector
            perpLine1.setLength(10f); //set length of perpLineVector
            perpLine2.setLength(10f); //set length of perpLineVector

            endLine = startPos.cpy().add(endLine); //convert back to point
            perpLine1 = endLine.cpy().add(perpLine1); // convert back to point
            perpLine2 = endLine.cpy().add(perpLine2); // convert back to point

            shapeRenderer.setProjectionMatrix(shapeCamera.combined);
            shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            //shapeRenderer.line(startPos, goalPos);
            shapeRenderer.rectLine(endLine, perpLine1, 3);
            shapeRenderer.rectLine(endLine, perpLine2, 3);
            shapeRenderer.end();
        }
    }

    private void renderGoal(float elapsedTime, SpriteBatch batch){
        Sprite g = parent.getLevelManager().getGoal(); /* This object is our goal. */
        Player p = parent.getLevelManager().getPlayer();

        if(g instanceof Planet){ //D
            Planet goal = (Planet)g;
           // float goalRadius = goal.getRadiusFromMass(goal.getMass());
            float goalRadius = goal.getRadius();
            Vector2 startPos = new Vector2(p.getX()+p.getWidth()/2, p.getY()+p.getHeight()/2);
            Vector2 goalPos = new Vector2(goal.getX()+goalRadius/2, goal.getY()+goalRadius/2);
            Vector2 endLine = goalPos.cpy().sub(startPos); //get difference vector
            Vector2 perpLine1 = endLine.cpy().rotate(135f); //get rotated difference vector
            Vector2 perpLine2 = endLine.cpy().rotate(-135f); //get rotated difference vector


            endLine.setLength(105f); // set length of distance vector
            perpLine1.setLength(15f); //set length of perpLineVector
            perpLine2.setLength(15f); //set length of perpLineVector

            endLine = startPos.cpy().add(endLine); //convert back to point
            perpLine1 = endLine.cpy().add(perpLine1); // convert back to point
            perpLine2 = endLine.cpy().add(perpLine2); // convert back to point


            if(g != null){ /* The goal can be null, make sure it isn't here. */
                //

                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setProjectionMatrix(shapeCamera.combined);
                shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
                shapeRenderer.setColor(Color.GREEN);

               //shapeRenderer.line(startPos, endLine);
                shapeRenderer.rectLine(endLine, perpLine1, 3);
                shapeRenderer.rectLine(endLine, perpLine2, 3);
                shapeRenderer.end();






                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
               // shapeRenderer.set(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setProjectionMatrix(camera.combined);

               /* RadialGradientPaint rgp = new RadialGradientPaint(
                        new Point((int)(0+0 / 2), (int) (0 + 0 / 2)),
                        (float) 0,
                        new float[]{.01f, .5f},
                        new java.awt.Color[]{java.awt.Color.GREEN, java.awt.Color.GREEN}
                );
                */



                shapeRenderer.circle((goalPos.x), (goalPos.y), goalRadius / 2);

                shapeRenderer.setProjectionMatrix(shapeCamera.combined);
                shapeRenderer.end();
                //Gdx.gl2
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
            case 3: //Level 3
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
        if(parent.parent.getGameState() == MyGdxGame.GAME_STATE.MIDGAME){
            //render the midgame background
            parent.getLevelManager().getMidGameBackground().render(elapsedTime, b);
        }else{
            //render the level background.
            parent.getLevelManager().getBackground().render(elapsedTime, b);
        }

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


    public float getCameraZoom() {
        return cameraZoom;
    }

    public void setCameraZoom(float cameraZoom) {
        this.cameraZoom = cameraZoom;
        camera.zoom = cameraZoom;
        backgroundCamera.zoom = cameraZoom;
    }
}
