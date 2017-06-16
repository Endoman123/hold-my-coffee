package com.coffee.main;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.EntityFactory;
import com.coffee.main.screen.*;
import com.coffee.util.Assets;
import com.coffee.util.HighScore;
import com.coffee.util.OptionsManager;

/**
 * The main application class.
 * This contains the global {@link SpriteBatch}, {@link Viewport}, and entity {@link Engine} that we use throughout the game,
 * as well as the main game loop.
 */
public class Application extends Game {
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private Viewport viewport;
	private InputMultiplexer inputMultiplexer;
	private Array<Screen> testScreens;
	private int curTest = 0;

	private boolean assetsLoaded = false;

	@Override
	public void create () {
		OptionsManager.init();
		HighScore.init();

		// Initialize global stuff before all the Screen stuff
		batch = new SpriteBatch(3000);
		viewport = new FitViewport(600, 800);
		inputMultiplexer = new InputMultiplexer();
		shapeRenderer = new ShapeRenderer();

		shapeRenderer.setAutoShapeType(true);

		Assets.MANAGER.load(Assets.UI.SKIN);
		Assets.MANAGER.load(Assets.UI.ATLAS);
		Assets.MANAGER.load(Assets.GameObjects.ATLAS);

		// An input listener to exit the game and toggle fullscreen
		inputMultiplexer.addProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				switch(keycode) {
					case Input.Keys.F11:
						OptionsManager.fullscreen = !OptionsManager.fullscreen;
						OptionsManager.update();
						break;
					default:
						return false;
				}
				return true;
			}
		});

		Gdx.input.setInputProcessor(inputMultiplexer);

		// Screen stuff
		testScreens = new Array<Screen>();
	}

	@Override
	public void render () {
		// Asset loading is quite the complicated conundrum dont'cha think?
		if (!assetsLoaded) {
			assetsLoaded = Assets.MANAGER.update();

			if (assetsLoaded) {
				EntityFactory.init();

				if (testScreens.size == 0) {
					testScreens.addAll(
							new MainMenu(),
							new AITest(),
							new CollisionTest(),
							new DrawSystemTest(),
							new PlayerTest(),
							new ViewportTest(),
							new PowerUpTest(),
							new StarsTest()
					);

					// Set the screen beforehand
					setScreen(testScreens.get(curTest));
				}
			}
		}

		if (getScreen() != null) {
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			getScreen().render(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60F));
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public ShapeRenderer getShapeRenderer() {
		return shapeRenderer;
	}

	public Viewport getViewport() {
		return viewport;
	}

	public InputMultiplexer getInputMultiplexer() {
		return inputMultiplexer;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		viewport.update(width, height, true);
	}

	/**
	 * An {@link InputProcessor} specifically made for switching through test screens.
	 * Do not implement in final game.
	 */
	private final class DebugInput extends InputAdapter {
		@Override
		public boolean keyDown(int keycode) {
			int s = curTest;
			switch (keycode) {
				case Input.Keys.ESCAPE:
					Gdx.app.exit();
					break;
				case Input.Keys.LEFT_BRACKET:
					if (testScreens != null) {
						curTest--;
						if (curTest < 0)
							curTest = testScreens.size - 1;
					}
					break;
				case Input.Keys.RIGHT_BRACKET:
					if (testScreens != null) {
						curTest++;
						if (curTest >= testScreens.size)
							curTest = 0;
					}
					break;
				default:
					return false;
			}
				if (s != curTest)
					setScreen(testScreens.get(curTest));

				return true;
		}
	}
}
