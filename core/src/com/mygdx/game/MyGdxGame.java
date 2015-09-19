package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

public class MyGdxGame extends ApplicationAdapter {
	/*  Used to keep track of the current state of the game. */
	public enum GAME_STATE {
		PREGAME, INGAME, MIDGAME, POSTGAME;
	}

	/* Used to keep track of the game world. */
	GameWorld gameWorld;
	private GAME_STATE state = GAME_STATE.PREGAME;
	public float elapsedTime = 0;
	private int frameNum = 0;
	public boolean devMode = false;

	@Override
	public void create () {
		//state = GAME_STATE.PREGAME;
		setGameState(GAME_STATE.PREGAME);
		gameWorld = new GameWorld(this); // Initialize the GameWorld
	}

	@Override
	public void render (){
		elapsedTime += Gdx.graphics.getDeltaTime();
		gameWorld.render(elapsedTime);
		frameNum++;
	}

	public GAME_STATE getGameState(){
		return state;
	}

	public void setGameState(GAME_STATE s){

		elapsedTime = 0;
		System.out.println("GAMESTATE: " + state + " -> " + s);

		 /* On gamestate changes, set the correct visibilities. */
		if(getGameState() == GAME_STATE.INGAME &&
				s == GAME_STATE.MIDGAME){ //INGAME -> MIDGAME transition
			boolean goalCompleted = gameWorld.getLevelManager().getLevelGoalCompleted();
			gameWorld.getLevelManager().setUiVisible(false);
			gameWorld.getLevelManager().setMidGameVisible(true);

			//if(goalCompleted){
			//	gameWorld.getLevelManager().nextLevel();
			//}
		}else if(getGameState() == GAME_STATE.MIDGAME &&
				s == GAME_STATE.INGAME){ //MIDGAME -> INGAME transition
			boolean goalCompleted2 = gameWorld.getLevelManager().getLevelGoalCompleted();
			if(!goalCompleted2){
				gameWorld.getLevelManager().resetLevel();
			}else{
				gameWorld.getLevelManager().nextLevel();
			}
			gameWorld.getLevelManager().setLevelGoalCompleted(false); //reset level goal completed
			gameWorld.getLevelManager().setUiVisible(true);
			gameWorld.getLevelManager().setMidGameVisible(false);
		}
		state = s;
	}

	public int getFrameNum() {
		return frameNum;
	}

	public void setFrameNum(int frameNum) {
		this.frameNum = frameNum;
	}

	public void resetFrameNum(){
		this.frameNum = 0;
	}

	public void incrementFrameNum(){
		this.frameNum++;
	}


}
