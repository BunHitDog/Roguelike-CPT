package com.roguelike.game;

import java.util.ArrayList;
import java.util.Random;

public class WaveManager {

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