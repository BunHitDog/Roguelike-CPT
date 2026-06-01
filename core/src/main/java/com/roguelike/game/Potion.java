package com.roguelike.game;

/**
 * Represents a collectible potion dropped by enemies.
 *
 * Potions drift outward slightly when spawned and can be either:
 * - Health potion: restores player health
 * - Buff potion: temporarily boosts damage
 */
public class Potion {

    /** Current X position in world space */
    public float x, y;

    /** Movement direction (normalized) */
    public float dx, dy;

    /** Size of potion sprite */
    public float size = 32f;

    /** Potion type:
     * true = health potion
     * false = buff potion
     */
    public boolean health;

    /**
     * Creates a new potion at a given position with a movement direction.
     *
     * @param x spawn X position
     * @param y spawn Y position
     * @param dx movement X direction
     * @param dy movement Y direction
     * @param health true = health potion, false = buff potion
     */
    public Potion(float x, float y, float dx, float dy, boolean health) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.health = health;
    }

    /**
     * Updates potion movement each frame.
     *
     * @param delta time since last frame
     */
    public void update(float delta) {
        x += dx * 80f * delta;
        y += dy * 80f * delta;
    }
}