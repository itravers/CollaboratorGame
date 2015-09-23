package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.Player.Player;
import com.mygdx.input.InputManager;

/**
 * Created by Isaac Assegai on 8/13/2015.
 */
public class GameWorld implements ContactListener{
    public MyGdxGame parent;  /* Parent */

    /* Game Manager Classes. */
    private AnimationManager animationManager; /* Tracks all animations. */
    private LevelManager levelManager; /* Manages Level Changes. */
    private InputManager inputManager; /* Manages user input. */
    private RenderManager renderManager; /* Manages all rendering options. */

    float scale;
    float baseZoom;

    // Player Related Fields
    private String playerName;

    // Constant Fields
    public final float PIXELS_TO_METERS = 10f;
    public final short CATEGORY_PLAYER = -1;
    public final short CATEGORY_PLANET = -2;

    /**
     * Creates a new game world. Sets up all needed pieces.
     * @param p The Parent wrapper of the game world.
     */
    public GameWorld(MyGdxGame p){
        parent = p;
        calculateScaling();
        animationManager = new AnimationManager(this); //needs to be done before level manager
        levelManager = new LevelManager(this); //before render manager

        renderManager = new RenderManager(this);
        inputManager = new InputManager(this);
        //navButtonProcessor = new NavButtonProcessor();

    }

    private void calculateScaling(){
        scale = Gdx.graphics.getWidth()/parent.developmentWidth; //scale is based on what the game was developed in.
        baseZoom = 1/scale; //base zoom is the reciprocal of scale
    }


    /**
     * Depending on the game state this will render either the pregame, ingame or postgame
     * @param elapsedTime The elapsed Time
     */
    public void render(float elapsedTime){
        renderManager.render(elapsedTime);
    }


    /**
     * Updates the player
     * @param elapsedTime The time passed
     */
    public void updatePlayer(float elapsedTime) {
        levelManager.getPlayer().update(elapsedTime);
    }

    /**
     * Updates the planets
     * @param elapsedTime The time passed
     */
    public void updatePlanets(float elapsedTime){
        for(int i = 0; i < levelManager.getPlanets().size(); i++){
            levelManager.getPlanets().get(i).update(elapsedTime);
        }
    }

    /**
     * Update the ghosts
     * @param elapsedTime The time passed
     */
    public void updateGhosts(float elapsedTime){
        //System.out.println("numGhosts: " + ghosts.size());
        for(int i = 0; i < levelManager.getGhosts().size(); i++){
            levelManager.getGhosts().get(i).update(elapsedTime);
        }
    }

    /**
     * Called by menu when game starts
     * @param name
     */
    public void setPlayerName(String name){
        //Gdx.input.setInputProcessor(inputManager);
        //setup the input manager whenever player name gets set, this isn't a good logical place for this
        //but right now is the only place I can find all the items i need to multiplex
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(inputManager);
        multiplexer.addProcessor(renderManager.stage);
       // multiplexer.addProcessor(navButtonProcessor);
        Gdx.input.setInputProcessor(multiplexer);
        playerName = name;
        levelManager.getNameLabel().setText(playerName);
        levelManager.getElapsedTimeLabel().setPosition(
                Gdx.graphics.getWidth() - levelManager.getElapsedTimeLabel().getWidth(),
                levelManager.getNameLabel().getY());
    }

    /**
     * Returns the players name.
     * @return The players name.
     */
    public String getPlayerName(){
        return playerName;
    }

