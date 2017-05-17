package com.coffee.main;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.main.screen.DrawSystemTest;

/**
 * The main application class.
 * This contains the global {@link SpriteBatch}, {@link Viewport}, and entity {@link Engine} that we use throughout the game,
 * as well as the main game loop.
 */
public class Application extends Game {
	private SpriteBatch batch;
	private Viewport viewport;
	private PooledEngine engine;
	
	@Override
	public void create () {
		batch = new SpriteBatch(5120);
		viewport = new ScreenViewport();
		engine = new PooledEngine();

		setScreen(new DrawSystemTest(this));
	}

	@Override
	public void render () {
		if (getScreen() != null)
			getScreen().render(Math.min(Gdx.graphics.getDeltaTime(), 1/60F));
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	public Batch getBatch() {
		return batch;
	}

	public Viewport getViewport() {
		return viewport;
	}

	public PooledEngine getEngine() {
		return engine;
	}
}
