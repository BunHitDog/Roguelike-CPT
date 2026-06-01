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

    // MAP / RENDERING
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    // PLAYER
    private Texture ninjaDown, ninjaLeft, ninjaRight, ninjaBack;
    private Texture currentPlayerTexture;
    private float playerX, playerY;
    private float playerHitFlashTimer = 0f;

    // STATUS EFFECTS
    private float burnTimer = 0f;
    private float burnDamageTimer = 0f;
    private float slowTimer = 0f;
    private float stunTimer = 0f;

    // WEAPON
    private Texture kunaiTexture;
    private float shootCooldown = 0f;
    private final float BASE_SHOOT_DELAY = 0.25f;
    private ArrayList<Kunai> kunais;

    // ENEMY PROJECTILES
    private Texture fireBallTexture, iceBallTexture, lightningBallTexture;
    private ArrayList<EnemyProjectile> enemyProjectiles;

    // ENEMIES
    private Texture slimeLeft, slimeRight, spikeSlime, kingSlime;
    private ArrayList<Enemy> enemies;
    private Random random;

    // POTIONS
    private Texture buffPotion, healthPotion;
    private boolean hasBuffPotion = false;
    private boolean hasHealthPotion = false;
    private boolean buffActive = false;
    private ArrayList<Potion> potions;

    // BACKGROUND
    private Texture background;

    // UI / HUD TEXTURES
    private Texture[] numbers = new Texture[10];
    private Texture xpTexture, bigXpTexture;
    private Texture statMenu, hpBar, xpBar, slash, levelIcon;
    private Texture controlMenu;
    private ArrayList<XpOrb> xpOrbs;

    // WORLD / CAMERA
    private float worldWidth, worldHeight;
    private float playableMaxX, playableMaxY, playableMinX, playableMinY;

    private static final float WORLD_SIZE = 2048f;
    private static final float PLAYER_SIZE = 48f;
    private static final float PLAYER_SPEED = 220f;

    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    private static final float CAMERA_LERP = 0.1f;

    // GAME STATE
    private int wave = 1;

    // HEALTH
    private int maxHealth = 5;
    private int currentHealth = maxHealth;
    private float damageTimer = 0f;
    private final float DAMAGE_INTERVAL = 3f;
    private boolean wasTouching = false;

    // TIMER
    private float survivalTime = 0f;

    // SCREEN SHAKE
    private float shakeTime = 0f;
    private float shakeIntensity = 4f;
    private Random shakeRandom = new Random();

    // DEBUG
    private boolean debugHitboxes = true;

    // PAUSE MENU
    private boolean paused = false;
    private Texture startButton, exitButton, pauseMenu;
    private float pauseCenterX = 640, pauseCenterY = 360;
    private int pauseSelection = 0;
    private float pauseAnim = 0f;

    // CONTROL MENU
    private boolean showControls = false;

    // LEVEL UP UI
    private boolean levelUpScreen = false;
    private int levelUpSelection = 0;
    private boolean levelingUp = false;
    private Texture levelScreen, healthIcon, magicIcon, speedIcon, strengthIcon;

    // LEVEL SYSTEM
    private int level = 1;
    private int xp = 0;
    private int xpToNextLevel = 20;

    // PLAYER STATS
    private int kunaiDamage = 1;
    private float speedMultiplier = 1f;
    private float fireRateMultiplier = 1f;

    private static final float SLIME_BASE_SPEED = PLAYER_SPEED * 0.5f;
    private static final float SPIKE_SLIME_SPEED = PLAYER_SPEED * 0.75f;
    public static final float SHOOTER_FIRE_INTERVAL = 2f;
    public static final float BOSS_SPEED = PLAYER_SPEED * 0.25f;
    public static final float BOSS_SHOOT_INTERVAL = SHOOTER_FIRE_INTERVAL / 2f;

    @Override
    public void create() {

        // CORE RENDERING
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // CAMERA SETUP
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        camera.zoom = 0.65f;

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, 1280, 720);

        // PLAYER TEXTURES
        ninjaDown = new Texture("Ninja.png");
        ninjaLeft = new Texture("Ninja-Left.png");
        ninjaRight = new Texture("Ninja-Right.png");
        ninjaBack = new Texture("Ninja-Back.png");
        currentPlayerTexture = ninjaDown;

        // ENEMY TEXTURES
        slimeLeft = new Texture("SlimeLeft.png");
        slimeRight = new Texture("SlimeRight.png");
        spikeSlime = new Texture("SpikeSlime.png");
        kingSlime = new Texture("KingSlime.png");

        // WEAPON
        kunaiTexture = new Texture("Kunai.png");

        // PROJECTILES + XP
        fireBallTexture = new Texture("Fire Ball.png");
        iceBallTexture = new Texture("Ice Ball.png");
        lightningBallTexture = new Texture("Lightning Ball.png");
        xpTexture = new Texture("Xp.png");

        // POTIONS
        buffPotion = new Texture("BuffPotion.png");
        healthPotion = new Texture("HealthPotion.png");

        // UI / MENU TEXTURES
        bigXpTexture = new Texture("BigXp.png");
        statMenu = new Texture("StatMenu.png");
        startButton = new Texture("Start.png");
        exitButton = new Texture("Exit.png");
        hpBar = new Texture("Hp.png");
        xpBar = new Texture("Experience.png");
        slash = new Texture("Slash.png");
        pauseMenu = new Texture("PauseMenu.png");
        levelIcon = new Texture("Level.png");
        levelScreen = new Texture("LevelScreen.png");
        healthIcon = new Texture("Health.png");
        magicIcon = new Texture("Magic.png");
        speedIcon = new Texture("Speed.png");
        strengthIcon = new Texture("Strength.png");
        controlMenu = new Texture("ControlMenu.png");

        // NUMBER TEXTURES
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

        // MAP
        map = new TmxMapLoader().load("maps/cpt_map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1f);

        // GAME DATA STRUCTURES
        enemies = new ArrayList<>();
        kunais = new ArrayList<>();
        enemyProjectiles = new ArrayList<>();
        xpOrbs = new ArrayList<>();
        potions = new ArrayList<>();
        random = new Random();

        // WORLD SIZE
        worldWidth = map.getProperties().get("width", Integer.class) *
                    map.getProperties().get("tilewidth", Integer.class);

        worldHeight = map.getProperties().get("height", Integer.class) *
                    map.getProperties().get("tileheight", Integer.class);

        // PLAYABLE AREA
        float border = 70f;
        playableMinX = border;
        playableMinY = border;
        playableMaxX = worldWidth - border;
        playableMaxY = worldHeight - border;

        System.out.println("Map size loaded: " + worldWidth + " x " + worldHeight);
        System.out.println("Playable border set to: " + border + " pixels");

        // PLAYER SPAWN
        playerX = (playableMinX + playableMaxX) / 2f - PLAYER_SIZE / 2f;
        playerY = (playableMinY + playableMaxY) / 2f - PLAYER_SIZE / 2f;

        // START GAME
        spawnWave();
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        // ======================================================
        // LEVEL UP SCREEN (GAME PAUSED)
        // ======================================================
        // If active, game logic is frozen and only level-up UI runs
        if (levelUpScreen) {

            // INPUT: move selection left/right
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                levelUpSelection--;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                levelUpSelection++;
            }

            // WRAP SELECTION (0–3 options loop)
            if (levelUpSelection < 0) levelUpSelection = 3;
            if (levelUpSelection > 3) levelUpSelection = 0;

            // CONFIRM SELECTION (apply upgrade)
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
                Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {

                if (levelUpSelection == 0) {
                    maxHealth += 1;
                    currentHealth = maxHealth; // full heal on upgrade
                }

                else if (levelUpSelection == 1) {
                    fireRateMultiplier += 0.01f; // faster shooting
                }

                else if (levelUpSelection == 2) {
                    speedMultiplier += 0.01f; // movement boost
                }

                else if (levelUpSelection == 3) {
                    kunaiDamage += 1; // damage increase
                }

                levelUpScreen = false;
                levelingUp = false;
            }

            // CLEAR SCREEN (level-up only view)
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // UI RENDER
            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();

            batch.draw(levelScreen, 0, 0, 1280, 720);

            float startX = 320;
            float y = 250;

            // ICON HIGHLIGHT ALPHA (selected = bright, others = faded)
            float alpha0 = (levelUpSelection == 0) ? 1f : 0.4f;
            float alpha1 = (levelUpSelection == 1) ? 1f : 0.4f;
            float alpha2 = (levelUpSelection == 2) ? 1f : 0.4f;
            float alpha3 = (levelUpSelection == 3) ? 1f : 0.4f;

            // HEALTH UPGRADE
            batch.setColor(1, 1, 1, alpha0);
            batch.draw(healthIcon, startX + 680, y - 50, 100, 100);

            // MAGIC / FIRE RATE UPGRADE
            batch.setColor(1, 1, 1, alpha1);
            batch.draw(magicIcon, startX + 680, y + 100, 100, 100);

            // SPEED UPGRADE
            batch.setColor(1, 1, 1, alpha2);
            batch.draw(speedIcon, startX + 140, y - 50, 100, 100);

            // STRENGTH / DAMAGE UPGRADE
            batch.setColor(1, 1, 1, alpha3);
            batch.draw(strengthIcon, startX + 140, y + 100, 100, 100);

            // RESET COLOR (important after alpha changes)
            batch.setColor(1, 1, 1, 1);

            batch.end();

            return; // stop full game render while level-up screen is active
        }

        // ======================================================
        // PAUSE TOGGLE
        // ======================================================
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            paused = !paused;

            // reset menu selection when opening pause menu
            if (paused) {
                pauseSelection = 0;
            }
        }

        // ======================================================
        // CONTROL MENU TOGGLE
        // ======================================================
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            showControls = !showControls;
        }

        // ======================================================
        // PAUSED GAME STATE (FREEZE GAME LOGIC)
        // ======================================================
        if (paused) {

            // MENU NAVIGATION
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
                Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                pauseSelection = 0;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) ||
                Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                pauseSelection = 1;
            }

            // MENU SELECT
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
                Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {

                if (pauseSelection == 0) {
                    paused = false; // resume game
                }

                if (pauseSelection == 1) {
                    Gdx.app.exit(); // exit game
                }
            }

            // CLEAR SCREEN (avoid frame artifacts)
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // RENDER PAUSE MENU UI ONLY
            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();

            batch.setColor(Color.WHITE);
            batch.draw(pauseMenu, 0, 0, 1280, 720);

            float centerX = 640 - 100;
            float centerY = 360;

            // START / RESUME BUTTON
            batch.setColor(pauseSelection == 0 ? 1f : 0.5f,
                        pauseSelection == 0 ? 1f : 0.5f,
                        pauseSelection == 0 ? 1f : 0.5f, 1f);
            batch.draw(startButton, centerX, centerY + 60, 200, 60);

            // EXIT BUTTON
            batch.setColor(pauseSelection == 1 ? 1f : 0.5f,
                        pauseSelection == 1 ? 1f : 0.5f,
                        pauseSelection == 1 ? 1f : 0.5f, 1f);
            batch.draw(exitButton, centerX, centerY - 60, 200, 60);

            // reset color state after tinting
            batch.setColor(Color.WHITE);

            batch.end();

            return;
        }

        // ======================================================
        // CONTROL MENU
        // ======================================================
        if (showControls) {

            Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();

            batch.draw(controlMenu, 0, 0, 1280, 720);

            batch.end();

            return;
        }

        // ======================================================
        // GAME RUNNING (NORMAL MODE)
        // ======================================================
        survivalTime += delta;
        shootCooldown -= delta;

        // ======================================================
        // POTION INPUT (USE ITEMS)
        // ======================================================

        // Q = health potion (full heal)
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) && hasHealthPotion) {
            currentHealth = maxHealth;
            hasHealthPotion = false;
        }

        // E = buff potion (damage boost)
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && hasBuffPotion) {
            buffActive = true;
            hasBuffPotion = false;
        }

        // ======================================================
        // STATUS EFFECTS (DOT / CONTROL STATES)
        // ======================================================
        if (burnTimer > 0) {

            burnTimer -= delta;
            burnDamageTimer += delta;

            // burn damage triggers once after duration threshold
            if (burnDamageTimer >= 10f) {
                currentHealth -= 1;
                burnTimer = 0f;
                burnDamageTimer = 0f;
            }
        }

        if (stunTimer > 0) {
            stunTimer -= delta;
        }

        if (playerHitFlashTimer > 0) {
            playerHitFlashTimer -= delta;
        }

        // ======================================================
        // PLAYER MOVEMENT INPUT
        // ======================================================
        float moveX = 0;
        float moveY = 0;

        if (stunTimer <= 0) {

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
        }

        // normalize movement vector
        float len = (float)Math.sqrt(moveX * moveX + moveY * moveY);
        if (len > 0) {
            moveX /= len;
            moveY /= len;
        }

        // apply speed modifiers
        float currentSpeed = PLAYER_SPEED * speedMultiplier;

        if (slowTimer > 0) {
            currentSpeed *= 0.5f;
            slowTimer -= delta;
        }

        // apply movement
        playerX += moveX * currentSpeed * delta;
        playerY += moveY * currentSpeed * delta;

        // clamp to map bounds
        playerX = Math.max(playableMinX, Math.min(playerX, playableMaxX - PLAYER_SIZE));
        playerY = Math.max(playableMinY, Math.min(playerY, playableMaxY - PLAYER_SIZE));

        // ======================================================
        // MOUSE AIMING
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

        // weapon hover position (visual aim indicator)
        float kunaiHoverX = playerX + PLAYER_SIZE / 2f + aimX * 32f;
        float kunaiHoverY = playerY + PLAYER_SIZE / 2f + aimY * 32f;

        // rotation for thrown kunai
        float hoverRotation = (float)Math.toDegrees(Math.atan2(aimY, aimX)) + 125f;

        // ======================================================
        // SHOOTING
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

            shootCooldown = BASE_SHOOT_DELAY / fireRateMultiplier;
        }

        // ======================================================
        // UPDATE KUNAIS (PROJECTILES + ENEMY COLLISION)
        // ======================================================
        Iterator<Kunai> kunaiIterator = kunais.iterator();

        while (kunaiIterator.hasNext()) {

            Kunai k = kunaiIterator.next();

            k.update(delta);

            boolean hit = false;

            // check collision against all enemies
            Iterator<Enemy> enemyIterator = enemies.iterator();

            while (enemyIterator.hasNext()) {

                Enemy e = enemyIterator.next();

                if (CollisionUtils.isColliding(
                        k.x, k.y,
                        e.x, e.y,
                        e.size / 2f)) {

                    // damage calculation (buff = x5 damage)
                    int damage = buffActive ? kunaiDamage * 5 : kunaiDamage;
                    e.hp -= damage;
                    e.hitFlashTimer = 0.15f;

                    hit = true;

                    // enemy death logic
                    if (e.hp <= 0) {

                        // ===========================
                        // POTION DROPS (RNG BASED)
                        // ===========================

                        // health potion drop (5%)
                        if (random.nextFloat() < 0.05f) {

                            float angle = random.nextFloat() * 360f;
                            float dx = (float)Math.cos(Math.toRadians(angle));
                            float dy = (float)Math.sin(Math.toRadians(angle));

                            potions.add(new Potion(
                                    e.x + e.size / 2f,
                                    e.y + e.size / 2f,
                                    dx,
                                    dy,
                                    true // health potion
                            ));
                        }

                        // buff potion drop (5%)
                        if (random.nextFloat() < 0.05f) {

                            float angle = random.nextFloat() * 360f;
                            float dx = (float)Math.cos(Math.toRadians(angle));
                            float dy = (float)Math.sin(Math.toRadians(angle));

                            potions.add(new Potion(
                                    e.x + e.size / 2f,
                                    e.y + e.size / 2f,
                                    dx,
                                    dy,
                                    false // buff potion
                            ));
                        }

                        // ===========================
                        // XP DROPS
                        // ===========================

                        // normal XP orbs
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
                                        false
                                ));
                            }
                        }

                        // big XP orb (rare)
                        if (random.nextFloat() < 0.10f) {

                            float angle = random.nextFloat() * 360f;
                            float dx = (float)Math.cos(Math.toRadians(angle));
                            float dy = (float)Math.sin(Math.toRadians(angle));

                            xpOrbs.add(new XpOrb(
                                    e.x + e.size / 2f,
                                    e.y + e.size / 2f,
                                    dx,
                                    dy,
                                    true
                            ));
                        }

                        enemyIterator.remove();
                    }

                    break; // stop after first hit enemy
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

            EnemyProjectile p = projectileIterator.next();

            // movement
            p.x += p.dx * p.speed * delta;
            p.y += p.dy * p.speed * delta;

            // collision with player
            if (CollisionUtils.isColliding(
                    p.x, p.y,
                    playerX, playerY,
                    PLAYER_SIZE)) {

                currentHealth -= 1;
                playerHitFlashTimer = 0.2f;

                // status effects based on projectile type
                if (p.type.equals("fire")) {
                    burnTimer = 20f;
                    burnDamageTimer = 0f;
                }

                else if (p.type.equals("ice")) {
                    slowTimer = 3f;
                }

                else {
                    stunTimer = 0.5f;
                }

                projectileIterator.remove();
            }
        }

        // ======================================================
        // UPDATE XP ORBS (PICKUP + LEVELING)
        // ======================================================
        Iterator<XpOrb> xpIterator = xpOrbs.iterator();

        while (xpIterator.hasNext()) {

            XpOrb orb = xpIterator.next();

            orb.update(delta);

            float dx = orb.x - playerX;
            float dy = orb.y - playerY;

            float distSq = dx * dx + dy * dy;

            // pickup radius check
            if (distSq < 900f) {

                int gainedXP = orb.big ? 10 : 2;
                xp += gainedXP;

                // LEVEL UP CHECK (prevents stacking level-ups)
                if (!levelingUp && xp >= xpToNextLevel) {

                    levelingUp = true;

                    xp -= xpToNextLevel;
                    level++;

                    xpToNextLevel = 20 + (level * 5);

                    levelUpScreen = true;
                    levelUpSelection = 0;
                }

                xpIterator.remove();
            }
        }

        // ======================================================
        // UPDATE POTIONS (PICKUP SYSTEM)
        // ======================================================
        Iterator<Potion> potionIterator = potions.iterator();

        while (potionIterator.hasNext()) {

            Potion p = potionIterator.next();

            float dx = (p.x + p.size / 2f) - (playerX + PLAYER_SIZE / 2f);
            float dy = (p.y + p.size / 2f) - (playerY + PLAYER_SIZE / 2f);

            float distSq = dx * dx + dy * dy;

            // pickup radius
            if (distSq < 900f) {

                if (p.health) {
                    hasHealthPotion = true;
                } else {
                    hasBuffPotion = true;
                }

                potionIterator.remove();
            }
        }

        // ======================================================
        // ENEMIES (UPDATE + PLAYER COLLISION)
        // ======================================================
        int touching = 0;

        for (Enemy e : enemies) {

            // update enemy AI + movement + shooting
            e.update(delta, playerX, playerY, enemyProjectiles,
                    fireBallTexture, iceBallTexture, lightningBallTexture, random);

            // player collision check
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
        // DAMAGE SYSTEM (CONTACT DAMAGE OVER TIME)
        // ======================================================
        if (touching > 0) {

            // first frame of contact
            if (!wasTouching) {
                currentHealth -= touching;
                damageTimer = 0f;
            }

            damageTimer += delta;

            // periodic damage while still touching enemies
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
            return; // player dead -> stop rendering frame logic
        }


        // ======================================================
        // CAMERA FOLLOW + SHAKE
        // ======================================================
        camera.position.x += (playerX - camera.position.x) * CAMERA_LERP;
        camera.position.y += (playerY - camera.position.y) * CAMERA_LERP;

        // screen shake effect
        if (shakeTime > 0) {

            shakeTime -= delta;

            camera.position.x += (shakeRandom.nextFloat() - 0.5f) * 2f * shakeIntensity;
            camera.position.y += (shakeRandom.nextFloat() - 0.5f) * 2f * shakeIntensity;
        }

        camera.update();

        // clamp camera to world bounds
        float cameraHalfWidth = camera.viewportWidth * camera.zoom * 0.5f;
        float cameraHalfHeight = camera.viewportHeight * camera.zoom * 0.5f;

        camera.position.x = Math.max(cameraHalfWidth, Math.min(camera.position.x, worldWidth - cameraHalfWidth));
        camera.position.y = Math.max(cameraHalfHeight, Math.min(camera.position.y, worldHeight - cameraHalfHeight));


        // ======================================================
        // WAVE PROGRESSION
        // ======================================================
        if (enemies.isEmpty()) {

            buffActive = false;

            xpOrbs.clear();
            potions.clear();

            wave++;
            currentHealth = maxHealth;

            spawnWave();
        }


        // ======================================================
        // RENDER WORLD
        // ======================================================
        Gdx.gl.glClearColor(0.92f, 0.92f, 0.92f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tilemap render
        renderer.setView(camera);
        renderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();


        // ======================================================
        // PLAYER RENDER
        // ======================================================
        if (playerHitFlashTimer > 0 || burnTimer > 0) {
            batch.setColor(Color.RED);
        }

        batch.draw(
                currentPlayerTexture,
                playerX,
                playerY,
                PLAYER_SIZE,
                PLAYER_SIZE
        );

        batch.setColor(Color.WHITE);


        // ======================================================
        // ENEMIES RENDER
        // ======================================================
        for (Enemy e : enemies) {

            Texture tex = (e.type == 2)
                    ? spikeSlime
                    : (e.movingLeft ? slimeLeft : slimeRight);

            if (e.hitFlashTimer > 0) {
                batch.setColor(Color.RED);
            }

            batch.draw(tex, e.x, e.y, e.size, e.size);
            batch.setColor(Color.WHITE);
        }


        // ======================================================
        // PROJECTILES + ITEMS RENDER
        // ======================================================

        // aim preview kunai
        batch.draw(
                kunaiTexture,
                kunaiHoverX, kunaiHoverY,
                13, 13,
                26, 26,
                1, 1,
                hoverRotation,
                0, 0,
                kunaiTexture.getWidth(),
                kunaiTexture.getHeight(),
                false, false
        );

        // active kunai
        for (Kunai k : kunais) {
            batch.draw(
                    kunaiTexture,
                    k.x, k.y,
                    13, 13,
                    26, 26,
                    1, 1,
                    k.rotation,
                    0, 0,
                    kunaiTexture.getWidth(),
                    kunaiTexture.getHeight(),
                    false, false
            );
        }

        // enemy projectiles
        for (EnemyProjectile p : enemyProjectiles) {
            batch.draw(p.texture, p.x, p.y, p.size, p.size);
        }

        // xp orbs
        for (XpOrb orb : xpOrbs) {
            batch.draw(
                    orb.big ? bigXpTexture : xpTexture,
                    orb.x, orb.y,
                    orb.size, orb.size
            );
        }

        // potions
        for (Potion p : potions) {
            batch.draw(
                    p.health ? healthPotion : buffPotion,
                    p.x, p.y,
                    p.size, p.size
            );
        }

        batch.end();

        // ======================================================
        // UI (STAT MENU + HP + XP OVERLAY)
        // ======================================================
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        // ======================================================
        // STAT MENU BACKGROUND
        // ======================================================
        batch.draw(statMenu, 10, 10, 220, 270);

        // ======================================================
        // POTION UI (BUFF + HEALTH)
        // ======================================================

        // anchor near stat menu (to the right)
        float potionBaseX = 250;
        float potionBaseY = 30;

        // buff potion
        if (hasBuffPotion) {
            batch.setColor(1f, 1f, 1f, 1f);
        } else {
            batch.setColor(0.25f, 0.25f, 0.25f, 1f);
        }
        batch.draw(buffPotion, potionBaseX - 120, potionBaseY - 10, 64, 64);

        // health potion
        if (hasHealthPotion) {
            batch.setColor(1f, 1f, 1f, 1f);
        } else {
            batch.setColor(0.25f, 0.25f, 0.25f, 1f);
        }
        batch.draw(healthPotion, potionBaseX - 200, potionBaseY - 10, 64, 64);

        // reset color
        batch.setColor(Color.WHITE);

        // ======================================================
        // UI BASE POSITION (anchored to stat menu)
        // ======================================================
        float baseX = 30;
        float baseY = 30;

        // LEVEL BAR
        batch.draw(levelIcon, baseX + 230, baseY + 100, 180, 40);

        // LEVEL NUMBER (draw after so it appears in front)
        drawNumberForward(level, (int)(baseX + 345), (int)(baseY + 112), 0.6f);

        // ======================================================
        // HP BAR
        // ======================================================
        batch.draw(hpBar, baseX + 230, baseY + 50, 180, 40);

        // HP number
        drawNumberForward(currentHealth,
                (int)(baseX + 320),
                (int)(baseY + 62),
                0.6f);

        // ======================================================
        // XP BAR
        // ======================================================
        batch.draw(xpBar, baseX + 230, baseY, 180, 40);

        // XP current
        drawNumberForward(xp,
                (int)(baseX + 300),
                (int)(baseY + 10),
                0.6f);

        // slash
        batch.draw(slash, baseX + 330, baseY + 8, 24, 24);

        // XP max
        drawNumberForward(xpToNextLevel,
                (int)(baseX + 360),
                (int)(baseY + 10),
                0.6f);

        // optional debug
        drawNumberForward(enemies.size(), 1180, 680, 0.6f);

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
    // NUMBER RENDERING
    // ======================================================
    private void drawNumberForward(int value, int x, int y) {
        drawNumberForward(value, x, y, 1f);
    }

    private void drawNumberForward(int value, int x, int y, float scale) {

        String s = String.valueOf(value);

        float size = 24f * scale;
        float spacing = 24f * scale;

        for (int i = 0; i < s.length(); i++) {

            int digit = Character.getNumericValue(s.charAt(i));

            batch.draw(
                    numbers[digit],
                    x + (i * spacing),
                    y,
                    size,
                    size
            );
        }
    }

    // ======================================================
    // WAVES
    // ======================================================
    private void spawnWave() {

        // boss wave
        if (wave == 100) {

            Enemy boss = new Enemy();

            boss.x = (playableMinX + playableMaxX) / 2f;
            boss.y = (playableMinY + playableMaxY) / 2f;

            boss.size = 200f;
            boss.hp = 1000;
            boss.speed = BOSS_SPEED;
            boss.shooter = true;
            boss.shootTimer = 0f;

            boss.type = 99;

            enemies.add(boss);
            return;
        }

        // normal waves
        int count = wave;

        for (int i = 0; i < count; i++) {

            Enemy e = new Enemy();

            e.x = playableMinX + random.nextFloat() *
                    (playableMaxX - playableMinX - 96f);

            e.y = playableMinY + random.nextFloat() *
                    (playableMaxY - playableMinY - 96f);

            int typeRoll = random.nextInt(3);

            if (typeRoll == 0) {

                e.shooter = false;
                e.size = 96f;
                e.hp = 9;

            } else if (typeRoll == 1) {

                e.shooter = true;
                e.size = 48f;
                e.hp = 2;
                e.shootTimer = random.nextFloat() * SHOOTER_FIRE_INTERVAL;

            } else {

                e.shooter = false;
                e.size = 96f;
                e.hp = 1;
                e.speed = SPIKE_SLIME_SPEED;
                e.type = 2;
            }

            enemies.add(e);
        }
    }

   // ======================================================
    // CLEANUP
    // ======================================================
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
        spikeSlime.dispose();

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