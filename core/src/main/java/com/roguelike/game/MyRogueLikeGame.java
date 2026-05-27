package com.roguelike.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class MyRogueLikeGame extends ApplicationAdapter {

    private SpriteBatch batch;

    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Kunai> kunais;
    private ArrayList<EnemyProjectile> enemyProjectiles;

    private Texture background;
    private Texture kunaiTexture;
    private Texture fireBallTexture, iceBallTexture, lightningBallTexture;
    private Texture slimeLeft, slimeRight;

    private Texture[] numbers = new Texture[10];

    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;

    private Random random;

    private int wave = 1;
    private float survivalTime = 0;

    private static final float WORLD_SIZE = 2048f;

    @Override
    public void create() {

        batch = new SpriteBatch();

        player = new Player();

        enemies = new ArrayList<>();
        kunais = new ArrayList<>();
        enemyProjectiles = new ArrayList<>();

        random = new Random();

        background = new Texture("dungeon.png");
        kunaiTexture = new Texture("Kunai.png");

        fireBallTexture = new Texture("Fire Ball.png");
        iceBallTexture = new Texture("Ice Ball.png");
        lightningBallTexture = new Texture("Lightning Ball.png");

        slimeLeft = new Texture("SlimeLeft.png");
        slimeRight = new Texture("SlimeRight.png");

        for (int i = 0; i < 10; i++) {
            numbers[i] = new Texture("Num" +
                    new String[]{"Zero","One","Two","Three","Four","Five","Six","Seven","Eight","Nine"}[i] + ".png");
        }

        player.loadTextures();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        camera.zoom = 0.65f;

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, 1280, 720);

        player.x = WORLD_SIZE / 2f;
        player.y = WORLD_SIZE / 2f;

        WaveManager.spawnWave(enemies, wave, WORLD_SIZE, random);
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();
        survivalTime += delta;

        player.update(delta);

        handleInput();
        updateKunais(delta);
        updateProjectiles(delta);
        updateEnemies(delta);

        if (enemies.isEmpty()) {
            wave++;
            WaveManager.spawnWave(enemies, wave, WORLD_SIZE, random);
        }

        renderGame();
    }

    private void handleInput() {

        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Kunai k = player.shoot(mouse.x, mouse.y);
            if (k != null) kunais.add(k);
        }
    }

    private void updateKunais(float delta) {

        Iterator<Kunai> it = kunais.iterator();

        while (it.hasNext()) {

            Kunai k = it.next();
            k.update(delta);

            for (Enemy e : enemies) {

                if (CollisionUtils.isColliding(k.x, k.y, e.x, e.y, e.size / 2f)) {

                    e.hp--;
                    e.hitFlashTimer = 0.15f;

                    it.remove();
                    break;
                }
            }
        }

        enemies.removeIf(e -> e.hp <= 0);
    }

    private void updateProjectiles(float delta) {

        Iterator<EnemyProjectile> it = enemyProjectiles.iterator();

        while (it.hasNext()) {

            EnemyProjectile p = it.next();
            p.update(delta);

            if (CollisionUtils.isColliding(
                    p.x, p.y,
                    player.x, player.y,
                    Player.SIZE)) {

                player.hit(p.type);

                it.remove();
            }
        }
    }

    private void updateEnemies(float delta) {

        for (Enemy e : enemies) {
            e.update(delta, player, enemyProjectiles,
                    fireBallTexture, iceBallTexture, lightningBallTexture, random);
        }
    }

    private void renderGame() {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.set(player.x, player.y, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(background, 0, 0, WORLD_SIZE, WORLD_SIZE);

        player.draw(batch);

        for (Enemy e : enemies)
            e.draw(batch, slimeLeft, slimeRight);

        for (Kunai k : kunais)
            k.draw(batch, kunaiTexture);

        for (EnemyProjectile p : enemyProjectiles)
            p.draw(batch);

        batch.end();

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        NumberRenderer.draw(batch, numbers, player.currentHealth, 20, 680);

        NumberRenderer.drawForward(
                batch,
                numbers,
                (int) survivalTime,
                580,
                680
        );

        String waveText = String.valueOf(wave);

        NumberRenderer.drawForward(
                batch,
                numbers,
                wave,
                1180 - (waveText.length() * 34),
                680
        );

        batch.end();
    }

    @Override
    public void dispose() {

        batch.dispose();
        player.dispose();

        background.dispose();
        kunaiTexture.dispose();

        slimeLeft.dispose();
        slimeRight.dispose();

        fireBallTexture.dispose();
        iceBallTexture.dispose();
        lightningBallTexture.dispose();

        for (Texture t : numbers) t.dispose();
    }
}