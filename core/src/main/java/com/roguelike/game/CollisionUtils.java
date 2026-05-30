package com.roguelike.game;

public class CollisionUtils {

    public static boolean isColliding(
            float x1, float y1,
            float x2, float y2,
            float radius) {

        float dx = x1 - x2;
        float dy = y1 - y2;

        // Avoid sqrt (faster performance)
        float distSquared = dx * dx + dy * dy;
        float radiusSquared = radius * radius;

        return distSquared < radiusSquared;
    }
}