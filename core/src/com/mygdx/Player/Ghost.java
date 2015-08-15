package com.mygdx.Player;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.GameWorld;
import com.mygdx.Player.Player;

import java.util.ArrayList;

import input.GameInput;

/**
 * Created by slack on 8/14/2015.
 */
public class Ghost extends Player {
    /**
     * Ghost Constructor
     *
     * @param textureAtlas The spritesheet, etc
     * @param world        The physics world the player exists in.
     * @param parent
     */
    public Ghost(TextureAtlas textureAtlas, World world, GameWorld parent, ArrayList<GameInput>inputList) {
        super(textureAtlas, world, parent);
        this.inputList = new ArrayList<GameInput>(inputList);
    }


    @Override
    public void update(float elapsedTime){
        if(!inputList.isEmpty()) checkInputList(elapsedTime);
        super.update(elapsedTime);
    }

    /**
     * Checks the first item on the input list to see if timestamp >= elapsedTime
     * if it is than we update the ghosts current input to reflect the latest input from the list
     * This will allow the super.update() to update the ghost for those inputs
     * @param elapsedTime
     */
    private void checkInputList(float elapsedTime){
        GameInput i = inputList.get(0);
        if(i.getTimeStamp() <= elapsedTime){
            Vector2 vel = getBody().getLinearVelocity();
            float angularVelocity = getBody().getAngularVelocity();
            int keycode = i.getKeycode();
            System.out.println("processing key: " + keycode + " type: " + i.getType() + " timestamp: " + i.getTimeStamp() + " elapsedTime: " + elapsedTime);
            inputList.remove(0);
            if(i.getType() == GameInput.InputType.KEYPRESSED){
                if(keycode == Input.Keys.W && vel.dst2(vel) <= MAX_VELOCITY) forwardPressed  = true;
                if(keycode == Input.Keys.S && vel.dst2(vel) <= MAX_VELOCITY) backwardPressed = true;
                if(keycode == Input.Keys.Q && angularVelocity <= MAX_ANGULAR_VELOCITY) rotateRightPressed = true;
                if(keycode == Input.Keys.E && angularVelocity <= MAX_ANGULAR_VELOCITY) rotateLeftPressed  = true;
            }else if(i.getType() == GameInput.InputType.KEYRELEASED){
                if(keycode == Input.Keys.W && vel.dst2(vel) <= MAX_VELOCITY) forwardPressed  = false;
                if(keycode == Input.Keys.S && vel.dst2(vel) <= MAX_VELOCITY) backwardPressed = false;
                if(keycode == Input.Keys.Q && angularVelocity <= MAX_ANGULAR_VELOCITY) rotateRightPressed = false;
                if(keycode == Input.Keys.E && angularVelocity <= MAX_ANGULAR_VELOCITY) rotateLeftPressed  = false;
            }

        }
    }
}
