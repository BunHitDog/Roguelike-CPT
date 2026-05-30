package com.roguelike.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NumberRenderer {

    // Draws right-to-left (matches old drawNumber)
    public static void draw(
            SpriteBatch batch,
            Texture[] numbers,
            int value,
            int x,
            int y) {

        if (value == 0) {
            batch.draw(numbers[0], x, y, 32, 32);
            return;
        }

        int offset = 0;
        int temp = value;

        while (temp > 0) {

            int digit = temp % 10;

            batch.draw(
                    numbers[digit],
                    x + offset,
                    y,
                    32,
                    32
            );

            offset += 34;
            temp /= 10;
        }
    }

    // Draws left-to-right
    public static void drawForward(
            SpriteBatch batch,
            Texture[] numbers,
            int value,
            int x,
            int y) {

        String s = String.valueOf(value);

        for (int i = 0; i < s.length(); i++) {

            int digit =
                    Character.getNumericValue(
                            s.charAt(i));

            batch.draw(
                    numbers[digit],
                    x + (i * 34),
                    y,
                    32,
                    32
            );
        }
    }
}