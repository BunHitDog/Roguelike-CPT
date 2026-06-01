package com.roguelike.game;

import java.util.Random;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represents an enemy in the game.
 *
 * Enemies move toward the player and can optionally act as ranged
 * shooter enemies that fire projectiles with random elemental effects.
 */
public class Enemy {

    /** Current position of the enemy. */
    public float x, y;

    /** Size of the enemy sprite. */
    public float size = 96f;

    /** Movement speed of the enemy. */
    public float speed = 80f;

    /** Health points of the enemy. */
    public int hp = 6;

    /** Determines whether this enemy can shoot projectiles. */
    public boolean shooter = false;

    /** Determines which sprite direction to render. */
    public boolean movingLeft;

    /** Tracks time between shots for shooter enemies. */
    public float shootTimer = 0f;

    /** Controls red flash effect when taking damage. */
    public float hitFlashTimer = 0f;

    /** Enemy type: 0 = normal, 1 = shooter, 2 = spike */
    public int type = 0;

    /**
     * Updates enemy movement and shooting behavior.
     */
    public void update(float delta, float playerX, float playerY,
                       java.util.ArrayList<EnemyProjectile> projectiles,
                       Texture fire, Texture ice, Texture lightning,
                       Random random) {

        // Reduce hit flash duration
        if (hitFlashTimer > 0) {
            hitFlashTimer -= delta;
        }

        // ===============================
        // SPIKE SLIME OVERRIDE BEHAVIOR
        // ===============================
        if (type == 2) {
            speed = 220f; // fast like player
            hp = 1;       // always 1 HP
        }

        // Calculate direction toward player
        float dx = playerX - x;
        float dy = playerY - y;

        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        // Move toward player if not already overlapping
        if (dist > 0.001f) {
            x += (dx / dist) * speed * delta;
            y += (dy / dist) * speed * delta;
        }

        // Update facing direction
        movingLeft = dx < 0;

        // Stop here if enemy is not a shooter
        if (!shooter) {
            return;
        }

        // Handle shooting cooldown
        shootTimer += delta;

        float fireRate;

        if (type == 99) {
            fireRate = MyRogueLikeGame.BOSS_SHOOT_INTERVAL;
        } else {
            fireRate = MyRogueLikeGame.SHOOTER_FIRE_INTERVAL;
        }

        if (shootTimer >= fireRate) {
            shootTimer = 0f;

            EnemyProjectile p = new EnemyProjectile();

            // Spawn projectile at enemy center
            p.x = x + size / 2f;
            p.y = y + size / 2f;

            // Aim toward player
            float dirX = playerX - x;
            float dirY = playerY - y;

            float dist2 = (float) Math.sqrt(dirX * dirX + dirY * dirY);

            if (dist2 > 0.001f) {
                p.dx = dirX / dist2;
                p.dy = dirY / dist2;
            }

            // Select random projectile type
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

    /**
     * Draws the enemy to the screen.
     */
    public void draw(SpriteBatch batch, Texture left, Texture right, Texture spikeTexture, Texture kingTexture) {

        // Flash red when recently hit
        if (hitFlashTimer > 0) {
            batch.setColor(1, 0, 0, 1);
        }

        // ===============================
        // SELECT CORRECT TEXTURE
        // ===============================
        Texture tex = right; // safe default

        if (type == 99) {
            tex = kingTexture;
        }
        else if (type == 2) {
            tex = spikeTexture;
        }
        else {
            tex = movingLeft ? left : right;
        }

        // ===============================
        // DRAW
        // ===============================
        batch.draw(tex, x, y, size, size);

        // Reset color
        batch.setColor(1, 1, 1, 1);
    }
}