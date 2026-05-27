package com.roguelike.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Enemy {

    public float x, y;
    public float size = 96f;
    public float speed = 80f;
    public int hp = 6;

    public boolean shooter = false;
    public boolean movingLeft;

    public float shootTimer = 0f;
    public float hitFlashTimer = 0f;

    public void update(float delta, Player player,
            ArrayList<EnemyProjectile> projectiles,
            Texture fire, Texture ice, Texture lightning,
            Random random) {

        if (hitFlashTimer > 0) {
            hitFlashTimer -= delta;
        }

        float dx = player.x - x;
        float dy = player.y - y;

        float dist = (float)Math.sqrt(dx * dx + dy * dy);

        if (dist > 0.001f) {
            x += (dx / dist) * speed * delta;
            y += (dy / dist) * speed * delta;
        }

        movingLeft = dx < 0;

        if (shooter) {

            shootTimer += delta;

            if (shootTimer >= 1.25f) {

                shootTimer = 0f;

                projectiles.add(
                        EnemyProjectile.create(
                                x, y,
                                player.x, player.y,
                                fire, ice, lightning,
                                random
                        )
                );
            }
        }
    }

    public void draw(SpriteBatch batch, Texture left, Texture right) {

        if (hitFlashTimer > 0) {
            batch.setColor(1, 0, 0, 1);
        }

        batch.draw(movingLeft ? left : right, x, y, size, size);

        batch.setColor(1, 1, 1, 1);
    }
}