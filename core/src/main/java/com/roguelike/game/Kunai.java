package com.roguelike.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Kunai {

    public float x, y;
    public float dx, dy;
    public float rotation;

    private float speed = 600f;

    public Kunai() {
        // empty constructor for older spawning style
    }

    // ✔️ ADD THIS (fixes Player.java error)
    public Kunai(float x, float y, float dx, float dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public void update(float delta) {
        x += dx * speed * delta;
        y += dy * speed * delta;
    }

    public void draw(SpriteBatch batch, Texture texture) {
        batch.draw(
                texture,
                x,
                y,
                13,
                13,
                26,
                26,
                1,
                1,
                rotation,
                0,
                0,
                texture.getWidth(),
                texture.getHeight(),
                false,
                false
        );
    }
}