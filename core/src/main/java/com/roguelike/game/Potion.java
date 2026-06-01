package com.roguelike.game;

public class Potion {

    public float x, y;
    public float dx, dy;

    public float size = 32f;

    public boolean health; // true = health, false = buff

    public Potion(float x, float y, float dx, float dy, boolean health) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.health = health;
    }

    public void update(float delta) {
        x += dx * 80f * delta;
        y += dy * 80f * delta;
    }
}