package com.roguelike.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NumberRenderer {

    public static void draw(
            SpriteBatch batch,
            Texture[] numbers,
            int value,
            int x,
            int y) {

        String s = String.valueOf(value);

        for (int i = 0; i < s.length(); i++) {
            int digit = Character.getNumericValue(s.charAt(i));

            batch.draw(numbers[digit], x + i * 34, y, 32, 32);
        }
    }

    public static void drawForward(
            SpriteBatch batch,
            Texture[] numbers,
            int value,
            int x,
            int y) {

        draw(batch, numbers, value, x, y);
    }
}