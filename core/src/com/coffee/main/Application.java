package com.coffee.main;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.main.screen.CollisionTest;

/**
 * The main application class.
 * This contains the global {@link SpriteBatch}, {@link Viewport}, and entity {@link Engine} that we use throughout the game,
 * as well as the main game loop.
 */
public class Application extends Game {
	private SpriteBatch batch;
	private Viewport viewport;
	private PooledEngine engine;
	private InputMultiplexer inputMultiplexer;
	
	@Override
	public void create () {
		// All the behind-the-scenes GDX stuff
		// Except for fullscreen mode, but you get the idea.
		Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
		Gdx.graphics.setFullscreenMode(mode);
		inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);

		inputMultiplexer.addProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				switch(keycode) {
					case Input.Keys.F11:
						if (Gdx.graphics.isFullscreen())
							Gdx.graphics.setWindowedMode(576, 1024);
						else {
							Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
							Gdx.graphics.setFullscreenMode(mode);
						}
						break;
					case Input.Keys.ESCAPE:
						Gdx.app.exit();
						break;
					default:
						return false;
				}
				return true;
			}
		});

		batch = new SpriteBatch(5120);
		viewport = new FitViewport(720, 1280);
		engine = new PooledEngine();

		setScreen(new CollisionTest(this));
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

	public InputMultiplexer getInputMultiplexer() {
		return inputMultiplexer;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		viewport.update(width, height, true);
	}
}
