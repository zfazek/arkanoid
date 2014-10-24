package com.ongroa.arkanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class MainMenuScreen implements Screen {

	Arkanoid game;
	GameScreen gameScreen;
	Rectangle controlButton;
	Rectangle startButton;
	Rectangle paddleButton;
	Rectangle ballSizeButton;
	Rectangle ballSpeedButton;
	Vector3 touchPoint;
	int rectangleX;
	int rectangleY;
	int rectangleWidth;
	int rectangleHeight;

	OrthographicCamera camera;

	public MainMenuScreen(final Arkanoid game) {
		this.game = game;
		gameScreen = new GameScreen(game);
		touchPoint = new Vector3();
		rectangleWidth = Arkanoid.SCREEN_WIDTH / 3;
		rectangleHeight = Arkanoid.SCREEN_HEIGHT / 4;
		rectangleX = Arkanoid.SCREEN_WIDTH / 2 - rectangleWidth / 2;
		rectangleY = Arkanoid.SCREEN_HEIGHT / 7;
		controlButton = new Rectangle(rectangleX, 
				1 * rectangleY, 
				rectangleWidth, 
				rectangleHeight);
		ballSpeedButton = new Rectangle(rectangleX, 
				3 * rectangleY, 
				rectangleWidth, 
				rectangleHeight);
		ballSizeButton = new Rectangle(rectangleX, 
				5 * rectangleY, 
				rectangleWidth, 
				rectangleHeight);
		paddleButton = new Rectangle(rectangleX, 
				7 * rectangleY, 
				rectangleWidth, 
				rectangleHeight);
		startButton = new Rectangle(rectangleX, 
				9 * rectangleY, 
				rectangleWidth, 
				rectangleHeight);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, Arkanoid.SCREEN_WIDTH, 800);
		Arkanoid.waitMillis(500);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		drawControlText();
		drawPaddleText();
		drawStartText();
		drawBallSpeedText();
		drawBallSizeText();
		game.batch.end();
		
		game.shape.setProjectionMatrix(camera.combined);
		game.shape.begin(ShapeType.Line);
		game.shape.setColor(Color.RED);
		game.shape.rect(controlButton.getX(), controlButton.getY(), controlButton.getWidth(), controlButton.getHeight());
		game.shape.rect(startButton.getX(), startButton.getY(), startButton.getWidth(), startButton.getHeight());
		game.shape.rect(paddleButton.getX(), paddleButton.getY(), paddleButton.getWidth(), paddleButton.getHeight());
		game.shape.rect(ballSpeedButton.getX(), ballSpeedButton.getY(), ballSpeedButton.getWidth(), ballSpeedButton.getHeight());
		game.shape.rect(ballSizeButton.getX(), ballSizeButton.getY(), ballSizeButton.getWidth(), ballSizeButton.getHeight());
		game.shape.end();

		if (Gdx.input.isTouched()) {
			camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
			if (startButton.contains(touchPoint.x, touchPoint.y)) {
				startGameScreen();
			}
			if (controlButton.contains(touchPoint.x, touchPoint.y)) {
				toggleControlMode();
			}
			if (paddleButton.contains(touchPoint.x, touchPoint.y)) {
				togglePaddleSize();
			}
			if (ballSpeedButton.contains(touchPoint.x, touchPoint.y)) {
				toggleBallSpeed();
			}
			if (ballSizeButton.contains(touchPoint.x, touchPoint.y)) {
				toggleBallSize();
			}
		}
		if (Gdx.input.isKeyPressed(Keys.SPACE)) {
			startGameScreen();
		}
	}

	private void toggleBallSize() {
		if (gameScreen.ball.si == Width.SMALL) {
			gameScreen.ball.setSize(Width.MEDIUM);
		} else if (gameScreen.ball.si == Width.MEDIUM) {
			gameScreen.ball.setSize(Width.LARGE);
		} else if (gameScreen.ball.si == Width.LARGE) {
			gameScreen.ball.setSize(Width.SMALL);
		}
		Arkanoid.waitMillis(500);
		
	}

	private void toggleBallSpeed() {
		if (gameScreen.ball.sp == Speed.SLOW) {
			gameScreen.ball.setSpeed(Speed.MEDIUM);
		} else if (gameScreen.ball.sp == Speed.MEDIUM) {
			gameScreen.ball.setSpeed(Speed.FAST);
		} else if (gameScreen.ball.sp == Speed.FAST) {
			gameScreen.ball.setSpeed(Speed.SLOW);
		}
		Arkanoid.waitMillis(500);
		
	}

	private void togglePaddleSize() {
		if (gameScreen.paddle.w == Width.SMALL) {
			gameScreen.paddle.setWidth(Width.MEDIUM);
		} else if (gameScreen.paddle.w == Width.MEDIUM) {
			gameScreen.paddle.setWidth(Width.LARGE);
		} else if (gameScreen.paddle.w == Width.LARGE) {
			gameScreen.paddle.setWidth(Width.SMALL);
		}
		Arkanoid.waitMillis(500);
	}

	private void toggleControlMode() {
		if (gameScreen.controlMode == ControlMode.TILT) {
			gameScreen.controlMode = ControlMode.TOUCH;
		} else {
			gameScreen.controlMode = ControlMode.TILT;
		}
		Arkanoid.waitMillis(500);
	}

	private void startGameScreen() {
		Arkanoid.waitMillis(500);
		game.setScreen(gameScreen);
		dispose();
	}

	private void drawStartText() {
		String msg = "START";
		float width = game.font.getBounds(msg).width;
		game.font.draw(game.batch, msg, startButton.getX() + rectangleWidth / 2 - width / 2, 
				startButton.getY() + rectangleHeight / 2);
	}

	private void drawBallSizeText() {
		String msg = null;
		if (gameScreen.ball.si == Width.SMALL) {
			msg = "BALL: SMALL";
		}
		if (gameScreen.ball.si == Width.MEDIUM) {
			msg = "BALL: MEDIUM";
		}
		if (gameScreen.ball.si == Width.LARGE) {
			msg = "BALL: LARGE";
		}
		float width = game.font.getBounds(msg).width;
		game.font.draw(game.batch, msg, ballSizeButton.getX() + rectangleWidth / 2 - width / 2, 
				ballSizeButton.getY() + rectangleHeight / 2);
	}

	private void drawBallSpeedText() {
		String msg = null;
		if (gameScreen.ball.sp == Speed.SLOW) {
			msg = "SPEED: SLOW";
		}
		if (gameScreen.ball.sp == Speed.MEDIUM) {
			msg = "SPEED: MEDIUM";
		}
		if (gameScreen.ball.sp == Speed.FAST) {
			msg = "SPEED: FAST";
		}
		float width = game.font.getBounds(msg).width;
		game.font.draw(game.batch, msg, ballSpeedButton.getX() + rectangleWidth / 2 - width / 2, 
				ballSpeedButton.getY() + rectangleHeight / 2);
	}

	private void drawControlText() {
		String msg = null;
		if (gameScreen.controlMode == ControlMode.TOUCH) {
			msg = "CONTROL: TOUCH";
		} else {
			msg = "CONTROL: TILT";
		}
		float width = game.font.getBounds(msg).width;
		game.font.draw(game.batch, msg, controlButton.getX() + rectangleWidth / 2 - width / 2, 
				controlButton.getY() + rectangleHeight / 2);
	}
	
	private void drawPaddleText() {
		String msg = null;
		if (gameScreen.paddle.w == Width.SMALL) {
			msg = "PADDLE: SMALL";
		}
		if (gameScreen.paddle.w == Width.MEDIUM) {
			msg = "PADDLE: MEDIUM";
		}
		if (gameScreen.paddle.w == Width.LARGE) {
			msg = "PADDLE: LARGE";
		}
		float width = game.font.getBounds(msg).width;
		game.font.draw(game.batch, msg, paddleButton.getX() + rectangleWidth / 2 - width / 2, 
				paddleButton.getY() + rectangleHeight / 2);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
}