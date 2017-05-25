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
		System.out.println("Created");

		Gdx.graphics.setWindowedMode(450, 800);

		// Initialize global stuff before all the Screen stuff
		batch = new SpriteBatch(5120);
		viewport = new FitViewport(450, 800);
		inputMultiplexer = new InputMultiplexer();
		shapeRenderer = new ShapeRenderer();

		shapeRenderer.setAutoShapeType(true);

		// An input listener to exit the game and toggle fullscreen
		inputMultiplexer.addProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				int s = curTest;
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

				if (s != curTest) {
					setScreen(testScreens.get(curTest));
				}

				return true;
			}
		});

		Gdx.input.setInputProcessor(inputMultiplexer);

		System.out.println("Initializing EntityFactory");
		EntityFactory.init(this);

		// Screen stuff
		testScreens = new Array<Screen>();
	}

	@Override
	public void render () {
		// Asset loading is quite the complicated conundrum dont'cha think?
		if (!assetsLoaded) {
			assetsLoaded = Assets.MANAGER.update();

			if (assetsLoaded) {
				EntityFactory.getAssets();

				if (testScreens.size == 0) {
					testScreens.addAll(
							new CollisionTest(),
							new DrawSystemTest(),
							new PlayerTest(),
							new ViewportTest(),
							new PowerUpTest()
					);

					// Set the screen beforehand
					setScreen(testScreens.get(curTest));
				}
			}

			System.out.println(assetsLoaded ?
					"Assets loaded!" :
					"Loading assets... " + Assets.MANAGER.getProgress() * 100 + "%"
			);
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
}
