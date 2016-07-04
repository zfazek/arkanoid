package com.ongroa.arkanoid;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

public class Paddle {
	public float x, y;
	public float xSpeed = 400.0f;
	public int width;
	public int height = 20;
	public int yOffset = 20;
	public static Color color;
	public Width w;

	Paddle() {
		x = (Arkanoid.SCREEN_WIDTH - width) / 2;
		y = yOffset;
		color = Color.RED;
		setWidth(Width.MEDIUM);
	}

	public void setWidth(Width wi) {
		switch(wi) {
		case SMALL: 
			width = 75;
			w = Width.SMALL;
			break;
		case MEDIUM:
			width = 100;
			w = Width.MEDIUM;
			break;
		case LARGE:
			width = 150;
			w = Width.LARGE;
			break;
		default:
			width = 100;
			w = Width.MEDIUM;
			break;
		}
	}

	public void update(float elapsedTime, GameScreen game) {
		if (game.controlMode == ControlMode.TOUCH) {
			if (Gdx.input.isTouched()) {
				if (Gdx.input.getX() < Arkanoid.SCREEN_WIDTH / 2) {
					x -= xSpeed * Gdx.graphics.getDeltaTime();
				} else {
					x += xSpeed * Gdx.graphics.getDeltaTime();
				}
			}
		}

		if (game.ball.sticked) {
			game.ball.stickBall();
		}
		
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			x -= xSpeed * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			x += xSpeed * Gdx.graphics.getDeltaTime();
		if (Gdx.app.getType() == ApplicationType.Android) {
			if (game.controlMode == ControlMode.TILT || game.ball.sticked) {
				x += 3 * Gdx.input.getAccelerometerY();
			}
		}
		if (x > Arkanoid.SCREEN_WIDTH) {
			x = Arkanoid.SCREEN_WIDTH;
		}
		if (x < -width) {
			x = -width;
		}
	}

	public void draw(Arkanoid game) {
		game.shape.setColor(color);
		game.shape.rect(x, y, width, height);
	}

}
