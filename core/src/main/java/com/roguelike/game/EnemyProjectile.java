package com.roguelike.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class EnemyProjectile {

    public float x, y;
    public float dx, dy;

    public String type;
    public Texture texture;

    public float size = 28f;
    public float speed = 260f;

    // ======================================================
    // MOVEMENT
    // ======================================================
    public void update(float delta) {
        x += dx * speed * delta;
        y += dy * speed * delta;
    }

    // ======================================================
    // DRAW
    // ======================================================
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, size, size);
    }
}