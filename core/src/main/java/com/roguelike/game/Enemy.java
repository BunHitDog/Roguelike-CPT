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

    /**
     * Updates enemy movement and shooting behavior.
     *
     * @param delta Time elapsed since the last frame
     * @param playerX Player x-position
     * @param playerY Player y-position
     * @param projectiles Active projectile list
     * @param fire Fire projectile texture
     * @param ice Ice projectile texture
     * @param lightning Lightning projectile texture
     * @param random Random number generator
     */
    public void update(float delta, float playerX, float playerY,
                       java.util.ArrayList<EnemyProjectile> projectiles,
                       Texture fire, Texture ice, Texture lightning,
                       Random random) {

        // Reduce hit flash duration
        if (hitFlashTimer > 0) {
            hitFlashTimer -= delta;
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

        if (shootTimer >= 1.25f) {
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
     *
     * @param batch SpriteBatch used for rendering
     * @param left Texture used when facing left
     * @param right Texture used when facing right
     */
    public void draw(SpriteBatch batch, Texture left, Texture right) {

        // Flash red when recently hit
        if (hitFlashTimer > 0) {
            batch.setColor(1, 0, 0, 1);
        }

        batch.draw(movingLeft ? left : right, x, y, size, size);

        // Reset drawing color
        batch.setColor(1, 1, 1, 1);
    }
}