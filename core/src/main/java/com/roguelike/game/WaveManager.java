package com.roguelike.game;

import java.util.ArrayList;
import java.util.Random;

public class WaveManager {

    public static void spawnWave(
            ArrayList<Enemy> enemies,
            int wave,
            float worldSize,
            Random random) {

        int count = wave;
        int shooterCount = Math.max(1, wave / 3);

        for (int i = 0; i < count; i++) {

            Enemy e = new Enemy();

            e.x = random.nextFloat() * worldSize;
            e.y = random.nextFloat() * worldSize;

            if (i < shooterCount) {
                e.shooter = true;
                e.size = 48f;
                e.hp = 3;
            } else {
                e.hp = 9;
            }

            enemies.add(e);
        }
    }
}