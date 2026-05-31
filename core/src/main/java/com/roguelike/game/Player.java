package com.roguelike.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represents the player character.
 *
 * Handles:
 * - Movement input and physics
 * - Status effects (burn, slow, stun)
 * - Shooting logic (kunai)
 * - Rendering and visual feedback
 */
public class Player {

    /** Size of the player in world units. */
    public static final float SIZE = 48f;

    /** Base movement speed of the player. */
    private static final float SPEED = 220f;

    /** Player world position. */
    public float x, y;

    /** Current health value. */
    public int currentHealth = 5;

    /** Flash timer used when taking damage. */
    public float hitFlashTimer = 0f;

    /** Slows movement when active. */
    public float slowTimer = 0f;

    /** Prevents movement/attacks when active. */
    public float stunTimer = 0f;

    /** Burn effect duration. */
    public float burnTimer = 0f;

    /** Internal timer used for burn tick damage. */
    private float burnTickTimer = 0f;

    /** Movement direction textures. */
    private Texture down, left, right, back;

    /** Currently active movement texture. */
    private Texture current;

    /** Cooldown timer for shooting. */
    private float shootCooldown = 0f;

    /**
     * Loads player textures from assets.
     */
    public void loadTextures() {

        down = new Texture("Ninja.png");
        left = new Texture("Ninja-Left.png");
        right = new Texture("Ninja-Right.png");
        back = new Texture("Ninja-Back.png");

        current = down;
    }

    /**
     * Updates player state each frame.
     *
     * @param delta Time in seconds since last frame.
     */
    public void update(float delta) {

        hitFlashTimer = Math.max(0, hitFlashTimer - delta);
        slowTimer = Math.max(0, slowTimer - delta);
        stunTimer = Math.max(0, stunTimer - delta);
        shootCooldown -= delta;

        // burn damage over time
        if (burnTimer > 0) {

            burnTimer -= delta;
            burnTickTimer += delta;

            if (burnTickTimer >= 10f) {
                currentHealth -= 1;
                burnTickTimer = 0f;
            }
        }

        // cannot move while stunned
        if (stunTimer > 0) {
            return;
        }

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

        float len = (float) Math.sqrt(moveX * moveX + moveY * moveY);

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

    /**
     * Attempts to shoot a kunai.
     *
     * @param mx Normalized x direction toward target.
     * @param my Normalized y direction toward target.
     * @return A new Kunai if shooting is allowed, otherwise null.
     */
    public Kunai shoot(float mx, float my) {

        if (stunTimer > 0 || shootCooldown > 0) {
            return null;
        }

        shootCooldown = 0.25f;

        return new Kunai(
            x + SIZE / 2f,
            y + SIZE / 2f,
            mx,
            my
        );
    }

    /**
     * Applies damage and status effects to the player.
     *
     * @param type Type of damage ("fire", "ice", "lightning")
     */
    public void hit(String type) {

        currentHealth -= 1;
        hitFlashTimer = 0.15f;

        if (type.equals("fire")) {
            burnTimer = 20f;
            burnTickTimer = 0f;
        } 
        else if (type.equals("ice")) {
            slowTimer = 2f;
        } 
        else if (type.equals("lightning")) {
            stunTimer = 1f;
        }
    }

    /**
     * Renders the player.
     *
     * @param batch SpriteBatch used for drawing.
     */
    public void draw(SpriteBatch batch) {

        if (hitFlashTimer > 0) {
            batch.setColor(1, 0, 0, 1);
        }

        batch.draw(current, x, y, SIZE, SIZE);

        batch.setColor(1, 1, 1, 1);
    }

    /**
     * Releases all loaded textures.
     * Should be called when disposing the game.
     */
    public void dispose() {

        down.dispose();
        left.dispose();
        right.dispose();
        back.dispose();
    }
}