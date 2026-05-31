package com.roguelike.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represents a projectile thrown by the player.
 *
 * Kunai travel in a straight line based on a direction vector,
 * rotate visually for effect, and are rendered using a texture.
 */
public class Kunai {

    /** Current position of the kunai. */
    public float x, y;

    /** Direction vector (should be normalized). */
    public float dx, dy;

    /** Visual rotation angle in degrees. */
    public float rotation;

    /** Movement speed of the kunai. */
    private float speed = 600f;

    /**
     * Default constructor used for manual field assignment spawning.
     */
    public Kunai() {
        // intentionally empty
    }

    /**
     * Alternative constructor for direct initialization.
     *
     * @param x Starting x position
     * @param y Starting y position
     * @param dx Direction x component
     * @param dy Direction y component
     */
    public Kunai(float x, float y, float dx, float dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Updates kunai movement each frame.
     *
     * @param delta Time elapsed since last frame
     */
    public void update(float delta) {
        x += dx * speed * delta;
        y += dy * speed * delta;
    }

    /**
     * Renders the kunai with rotation applied.
     *
     * @param batch SpriteBatch used for rendering
     * @param texture Texture used for the kunai
     */
    public void draw(SpriteBatch batch, Texture texture) {
        batch.draw(texture, x, y, 13, 13, 26, 26, 1, 1, rotation, 0, 0, texture.getWidth(), texture.getHeight(), false, false
        );
    }
}