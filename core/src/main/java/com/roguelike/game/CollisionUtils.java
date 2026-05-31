package com.roguelike.game;

/**
 * Utility class for handling collision detection.
 * 
 * This class contains static helper methods used throughout the game
 * for checking whether two objects are colliding based on circular
 * hitbox detection.
 */
public class CollisionUtils {

    /**
     * Checks whether two objects are colliding using radius-based
     * distance comparison.
     *
     * This method avoids using Math.sqrt() for better performance
     * by comparing squared distances instead.
     *
     * @param x1 The x-position of the first object
     * @param y1 The y-position of the first object
     * @param x2 The x-position of the second object
     * @param y2 The y-position of the second object
     * @param radius The collision radius threshold
     * @return true if the objects are colliding, false otherwise
     */
    public static boolean isColliding(float x1, float y1, float x2, float y2, float radius) {

        // Calculate horizontal and vertical distance
        float dx = x1 - x2;
        float dy = y1 - y2;

        // Square the distance values
        float distSquared = dx * dx + dy * dy;

        // Square the collision radius
        float radiusSquared = radius * radius;

        // Collision occurs if distance is less than radius
        if (distSquared < radiusSquared) {
            return true;
        } else {
            return false;
        }
    }
}