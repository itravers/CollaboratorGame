package com.mygdx.input;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.GameWorld;
import com.mygdx.game.MyGdxGame;


/**
 * Created by slack on 8/14/2015.
 */
public class InputManager implements InputProcessor{
    GameWorld parent;
    public InputManager(GameWorld parent){
        this.parent = parent;
    }
    /**
     * Listens for key presses.
     * @param keycode The code of the key pressed
     * @return returns true when done. False if there is a problem.
     */
    @Override
    public boolean keyDown(int keycode) {

        Vector2 vel = parent.getLevelManager().getPlayer().getBody().getLinearVelocity();
        float angularVelocity = parent.getLevelManager().getPlayer().getBody().getAngularVelocity();
        if(keycode == Input.Keys.W && vel.dst2(vel) <= parent.getLevelManager().getPlayer().MAX_VELOCITY){
            parent.getLevelManager().getPlayer().forwardPressed  = true;
        }
        if(keycode == Input.Keys.S && vel.dst2(vel) <= parent.getLevelManager().getPlayer().MAX_VELOCITY){
            parent.getLevelManager().getPlayer().backwardPressed = true;
        }
        if(((keycode == Input.Keys.Q)||(keycode == Input.Keys.A)) && angularVelocity <= parent.getLevelManager().getPlayer().MAX_ANGULAR_VELOCITY){
            parent.getLevelManager().getPlayer().rotateRightPressed = true;
        }
        if(((keycode == Input.Keys.E)||(keycode == Input.Keys.D)) && angularVelocity <= parent.getLevelManager().getPlayer().MAX_ANGULAR_VELOCITY){
            parent.getLevelManager().getPlayer().rotateLeftPressed  = true;
        }
        if(keycode == Input.Keys.SPACE && parent.parent.getGameState() == MyGdxGame.GAME_STATE.INGAME){
            parent.getRenderManager().setDrawSprite(!parent.getRenderManager().isDrawSprite());
        }
        if(keycode == Input.Keys.ENTER && parent.parent.getGameState() == MyGdxGame.GAME_STATE.INGAME){
            parent.getLevelManager().nextLevel();
        }
        if(keycode == Input.Keys.SPACE && parent.parent.getGameState() == MyGdxGame.GAME_STATE.MIDGAME){ //transition from midgame -> ingame
            parent.parent.setGameState(MyGdxGame.GAME_STATE.INGAME);
        }
        if(keycode == Input.Keys.ESCAPE){ // transition from ingame -> midgame
            parent.parent.setGameState(MyGdxGame.GAME_STATE.MIDGAME);
            parent.getLevelManager().getMidGameMessage().setVisible(true);
            parent.getLevelManager().getNameLabel().setVisible(false);
            parent.getLevelManager().getElapsedTimeLabel().setVisible(false);
        }
        if(keycode == Input.Keys.P){ // transition from ingame -> midgame

            parent.parent.devMode = !parent.parent.devMode;
            System.out.println("toggle dev mode: " + parent.parent.devMode);
        }

        GameInput gInput = new GameInput(GameInput.InputType.KEYPRESSED, keycode, parent.parent.getFrameNum(),
                parent.parent.elapsedTime, parent.getLevelManager().getPlayer());
        parent.getLevelManager().getPlayer().inputList.add(gInput);
        return true;
    }

    /**
     * Listens for key releases
     * @param keycode The code of the key released.
     * @return Returns true if good, false if not.
     */
    @Override
    public boolean keyUp(int keycode) {
        if(keycode == com.badlogic.gdx.Input.Keys.W) parent.getLevelManager().getPlayer().forwardPressed = false;
        if(keycode == com.badlogic.gdx.Input.Keys.S) parent.getLevelManager().getPlayer().backwardPressed = false;
        if((keycode == com.badlogic.gdx.Input.Keys.Q) || (keycode == com.badlogic.gdx.Input.Keys.A)) parent.getLevelManager().getPlayer().rotateRightPressed = false;
        if((keycode == com.badlogic.gdx.Input.Keys.E)||(keycode == com.badlogic.gdx.Input.Keys.D)) parent.getLevelManager().getPlayer().rotateLeftPressed  = false;
        //System.out.println("pressed at" + parent.parent.getFrameNum());
        GameInput gInput = new GameInput(GameInput.InputType.KEYRELEASED, keycode,
                parent.parent.getFrameNum(), parent.parent.elapsedTime, parent.getLevelManager().getPlayer());
        parent.getLevelManager().getPlayer().inputList.add(gInput);
        return true;
    }

    /**
     * Listens for key typing, should probably use keyDown and keyUp instead.
     * @param character
     * @return
     */
    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    /**
     * Touch listener for mouse or touchscreen
     * @param screenX The X coords
     * @param screenY The Y coords
     * @param pointer
     * @param button
     * @return
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        float zoom = parent.getRenderManager().getCameraZoom();
        if(amount <= 0){
            float zoomChange = zoom/10;
            zoom -= zoomChange;
            if(zoom <= .4 && !parent.parent.devMode) zoom = .4f;
            System.out.println("Zoom In");
        }else{
            float zoomChange = zoom/10;
            zoom += zoomChange;

            if(zoom > 2.5 && !parent.parent.devMode){
                zoom = 2.5f;
                System.out.println("Zoom Out");
            }

        }
        parent.getRenderManager().setCameraZoom(zoom);
        return false;
    }


}
