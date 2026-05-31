package com.roguelike.game;

/**
 * Represents an experience orb dropped by enemies.
 *
 * Orbs move outward initially, then slow down over time.
 * They can be either normal or "big" (higher value).
 */
public class XpOrb {

    /** Current world position of the orb. */
    public float x, y;

    /** Movement direction (should be normalized). */
    public float dx, dy;

    /** Visual size of the orb. */
    public float size;

    /** Current movement speed (gradually decays over time). */
    public float speed;

    /** Whether this orb is a high-value (big) XP drop. */
    public boolean big;

    /**
     * Creates a new XP orb.
     *
     * @param x Initial x position
     * @param y Initial y position
     * @param dx Direction x component
     * @param dy Direction y component
     * @param big Whether this orb is a large XP orb
     */
    public XpOrb(float x, float y, float dx, float dy, boolean big) {

        this.x = x;
        this.y = y;

        this.dx = dx;
        this.dy = dy;

        this.big = big;

        if (big) {
            size = 30f;
            speed = 150f;
        } else {
            size = 20f;
            speed = 120f;
        }
    }

    /**
     * Updates orb movement and applies friction-like slowdown.
     *
     * @param delta Time elapsed since last frame
     */
    public void update(float delta) {

        x += dx * speed * delta;
        y += dy * speed * delta;

        // gradual slowdown
        speed *= 0.96f;
    }
}