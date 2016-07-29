package com.mygdx.game;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by slack on 9/23/2015.
 */
public class GestureManager implements GestureDetector.GestureListener{
    private GameWorld parent;

    public GestureManager(GameWorld p){
        parent = p;
    }
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {

        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        if(Math.abs(velocityX) > 1000 || Math.abs(velocityY) > 1000){
            System.out.println("FLING: "+velocityX+":"+velocityY);
            parent.parent.setGameState(MyGdxGame.GAME_STATE.MIDGAME);
            parent.getLevelManager().getMidGameMessage().setVisible(true);
            parent.getLevelManager().getNameLabel().setVisible(false);
            parent.getLevelManager().getElapsedTimeLabel().setVisible(false);
        }

        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if(!parent.getLevelManager().navButtonProcessor.navButtonPressed){
            float newScale =  (distance / initialDistance);
            System.out.println("zoom newScale:" + newScale);

            float zoom = parent.getRenderManager().getCameraZoom();
            float baseZoom = parent.getRenderManager().getBaseZoom();
            if(newScale > 1){
                float zoomChange = zoom/(100/newScale);
                zoom -= zoomChange;
                if(zoom <= baseZoom*.4f && !parent.parent.devMode) zoom = baseZoom*.4f;
                System.out.println("Zoom In " + zoom);
            }else{
                float zoomChange = zoom/100;
                zoom += zoomChange;

                if(zoom > baseZoom*2.5 && !parent.parent.devMode){
                    zoom = baseZoom*2.5f;
                    System.out.println("Zoom Out " + zoom);
                }

            }
            //parent.getRenderManager().viewport.getC
            parent.getRenderManager().setCameraZoom(zoom);
        }

        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
}
