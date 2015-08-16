package com.mygdx.Player;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.GameWorld;

import java.util.ArrayList;

import input.GameInput;

/**
 * Created by slack on 8/14/2015.
 */
public class Ghost extends Player {
    private BitmapFont indexFont;
    int index;
    int nextInput;
    /**
     * Ghost Constructor
     *
     * @param textureAtlas The spritesheet, etc
     * @param world        The physics world the player exists in.
     * @param parent
     */
    public Ghost(Vector2 pos, TextureAtlas textureAtlas, World world, GameWorld parent, ArrayList<GameInput>inputList, int index) {
        super(pos, textureAtlas, world, parent);
        this.index = index;
        nextInput = 0;
        indexFont = new BitmapFont();
        indexFont.setColor(Color.RED);
        super.inputList = inputList;
    }

    public void render(float elapsedTime, SpriteBatch batch){
        super.render(elapsedTime, batch);
        drawIndex(batch);
    }

    private void drawIndex(SpriteBatch batch){
        indexFont.draw(batch, Integer.toString(index), getX()+getWidth()/2, getY()+getHeight()/2);
        indexFont.draw(batch, "LIST# " + inputList.size(), getX()+getWidth()/2, getY()+getHeight()/2+20);
    }

    public void dispose(){
        indexFont.dispose();
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
        if(nextInput < inputList.size()){
            GameInput i = inputList.get(nextInput);
            //if(i.getTimeStamp() <= elapsedTime){

            if(i.getFrameNum() <= parent.parent.getFrameNum()){
                System.out.print("EXECUTING INPUT for ");
                Vector2 vel = getBody().getLinearVelocity();
                float angularVelocity = getBody().getAngularVelocity();
                int keycode = i.getKeycode();
                //System.out.println("index:" + index + " processing key: " + keycode + " type: " + i.getType() + " Ghostframe: " + i.getFrameNum() + " gameFrame: " + parent.parent.getFrameNum());
                //inputList.remove(0);
                nextInput++;
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
            System.out.println("frame check: " + i.getFrameNum() + " vs " + parent.parent.getFrameNum());
        }

    }
}
