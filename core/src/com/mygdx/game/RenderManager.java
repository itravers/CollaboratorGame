package com.mygdx.game;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mygdx.Player.Player;

/**
 * This class is resposible for all rendering calls, and fields
 * Created by Isaac Assegai on 8/17/2015.
 */
public class RenderManager {
    GameWorld parent;
    Color green;
    Color red;
    Color blue;

    // Rendering Related Fields
    private SpriteBatch batch;
    private SpriteBatch backGroundBatch;

    //3d testing
    private OrthographicCamera pCamera;
    private ModelBatch modelBatch;
    private Model model;
    private ModelInstance modelInstance;
    private Environment environment;
    private AnimationController aController;

    private OrthCamera camera; //drawing game pieces
   // public ScalingViewport viewport;

    private float baseZoom;
    private float cameraZoom;
    private ShapeRenderer shapeRenderer;
    private OrthCamera shapeCamera; // need this because other cameras zoom

    private OrthCamera backgroundCamera; //drawing sprites
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

        red = new Color(193/255f, 39/255f, 45/255f, .5f);
        green = new Color(55/255f, 255/255f, 55/255f, .5f);
        blue = new Color(0/255f, 210/255f, 246/255f, .5f);
        setupUI();

        batch = new SpriteBatch();
        backGroundBatch = new SpriteBatch();
       // parent.getLevelManager().setupBackground();
        parent.getLevelManager().setupMenu(this.parent, batch);
        parent.getAnimationManager().setupAnimations();
        camera = new OrthCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        pCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      //  pCamera.position.set(0, 0, 7f);
       // pCamera.rotate(90);
        //pCamera.rotate(90,0,0,1);

       // pCamera.rotate

       // pCamera.lookAt(0, 0, 0);
        //pCamera.near = 0.1f;
        //pCamera.far = 300.0f;
        modelBatch = new ModelBatch();
        UBJsonReader jsonReader = new UBJsonReader();
        G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
        model = modelLoader.loadModel(Gdx.files.getFileHandle("data/CanMan4.g3db", Files.FileType.Internal));
        modelInstance = new ModelInstance(model);

        //maybe don't need this


        //move model down a bit
        modelInstance.transform.translate(0, -15, -60);
        modelInstance.transform.rotate(0, 1, 0, 90);

        //setup some light
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
        aController = new AnimationController(modelInstance);
        System.out.println("animations: " + modelInstance.animations);
        aController.setAnimation("Walk_Polish",1, new AnimationController.AnimationListener(){

            @Override
            public void onEnd(AnimationController.AnimationDesc animation) {
                // this will be called when the current animation is done.
                // queue up another animation called "balloon".
                // Passing a negative to loop count loops forever.  1f for speed is normal speed.
                aController.queue("Walk_Polish",-1,1f,null,0f);
            }

            @Override
            public void onLoop(AnimationController.AnimationDesc animation) {
                // TODO Auto-generated method stub

            }

        });




        shapeCamera = new OrthCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundCamera = new OrthCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
        float angle = p.getBody().getAngle();
        shapeCamera.position.set(p.getX() + p.getWidth(),
                p.getY() + p.getHeight(), 0);
        shapeCamera.update();
        shapeCamera.setToAngle(angle);
        camera.position.set(p.getX() + p.getWidth(),
                p.getY() + p.getHeight(), 0);


        camera.setToAngle(angle);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        backgroundCamera.position.set(p.getX() + p.getWidth(),
                p.getY() + p.getHeight(), 0);
        backgroundCamera.update();
        backGroundBatch.setProjectionMatrix(backgroundCamera.combined);
        parent.getLevelManager().getWorld().step(1f / 60f, 6, 2);




        //2d rendering

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
         Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
       // Gdx.gl.glClearColor(0, 0, 0, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(parent.PIXELS_TO_METERS, parent.PIXELS_TO_METERS, 0);
        renderBackground(elapsedTime, backGroundBatch); //Should be done before other renders

