package com.ongroa.arkanoid;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class GameScreen implements Screen {

	private Arkanoid game;
	OrthographicCamera camera;
	Rectangle restartButton;

	ArrayList<Brick> bricks;
	public int nBricksRow;
	public int nBricksColumn;
	public int gapBricks;

	int score;
	ControlMode controlMode;
	Ball ball;
	Paddle paddle;
	int lives;
	boolean gameOver;
	boolean paused;

	long lastFpsTime;
	long lastButtonPressedTime;
	double fps;
	private Vector3 touchPoint;

	GameScreen(Arkanoid game) {
		this.game = game;
		paddle = new Paddle();
		ball = new Ball(this);
		touchPoint = new Vector3();
		restartButton = new Rectangle(0, 0, 64, 64);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Arkanoid.SCREEN_WIDTH, Arkanoid.SCREEN_HEIGHT);

		bricks = new ArrayList<Brick>();
		nBricksRow = 4;
		nBricksColumn = 11;
		gapBricks = 5;
		createBricks();

		lives = 3;
		gameOver = false;
		paused = false;
		score = 0;
		controlMode = ControlMode.TOUCH;

		lastFpsTime = 0;
		lastButtonPressedTime = 0;
		fps = 0.0;
	}

	private void createBricks() {
		float brickWidth = (Arkanoid.SCREEN_WIDTH - (nBricksColumn + 1.0f) * gapBricks) / nBricksColumn; 
		//		brickWidth = 100;
		float brickHeight = Brick.height;
		for (int i = 0; i < nBricksRow; ++i)
			for (int j = 0; j < nBricksColumn; ++j) {
				float x = gapBricks + j * gapBricks + j * brickWidth;
				float y = Arkanoid.SCREEN_HEIGHT - Arkanoid.SCREEN_HEIGHT / 6 - i * (brickHeight + gapBricks);
				//				y = 300;
				int value = nBricksRow - i;
				Brick brick = new Brick(x, y, brickWidth, value);
				bricks.add(brick);
			}
	}

	@Override
	public void render(float delta) {
		draw();
		update();
	}

	private void update() {
		checkGameOver();
		if (! gameOver) {
			if (paused) {
				if (Gdx.input.isTouched()) {
					camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
					if (restartButton.contains(touchPoint.x, touchPoint.y) && 
							System.currentTimeMillis() - lastButtonPressedTime > Arkanoid.KEYPRESS_DELAY) {
						paused = false;
						lastButtonPressedTime = System.currentTimeMillis();
					}
				}
				if (Gdx.input.isKeyPressed(Keys.SPACE) && 
						System.currentTimeMillis() - lastButtonPressedTime > Arkanoid.KEYPRESS_DELAY) {
					paused = false;
					lastButtonPressedTime = System.currentTimeMillis();
				}
			} else {
				if (Gdx.input.isTouched()) {
					camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
					if (restartButton.contains(touchPoint.x, touchPoint.y) && 
							System.currentTimeMillis() - lastButtonPressedTime > Arkanoid.KEYPRESS_DELAY) {
						paused = true;
						lastButtonPressedTime = System.currentTimeMillis();
					}
				}
				if (ball.sticked == false && lastButtonPressedTime > 0 && Gdx.input.isKeyPressed(Keys.SPACE) && 
						System.currentTimeMillis() - lastButtonPressedTime > Arkanoid.KEYPRESS_DELAY) {
					paused = true;
					lastButtonPressedTime = System.currentTimeMillis();
				}
				if (ball.update(Gdx.graphics.getDeltaTime())) {

					//
					lastButtonPressedTime = System.currentTimeMillis();
				}
				checkCollision();
				paddle.update(Gdx.graphics.getDeltaTime(), this);
			}
		}
		if (gameOver && Gdx.input.isTouched()) {
			camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
			if (restartButton.contains(touchPoint.x, touchPoint.y)) {
				game.setScreen(new MainMenuScreen(game));
			}
		}
	}

	private void drawGameOver() {
		if (gameOver) {
			String msg = null;
			if (bricks.size() == 0) {
				msg = "WELL DONE!";
			} else {
				msg = "GAME OVER!";
			}
			float width = game.font.getBounds(msg).width;
			game.font.draw(game.batch, 
					String.format(msg,
							fps),
							Arkanoid.SCREEN_WIDTH / 2 - width / 2, 
							Arkanoid.SCREEN_HEIGHT / 2);
			if (Gdx.input.isKeyPressed(Keys.SPACE)) {
				game.setScreen(new MainMenuScreen(game));
			}
		}
	}

	private void checkGameOver() {
		if (bricks.size() == 0) {
			if (! gameOver) {
				score *= lives;
			}
			gameOver = true;
		}
	}

	private void checkCollision() {
		float ballRight = ball.x + ball.size;
		float ballLeft = ball.x - ball.size;
		float ballUp = ball.y + ball.size;
		float ballDown = ball.y - ball.size;
		for (Brick brick : bricks) {
			if (ballRight > brick.x && ballLeft < brick.x + brick.width &&
					ballUp > brick.y && ballDown < brick.y + Brick.height) {
				brick.visible = false;
				score += brick.value;
				if (ball.ySpeed < 0 && ballDown < brick.y + Brick.height && ballRight < brick.x + brick.width && ballLeft > brick.x) {
					ball.ySpeed = Math.abs(ball.ySpeed);
					//					System.out.println("up");
					break;
				} 
				if (ball.ySpeed > 0 && ballUp > brick.y && ballRight < brick.x + brick.width && ballLeft > brick.x) {
					ball.ySpeed = -Math.abs(ball.ySpeed);
					//					System.out.println("down");
					break;
				} 
				if (ball.xSpeed < 0 && ballLeft < brick.x + brick.width && ballUp < brick.y + Brick.height && ballDown > brick.y) {
					ball.xSpeed = Math.abs(ball.xSpeed);
					//					System.out.println("right");
					break;
				} 
				if (ball.xSpeed > 0 && ballRight > brick.x && ballUp < brick.y + Brick.height && ballDown > brick.y) {
					ball.xSpeed = -Math.abs(ball.xSpeed);
					//					System.out.println("left");
					break;
				}  
				if (ball.xSpeed > 0 && ball.ySpeed > 0) {
					if (ballRight > brick.x + brick.width) {
						ball.ySpeed = -ball.ySpeed;
						break;
					}
					if (ballUp > brick.y + Brick.height) {
						ball.xSpeed = -ball.xSpeed;
						break;
					}
					if (Math.abs(ball.x + ball.size - brick.x) > Math.abs(ball.y + ball.size - brick.y)) {
						ball.ySpeed = -ball.ySpeed;
						break;
					} else {
						ball.xSpeed = -ball.xSpeed;
						break;
					}
				}
				if (ball.xSpeed < 0 && ball.ySpeed > 0) {
					if (ballLeft < brick.x) {
						ball.ySpeed = -ball.ySpeed;
						break;
					}
					if (ballUp > brick.y + Brick.height) {
						ball.xSpeed = -ball.xSpeed;
						break;
					}
					if (Math.abs(ball.x - brick.x - brick.width) > Math.abs(ball.y + ball.size - brick.y)) {
						ball.ySpeed = -ball.ySpeed;
						break;
					} else {
						ball.xSpeed = -ball.xSpeed;
						break;
					}
				}
				if (ball.xSpeed > 0 && ball.ySpeed < 0) {
					if (ballDown < brick.y) {
						ball.xSpeed = -ball.xSpeed;
						break;
					}
					if (ballRight > brick.x + brick.width) {
						ball.ySpeed = -ball.ySpeed;
						break;
					}
					if (Math.abs(ball.x + ball.size - brick.x) > Math.abs(ball.y - brick.y - Brick.height)) {
						ball.ySpeed = -ball.ySpeed;
						break;
					} else {
						ball.xSpeed = -ball.xSpeed;
						break;
					}
				}
				if (ball.xSpeed < 0 && ball.ySpeed < 0) {
					if (ballLeft < brick.x) {
						ball.ySpeed = -ball.ySpeed;
						break;
					}
					if (ballDown < brick.y) {
						ball.xSpeed = -ball.xSpeed;
						break;
					}
					if (Math.abs(ball.x - brick.x - brick.width) > Math.abs(ball.y - brick.y - Brick.height)) {
						ball.ySpeed = -ball.ySpeed;
						break;
					} else {
						ball.xSpeed = -ball.xSpeed;
						break;
					}
				}
			}
		}
		Iterator<Brick> iter = bricks.iterator();
		while (iter.hasNext()) {
			if (! iter.next().visible) {
				iter.remove();
			}
		}	
	}

	private void draw() {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();

		game.shape.setProjectionMatrix(camera.combined);
		game.shape.begin(ShapeType.Filled);
		drawBricks();
		paddle.draw(game);
		if (! gameOver) {
			ball.draw(game);
		}
		game.shape.end();

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		drawScore();
		drawFps();
		drawLives();
		drawRestartButton();
		drawGameOver();
		game.batch.end();
	}

	private void drawRestartButton() {
		int x = 5;
		int y = 17;
		if (gameOver) {
			game.font.draw(game.batch, "RESTART", x, y);
		} else {
			if (paused) {
				game.font.draw(game.batch, "START", x, y);
			} else {
				game.font.draw(game.batch, "PAUSE", x, y);
			}
		}
	}

	private void drawScore() {
		game.font.draw(game.batch, 
				String.format("SCORE: %d",
						score),
						60, 
						Arkanoid.SCREEN_HEIGHT);
	}

	private void drawBricks() {
		for (Brick brick : bricks) {
			brick.draw(game);
		}
	}

	private void drawLives() {
		game.font.draw(game.batch, 
				String.format("LIVES: %d",
						lives),
						Arkanoid.SCREEN_WIDTH - 80, 
						Arkanoid.SCREEN_HEIGHT);
	}

	private void drawFps() {
		if (System.currentTimeMillis() - lastFpsTime > 200) {
			fps = 1 / Gdx.graphics.getDeltaTime();
			lastFpsTime = System.currentTimeMillis();
		}
		game.font.draw(game.batch, 
				String.format("FPS: %.0f",
						fps),
						0, 
						Arkanoid.SCREEN_HEIGHT);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
