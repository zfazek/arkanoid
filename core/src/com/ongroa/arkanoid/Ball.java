package com.ongroa.arkanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

public class Ball {
	public float x, y;
	public float speed;
	public float xSpeed, ySpeed;
	public int size;
	private GameScreen game;
	public static Color color;
	private boolean missed;
	public boolean sticked;
	public Speed sp;
	public Width si;

	Ball(GameScreen game) {
		this.game = game;
		setSpeed(Speed.MEDIUM);
		setSize(Width.MEDIUM);
		stickBall();
		color = Color.GREEN;
	}

	public void setSpeed(Speed s) {
		switch(s) {
		case SLOW: 
			speed = 100.0f;
			sp = Speed.SLOW;
			break;
		case MEDIUM:
			speed = 150.0f;
			sp = Speed.MEDIUM;
			break;
		case FAST:
			speed = 200.0f;
			sp = Speed.FAST;
			break;
		default:
			speed = 150.f;
			sp = Speed.MEDIUM;
			break;
		}
	}

	public void setSize(Width sz) {
		switch(sz) {
		case SMALL: 
			size = 5;
			si = Width.SMALL;
			break;
		case MEDIUM:
			size = 10;
			si = Width.MEDIUM;
			break;
		case LARGE:
			size = 15;
			si = Width.LARGE;
			break;
		default:
			speed = 10;
			si = Width.MEDIUM;
			break;
		}
	}

	public void stickBall() {
		sticked = true;
		x = game.paddle.x + 3 * game.paddle.width / 4;
		y = game.paddle.yOffset + game.paddle.height + size;
		xSpeed = 0.0f;
		ySpeed = 0.0f;
		missed = false;

	}

	public boolean update(float elapsedTime) {
		if (sticked) {
			if (Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isTouched()) {
				sticked = false;
				xSpeed = speed;
				ySpeed = speed;
				return true;
			}
		}

		x += elapsedTime * xSpeed;
		y += elapsedTime * ySpeed;

		if (! missed && y - size < game.paddle.y + game.paddle.height) {
			if (x + size > game.paddle.x && x - size < game.paddle.x + game.paddle.width) {
				ySpeed = Math.abs(ySpeed);
				float paddleX1 = game.paddle.x;
				float den = game.paddle.width / 2;
				float paddleMiddle = paddleX1 + den;
				float ratio = 5 * (x - paddleMiddle) / game.paddle.width;
				xSpeed = ratio * speed;
			} else {
				missed = true;
			}
		}

		if (x > Arkanoid.SCREEN_WIDTH - size) {
			x = Arkanoid.SCREEN_WIDTH - size;
			xSpeed = -Math.abs(xSpeed);
		}
		if (x < size) {
			x = size;
			xSpeed = Math.abs(xSpeed);
		}
		if (y < -size) {
			--game.lives;
			if (game.lives > 0) {
				stickBall();
			} else {
				xSpeed = 0;
				ySpeed = 0;
				game.gameOver = true;
			}
		}
		if (y > Arkanoid.SCREEN_HEIGHT - size) {
			y = Arkanoid.SCREEN_HEIGHT - size;
			ySpeed = -Math.abs(ySpeed);
		}
	return false;
}

public void draw(Arkanoid game) {
	game.shape.setColor(color);
	game.shape.circle(x, y, size);
}


}
