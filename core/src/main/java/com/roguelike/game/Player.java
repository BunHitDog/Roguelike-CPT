package com.roguelike.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {

    public static final float SIZE = 48f;
    private static final float SPEED = 220f;

    public float x, y;
    public int currentHealth = 9;

    public float hitFlashTimer = 0f;

    // =========================
    // STATUS EFFECTS
    // =========================
    public float slowTimer = 0f;
    public float stunTimer = 0f;

    public float burnTimer = 0f;
    private float burnTickTimer = 0f;

    private Texture down, left, right, back;
    private Texture current;

    private float shootCooldown = 0f;

    public void loadTextures() {
        down = new Texture("Ninja.png");
        left = new Texture("Ninja-Left.png");
        right = new Texture("Ninja-Right.png");
        back = new Texture("Ninja-Back.png");

        current = down;
    }

    public void update(float delta) {

        hitFlashTimer = Math.max(0, hitFlashTimer - delta);

        slowTimer = Math.max(0, slowTimer - delta);
        stunTimer = Math.max(0, stunTimer - delta);

        if (burnTimer > 0) {
            burnTimer -= delta;
            burnTickTimer += delta;

            if (burnTickTimer >= 10f) {
                currentHealth -= 1;
                burnTickTimer = 0f;
            }
        }

        shootCooldown -= delta;

        if (stunTimer > 0) return;

        float moveX = 0;
        float moveY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveY++;
            current = back;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveY--;
            current = down;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveX--;
            current = left;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveX++;
            current = right;
        }

        float len = (float)Math.sqrt(moveX * moveX + moveY * moveY);

        if (len > 0) {
            moveX /= len;
            moveY /= len;
        }

        float speed = SPEED;

        if (slowTimer > 0) {
            speed *= 0.5f;
        }

        x += moveX * speed * delta;
        y += moveY * speed * delta;
    }

    public Kunai shoot(float mx, float my) {

        if (stunTimer > 0) return null;
        if (shootCooldown > 0) return null;

        shootCooldown = 0.25f;

        return new Kunai(
            x + SIZE / 2,
            y + SIZE / 2,
            mx,
            my
        );
    }

    public void hit(String type) {

        currentHealth -= 1;
        hitFlashTimer = 0.15f;

        if (type.equals("fire")) {
            burnTimer = 20f;
            burnTickTimer = 0f;
        } else if (type.equals("ice")) {
            slowTimer = 2f;
        } else if (type.equals("lightning")) {
            stunTimer = 1f;
        }
    }

    public void draw(SpriteBatch batch) {

        if (hitFlashTimer > 0) {
            batch.setColor(1, 0, 0, 1);
        }

        batch.draw(current, x, y, SIZE, SIZE);

        batch.setColor(1, 1, 1, 1);
    }

    public void dispose() {
        down.dispose();
        left.dispose();
        right.dispose();
        back.dispose();
    }
}