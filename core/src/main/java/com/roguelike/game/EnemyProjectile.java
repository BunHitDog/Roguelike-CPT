package com.roguelike.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class EnemyProjectile {

    public float x, y, dx, dy;
    public String type;
    public Texture texture;

    private float speed = 260f;

    public static EnemyProjectile create(float x, float y,
                                         float tx, float ty,
                                         Texture fire, Texture ice,
                                         Texture lightning,
                                         Random random) {

        EnemyProjectile p = new EnemyProjectile();

        p.x = x;
        p.y = y;

        float dist = (float)Math.sqrt((tx-x)*(tx-x)+(ty-y)*(ty-y));
        p.dx = (tx-x)/dist;
        p.dy = (ty-y)/dist;

        int r = random.nextInt(3);

        if (r == 0) { p.type = "fire"; p.texture = fire; }
        else if (r == 1) { p.type = "ice"; p.texture = ice; }
        else { p.type = "lightning"; p.texture = lightning; }

        return p;
    }

    public void update(float delta) {
        x += dx * speed * delta;
        y += dy * speed * delta;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, 28, 28);
    }
}