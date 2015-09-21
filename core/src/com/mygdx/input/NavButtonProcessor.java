package com.mygdx.input;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.LevelManager;


/**
 * Created by Isaac Assegai on 9/21/2015.
 */
public class NavButtonProcessor implements EventListener {
    LevelManager parent;
    public NavButtonProcessor(LevelManager p){
        parent = p;
    }

    @Override
     public boolean handle(Event e) {
        String type = "";
        Actor listenActor = e.getListenerActor();
        if(e instanceof InputEvent){
          type = ((InputEvent)e).getType().name();
        }

        if(e instanceof ChangeListener.ChangeEvent){
            type = "touchUp";
        }

        if(type.equals("touchDown") && listenActor == parent.navUpButton){ //player wants to go forward
            parent.getPlayer().forwardPressed  = true;
        }else if(type.equals("touchUp") && listenActor == parent.navUpButton){
            parent.getPlayer().forwardPressed  = false;
        }else if(type.equals("touchDown") && listenActor == parent.navDownButton){ //player wants to go forward
            parent.getPlayer().backwardPressed  = true;
        }else if(type.equals("touchUp") && listenActor == parent.navDownButton){
            parent.getPlayer().backwardPressed  = false;
        }else if(type.equals("touchDown") && listenActor == parent.navRightButton){ //player wants to go forward
            parent.getPlayer().rotateLeftPressed  = true;
        }else if(type.equals("touchUp") && listenActor == parent.navRightButton){
            parent.getPlayer().rotateLeftPressed  = false;
        }else if(type.equals("touchDown") && listenActor == parent.navLeftButton){ //player wants to go forward
            parent.getPlayer().rotateRightPressed  = true;
        }else if(type.equals("touchUp") && listenActor == parent.navLeftButton){
            parent.getPlayer().rotateRightPressed  = false;
        }

        return false;
    }

}
