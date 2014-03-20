package com.me.mygdxgame.Screens;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.me.mygdxgame.Entities.Asteroid;
import com.me.mygdxgame.Entities.PlayerEntity;

public class CombatScreen extends OrionScreen 
{
	public class ParallaxCamera extends OrthographicCamera {
		Matrix4 parallaxView = new Matrix4();
		Matrix4 parallaxCombined = new Matrix4();
		Vector3 tmp = new Vector3();
		Vector3 tmp2 = new Vector3();

		public ParallaxCamera (float viewportWidth, float viewportHeight) {
			super(viewportWidth, viewportHeight);
		}

		public Matrix4 calculateParallaxMatrix (float parallaxX, float parallaxY) {
			update();
			tmp.set(position);
			tmp.x *= parallaxX;
			tmp.y *= parallaxY;

			parallaxView.setToLookAt(tmp, tmp2.set(tmp).add(direction), up);
			parallaxCombined.set(projection);
			Matrix4.mul(parallaxCombined.val, parallaxView.val);
			return parallaxCombined;
		}
	}
	
	private ParallaxCamera cam;
	private Texture background;
	private Texture parralax1;
	private Texture parralax2;
	private SpriteBatch spriteBatch;
	static int WIDTH  = 1024;
    static int HEIGHT = 768;
    private Rectangle glViewport;
    BitmapFont font;
    public PlayerEntity player;
    World w;
    Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    Asteroid asty;
    
	public CombatScreen()
	{
		background = new Texture(Gdx.files.internal("data/background.jpg"));
		background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		parralax1 = new Texture(Gdx.files.internal("data/slide.png"));
		parralax1.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		parralax2 = new Texture(Gdx.files.internal("data/slide.png"));
		parralax2.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		spriteBatch = new SpriteBatch();
		cam = new ParallaxCamera(WIDTH, HEIGHT);        
        cam.position.set(WIDTH / 2, HEIGHT / 2, 0);
        font = new BitmapFont(Gdx.files.internal("data/font16.fnt"), false);
        w = new World(new Vector2(0,0), true );
        player = new PlayerEntity("data/ship0.png", w, 0, 0, -90);
        asty = new Asteroid("data/asteroid.png", w, 30, 30 );
        glViewport = new Rectangle(0, 0, WIDTH, HEIGHT);
	}
		
	@Override
	public void update(float delta) 
	{
		// TODO Auto-generated method stub
		/*if (Gdx.input.justTouched()) {
			m_isDone = true;
		}*/
		
	}
	
	private void handleInput() 
	{
		/*
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            cam.zoom += 0.02;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
            cam.zoom -= 0.02;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                cam.translate(-30, 0, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                cam.translate(30, 0, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                cam.translate(0, -30, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
                cam.translate(0, 30, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            cam.rotate(-.5f, 0, 0, 1);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.E)) {
            cam.rotate(.5f, 0, 0, 1);
        }*/
		player.HandleMovement( cam );
    }

	@Override
	public void draw(float delta) 
	{
		handleInput(); 
		w.step(1/60f, 60, 20);
        GL10 gl = Gdx.graphics.getGL10();

        // Camera --------------------- /
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glViewport((int) glViewport.x, (int) glViewport.y,
                (int) glViewport.width, (int) glViewport.height);

        cam.position.set( player.m_body.getPosition().x, player.m_body.getPosition().y, 0);
        cam.update();
        cam.apply(gl);
        

		/*viewMatrix.setToOrtho2D(0, 0, 480, 320);
		spriteBatch.setProjectionMatrix(cam.combined);
		//spriteBatch.setTransformMatrix(transformMatrix);
		spriteBatch.begin();
		spriteBatch.disableBlending();
		spriteBatch.setColor(Color.WHITE);
		spriteBatch.draw(background, 0, 0 );
		spriteBatch.enableBlending();
		spriteBatch.setBlendFunction(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		/*float width = font.getBounds(text).width;
		font.draw(spriteBatch, text, 240 - width / 2, 128);
		if (Gdx.app.getType() == ApplicationType.WebGL) {
			text = "Press Enter for Fullscreen Mode";
			width = font.getBounds(text).width;
			font.draw(spriteBatch, "Press Enter for Fullscreen Mode", 240 - width / 2, 128 - font.getLineHeight());
		}
		spriteBatch.end();*/
        
     // background layer, no parallax, centered around origin
        spriteBatch.setProjectionMatrix(cam.calculateParallaxMatrix(0, 0));
        spriteBatch.disableBlending();
     		spriteBatch.begin();
     		spriteBatch.draw(background, -(int)(background.getWidth() / 2), -(int)(background.getHeight() / 2));
     		spriteBatch.end();
     		spriteBatch.enableBlending();

     		// midground layer, 0.5 parallax (move at half speed on x, full speed on y)
     		/*/ layer is 2048x2048
     		spriteBatch.setProjectionMatrix(cam.calculateParallaxMatrix(0.5f, .5f));
     		spriteBatch.begin();
     		int x = (int) (cam.position.x / 4096);
     		int y = (int) cam.position.y;
     		spriteBatch.draw(parralax1, x*2048, 0);
     		spriteBatch.end();
     		*/
     		DrawParralaxLayer( 40f, parralax1 );
     		DrawParralaxLayer( 80f, parralax2 );
     		
     		// foreground layer, 1.0 parallax (move at full speed)
     		// layer is 2048x320
     		spriteBatch.setProjectionMatrix(cam.calculateParallaxMatrix(1f, 1));
     		spriteBatch.begin();
     		for (int i = 0; i < 9; i++) {
     			//spriteBatch.draw(parralax2, i * parralax2.getWidth() - 1024, -160);
     		}
     		spriteBatch.end();
     		
     		spriteBatch.begin();
    		player.Draw( spriteBatch );
    		asty.Draw( spriteBatch );
    		spriteBatch.end();
     		
    		debugRenderer.render(w, cam.combined);
     	// draw fps
     		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
     		spriteBatch.begin();
    		font.draw(spriteBatch, "boost juice: " + player.m_boostJuice, 0, 30);
    		spriteBatch.end();
    		
    		

	}
	
