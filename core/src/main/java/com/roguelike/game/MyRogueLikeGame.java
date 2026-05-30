package com.roguelike.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;

public class MyRogueLikeGame extends ApplicationAdapter {

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    // ======================================================
    // PLAYER
    // ======================================================
    private Texture ninjaDown, ninjaLeft, ninjaRight, ninjaBack;
    private Texture currentPlayerTexture;

    private float playerX, playerY;

    private float playerHitFlashTimer = 0f;

    // STATUS EFFECTS
    private float burnTimer = 0f;
    private float burnDamageTimer = 0f;

    private float slowTimer = 0f;

    private float stunTimer = 0f;

    // ======================================================
    // WEAPON
    // ======================================================
    private Texture kunaiTexture;

    private float shootCooldown = 0f;
    private final float SHOOT_DELAY = 0.25f;

    private ArrayList<Kunai> kunais;

    // ======================================================
    // ENEMY PROJECTILES
    // ======================================================
    private Texture fireBallTexture;
    private Texture iceBallTexture;
    private Texture lightningBallTexture;

    private ArrayList<EnemyProjectile> enemyProjectiles;

    // ======================================================
    // ENEMY
    // ======================================================
    private Texture slimeLeft, slimeRight;

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
    private Texture xpTexture;
    private Texture bigXpTexture;
    private Texture statMenu;
    private ArrayList<XpOrb> xpOrbs;

    // ======================================================
    // WORLD / CAMERA
    // ======================================================
    private float worldWidth;
    private float worldHeight;

    private float playableMaxX;
    private float playableMaxY;
    private float playableMinX;
    private float playableMinY;


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

        // PROJECTILES
        fireBallTexture = new Texture("Fire Ball.png");
        iceBallTexture = new Texture("Ice Ball.png");
        lightningBallTexture = new Texture("Lightning Ball.png");
        xpTexture = new Texture("Xp.png");
        
        // MENU
        bigXpTexture = new Texture("BigXp.png");
        statMenu = new Texture("StatMenu.png");

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

        // TILEMAP
        map = new TmxMapLoader().load("maps/cpt_map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1f);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        camera.zoom = 0.65f;

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, 1280, 720);

        enemies = new ArrayList<>();
        kunais = new ArrayList<>();
        enemyProjectiles = new ArrayList<>();
        xpOrbs = new ArrayList<>();

        random = new Random();

        // Get actual map size from TiledMap
        worldWidth = map.getProperties().get("width", Integer.class) * 
                    map.getProperties().get("tilewidth", Integer.class);

        worldHeight = map.getProperties().get("height", Integer.class) * 
                    map.getProperties().get("tileheight", Integer.class);

        // === IMPROVED PLAYABLE AREA ===
        float border = 70f;                    // ← Change this number to adjust border thickness
        playableMinX = border;
        playableMinY = border;
        playableMaxX = worldWidth - border;
        playableMaxY = worldHeight - border;

        System.out.println("Map size loaded: " + worldWidth + " x " + worldHeight);
        System.out.println("Playable border set to: " + border + " pixels");

        // Spawn player in the center of the actual map
        playerX = (playableMinX + playableMaxX) / 2f - PLAYER_SIZE / 2f;
        playerY = (playableMinY + playableMaxY) / 2f - PLAYER_SIZE / 2f;
        spawnWave();
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        survivalTime += delta;

        shootCooldown -= delta;

        // ======================================================
        // STATUS EFFECTS
        // ======================================================
        if (burnTimer > 0) {

            burnTimer -= delta;

            burnDamageTimer += delta;

            if (burnDamageTimer >= 10f) {

                currentHealth -= 1;

                burnDamageTimer = 0f;
            }
        }

        if (slowTimer > 0) {
            slowTimer -= delta;
        }

        if (stunTimer > 0) {
            stunTimer -= delta;
        }

        if (playerHitFlashTimer > 0) {
            playerHitFlashTimer -= delta;
        }

        // ======================================================
        // PLAYER MOVEMENT
        // ======================================================
        float moveX = 0;
        float moveY = 0;

