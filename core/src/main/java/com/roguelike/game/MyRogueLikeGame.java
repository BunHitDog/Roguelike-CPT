package com.roguelike.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Random;

public class MyRogueLikeGame extends ApplicationAdapter {

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    // PLAYER
    private Texture ninjaDown, ninjaLeft, ninjaRight, ninjaBack;
    private Texture currentPlayerTexture;

    private float playerX, playerY;

    // ENEMY
    private Texture slimeLeft, slimeRight;

    private class Enemy {
        float x, y;
        float size = 48f;
        float speed = 80f;
        boolean movingLeft;
    }

    private ArrayList<Enemy> enemies;
    private Random random;

    // NUMBERS
    private Texture[] numbers = new Texture[10];

    // WORLD / CAMERA
    private static final float WORLD_SIZE = 2048f;
    private static final float PLAYER_SIZE = 48f;
    private static final float PLAYER_SPEED = 220f;

    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;

    private static final float CAMERA_LERP = 0.1f;

    // WAVES
    private int wave = 1;
    private float waveTimer = 0f;
    private float waveDuration = 8f;

    // HEALTH
    private int currentHealth = 9;
    private float damageTimer = 0f;
    private final float DAMAGE_INTERVAL = 3f;
    private boolean wasTouching = false;

    // TIMER
    private float survivalTime = 0f;

    // SHAKE
    private float shakeTime = 0f;
    private float shakeIntensity = 4f;
    private Random shakeRandom = new Random();

    // BACKGROUND (FIXED)
    private Texture background;

    private boolean debugHitboxes = true;

    @Override
    public void create() {

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // PLAYER
        ninjaDown = new Texture("Ninja.png");
        ninjaLeft = new Texture("Ninja-Left.png");
        ninjaRight = new Texture("Ninja-Right.png");
        ninjaBack = new Texture("Ninja-Back.png");
        currentPlayerTexture = ninjaDown;

        // ENEMY
        slimeLeft = new Texture("SlimeLeft.png");
        slimeRight = new Texture("SlimeRight.png");

        // BACKGROUND (LOAD ONCE)
        background = new Texture("dungeon.png");

        // NUMBERS
        numbers[0] = new Texture("NumZero.png");
        numbers[1] = new Texture("NumOne.png");
        numbers[2] = new Texture("NumTwo.png");
        numbers[3] = new Texture("NumThree.png");
        numbers[4] = new Texture("NumFour.png");
        numbers[5] = new Texture("NumFive.png");
        numbers[6] = new Texture("NumSix.png");
        numbers[7] = new Texture("NumSeven.png");
        numbers[8] = new Texture("NumEight.png");
        numbers[9] = new Texture("NumNine.png");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        camera.zoom = 0.65f;

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, 1280, 720);

        enemies = new ArrayList<>();
        random = new Random();

        playerX = WORLD_SIZE / 2f;
        playerY = WORLD_SIZE / 2f;

        spawnWave();
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();
        survivalTime += delta;

        // ================= PLAYER =================
        float moveX = 0, moveY = 0;

        // WASD + ARROWS FIX
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            moveY++;
            currentPlayerTexture = ninjaBack;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            moveY--;
            currentPlayerTexture = ninjaDown;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveX--;
            currentPlayerTexture = ninjaLeft;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveX++;
            currentPlayerTexture = ninjaRight;
        }

        float len = (float)Math.sqrt(moveX * moveX + moveY * moveY);
        if (len > 0) {
            moveX /= len;
            moveY /= len;
        }

        playerX += moveX * PLAYER_SPEED * delta;
        playerY += moveY * PLAYER_SPEED * delta;

        playerX = Math.max(0, Math.min(playerX, WORLD_SIZE - PLAYER_SIZE));
        playerY = Math.max(0, Math.min(playerY, WORLD_SIZE - PLAYER_SIZE));

        // ================= ENEMIES =================
        int touching = 0;

        for (Enemy e : enemies) {

            float dx = playerX - e.x;
            float dy = playerY - e.y;

            float dist = (float)Math.sqrt(dx * dx + dy * dy);

            if (dist > 0.001f) {
                e.x += (dx / dist) * e.speed * delta;
                e.y += (dy / dist) * e.speed * delta;
            }

            e.movingLeft = dx < 0;

            if (isColliding(playerX, playerY, e.x, e.y, PLAYER_SIZE)) {
                touching++;
                triggerShake();
            }
        }

        // ================= DAMAGE =================
        if (touching > 0) {

            if (!wasTouching) {
                currentHealth -= touching;
                damageTimer = 0f;
            }

            damageTimer += delta;

            if (damageTimer >= DAMAGE_INTERVAL) {
                currentHealth -= touching;
                damageTimer = 0f;
            }

            wasTouching = true;

        } else {
            damageTimer = 0f;
            wasTouching = false;
        }

        currentHealth = Math.max(0, currentHealth);
        if (currentHealth <= 0) return;

        // ================= CAMERA =================
        camera.position.x += (playerX - camera.position.x) * CAMERA_LERP;
        camera.position.y += (playerY - camera.position.y) * CAMERA_LERP;

        if (shakeTime > 0) {
            shakeTime -= delta;
            camera.position.x += (shakeRandom.nextFloat() - 0.5f) * 2f * shakeIntensity;
            camera.position.y += (shakeRandom.nextFloat() - 0.5f) * 2f * shakeIntensity;
        }

        camera.update();

        // ================= WAVES =================
        waveTimer += delta;
        if (waveTimer >= waveDuration) {
            waveTimer = 0;
            wave++;
            spawnWave();
        }

        // ================= RENDER WORLD =================
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // ✅ FIXED BACKGROUND (NOW WORKS)
        batch.draw(background, 0, 0, WORLD_SIZE, WORLD_SIZE);

        batch.draw(currentPlayerTexture, playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);

        for (Enemy e : enemies) {
            Texture t = e.movingLeft ? slimeLeft : slimeRight;
            batch.draw(t, e.x, e.y, e.size, e.size);
        }

        batch.end();

        // ================= UI =================
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        drawNumber(currentHealth, 20, 680);

        int timeInt = (int)survivalTime;
        String s = String.valueOf(timeInt);
        int centerX = (1280 / 2) - (s.length() * 17);
        drawNumber(timeInt, centerX, 680);

        batch.end();

        // ================= DEBUG =================
        if (debugHitboxes) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);

            shapeRenderer.rect(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);

            for (Enemy e : enemies) {
                shapeRenderer.rect(e.x, e.y, e.size, e.size);
            }

            shapeRenderer.end();
        }
    }

    private void triggerShake() {
        shakeTime = 0.12f;
        shakeIntensity = 4f;
    }

    private void drawNumber(int value, int x, int y) {

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

    private boolean isColliding(float x1, float y1, float x2, float y2, float size) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy) < size;
    }

    private void spawnWave() {
        enemies.clear();

        int count = wave;

        for (int i = 0; i < count; i++) {
            Enemy e = new Enemy();
            e.x = random.nextFloat() * WORLD_SIZE;
            e.y = random.nextFloat() * WORLD_SIZE;
            enemies.add(e);
        }
    }

    @Override
    public void dispose() {

        batch.dispose();
        shapeRenderer.dispose();

        ninjaDown.dispose();
        ninjaLeft.dispose();
        ninjaRight.dispose();
        ninjaBack.dispose();

        slimeLeft.dispose();
        slimeRight.dispose();

        background.dispose();

        for (Texture t : numbers) t.dispose();
    }
}