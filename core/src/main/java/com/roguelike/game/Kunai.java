package com.roguelike.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Kunai {

    public float x, y, dx, dy;
    public float rotation;

    private float speed = 600f;

    public Kunai(float x, float y, float tx, float ty) {

        this.x = x;
        this.y = y;

        float dist = (float)Math.sqrt((tx-x)*(tx-x)+(ty-y)*(ty-y));

        dx = (tx-x)/dist;
        dy = (ty-y)/dist;

        rotation = (float)Math.toDegrees(Math.atan2(dy, dx)) + 125f;
    }

    public void update(float delta) {
        x += dx * speed * delta;
        y += dy * speed * delta;
    }

    public void draw(SpriteBatch batch, Texture texture) {
        batch.draw(texture, x, y, 13, 13, 26, 26, 1, 1, rotation,
                0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }
}