	private void DrawParralaxLayer( float parallaxFactor, Texture texture ) 
	{
		spriteBatch.setProjectionMatrix(cam.calculateParallaxMatrix(parallaxFactor, parallaxFactor));
		spriteBatch.begin();
		int x = (int) (cam.position.x / (texture.getWidth() / parallaxFactor));
		int y = (int) (cam.position.y / (texture.getHeight() / parallaxFactor));
		
		//constant y series
		spriteBatch.draw(texture, (x)*texture.getWidth(), (y)*texture.getHeight());
		spriteBatch.draw(texture, (x-1)*texture.getWidth(), (y)*texture.getHeight());
		spriteBatch.draw(texture, (x-2)*texture.getWidth(), (y)*texture.getHeight());
		spriteBatch.draw(texture, (x+1)*texture.getWidth(), (y)*texture.getHeight());
		spriteBatch.draw(texture, (x+2)*texture.getWidth(), (y)*texture.getHeight());
		
		//constant x series
		spriteBatch.draw(texture, (x)*texture.getWidth(), (y-1)*texture.getHeight());
		spriteBatch.draw(texture, (x)*texture.getWidth(), (y-2)*texture.getHeight());
		spriteBatch.draw(texture, (x)*texture.getWidth(), (y+1)*texture.getHeight());
		spriteBatch.draw(texture, (x)*texture.getWidth(), (y+2)*texture.getHeight());
		
		//negative var series
		spriteBatch.draw(texture, (x-1)*texture.getWidth(), (y-1)*texture.getHeight());
		spriteBatch.draw(texture, (x-2)*texture.getWidth(), (y-2)*texture.getHeight());
		spriteBatch.draw(texture, (x-2)*texture.getWidth(), (y-1)*texture.getHeight());
		spriteBatch.draw(texture, (x-1)*texture.getWidth(), (y-2)*texture.getHeight());
		
		//positive var series
		spriteBatch.draw(texture, (x+1)*texture.getWidth(), (y+1)*texture.getHeight());
		spriteBatch.draw(texture, (x+2)*texture.getWidth(), (y+2)*texture.getHeight());
		spriteBatch.draw(texture, (x+2)*texture.getWidth(), (y+1)*texture.getHeight());
		spriteBatch.draw(texture, (x+1)*texture.getWidth(), (y+2)*texture.getHeight());
		
		// opposites series
		spriteBatch.draw(texture, (x-1)*texture.getWidth(), (y+1)*texture.getHeight());
		spriteBatch.draw(texture, (x-2)*texture.getWidth(), (y+1)*texture.getHeight());
		
		spriteBatch.draw(texture, (x+1)*texture.getWidth(), (y-1)*texture.getHeight());
		spriteBatch.draw(texture, (x+2)*texture.getWidth(), (y-1)*texture.getHeight());
		
		//spriteBatch.draw(texture, (x-1)*texture.getWidth(), (y+1)*texture.getHeight());
		spriteBatch.draw(texture, (x-1)*texture.getWidth(), (y+2)*texture.getHeight());
		
		//spriteBatch.draw(texture, (x+1)*texture.getWidth(), (y-1)*texture.getHeight());
		spriteBatch.draw(texture, (x+1)*texture.getWidth(), (y-2)*texture.getHeight());
		spriteBatch.end();
	}

	@Override
	public boolean isDone() 
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void dispose()
	{
		background.dispose();
		parralax1.dispose();
		parralax2.dispose();
		spriteBatch.dispose();
		font.dispose();
	}

}