        if (stunTimer <= 0) {

            if (Gdx.input.isKeyPressed(Input.Keys.W)
                    || Gdx.input.isKeyPressed(Input.Keys.UP)) {

                moveY++;
                currentPlayerTexture = ninjaBack;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.S)
                    || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {

                moveY--;
                currentPlayerTexture = ninjaDown;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.A)
                    || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

                moveX--;
                currentPlayerTexture = ninjaLeft;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)
                    || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {

                moveX++;
                currentPlayerTexture = ninjaRight;
            }
        }

        float len =
                (float)Math.sqrt(moveX * moveX + moveY * moveY);

        if (len > 0) {

            moveX /= len;
            moveY /= len;
        }

        float currentSpeed = PLAYER_SPEED;

        if (slowTimer > 0) {
            currentSpeed *= 0.5f;
        }

        playerX += moveX * currentSpeed * delta;
        playerY += moveY * currentSpeed * delta;

        // === KEEP PLAYER WITHIN MAP BOUNDS ===
        playerX = Math.max(playableMinX, Math.min(playerX, playableMaxX - PLAYER_SIZE));
        playerY = Math.max(playableMinY, Math.min(playerY, playableMaxY - PLAYER_SIZE));

        // ======================================================
        // MOUSE POSITION
        // ======================================================
        Vector3 mouse =
                new Vector3(
                        Gdx.input.getX(),
                        Gdx.input.getY(),
                        0);

        camera.unproject(mouse);

        float mouseDX =
                mouse.x - (playerX + PLAYER_SIZE / 2f);

        float mouseDY =
                mouse.y - (playerY + PLAYER_SIZE / 2f);

        float mouseDist =
                (float)Math.sqrt(
                        mouseDX * mouseDX
                                + mouseDY * mouseDY);

        float aimX = 1;
        float aimY = 0;

        if (mouseDist > 0.001f) {

            aimX = mouseDX / mouseDist;
            aimY = mouseDY / mouseDist;
        }

        float kunaiHoverX =
                playerX + PLAYER_SIZE / 2f + aimX * 32f;

        float kunaiHoverY =
                playerY + PLAYER_SIZE / 2f + aimY * 32f;

        // KUNAI ROTATION
        float hoverRotation =
            (float)Math.toDegrees(Math.atan2(aimY, aimX)) + 125f;

        // ======================================================
        // SHOOT
        // ======================================================
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)
                && shootCooldown <= 0f
                && stunTimer <= 0) {

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

            k.update(delta);

            boolean hit = false;

            Iterator<Enemy> enemyIterator = enemies.iterator();

            while (enemyIterator.hasNext()) {

                Enemy e = enemyIterator.next();

                if (CollisionUtils.isColliding(
        k.x, k.y,
        e.x, e.y,
        e.size / 2f)) {

                    e.hp--;
                    e.hitFlashTimer = 0.15f;

                    hit = true;

                    if (e.hp <= 0) {

                        // XP DROP (KEEP YOUR EXISTING CODE HERE EXACTLY)
                        for (int i = 0; i < 3; i++) {
                            if (random.nextFloat() < 0.25f) {

                                float angle = random.nextFloat() * 360f;

                                float dx = (float)Math.cos(Math.toRadians(angle));
                                float dy = (float)Math.sin(Math.toRadians(angle));

                                xpOrbs.add(new XpOrb(
                                        e.x + e.size / 2f,
                                        e.y + e.size / 2f,
                                        dx,
                                        dy,
                                        false));
                            }
                        }

                        if (random.nextFloat() < 0.10f) {

                            float angle = random.nextFloat() * 360f;

                            float dx = (float)Math.cos(Math.toRadians(angle));
                            float dy = (float)Math.sin(Math.toRadians(angle));

                            xpOrbs.add(new XpOrb(
                                    e.x + e.size / 2f,
                                    e.y + e.size / 2f,
                                    dx,
                                    dy,
                                    true));
                        }

                        enemyIterator.remove();
                    }

                    break;
                }
            }

            if (hit) kunaiIterator.remove();
        }

        // ======================================================
        // UPDATE ENEMY PROJECTILES
        // ======================================================
        Iterator<EnemyProjectile> projectileIterator =
                enemyProjectiles.iterator();

        while (projectileIterator.hasNext()) {

            EnemyProjectile p =
                    projectileIterator.next();

            p.x += p.dx * p.speed * delta;
            p.y += p.dy * p.speed * delta;

            if (CollisionUtils.isColliding(
        p.x, p.y,
        playerX, playerY,
        PLAYER_SIZE)) { 

                currentHealth -= 1;

                playerHitFlashTimer = 0.2f;

                if (p.type.equals("fire")) {

                    burnTimer = 20f;
                    burnDamageTimer = 0f;
                }

                else if (p.type.equals("ice")) {

                    slowTimer = 3f;
                }

                else {

                    stunTimer = 1f;
                }

                projectileIterator.remove();
            }
        }

        Iterator<XpOrb> xpIterator = xpOrbs.iterator();

        while (xpIterator.hasNext()) {

            XpOrb orb = xpIterator.next();

            orb.update(delta);
        }

        // ======================================================
        // ENEMIES
        // ======================================================
        int touching = 0;

        for (Enemy e : enemies) {

            e.update(delta, playerX, playerY, enemyProjectiles, fireBallTexture, iceBallTexture, lightningBallTexture, random);

            if (CollisionUtils.isColliding(
        e.x, e.y,
        playerX, playerY,
        e.size / 2f)) {

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
        }

        else {

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
                (playerX - camera.position.x)
                        * CAMERA_LERP;

        camera.position.y +=
                (playerY - camera.position.y)
                        * CAMERA_LERP;

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

        float cameraHalfWidth = camera.viewportWidth * camera.zoom * 0.5f;
        float cameraHalfHeight = camera.viewportHeight * camera.zoom * 0.5f;

        camera.position.x = Math.max(cameraHalfWidth, Math.min(camera.position.x, worldWidth - cameraHalfWidth));
        camera.position.y = Math.max(cameraHalfHeight, Math.min(camera.position.y, worldHeight - cameraHalfHeight));

        // ======================================================
        // NEXT WAVE
        // ======================================================
        if (enemies.isEmpty()) {

            wave++;

            spawnWave();
        }

        // ======================================================
        // RENDER
        // ======================================================
        Gdx.gl.glClearColor(0.92f, 0.92f, 0.92f, 1);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);
        renderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // PLAYER
        if (playerHitFlashTimer > 0 || burnTimer > 0) {
            batch.setColor(Color.RED);
        }

        batch.draw(
                currentPlayerTexture,
                playerX,
                playerY,
                PLAYER_SIZE,
                PLAYER_SIZE);

        batch.setColor(Color.WHITE);

        // ENEMIES
        for (Enemy e : enemies) {

            Texture tex =
                    e.movingLeft
                            ? slimeLeft
                            : slimeRight;

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

        // ENEMY PROJECTILES
        for (EnemyProjectile p : enemyProjectiles) {

            batch.draw(
                    p.texture,
                    p.x,
                    p.y,
                    p.size,
                    p.size
            );
        }

        for (XpOrb orb : xpOrbs) {

            batch.draw(
                orb.big ? bigXpTexture : xpTexture,
                orb.x,
                orb.y,
                orb.size,
                orb.size
            );
        }

        batch.end();

        // ======================================================
        // UI
        // ======================================================
        batch.setProjectionMatrix(uiCamera.combined);

        batch.begin();

        batch.draw(
                statMenu,
                10,
                10,
                100, 
                150
        );

        drawNumber(currentHealth, 20, 680);

        int timeInt = (int)survivalTime;

        String s = String.valueOf(timeInt);

        int centerX =
                (1280 / 2) - (s.length() * 17);

        drawNumberForward(timeInt, centerX, 680);

        drawNumberForward(enemies.size(), 1180, 680);

        batch.end();

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

            batch.draw(
                    numbers[digit],
                    x + offset,
                    y,
                    32,
                    32);

            offset += 34;

            temp /= 10;
        }
    }

    // ======================================================
    // TIMER FIX
    // ======================================================
    private void drawNumberForward(
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

    // ======================================================
    // WAVES
    // ======================================================
    private void spawnWave() {

        int count = wave;

        int shooterCount =
                Math.max(1, wave / 3);

        for (int i = 0; i < count; i++) {

            Enemy e = new Enemy();

            e.x = playableMinX + random.nextFloat() * (playableMaxX - playableMinX - e.size);
            e.y = playableMinY + random.nextFloat() * (playableMaxY - playableMinY - e.size);;

            if (i < shooterCount) {

                e.shooter = true;

                e.size = 48f;

                e.hp = 3; // shooter slimes

                e.shootTimer =
                        random.nextFloat() * 1.25f;

            } else {

                e.shooter = false;

                e.size = 96f; // regular slimes bigger

                e.hp = 9; // 3x shooter HP
            }

            enemies.add(e);
        }
    }

    @Override
    public void dispose() {

        batch.dispose();
        bigXpTexture.dispose();

        shapeRenderer.dispose();

        ninjaDown.dispose();
        ninjaLeft.dispose();
        ninjaRight.dispose();
        ninjaBack.dispose();

        slimeLeft.dispose();
        slimeRight.dispose();

        kunaiTexture.dispose();

        fireBallTexture.dispose();
        iceBallTexture.dispose();
        lightningBallTexture.dispose();

        xpTexture.dispose();

        for (Texture t : numbers) {
            t.dispose();
        }

        if (map != null) map.dispose();
        if (renderer != null) renderer.dispose();
    }
}