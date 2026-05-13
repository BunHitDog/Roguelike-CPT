package com.roguelike.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.Random;

public class MyRogueLikeGame extends ApplicationAdapter {

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    // ===== TEXTURES =====
    private Texture playerImage;
    private Texture enemyTexture;
    private Texture background;

    // ===== WORLD =====
    private static final float WORLD_SIZE = 2048;

    // ===== ENTITY SETTINGS =====
    private static final float ENTITY_SIZE = 48;

    // ⚡ slightly faster than before
    private static final float SPEED = 160f;
    private static final float ENEMY_SPEED = 160f;

    // ===== PLAYER =====
    private float playerX, playerY;

    // ===== ENEMIES =====
    private class Enemy {
        float x, y;
        float size = ENTITY_SIZE;
        float speed = ENEMY_SPEED;
    }

    private ArrayList<Enemy> enemies;
    private Random random;

    // ===== CAMERA =====
    private OrthographicCamera camera;
    private static final float CAMERA_LERP = 0.1f;

    // ===== WAVES =====
    private int wave = 1;
    private float waveTimer = 0f;
    private float waveDuration = 10f;

    // ===== SCREEN SHAKE =====
    private float shakeTime = 0f;
    private float shakeIntensity = 3f;

    @Override
    public void create() {

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        playerImage = new Texture("libgdx.png");
        enemyTexture = new Texture("enemy.png");
        background = new Texture("dungeon.png");

        playerX = WORLD_SIZE / 2f;
        playerY = WORLD_SIZE / 2f;

        enemies = new ArrayList<>();
        random = new Random();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        camera.zoom = 0.65f;

        spawnWave();
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        // ===== PLAYER MOVEMENT =====
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))
            playerY += SPEED * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))
            playerY -= SPEED * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))
            playerX -= SPEED * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            playerX += SPEED * delta;

        // ===== WORLD BOUNDS =====
        playerX = Math.max(0, Math.min(playerX, WORLD_SIZE - ENTITY_SIZE));
        playerY = Math.max(0, Math.min(playerY, WORLD_SIZE - ENTITY_SIZE));

        // ===== ENEMY AI =====
        for (Enemy e : enemies) {

            float dx = playerX - e.x;
            float dy = playerY - e.y;

            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            if (dist != 0) {
                e.x += (dx / dist) * e.speed * delta;
                e.y += (dy / dist) * e.speed * delta;
            }

            if (isColliding(playerX, playerY, e.x, e.y, ENTITY_SIZE)) {
                triggerShake();
            }
        }

        // ===== CAMERA FOLLOW + SHAKE =====
        camera.position.x += (playerX - camera.position.x) * CAMERA_LERP;
        camera.position.y += (playerY - camera.position.y) * CAMERA_LERP;

        if (shakeTime > 0) {
            shakeTime -= delta;

            float offsetX = (random.nextFloat() - 0.5f) * 2 * shakeIntensity;
            float offsetY = (random.nextFloat() - 0.5f) * 2 * shakeIntensity;

            camera.position.x += offsetX;
            camera.position.y += offsetY;
        }

        camera.update();

        // ===== WAVES =====
        waveTimer += delta;
        if (waveTimer >= waveDuration) {
            waveTimer = 0;
            wave++;
            spawnWave();
        }

        // ===== CLEAR =====
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // ===== DRAW =====
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(background, 0, 0, WORLD_SIZE, WORLD_SIZE);

        batch.draw(playerImage, playerX, playerY, ENTITY_SIZE, ENTITY_SIZE);

        for (Enemy e : enemies) {
            batch.draw(enemyTexture, e.x, e.y, e.size, e.size);
        }

        batch.end();

        // ===== BORDER =====
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(0, 0, WORLD_SIZE, WORLD_SIZE);
        shapeRenderer.end();
    }

    private boolean isColliding(float x1, float y1, float x2, float y2, float size) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy) < size;
    }

    private void triggerShake() {
        shakeTime = 0.15f;
        shakeIntensity = 3f;
    }

    private void spawnWave() {

        enemies.clear();

        int count = 3 + wave * 2;

        for (int i = 0; i < count; i++) {
            Enemy e = new Enemy();
            e.x = random.nextFloat() * WORLD_SIZE;
            e.y = random.nextFloat() * WORLD_SIZE;
            enemies.add(e);
        }

        System.out.println("Wave " + wave + " spawned: " + count + " enemies");
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        playerImage.dispose();
        enemyTexture.dispose();
        background.dispose();
    }
}