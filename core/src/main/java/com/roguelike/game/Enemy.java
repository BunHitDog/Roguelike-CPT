package com.roguelike.game;

import java.util.Random;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represents an enemy in the game.
 *
 * Enemies automatically move toward the player and may optionally shoot
 * projectiles with random elemental effects (fire, ice, lightning).
 */
public class Enemy {

    /** X position in world space */
    public float x, y;

    /** Size of enemy sprite (width/height) */
    public float size = 96f;

    /** Movement speed */
    public float speed = 80f;

    /** Current health points */
    public int hp = 6;

    /** Whether this enemy can shoot projectiles */
    public boolean shooter = false;

    /** Direction flag used for sprite rendering (true = facing left) */
    public boolean movingLeft;

    /** Timer controlling shooting cooldown */
    public float shootTimer = 0f;

    /** Flash timer used when enemy takes damage (red flash effect) */
    public float hitFlashTimer = 0f;

    /** Enemy type: 0 = normal, 1 = shooter, 2 = spike, 99 = boss */
    public int type = 0;

    /**
     * Updates enemy movement, AI behavior, and shooting.
     *
     * @param delta time since last frame
     * @param playerX player X position
     * @param playerY player Y position
     * @param projectiles list where enemy projectiles are added
     * @param fire texture for fire projectile
     * @param ice texture for ice projectile
     * @param lightning texture for lightning projectile
     * @param random RNG used for projectile type selection
     */
    public void update(
            float delta,
            float playerX,
            float playerY,
            java.util.ArrayList<EnemyProjectile> projectiles,
            Texture fire,
            Texture ice,
            Texture lightning,
            Random random) {

        // reduce hit flash timer each frame
        if (hitFlashTimer > 0) {
            hitFlashTimer -= delta;
        }

        // spike enemy override stats
        if (type == 2) {
            speed = 220f;
            hp = 1;
        }

        // direction toward player
        float dx = playerX - x;
        float dy = playerY - y;

        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        // move toward player if not overlapping
        if (dist > 0.001f) {
            x += (dx / dist) * speed * delta;
            y += (dy / dist) * speed * delta;
        }

        // update sprite direction
        movingLeft = dx < 0;

        // stop if not shooter
        if (!shooter) return;

        // update shooting timer
        shootTimer += delta;

        // determine fire rate based on enemy type
        float fireRate = (type == 99)
                ? MyRogueLikeGame.BOSS_SHOOT_INTERVAL
                : MyRogueLikeGame.SHOOTER_FIRE_INTERVAL;

        // shoot projectile if cooldown finished
        if (shootTimer >= fireRate) {
            shootTimer = 0f;

            EnemyProjectile p = new EnemyProjectile();

            // spawn at enemy center
            p.x = x + size / 2f;
            p.y = y + size / 2f;

            // direction toward player
            float dirX = playerX - x;
            float dirY = playerY - y;

            float dist2 = (float) Math.sqrt(dirX * dirX + dirY * dirY);

            if (dist2 > 0.001f) {
                p.dx = dirX / dist2;
                p.dy = dirY / dist2;
            }

            // random projectile element
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
     * Renders the enemy to the screen.
     *
     * @param batch SpriteBatch used for drawing
     * @param left texture when facing left
     * @param right texture when facing right
     * @param spikeTexture texture for spike enemy
     * @param kingTexture texture for boss enemy
     */
    public void draw(
            SpriteBatch batch,
            Texture left,
            Texture right,
            Texture spikeTexture,
            Texture kingTexture) {

        // red flash when hit
        if (hitFlashTimer > 0) {
            batch.setColor(1, 0, 0, 1);
        }

        // choose texture based on type/state
        Texture tex = right;

        if (type == 99) {
            tex = kingTexture;
        } else if (type == 2) {
            tex = spikeTexture;
        } else {
            tex = movingLeft ? left : right;
        }

        // draw enemy
        batch.draw(tex, x, y, size, size);

        // reset color so it doesn't affect other sprites
        batch.setColor(1, 1, 1, 1);
    }
}