package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
   // public ScalingViewport viewport;

    private float baseZoom;
    private float cameraZoom;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera shapeCamera; // need this because other cameras zoom

    private ParallaxCamera backgroundCamera; //drawing sprites
    private Matrix4 debugMatrix;

    private Box2DDebugRenderer debugRenderer;

    private boolean drawSprite = true;
    private FPSLogger fpsLogger;



    //UI Related Fields
    private Skin skin;
    public Stage stage; //for drawing ui
    public float scale; //used for determining sizes of things based on device width

    public RenderManager(GameWorld parent){
        this.parent = parent;
        setupScaling();
        setupRendering();
    }

    private void setupScaling(){
        scale = parent.scale;
        baseZoom = parent.baseZoom;
    }

    /**
     * Prepare objects for rendering.
     */
    private void setupRendering(){
        System.out.println("setup Rendering");
        System.out.println("W x H: " + Gdx.graphics.getWidth() + " x " + Gdx.graphics.getHeight());
        fpsLogger = new FPSLogger();
        setupUI();

        batch = new SpriteBatch();
        backGroundBatch = new SpriteBatch();
       // parent.getLevelManager().setupBackground();
        parent.getLevelManager().setupMenu(this.parent, batch);
        parent.getAnimationManager().setupAnimations();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundCamera = new ParallaxCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(camera.combined);
        backGroundBatch.setProjectionMatrix(backgroundCamera.combined);
        shapeRenderer = new ShapeRenderer();
        setupScreenSizeDependantItems();
    }

    //Several things in the game are going to be dependant on the users screen size
    //zoom
    //gui size
    private void setupScreenSizeDependantItems(){
        if(Gdx.graphics.getWidth() <= 480){
            baseZoom = 1f;
        }else if(Gdx.graphics.getWidth() >= 900){
            baseZoom = .5f;
        }
        setCameraZoom(baseZoom);
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
        renderHUD(elapsedTime, batch);
        if(!drawSprite) debugRenderer.render(parent.getLevelManager().getWorld(), debugMatrix); /* Render box2d physics items */

        //Update after rendering, this will be rendered next frame
        parent.updatePlayer(elapsedTime);
        parent.updatePlanets(elapsedTime);
        parent.updateGhosts(elapsedTime);
    }

    private void renderHUD(float elapsedTime, SpriteBatch batch){
        Vector2 topMiddleScreen = new Vector2(parent.getLevelManager().getPlayer().getX()+parent.getLevelManager().getPlayer().getWidth()/2,
                parent.getLevelManager().getPlayer().getY()+Gdx.graphics.getHeight()/2+parent.getLevelManager().getPlayer().getHeight()/1);
        drawHealthmeter(elapsedTime, batch, topMiddleScreen);
        drawBoostmeter(elapsedTime, batch, topMiddleScreen);
        drawSpeedometer(elapsedTime, batch, topMiddleScreen);

    }

    private void drawBoostmeter(float elapsedTime, SpriteBatch batch, Vector2 topMiddleScreen){
        float boxWidth = (Gdx.graphics.getWidth()/2)-73f;
        float boxHeight = 36f*scale;
        float boxRight = topMiddleScreen.x+Gdx.graphics.getWidth()/2+parent.getLevelManager().getPlayer().getWidth()/2;
        float boxTop = topMiddleScreen.y - boxHeight;
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.setProjectionMatrix(shapeCamera.combined);
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //draw the box
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.box(boxRight - boxWidth, boxTop, 0, boxWidth, boxHeight, 0);
        shapeRenderer.arc(boxRight - boxWidth, topMiddleScreen.y, 36f, 270, -45);

        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.box(boxRight - boxWidth + 3, boxTop + 3, 0, boxWidth - 6, boxHeight - 6, 0);
        shapeRenderer.arc(boxRight - boxWidth + 3, topMiddleScreen.y - 3, 30f, 270, -45);
        shapeRenderer.end();
    }

    private void drawHealthmeter(float elapsedTime, SpriteBatch batch, Vector2 topMiddleScreen){
        //for testing we are going to tie the health meter to the player speed, in effect turning it into a speedometer as well.

        float boxWidth = ((Gdx.graphics.getWidth()/2)-73f*scale);
        float boxHeight = 35 * scale;
        float boxLeft = topMiddleScreen.x-Gdx.graphics.getWidth()/2+parent.getLevelManager().getPlayer().getWidth()/2;
        float boxTop = topMiddleScreen.y - boxHeight;
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.setProjectionMatrix(shapeCamera.combined);
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //draw the box
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.box(boxLeft, boxTop, 0, boxWidth, boxHeight, 0);
        shapeRenderer.arc(boxLeft + boxWidth, topMiddleScreen.y, 36f, 270, 40);

        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.box(boxLeft + 3, boxTop + 3, 0, boxWidth - 6, boxHeight - 6, 0);
        shapeRenderer.arc(boxLeft + 3 + boxWidth - 6, topMiddleScreen.y - 3, 30f, 270, 45);
       // shapeRenderer.arc(boxLeft+boxWidth, topMiddleScreen.y, 36f, 270, 45);
        shapeRenderer.end();
    }

    private void drawSpeedometer(float elapsedTime, SpriteBatch batch, Vector2 topMiddleScreen){
        //A vector that tells us where the top of the screen is, from the players position,
        Vector2 speedoMeterPos = topMiddleScreen.cpy().add(parent.getLevelManager().getPlayer().getWidth()/2,0);

        float speedometerRadius = 75*scale;
        Color speedometerColor = getSpeedometerColor();
        Color seperatingLineColor = getSeperatingLineColor(speedometerColor);
        float speed = parent.getLevelManager().getPlayer().getBody().getLinearVelocity().len();

        //calculate seperating line
        Vector2 n0 = new Vector2(speedoMeterPos.x-speedometerRadius+1, speedoMeterPos.y);
        Vector2 l0 = n0.cpy().sub(speedoMeterPos);
        float maxV = parent.getLevelManager().getPlayer().MAX_VELOCITY;
        float crashV = parent.getLevelManager().getPlayer().CRASH_VELOCITY;
        float rotation = (crashV * 180) / maxV;
        Vector2 l1 = l0.cpy().rotate(rotation);
        Vector2 n1 = speedoMeterPos.cpy().add(l1);

        //calculate speedometer line
        rotation = (speed * 180) / maxV;
        l1 = l0.cpy().rotate(rotation);
        Vector2 s1 = speedoMeterPos.cpy().add(l1);


        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.setProjectionMatrix(shapeCamera.combined);
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //draw the stroke circle
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(speedoMeterPos.x, speedoMeterPos.y, speedometerRadius + 5);

        //draw the main circle
        shapeRenderer.setColor(speedometerColor);
        shapeRenderer.circle(speedoMeterPos.x, speedoMeterPos.y, speedometerRadius);

        //draw a line seperating the main circle depending on MaxVelocities ratio to Crash Velocity
        shapeRenderer.setColor(seperatingLineColor);
        shapeRenderer.rectLine(speedoMeterPos, n1, 3*scale);

        //draw the speedometer line, showing us how fast we go.
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rectLine(speedoMeterPos, s1, 4*scale);

        shapeRenderer.end();

        //float speed = parent.getLevelManager().getPlayer().getBody().getLinearVelocity().len();
        /*
        Label lSpeed =  parent.getLevelManager().getPlayerSpeedLabel();
        lSpeed.setScale(scale, scale);
        float xPos = (Gdx.graphics.getWidth() / 2 - lSpeed.getWidth() / 2)+parent.getLevelManager().getPlayer().getWidth()/1;
        float yPos = Gdx.graphics.getHeight() - lSpeed.getHeight()-(1*scale);
        lSpeed.setPosition(xPos, yPos);

        lSpeed.setText(String.format("%.02f", speed));
        */
        stage.draw();
    }

    /**
     * Decides the color of the speedometer line based on the color of the speedometer
     * if the speedometer is green or blue the line will be red
     * if the speedometer is red, than the line will be green.
     */
    private Color getSeperatingLineColor(Color c){
        Color lineColor;
        if(c == Color.GREEN || c == Color.BLUE){
            lineColor = Color.RED;
        }else{
            lineColor = Color.GREEN;
        }
        return lineColor;
    }

    /**
     * The Speedometer color depends on the relationship of the players current speed
     * to it's crash velocity. If the player is more than 1m/s less than crash velocity
     * the speedometer will be green, if the player is within +-1m/s of crash velocity
     * than the speedometer will be yellow. If the player is +1m/s to crash velocity
     * than the meter is red.
     * @return
     */
    private Color getSpeedometerColor(){
        Color c;
        float speed = parent.getLevelManager().getPlayer().getBody().getLinearVelocity().len();
        float crashSpeed = parent.getLevelManager().getPlayer().CRASH_VELOCITY;
        if(speed < crashSpeed - 1){
            c = Color.GREEN;//new Color(41f, 255f, 41f, 1f);
           // c = new Color(0, 75, 255, 133);
        }else if(speed > crashSpeed + 1){
            c = Color.RED;
        }else{
            c = Color.BLUE;
        }
        return c;
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
        fpsLogger.log();
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
            //render the level background.
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
        //stage.draw();
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
       // shapeCamera.zoom = cameraZoom;
    }

    //public void resize(int w, int h){
    //    viewport.update(w, h);
    //}


    public OrthographicCamera getCamera() {
        return camera;
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }


    public float getBaseZoom() {
        return baseZoom;
    }

    public void setBaseZoom(float baseZoom) {
        this.baseZoom = baseZoom;
    }


}
