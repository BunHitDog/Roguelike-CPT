package com.roguelike.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represents a projectile fired by enemy shooter units.
 *
 * Projectiles move in a straight line based on a normalized direction vector
 * and deal effects depending on their type (fire, ice, lightning).
 */
public class EnemyProjectile {

    /** Current position of the projectile. */
    public float x, y;

    /** Direction vector (should be normalized). */
    public float dx, dy;

    /** Type of projectile effect (fire, ice, lightning). */
    public String type;

    /** Texture used to render the projectile. */
    public Texture texture;

    /** Render size of the projectile. */
    public float size = 28f;

    /** Movement speed of the projectile. */
    public float speed = 260f;

    /**
     * Updates projectile position based on direction and speed.
     *
     * @param delta Time elapsed since last frame
     */
    public void update(float delta) {
        x += dx * speed * delta;
        y += dy * speed * delta;
    }

    /**
     * Renders the projectile on screen.
     *
     * @param batch SpriteBatch used for drawing
     */
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, size, size);
    }
}