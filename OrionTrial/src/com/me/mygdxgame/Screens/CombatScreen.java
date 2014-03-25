package com.me.mygdxgame.Screens;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.me.mygdxgame.Entities.Asteroid;
import com.me.mygdxgame.Entities.MydebugRenderer;
import com.me.mygdxgame.Entities.PlayerEntity;
import com.me.mygdxgame.Entities.ViewedCollidable;
import com.badlogic.gdx.graphics.Color;

import box2dlight.ConeLight;
import box2dlight.Light;
import box2dlight.PointLight;
import box2dlight.RayHandler;

public class CombatScreen extends OrionScreen implements ContactListener
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
    MydebugRenderer debugRenderer = new MydebugRenderer();
    Asteroid asty;
    ArrayList<ViewedCollidable> m_deadThings = new ArrayList<ViewedCollidable>();
	ArrayList<ViewedCollidable> m_aliveThings = new ArrayList<ViewedCollidable>();
	RayHandler rayHandler;
    
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
        player = new PlayerEntity("data/ship0.png", w, 0, 0, -90, 50f);
        asty = new Asteroid("data/asteroid.png", w, 10, 10 );
        glViewport = new Rectangle(0, 0, WIDTH, HEIGHT);
        w.setContactListener(this);
        m_aliveThings.add((ViewedCollidable)asty);
        
        /** BOX2D LIGHT STUFF BEGIN */
        RayHandler.setGammaCorrection(true);
        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(w);
        rayHandler.setAmbientLight(0.05f, 0.05f, 0.05f, 0.1f);
        rayHandler.setCulling(true);
        // rayHandler.setBlur(false);
        rayHandler.setBlurNum(1);
        //rayHandler.setShadows(true);
        cam.update(true);

        // rayHandler.setCombinedMatrix(camera.combined, camera.position.x,
        // camera.position.y, camera.viewportWidth * camera.zoom,
        // camera.viewportHeight * camera.zoom);
        //for (int i = 0; i < BALLSNUM; i++) {
        // final Color c = new Color(MathUtils.random()*0.4f,
        // MathUtils.random()*0.4f,
        // MathUtils.random()*0.4f, 1f);
        Light light = new PointLight(rayHandler, 128);
        Color color = new Color(1f,1f,1f,1f);
        Light light2 = new ConeLight(rayHandler, 64, color,
    			300, 0, 0, 0,
    			15);
        light.setDistance(300f);
        light2.setDistance(800);
        // Light light = new ConeLight(rayHandler, RAYS_PER_BALL, null,
        // LIGHT_DISTANCE, 0, 0, 0, 60);
        // light.setStaticLight(true);
        light.attachToBody(player.m_body, 0f, 0f);
        light2.attachToBody(player.m_body, 0f, 0f);
       
        light.setColor( 1, 1, 1, 1f);
        // light.setColor(0.1f,0.1f,0.1f,0.1f);

        //}
        // new DirectionalLight(rayHandler, 24, new Color(0,0.4f,0,1f), -45);
        /** BOX2D LIGHT STUFF END */
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
        GL20 gl = Gdx.graphics.getGL20();

        // Camera --------------------- /
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gl.glViewport((int) glViewport.x, (int) glViewport.y,
                (int) glViewport.width, (int) glViewport.height);

        cam.position.set( player.m_body.getPosition().x*29f, player.m_body.getPosition().y*29f, 0);
        cam.update();
        //cam.apply(gl);
        

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
     		DrawParralaxLayer( .25f, parralax1 );
     		DrawParralaxLayer( .5f, parralax2 );
     		
     		//spriteBatch.setProjectionMatrix();
     		
     		// foreground layer, 1.0 parallax (move at full speed)
     		// layer is 2048x320
     		spriteBatch.setProjectionMatrix(cam.calculateParallaxMatrix(1f, 1f));
     		spriteBatch.begin();
     		for (int i = 0; i < 9; i++) {
     			//spriteBatch.draw(parralax2, i * parralax2.getWidth() - 1024, -160);
     		}
     		spriteBatch.end();
     		
     		spriteBatch.begin();
    		player.Draw( spriteBatch );
    		for(int i = 0; i < m_aliveThings.size(); i++)
    		{
    			ViewedCollidable tmp = (ViewedCollidable) m_aliveThings.get(i);
    			
    			if(tmp.m_integrity > 0 || !tmp.deathThroesDone() )
    			{
    				tmp.Draw(spriteBatch);
    			}
    			else
    			{
    				m_aliveThings.remove( i );
    				i--;
    				m_deadThings.add( tmp );
    			}
    		}
    		spriteBatch.end();
    		
    		/** BOX2D LIGHT STUFF BEGIN */

    		
    		rayHandler.setCombinedMatrix(cam.combined, cam.position.x,
    		cam.position.y, cam.viewportWidth * cam.zoom,
    		cam.viewportHeight * cam.zoom);

    		// rayHandler.setCombinedMatrix(camera.combined);
    		//if (stepped)
    		rayHandler.update();
    		rayHandler.render();

    		/** BOX2D LIGHT STUFF END */
    		
     		
    		debugRenderer.render(w, cam.combined);
    		
     	// draw fps
     		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
     		spriteBatch.begin();
     		font.draw(spriteBatch, "Integrity: " + asty.m_integrity , 0, 150);
     		font.draw(spriteBatch, "boost juice: " + player.me.m_boostJuice , 0, 120);
     		font.draw(spriteBatch, "x: " + player.m_body.getPosition().x , 0, 90);
     		font.draw(spriteBatch, "Y: " + player.m_body.getPosition().y , 0, 60);
     		font.draw(spriteBatch, "vel: " + player.m_body.getLinearVelocity().dst(0, 0), 0, 30);
    		spriteBatch.end();
    		
    		for(int i = 0; i < m_deadThings.size(); i++)
    		{
    			ViewedCollidable tmp = (ViewedCollidable) m_deadThings.get(i);
    			w.destroyBody(tmp.m_body);
    		}
    		m_deadThings.clear();

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

	@Override
	public void beginContact(Contact contact) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endContact(Contact contact) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) 
	{
		// TODO Auto-generated method stub
		Body body1 = contact.getFixtureA().getBody();
		Body body2 = contact.getFixtureB().getBody();
		ViewedCollidable object1 = (ViewedCollidable) body1.getUserData();
		ViewedCollidable object2 = (ViewedCollidable) body2.getUserData();
		float crashVelocity = Math.abs( body1.getLinearVelocity().dst(0, 0) - body2.getLinearVelocity().dst(0, 0) );
		object1.damageCalc( object2, crashVelocity );
		object2.damageCalc( object1, crashVelocity );
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) 
	{
		// TODO Auto-generated method stub
		
	}
	
	

}
