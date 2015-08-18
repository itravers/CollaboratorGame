package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.Player.Player;
import input.InputManager;

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
        levelManager = new LevelManager(this);
        animationManager = new AnimationManager(this);
        renderManager = new RenderManager(this);
        inputManager = new InputManager(this);
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
        levelManager.checkGoal();
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
        Gdx.input.setInputProcessor(inputManager);
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
        Vector2 v_planetToPlayer = s.getBody().getPosition().sub(p.getBody().getPosition());
        float f_planetToPlayer = v_planetToPlayer.angle();
        float f_playerDir = (float)Math.toDegrees(s.getBody().getAngle());
        f_planetToPlayer = f_planetToPlayer % 360; //limit to 360 degrees
        f_playerDir = f_playerDir % 360; //limit to 360 degrees.
        float angleDif = Math.abs(f_planetToPlayer - f_playerDir);
        angleDif = angleDif % 360;

        //System.out.println("planetToPlayer: " + f_planetToPlayer + " playerDir: " + f_playerDir + " angleDif: " + angleDif);

        if(45 >= angleDif || 135 <= angleDif){
           // System.out.print(" angle is bad ");
            //the player hit the planet while facing the planet, it crashed
            //s.setCurrentState(Player.STATE.EXPLOADING);
            returnVal = true;
        }else{
            //System.out.print(" angle is good ");
            /*the player hit the planet while facing the opposite direction.
              We now need to check if the player was going slow enough */
            float speed = levelManager.getPlayer().getBody().getLinearVelocity().len();
            //System.out.println("speed: " +speed);
            if(speed > levelManager.getPlayer().MAX_VELOCITY/3){
                //System.out.print(" speed is bad ");
                //s.setCurrentState(Player.STATE.EXPLOADING);
                returnVal = true;
            }else{
               // System.out.print(" speed is good ");
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
}
