package com.ongroa.arkanoid;

import com.badlogic.gdx.graphics.Color;

public class Brick {

	public static Color color;
	public float x, y;
	public float width;
	public static int height = 20;
	public boolean visible;
	public int value;


	Brick(float x, float y, float w, int v) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.value = v;
		color = Color.YELLOW;
		visible = true;
	}


	public void update() {

	}

	public void draw(Arkanoid game) {
		if (visible) {
			game.shape.setColor(color);
			game.shape.rect(x, y, width, height);
		}
	}
}
