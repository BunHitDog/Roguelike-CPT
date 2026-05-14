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
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class MyRogueLikeGame extends ApplicationAdapter {

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    // ======================================================
    // PLAYER
    // ======================================================
    private Texture ninjaDown, ninjaLeft, ninjaRight, ninjaBack;
    private Texture currentPlayerTexture;

    private float playerX, playerY;

    // PLAYER HIT FLASH
    private float playerHitFlashTimer = 0f;

    // ======================================================
    // WEAPON
    // ======================================================
    private Texture kunaiTexture;

    private float shootCooldown = 0f;
    private final float SHOOT_DELAY = 0.25f;

    private class Kunai {
        float x, y;
        float dx, dy;
        float rotation;

        float speed = 600f;
        float size = 26f;
    }

    private ArrayList<Kunai> kunais;

    // ======================================================
    // ENEMY
    // ======================================================
    private Texture slimeLeft, slimeRight;

    private class Enemy {
        float x, y;
        float size = 48f;
        float speed = 80f;
        boolean movingLeft;

        int hp = 3;

        float hitFlashTimer = 0f;
    }

    private ArrayList<Enemy> enemies;
    private Random random;

    // ======================================================
    // BACKGROUND
    // ======================================================
    private Texture background;

    // ======================================================
    // NUMBERS
    // ======================================================
    private Texture[] numbers = new Texture[10];

    // ======================================================
    // WORLD / CAMERA
    // ======================================================
    private static final float WORLD_SIZE = 2048f;
    private static final float PLAYER_SIZE = 48f;
    private static final float PLAYER_SPEED = 220f;

    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;

    private static final float CAMERA_LERP = 0.1f;

    // ======================================================
    // WAVES
    // ======================================================
    private int wave = 1;

    // ======================================================
    // HEALTH
    // ======================================================
    private int currentHealth = 9;

    private float damageTimer = 0f;
    private final float DAMAGE_INTERVAL = 3f;

    private boolean wasTouching = false;

    // ======================================================
    // TIMER
    // ======================================================
    private float survivalTime = 0f;

    // ======================================================
    // SHAKE
    // ======================================================
    private float shakeTime = 0f;
    private float shakeIntensity = 4f;

    private Random shakeRandom = new Random();

    // ======================================================
    // DEBUG
    // ======================================================
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

        // WEAPON
        kunaiTexture = new Texture("Kunai.png");

        // BACKGROUND
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
        kunais = new ArrayList<>();

        random = new Random();

        playerX = WORLD_SIZE / 2f;
        playerY = WORLD_SIZE / 2f;

        spawnWave();
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        survivalTime += delta;
        shootCooldown -= delta;

        // PLAYER FLASH TIMER
        if (playerHitFlashTimer > 0) {
            playerHitFlashTimer -= delta;
        }

        // ======================================================
        // PLAYER MOVEMENT
        // ======================================================
        float moveX = 0;
        float moveY = 0;

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

        // ======================================================
        // MOUSE WORLD POSITION
        // ======================================================
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);

        float mouseDX = mouse.x - (playerX + PLAYER_SIZE / 2f);
        float mouseDY = mouse.y - (playerY + PLAYER_SIZE / 2f);

        float mouseDist = (float)Math.sqrt(mouseDX * mouseDX + mouseDY * mouseDY);

        float aimX = 1;
        float aimY = 0;

        if (mouseDist > 0.001f) {
            aimX = mouseDX / mouseDist;
            aimY = mouseDY / mouseDist;
        }

        float kunaiHoverX = playerX + PLAYER_SIZE / 2f + aimX * 32f;
        float kunaiHoverY = playerY + PLAYER_SIZE / 2f + aimY * 32f;

        // FIXED ROTATION
        float hoverRotation =
                (float)Math.toDegrees(Math.atan2(aimY, aimX)) - 120f;

        // ======================================================
        // SHOOT
        // ======================================================
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)
                && shootCooldown <= 0f) {

            Kunai k = new Kunai();

            k.x = kunaiHoverX;
            k.y = kunaiHoverY;

            k.dx = aimX;
            k.dy = aimY;

            k.rotation = hoverRotation;

            kunais.add(k);

            shootCooldown = SHOOT_DELAY;
        }

        // ======================================================
        // UPDATE KUNAIS
        // ======================================================
        Iterator<Kunai> kunaiIterator = kunais.iterator();

        while (kunaiIterator.hasNext()) {

            Kunai k = kunaiIterator.next();

            k.x += k.dx * k.speed * delta;
            k.y += k.dy * k.speed * delta;

            boolean removeKunai = false;

            Iterator<Enemy> enemyIterator = enemies.iterator();

            while (enemyIterator.hasNext()) {

                Enemy e = enemyIterator.next();

                if (isColliding(k.x, k.y, e.x, e.y, e.size)) {

                    e.hp--;

                    e.hitFlashTimer = 0.15f;

                    removeKunai = true;

                    if (e.hp <= 0) {
                        enemyIterator.remove();
                    }

                    break;
                }
            }

            if (removeKunai) {
                kunaiIterator.remove();
            }
        }

        // ======================================================
        // ENEMIES
        // ======================================================
        int touching = 0;

        for (Enemy e : enemies) {

            if (e.hitFlashTimer > 0) {
                e.hitFlashTimer -= delta;
            }

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

                playerHitFlashTimer = 0.1f;
            }
        }

        // ======================================================
        // DAMAGE
        // ======================================================
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

        if (currentHealth <= 0) {
            return;
        }

        // ======================================================
        // CAMERA
        // ======================================================
        camera.position.x +=
                (playerX - camera.position.x) * CAMERA_LERP;

        camera.position.y +=
                (playerY - camera.position.y) * CAMERA_LERP;

        if (shakeTime > 0) {

            shakeTime -= delta;

            camera.position.x +=
                    (shakeRandom.nextFloat() - 0.5f)
                            * 2f * shakeIntensity;

            camera.position.y +=
                    (shakeRandom.nextFloat() - 0.5f)
                            * 2f * shakeIntensity;
        }

        camera.update();

        // ======================================================
        // NEXT WAVE ONLY WHEN CLEARED
        // ======================================================
        if (enemies.isEmpty()) {

            wave++;

            spawnWave();
        }

        // ======================================================
        // RENDER
        // ======================================================
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(background, 0, 0, WORLD_SIZE, WORLD_SIZE);

        // PLAYER FLASH
        if (playerHitFlashTimer > 0) {
            batch.setColor(Color.RED);
        }

        batch.draw(currentPlayerTexture,
                playerX,
                playerY,
                PLAYER_SIZE,
                PLAYER_SIZE);

        batch.setColor(Color.WHITE);

        // ENEMIES
        for (Enemy e : enemies) {

            Texture tex =
                    e.movingLeft ? slimeLeft : slimeRight;

            if (e.hitFlashTimer > 0) {
                batch.setColor(Color.RED);
            }

            batch.draw(tex, e.x, e.y, e.size, e.size);

            batch.setColor(Color.WHITE);
        }

        // HOVER KUNAI
        batch.draw(
                kunaiTexture,
                kunaiHoverX,
                kunaiHoverY,
                13,
                13,
                26,
                26,
                1,
                1,
                hoverRotation,
                0,
                0,
                kunaiTexture.getWidth(),
                kunaiTexture.getHeight(),
                false,
                false
        );

        // SHOT KUNAIS
        for (Kunai k : kunais) {

            batch.draw(
                    kunaiTexture,
                    k.x,
                    k.y,
                    13,
                    13,
                    26,
                    26,
                    1,
                    1,
                    k.rotation,
                    0,
                    0,
                    kunaiTexture.getWidth(),
                    kunaiTexture.getHeight(),
                    false,
                    false
            );
        }

        batch.end();

        // ======================================================
        // UI
        // ======================================================
        batch.setProjectionMatrix(uiCamera.combined);

        batch.begin();

        // HP
        drawNumber(currentHealth, 20, 680);

        // TIMER
        int timeInt = (int)survivalTime;

        String s = String.valueOf(timeInt);

        int centerX = (1280 / 2) - (s.length() * 17);

        drawNumberForward(timeInt, centerX, 680);

        // ENEMIES LEFT
        drawNumberForward(enemies.size(), 1180, 680);

        batch.end();

        // ======================================================
        // DEBUG
        // ======================================================
        if (debugHitboxes) {

            shapeRenderer.setProjectionMatrix(camera.combined);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            shapeRenderer.setColor(Color.RED);

            shapeRenderer.rect(
                    playerX,
                    playerY,
                    PLAYER_SIZE,
                    PLAYER_SIZE
            );

            for (Enemy e : enemies) {
                shapeRenderer.rect(e.x, e.y, e.size, e.size);
            }

            shapeRenderer.end();
        }
    }

    // ======================================================
    // SHAKE
    // ======================================================
    private void triggerShake() {
        shakeTime = 0.12f;
        shakeIntensity = 4f;
    }

    // ======================================================
    // NUMBER DRAW
    // ======================================================
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

    // TIMER FIX
    private void drawNumberForward(int value, int x, int y) {

        String s = String.valueOf(value);

        for (int i = 0; i < s.length(); i++) {

            int digit =
                    Character.getNumericValue(s.charAt(i));

            batch.draw(
                    numbers[digit],
                    x + (i * 34),
                    y,
                    32,
                    32
            );
        }
    }

    // ======================================================
    // COLLISION
    // ======================================================
    private boolean isColliding(
            float x1,
            float y1,
            float x2,
            float y2,
            float size) {

        float dx = x1 - x2;
        float dy = y1 - y2;

        return Math.sqrt(dx * dx + dy * dy) < size;
    }

    // ======================================================
    // WAVES
    // ======================================================
    private void spawnWave() {

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

        kunaiTexture.dispose();

        background.dispose();

        for (Texture t : numbers) {
            t.dispose();
        }
    }
}