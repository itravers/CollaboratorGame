package input;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.Player.Player;
import com.mygdx.game.GameWorld;
import com.mygdx.game.MyGdxGame;

import java.util.ArrayList;

/**
 * Created by slack on 8/14/2015.
 */
public class InputManager implements InputProcessor {
    GameWorld parent;
    Player player;
    public InputManager(GameWorld parent){
        this.parent = parent;
        player = parent.getPlayer();
    }
    /**
     * Listens for key presses.
     * @param keycode The code of the key pressed
     * @return returns true when done. False if there is a problem.
     */
    @Override
    public boolean keyDown(int keycode) {
        Vector2 vel = player.getBody().getLinearVelocity();
        float angularVelocity = player.getBody().getAngularVelocity();
        if(keycode == Input.Keys.W && vel.dst2(vel) <= player.MAX_VELOCITY)player.forwardPressed  = true;
        if(keycode == Input.Keys.S && vel.dst2(vel) <= player.MAX_VELOCITY)player.backwardPressed = true;
        if(keycode == Input.Keys.Q && angularVelocity <= player.MAX_ANGULAR_VELOCITY) player.rotateRightPressed = true;
        if(keycode == Input.Keys.E && angularVelocity <= player.MAX_ANGULAR_VELOCITY)  player.rotateLeftPressed  = true;
        if(keycode == Input.Keys.SPACE && parent.parent.getGameState() == MyGdxGame.GAME_STATE.INGAME) parent.drawSprite = ! parent.drawSprite;
        if(keycode == Input.Keys.SPACE && parent.parent.getGameState() == MyGdxGame.GAME_STATE.MIDGAME){
            //reset game code here later

            parent.parent.setGameState(MyGdxGame.GAME_STATE.INGAME);
            parent.midGameMsgLbl.setVisible(false);
            parent.nameLabel.setVisible(true);
            parent.elapsedTimeLabel.setVisible(true);
        }
        if(keycode == Input.Keys.ESCAPE){
            parent.parent.setGameState(MyGdxGame.GAME_STATE.MIDGAME);
            parent.midGameMsgLbl.setVisible(true);
            parent.nameLabel.setVisible(false);
            parent.elapsedTimeLabel.setVisible(false);
        }
        player.inputList.add(new GameInput(GameInput.InputType.KEYPRESSED, keycode, parent.inputTime));
        return true;
    }

    /**
     * Listens for key releases
     * @param keycode The code of the key released.
     * @return Returns true if good, false if not.
     */
    @Override
    public boolean keyUp(int keycode) {
        if(keycode == com.badlogic.gdx.Input.Keys.W) player.forwardPressed = false;
        if(keycode == com.badlogic.gdx.Input.Keys.S) player.backwardPressed = false;
        if(keycode == com.badlogic.gdx.Input.Keys.Q) player.rotateRightPressed = false;
        if(keycode == com.badlogic.gdx.Input.Keys.E) player.rotateLeftPressed  = false;
        player.inputList.add(new GameInput(GameInput.InputType.KEYRELEASED, keycode, parent.inputTime));
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
        return false;
    }
}
