package com.roguelike.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Utility class for rendering numbers using sprite-based digits.
 *
 * Supports both left-to-right and right-to-left number rendering.
 */
public class NumberRenderer {

    /**
     * Draws a number right-to-left (units first).
     *
     * Example: 123 becomes 3-2-1 visually.
     *
     * @param batch SpriteBatch used for rendering
     * @param numbers Array of digit textures (0–9)
     * @param value Number to render
     * @param x Starting x position
     * @param y Starting y position
     */
    public static void draw(SpriteBatch batch, Texture[] numbers, int value, int x, int y) {

        if (value == 0) {
            batch.draw(numbers[0], x, y, 32, 32);
            return;
        }

        int offset = 0;
        int temp = value;

        while (temp > 0) {

            int digit = temp % 10;

            batch.draw(numbers[digit], x + offset, y, 32, 32);

            offset += 34;
            temp /= 10;
        }
    }

    /**
     * Draws a number left-to-right (standard reading order).
     *
     * Example: 123 becomes 1-2-3 visually.
     *
     * @param batch SpriteBatch used for rendering
     * @param numbers Array of digit textures (0–9)
     * @param value Number to render
     * @param x Starting x position
     * @param y Starting y position
     */
    public static void drawForward(SpriteBatch batch, Texture[] numbers, int value, int x, int y) {

        String s = String.valueOf(value);

        for (int i = 0; i < s.length(); i++) {

            int digit = Character.getNumericValue(s.charAt(i));

            batch.draw(numbers[digit], x + (i * 34), y, 32, 32);
        }
    }
}