package com.coffee.main;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.EntityBuilder;
import com.coffee.main.screen.CollisionTest;
import com.coffee.main.screen.DrawSystemTest;
import com.coffee.main.screen.PlayerTest;
import com.coffee.main.screen.ViewportTest;

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
	private Array<Screen> testScreens;
	private int curTest = 0;

	@Override
	public void create () {
		System.out.println("Created");

		Gdx.graphics.setWindowedMode(450, 800);

		// Initialize global stuff before all the Screen stuff
		batch = new SpriteBatch(5120);
		viewport = new FitViewport(720, 1280);
		engine = new PooledEngine();
		inputMultiplexer = new InputMultiplexer();

		// An input listener to exit the game and toggle fullscreen
		inputMultiplexer.addProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				switch(keycode) {
					case Input.Keys.F11:
						Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
						if (Gdx.graphics.isFullscreen())
							Gdx.graphics.setWindowedMode(450, 800);
						else {
							Gdx.graphics.setFullscreenMode(mode);
						}
						break;
					case Input.Keys.ESCAPE:
						Gdx.app.exit();
						break;
					case Input.Keys.LEFT_BRACKET:
						if (testScreens != null) {
							curTest--;
							if (curTest < 0)
								curTest = testScreens.size - 1;

							setScreen(testScreens.get(curTest));
						}
						break;
					case Input.Keys.RIGHT_BRACKET:
						if (testScreens != null) {
							curTest++;
							if (curTest >= testScreens.size)
								curTest = 0;

							setScreen(testScreens.get(curTest));
						}
						break;
					default:
						return false;
				}
				return true;
			}
		});

		Gdx.input.setInputProcessor(inputMultiplexer);

		System.out.println("Initializing EntityBuilder");
		EntityBuilder.init(this);

		// Screen stuff
		testScreens = new Array<Screen>();

		testScreens.addAll(
				new CollisionTest(this),
				new DrawSystemTest(this),
				new PlayerTest(this),
				new ViewportTest(this)
		);

		// Set the screen beforehand
		setScreen(testScreens.get(curTest));
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

	public SpriteBatch getBatch() {
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
