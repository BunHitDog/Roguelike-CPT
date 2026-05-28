package com.roguelike.game;

public class CollisionUtils {

    public static boolean isColliding(
            float x1, float y1,
            float x2, float y2,
            float size) {

        float dx = x1 - x2;
        float dy = y1 - y2;

        return Math.sqrt(dx * dx + dy * dy) < size;
    }
}