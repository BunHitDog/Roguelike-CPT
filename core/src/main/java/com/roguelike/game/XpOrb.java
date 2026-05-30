package com.roguelike.game;

public class XpOrb {

    public float x, y;
    public float dx, dy;

    public float size;
    public float speed;

    public boolean big;

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

    public void update(float delta) {

        x += dx * speed * delta;
        y += dy * speed * delta;

        // friction slowdown
        speed *= 0.96f;
    }
}