        batch.begin();
        if(drawSprite){
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


        //3d rendering
        // You've seen all this before, just be sure to clear the GL_DEPTH_BUFFER_BIT when working in 3D
        //Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
       // Gdx.gl.glClearColor(1, 1, 1, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        // For some flavor, lets spin our camera around the Y axis by 1 degree each time render is called
        //camera.rotateAround(Vector3.Zero, new Vector3(0,1,0),1f);
        // When you change the camera details, you need to call update();
        // Also note, you need to call update() at least once.
        pCamera.update();
        pCamera.zoom = camera.zoom / 4f;
        //camera.update();
        // You need to call update on the animation controller so it will advance the animation.  Pass in frame delta
        aController.update(Gdx.graphics.getDeltaTime());
        // Like spriteBatch, just with models!  pass in the box Instance and the environment
        modelBatch.begin(pCamera);
        modelBatch.render(modelInstance, environment);
        modelBatch.end();

        if(!drawSprite) debugRenderer.render(parent.getLevelManager().getWorld(), debugMatrix); /* Render box2d physics items */

        //Update after rendering, this will be rendered next frame
        parent.updatePlayer(elapsedTime);
        parent.updatePlanets(elapsedTime);
        parent.updateGhosts(elapsedTime);
    }

    private void renderHUD(float elapsedTime, SpriteBatch batch){
        drawHUD(stage);
        stage.draw();
    }

    private void drawHUD(Stage s){
        parent.getGuiManager().render(batch, shapeRenderer);
    }

  

    /**
     * Decides the color of the speedometer line based on the color of the speedometer
     * if the speedometer is green or blue the line will be red
     * if the speedometer is red, than the line will be green.
     */
    public Color getSeperatingLineColor(float speed){
    	Color lineColor;
    	if(speed >= parent.getLevelManager().getPlayer().CRASH_VELOCITY){
    		lineColor = green;
    	}else{
    		lineColor = red;
    	}
        
        return lineColor;
    }


    private void renderGravityIndicator(float elapsedTime, SpriteBatch batch){
        /*Player p = parent.getLevelManager().getPlayer();
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
        */
        //Instead of rendering gravity, i actually want to render a velocity direction
        Vector2 playerDir;
        Player p = parent.getLevelManager().getPlayer();
        if(p.onPlanet()){
            playerDir = new Vector2(0,0);
        }else{
            playerDir = p.getBody().getLinearVelocity();
        }


        Vector2 startPos = new Vector2(p.getX() + p.getWidth() / 2, p.getY() + p.getHealth() /2);//new Vector2(p.getX() + p.getWidth() / 2, p.getY() + p.getHealth() / 2);
        Vector2 endPos = playerDir.cpy();//.sub(startPos);

        Vector2 perpLine1 = endPos.cpy().rotate(135f); //get rotated difference vector
        Vector2 perpLine2 = endPos.cpy().rotate(-135f); //get rotated difference vector

        float length = playerDir.len()*4;

        endPos.setLength(125f); // set length of distance vector
        perpLine1.setLength(length); //set length of perpLineVector
        perpLine2.setLength(length); //set length of perpLineVector

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

            float length = p.getBody().getPosition().sub(closestPlanet.getBody().getPosition()).len();
            length = Math.abs(length);
            //length += closestPlanet.getRadius();
            length = length - closestPlanet.getRadius()/20;
            //length = 100 - length;
            length = 100-length;
          //  System.out.println("length: " + length + " rad: " + closestPlanet.getRadius()/20);
            if(length < 5)length = 5;
            if(length > 100) length = 100;
            if(p.onPlanet())length = 0;
           // System.out.println("rad: " + closestPlanet.getRadius());
           // System.out.println("length: " + length);
            endLine.setLength(90f); // set length of distance vector
            perpLine1.setLength(length); //set length of perpLineVector
            perpLine2.setLength(length); //set length of perpLineVector

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
                shapeRenderer.setColor(green);

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
                        new java.awt.Color[]{java.awt.green, java.awt.green}
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
       // fpsLogger.log();
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


    public OrthCamera getBackgroundCamera() {
        return backgroundCamera;
    }

    public void setBackgroundCamera(OrthCamera backgroundCamera) {
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

    public void setCamera(OrthCamera camera) {
        this.camera = camera;
    }


    public float getBaseZoom() {
        return baseZoom;
    }

    public void setBaseZoom(float baseZoom) {
        this.baseZoom = baseZoom;
    }


}