    @Override
    public void beginContact(Contact contact) {
        Object a = contact.getFixtureA().getBody().getUserData();
        Object b = contact.getFixtureB().getBody().getUserData();
        if((a instanceof Planet && b instanceof Player) || (b instanceof Planet && a instanceof Player)){
            //First assign player to s
            Player s; //s for ship
            if(a instanceof Player){
                s = (Player)a;
            }else{
                s = (Player)b;
            }

            //Then assign the planet to p
            Planet p; //p for planet
            if(a instanceof Planet){
                p = (Planet)a;
            }else{
                p = (Planet)b;
            }

            //then decide what to do based on the Players state.
            /* We were flying, now we've hit a planet, so we are either landing or crashing.
             * In order to land the player must be faced generally away from the planet
             * and we must stay under a certain speed*/
            if(s.getCurrentState() == Player.STATE.FLYING){
                if(didPlayerCrashIntoPlanet(s, p)){
                    s.setCurrentState(Player.STATE.EXPLOADING);
                }else{
                    s.setCurrentState(Player.STATE.LANDED);
                    levelManager.checkGoal(p, s);
                }
            }else if(s.getCurrentState() == Player.STATE.LANDED){

            }else if(s.getCurrentState() == Player.STATE.EXPLOADING){
                //we don't need to do anything here, we transition elsewhere
            }else if(s.getCurrentState() == Player.STATE.DEAD){
                //we don't need to do anything here, we transition elsewhere
            }
        }
    }

    /**
     * Checks to see if the given player - s crashed into the given
     * planet - p during the collision that called this function.
     * If s is facing the opposite direction as p and s' velocity
     * is slow enough
     * @param s The Player that we are checking
     * @param p The Planet that we are checking
     * @return True if the player crashed, false if not.
     */
    private boolean didPlayerCrashIntoPlanet(Player s, Planet p){
        boolean returnVal = false;
        Vector2 playerPos = new Vector2(s.getBody().getPosition());
        Vector2 planetPos = new Vector2(p.getBody().getPosition());
        Vector2 planetToPlayer = playerPos.cpy().sub(planetPos);
        Vector2 playerDir = new Vector2(MathUtils.cos(s.getBody().getAngle()), MathUtils.sin(s.getBody().getAngle()));
        playerDir = playerDir.rotate(90);
        float angleDif = planetToPlayer.angle(playerDir);
        angleDif = Math.abs(angleDif);
        if(angleDif >= 45f || 360 - angleDif <= 45){
            returnVal = true;
        }else{
            /*the player hit the planet while facing the opposite direction.
              We now need to check if the player was going slow enough */
            float speed = levelManager.getPlayer().getBody().getLinearVelocity().len();
            if(speed > levelManager.getPlayer().CRASH_VELOCITY){
                returnVal = true;
            }else{
                returnVal = false;
            }

        }

        return returnVal;
    }

    @Override
    public void endContact(Contact contact) {
        Object a = contact.getFixtureA().getBody().getUserData();
        Object b = contact.getFixtureB().getBody().getUserData();
        if((a instanceof Planet && b instanceof Player) || (b instanceof Planet && a instanceof Player)){
            //System.out.println("Ended collision with planet");
            //First assign player to s
            Player s; //s for ship
            if(a instanceof Player){
                s = (Player)a;
            }else{
                s = (Player)b;
            }

            //Then assign the planet to p
            Planet p; //p for planet
            if(a instanceof Planet){
                p = (Planet)a;
            }else{
                p = (Planet)b;
            }

            //we could transition states here from landed to flying, but we want to
            // that in the player.render itself.
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public void setLevelManager(LevelManager levelManager) {
        this.levelManager = levelManager;
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public void setInputManager(InputManager inputManager) {
        this.inputManager = inputManager;
    }

    public RenderManager getRenderManager() {
        return renderManager;
    }

    public void setRenderManager(RenderManager renderManager) {
        this.renderManager = renderManager;
    }

    public AnimationManager getAnimationManager() {
        return animationManager;
    }

    public void setAnimationManager(AnimationManager animationManager) {
        this.animationManager = animationManager;
    }

    /*public NavButtonProcessor getNavButtonProcessor() {
        return navButtonProcessor;
    }

    public void setNavButtonProcessor(NavButtonProcessor navButtonProcessor) {
        this.navButtonProcessor = navButtonProcessor;
    }*/
}
