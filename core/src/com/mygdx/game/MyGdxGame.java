package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

public class MyGdxGame extends ApplicationAdapter {
	/*  Used to keep track of the current state of the game. */
	public enum GAME_STATE {
		PREGAME, INGAME, POSTGAME;
	}

	/* Used to keep track of the game world. */
	GameWorld gameWorld;
	private GAME_STATE state = GAME_STATE.PREGAME;
	private float elapsedTime = 0;



	@Override
	public void create () {
		gameWorld = new GameWorld(this); // Initialize the GameWorld
	}

	@Override
	public void render (){
		elapsedTime += Gdx.graphics.getDeltaTime();
		gameWorld.render(elapsedTime);
	}

	public GAME_STATE getGameState(){
		return state;
	}

	public void setGameState(GAME_STATE s){
		state = s;
	}


}
