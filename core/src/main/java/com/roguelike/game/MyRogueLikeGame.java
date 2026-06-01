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
    private final float BASE_SHOOT_DELAY = 0.25f;

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
    private Texture spikeSlime;
    private Texture kingSlime;

    private ArrayList<Enemy> enemies;
    private Random random;

    // ======================================================
    // POTIONS
    // ======================================================
    private Texture buffPotion;
    private Texture healthPotion;
    private boolean hasBuffPotion = false;
    private boolean hasHealthPotion = false;
    private boolean buffActive = false;
    private ArrayList<Potion> potions;

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
    private Texture hpBar;
    private Texture xpBar;
    private Texture slash;
    private Texture levelIcon;
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
    private int maxHealth = 5;
    private int currentHealth = maxHealth;

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

    // ======================================================
    // PAUSE MENU
    // ======================================================
    private boolean paused = false;

    private Texture startButton;
    private Texture exitButton;
    private float pauseCenterX = 640;
    private float pauseCenterY = 360;
    private int pauseSelection = 0;
    private float pauseAnim = 0f;
    private Texture pauseMenu;
    private Texture levelScreen;
    private Texture healthIcon;
    private Texture magicIcon;
    private Texture speedIcon;
    private Texture strengthIcon;

    // LEVEL MENU

    private boolean levelUpScreen = false;
    private int levelUpSelection = 0;
    private boolean levelingUp = false;

    // ======================================================
    // LEVELING SYSTEM
    // ======================================================
    private int level = 1;
    private int xp = 0;
    private int xpToNextLevel = 20;

    // ======================================================
    // PLAYER STATS (UPGRADES)
    // ======================================================

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
        spikeSlime = new Texture("SpikeSlime.png");
        kingSlime = new Texture("KingSlime.png");

        // WEAPON
        kunaiTexture = new Texture("Kunai.png");

        // PROJECTILES
        fireBallTexture = new Texture("Fire Ball.png");
        iceBallTexture = new Texture("Ice Ball.png");
        lightningBallTexture = new Texture("Lightning Ball.png");
        xpTexture = new Texture("Xp.png");

        // POTIONS

        buffPotion = new Texture("BuffPotion.png");
        healthPotion = new Texture("HealthPotion.png");
        
        // MENU
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
        potions = new ArrayList<>();
        

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

        // ======================================================
        // LEVEL UP SCREEN (FREEZE GAME)
        // ======================================================
        if (levelUpScreen) {

            // ======================================================
            // LEVEL UP INPUT (SIMPLE LINEAR NAVIGATION)
            // ======================================================

            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                levelUpSelection--;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                levelUpSelection++;
            }

            // wrap around (IMPORTANT)
            if (levelUpSelection < 0) {
                levelUpSelection = 3;
            }
            if (levelUpSelection > 3) {
                levelUpSelection = 0;
            }

            // confirm selection
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
                Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    

                if (levelUpSelection == 0) {
                    maxHealth += 1;
                    currentHealth = maxHealth;
                }

                else if (levelUpSelection == 1) {
                    fireRateMultiplier += 0.01f; // Magic = faster shooting
                }

                else if (levelUpSelection == 2) {
                    speedMultiplier += 0.01f; // Speed = movement boost
                }

                else if (levelUpSelection == 3) {
                    kunaiDamage += 1; // Strength = more damage
                }
                levelUpScreen = false;
                levelingUp = false;
            }

            // DRAW LEVEL SCREEN ONLY

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();

            batch.draw(levelScreen, 0, 0, 1280, 720);

            float startX = 320;
            float y = 250;

            // highlight selected icon
            float alpha0 = (levelUpSelection == 0) ? 1f : 0.4f;
            float alpha1 = (levelUpSelection == 1) ? 1f : 0.4f;
            float alpha2 = (levelUpSelection == 2) ? 1f : 0.4f;
            float alpha3 = (levelUpSelection == 3) ? 1f : 0.4f;

            batch.setColor(1, 1, 1, alpha0);
            batch.draw(healthIcon, startX + 680, y - 50, 100, 100);

            batch.setColor(1, 1, 1, alpha1);
            batch.draw(magicIcon, startX + 680, y + 100, 100, 100);

            batch.setColor(1, 1, 1, alpha2);
            batch.draw(speedIcon, startX + 140, y - 50, 100, 100);

            batch.setColor(1, 1, 1, alpha3);
            batch.draw(strengthIcon, startX + 140, y + 100, 100, 100);

            batch.setColor(1, 1, 1, 1);

            batch.end();

            return;
        }

        // ======================================================
        // PAUSE TOGGLE
        // ======================================================
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            paused = !paused;

            // RESET MENU STATE WHEN OPENING PAUSE
            if (paused) {
                pauseSelection = 0;
            }
        }

        // ======================================================
        // PAUSED GAME LOGIC (FREEZE EVERYTHING)
        // ======================================================
        if (paused) {

            // navigation
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
                Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                pauseSelection = 0;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) ||
                Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                pauseSelection = 1;
            }

            // select option
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
                Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {

                if (pauseSelection == 0) {
                    paused = false;
                }

                if (pauseSelection == 1) {
                    Gdx.app.exit();
                }
            }

            // ======================================================
            // CLEAR SCREEN (NO WHITE FLASH)
            // ======================================================
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // ======================================================
            // DRAW PAUSE MENU ONLY
            // ======================================================
            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();

            batch.setColor(Color.WHITE);
            batch.draw(pauseMenu, 0, 0, 1280, 720);

            float centerX = 640 - 100;
            float centerY = 360;

            // ======================================================
            // START BUTTON (always visible)
            // ======================================================
            if (pauseSelection == 0) {
                batch.setColor(1f, 1f, 1f, 1f);      // bright (selected)
            } else {
                batch.setColor(0.5f, 0.5f, 0.5f, 1f); // dim (not selected)
            }
            batch.draw(startButton, centerX, centerY + 60, 200, 60);

            // ======================================================
            // EXIT BUTTON (always visible)
            // ======================================================
            if (pauseSelection == 1) {
                batch.setColor(1f, 1f, 1f, 1f);
            } else {
                batch.setColor(0.5f, 0.5f, 0.5f, 1f);
            }
            batch.draw(exitButton, centerX, centerY - 60, 200, 60);

            // reset color
            batch.setColor(Color.WHITE);

            batch.setColor(Color.WHITE);

            batch.end();

            return;
        }

        // ======================================================
        // GAME RUNNING (NORMAL MODE)
        // ======================================================
        survivalTime += delta;

        shootCooldown -= delta;

        // =====================================
        // POTION USE
        // =====================================

        // Q = health potion
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) && hasHealthPotion) {
            currentHealth = maxHealth;
            hasHealthPotion = false;
        }

        // E = buff potion
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && hasBuffPotion) {
            buffActive = true;
            hasBuffPotion = false;
        }

        // ======================================================
        // STATUS EFFECTS
        // ======================================================
        if (burnTimer > 0) {

            burnTimer -= delta;
            burnDamageTimer += delta;

            // deal damage ONCE after 10 seconds, then burn ends
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

        float currentSpeed = PLAYER_SPEED * speedMultiplier;

        if (slowTimer > 0) {
            currentSpeed *= 0.5f;
            slowTimer -= delta;
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

            shootCooldown = BASE_SHOOT_DELAY / fireRateMultiplier;
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

                    int damage = buffActive ? kunaiDamage * 5 : kunaiDamage;
                    e.hp -= damage;
                    e.hitFlashTimer = 0.15f;

                    hit = true;

                    if (e.hp <= 0) {

                        // ===========================
                        // HEALTH POTION DROP (5%)
                        // ===========================
                        if (random.nextFloat() < 0.05f) {

                            float angle = random.nextFloat() * 360f;

                            float dx = (float)Math.cos(Math.toRadians(angle));
                            float dy = (float)Math.sin(Math.toRadians(angle));

                            potions.add(new Potion(
                                    e.x + e.size / 2f,
                                    e.y + e.size / 2f,
                                    dx,
                                    dy,
                                    true   // health potion
                            ));
                        }

                        // ===========================
                        // BUFF POTION DROP (5%)
                        // ===========================
                        if (random.nextFloat() < 0.05f) {

                            float angle = random.nextFloat() * 360f;

                            float dx = (float)Math.cos(Math.toRadians(angle));
                            float dy = (float)Math.sin(Math.toRadians(angle));

                            potions.add(new Potion(
                                    e.x + e.size / 2f,
                                    e.y + e.size / 2f,
                                    dx,
                                    dy,
                                    false  // buff potion
                            ));
                        }

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

                    stunTimer = 0.5f;
                }

                projectileIterator.remove();
            }
        }

        Iterator<XpOrb> xpIterator = xpOrbs.iterator();

        while (xpIterator.hasNext()) {

            XpOrb orb = xpIterator.next();

            orb.update(delta);

            float dx = orb.x - playerX;
            float dy = orb.y - playerY;

            float distSq = dx * dx + dy * dy;

            // pickup radius
            if (distSq < 900f) {

                int gainedXP = orb.big ? 10 : 2;
                xp += gainedXP;

                // LEVEL UP CHECK (FIXED - NO STACKING BUG)
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

        Iterator<Potion> potionIterator = potions.iterator();

        while (potionIterator.hasNext()) {

            Potion p = potionIterator.next();

            float dx = (p.x + p.size / 2f) - (playerX + PLAYER_SIZE / 2f);
            float dy = (p.y + p.size / 2f) - (playerY + PLAYER_SIZE / 2f);

            float distSq = dx * dx + dy * dy;

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

            buffActive = false;

            xpOrbs.clear();
            potions.clear(); // 🔥 ADD THIS

            wave++;

            currentHealth = maxHealth;

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

            Texture tex;

            if (e.type == 2) {
                tex = spikeSlime; // ✅ SPIKE SLIME IMAGE
            } else {
                tex = e.movingLeft ? slimeLeft : slimeRight;
            }

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

        for (Potion p : potions) {

            batch.draw(
                    p.health ? healthPotion : buffPotion,
                    p.x,
                    p.y,
                    p.size,
                    p.size
            );
        }

        batch.end();

        // ======================================================
        // UI (STAT MENU + HP + XP OVERLAY)
        // ======================================================
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        // ======================================================
        // STAT MENU BACKGROUND (KEEP THIS)
        // ======================================================
        batch.draw(statMenu, 10, 10, 220, 270);

        // ======================================================
        // POTION UI (BUFF + HEALTH)
        // ======================================================

        // anchor near stat menu (to the right of it)
        float potionBaseX = 250;
        float potionBaseY = 30;

        // Buff potion
        if (hasBuffPotion) {
            batch.setColor(1f, 1f, 1f, 1f);
        } else {
            batch.setColor(0.25f, 0.25f, 0.25f, 1f);
        }
        batch.draw(buffPotion, potionBaseX - 120, potionBaseY - 10, 64, 64);

        // Health potion
        if (hasHealthPotion) {
            batch.setColor(1f, 1f, 1f, 1f);
        } else {
            batch.setColor(0.25f, 0.25f, 0.25f, 1f);
        }
        batch.draw(healthPotion, potionBaseX - 200, potionBaseY - 10, 64, 64);

        // reset color
        batch.setColor(Color.WHITE);

        // reset color (important)

        // ======================================================
        // UI BASE POSITION (anchored to stat menu)
        // ======================================================
        float baseX = 30;
        float baseY = 30;

        // LEVEL BAR
        batch.draw(levelIcon, baseX + 230, baseY + 100, 180, 40);

        // LEVEL NUMBER (draw AFTER so it appears in front)
        drawNumberForward(level,(int)(baseX + 345), (int)(baseY + 112), 0.6f);

        // ======================================================
        // HP BAR (inside stat menu)
        // ======================================================
        batch.draw(hpBar, baseX + 230, baseY + 50, 180, 40);

        // HP number on bar (SMALLER)
        // ======================================================
        drawNumberForward(currentHealth,
                (int)(baseX + 320),
                (int)(baseY + 62),
                0.6f);

        // ======================================================
        // XP BAR (inside stat menu, below HP)
        // ======================================================
        batch.draw(xpBar, baseX + 230, baseY, 180, 40);

        // XP text: xp / xpToNextLevel (SMALLER)
        // ======================================================
        drawNumberForward(xp,
                (int)(baseX + 300),
                (int)(baseY + 10),
                0.6f);

        // slash in middle
        batch.draw(slash, baseX + 330, baseY + 8, 24, 24);

        // max XP (SMALLER)
        // ======================================================
        drawNumberForward(xpToNextLevel,
                (int)(baseX + 360),
                (int)(baseY + 10),
                0.6f);

        // ======================================================
        // OPTIONAL: small enemy count or time (keep if you want)
        // ======================================================
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

        // ============================
        // BOSS WAVE (ROUND 100)
        // ============================
        if (wave == 100) {

            Enemy boss = new Enemy();

            boss.x = (playableMinX + playableMaxX) / 2f;
            boss.y = (playableMinY + playableMaxY) / 2f;

            boss.size = 200f;
            boss.hp = 1000;
            boss.speed = BOSS_SPEED;
            boss.shooter = true;
            boss.shootTimer = 0f;

            // optional marker (if you want future logic)
            boss.type = 99;

            enemies.add(boss);
            return;
        }

        // ============================
        // NORMAL WAVES
        // ============================
        int count = wave;

        for (int i = 0; i < count; i++) {

            Enemy e = new Enemy();

            e.x = playableMinX + random.nextFloat() * (playableMaxX - playableMinX - 96f);
            e.y = playableMinY + random.nextFloat() * (playableMaxY - playableMinY - 96f);

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
                e.type = 2; // 🔥 THIS IS THE KEY FIX
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