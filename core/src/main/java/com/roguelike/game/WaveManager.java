package com.roguelike.game;

import java.util.ArrayList;
import java.util.Random;

/**
 * Utility class responsible for generating enemy waves.
 *
 * Each wave increases difficulty by:
 * - Increasing enemy count
 * - Introducing shooter enemies
 * - Randomly spawning enemies within playable bounds
 */
public class WaveManager {

    /**
     * Spawns a new wave of enemies into the game.
     *
     * Enemy composition:
     * - Base enemies scale with wave number
     * - Shooter enemies appear every few waves
     *
     * @param enemies List to populate with newly spawned enemies
     * @param wave Current wave number (controls difficulty scaling)
     * @param playableMinX Minimum X boundary for spawning
     * @param playableMinY Minimum Y boundary for spawning
     * @param playableMaxX Maximum X boundary for spawning
     * @param playableMaxY Maximum Y boundary for spawning
     * @param random Random instance used for spawn variation
     */
    public static void spawnWave(
            ArrayList<Enemy> enemies,
            int wave,
            float playableMinX,
            float playableMinY,
            float playableMaxX,
            float playableMaxY,
            Random random) {

        int count = wave;
        int shooterCount = Math.max(1, wave / 3);

        for (int i = 0; i < count; i++) {

            Enemy e = new Enemy();

            // Determine enemy type
            if (i < shooterCount) {

                e.shooter = true;
                e.size = 48f;
                e.hp = 3;
                e.shootTimer = random.nextFloat() * 1.25f;

            } else {

                e.shooter = false;
                e.size = 96f;
                e.hp = 9;
            }

            // Random spawn position inside bounds
            e.x = playableMinX +
                    random.nextFloat() *
                    (playableMaxX - playableMinX - e.size);

            e.y = playableMinY +
                    random.nextFloat() *
                    (playableMaxY - playableMinY - e.size);

            enemies.add(e);
        }
    }
}