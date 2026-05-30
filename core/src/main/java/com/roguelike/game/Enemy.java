package com.roguelike.game;

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

    public void update(
            float delta,
            float playerX,
            float playerY,
            java.util.ArrayList<EnemyProjectile> projectiles,
            Texture fire,
            Texture ice,
            Texture lightning,
            Random random) {

        // ======================================================
        // HIT FLASH TIMER
        // ======================================================
        if (hitFlashTimer > 0) {
            hitFlashTimer -= delta;
        }

        // ======================================================
        // MOVE TOWARD PLAYER
        // ======================================================
        float dx = playerX - x;
        float dy = playerY - y;

        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist > 0.001f) {
            x += (dx / dist) * speed * delta;
            y += (dy / dist) * speed * delta;
        }

        movingLeft = dx < 0;

        // ======================================================
        // SHOOTING LOGIC (ONLY FOR SHOOTERS)
        // ======================================================
        if (!shooter) return;

        shootTimer += delta;

        if (shootTimer >= 1.25f) {

            shootTimer = 0f;

            EnemyProjectile p = new EnemyProjectile();

            // spawn at enemy center
            p.x = x + size / 2f;
            p.y = y + size / 2f;

            float dirX = playerX - x;
            float dirY = playerY - y;

            float dist2 = (float) Math.sqrt(dirX * dirX + dirY * dirY);

            if (dist2 > 0.001f) {
                p.dx = dirX / dist2;
                p.dy = dirY / dist2;
            }

            // random projectile type
            int rand = random.nextInt(3);

            if (rand == 0) {
                p.type = "fire";
                p.texture = fire;
            } else if (rand == 1) {
                p.type = "ice";
                p.texture = ice;
            } else {
                p.type = "lightning";
                p.texture = lightning;
            }

            projectiles.add(p);
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