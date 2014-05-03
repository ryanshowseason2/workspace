package com.me.mygdxgame.Screens;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.me.mygdxgame.Entities.Asteroid;
import com.me.mygdxgame.Entities.EnemyShip;
import com.me.mygdxgame.Entities.MydebugRenderer;
import com.me.mygdxgame.Entities.PlayerEntity;
import com.me.mygdxgame.Entities.ViewedCollidable;
import com.me.mygdxgame.Entities.WingBlade;
import com.me.mygdxgame.Equipables.Hacking;
import com.me.mygdxgame.Equipables.Laser;
import com.me.mygdxgame.Equipables.LongRangeSensors;
import com.me.mygdxgame.Equipables.MachineGun;
import com.me.mygdxgame.Equipables.MagneticWave;
import com.me.mygdxgame.Equipables.Missile;
import com.me.mygdxgame.Equipables.NoWeapon;
import com.me.mygdxgame.Equipables.Railgun;
import com.me.mygdxgame.Equipables.WingBlades;
import com.badlogic.gdx.scenes.scene2d.Actor;

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
	EnemyShip shippy;
	
	Skin skin;
	Stage m_stage;
	Texture m_defaultButtonTexture;
	Texture m_changeEquipmentTexture;
	Button m_longRange;
	Button m_mediumRange;
	Button m_shortRange;
	Button m_changeEquipment;
	InputMultiplexer m_inputSplitter = new InputMultiplexer();
	
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
        m_stage = new Stage();
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        Dialog window = new Dialog("", skin);
        player = new PlayerEntity("data/shipsized.png", "data/myshipcollision.json", w, 0, 0, -90, 40f, m_aliveThings, cam, m_stage);
        //player.AddShortRangeCounterMeasure( new MachineGun( w, player, m_aliveThings, 20 ) );
        asty = new Asteroid("data/asteroid.png", "", w, 0, 40, m_aliveThings );
        asty = new Asteroid("data/asteroid.png", "",w, 5, 40, m_aliveThings );
        asty = new Asteroid("data/asteroid.png", "",w, 10, 40, m_aliveThings );
        asty = new Asteroid("data/asteroid.png", "",w, -5, 40, m_aliveThings );
        glViewport = new Rectangle(0, 0, WIDTH, HEIGHT);
        w.setContactListener(this);
        shippy = new EnemyShip( "data/stateczek.png", "",w, 0, 50, -90, 50, 2, m_aliveThings );
        shippy.AddToFighterGroup( new EnemyShip( "data/stateczek.png", "", w, 0, 90, -90, 50, 2, m_aliveThings ) );
        shippy.AddShortRangeCounterMeasure( new MachineGun( w, shippy, m_aliveThings ) );
        
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


        Light light = new PointLight(rayHandler, 128);
        Color color = new Color(1f,1f,1f,1f);
        Light light2 = new ConeLight(rayHandler, 64, color,
					    			300, 0, 0, 0,
					    			15);
        light.setDistance(300f);
        light2.setDistance(800);
        light.attachToBody(player.m_body, 0f, 0f);
        light2.attachToBody(player.m_body, 0f, 0f);
       
        light.setColor( 1, 1, 1, 1f);

        /** BOX2D LIGHT STUFF END */
        
        
        m_defaultButtonTexture = new Texture(Gdx.files.internal("data/unselected.png"));
        m_changeEquipmentTexture = new Texture(Gdx.files.internal("data/changeweapon.png"));
        TextureRegion defaultImg = new TextureRegion(m_defaultButtonTexture);
        TextureRegion changeImg = new TextureRegion(m_changeEquipmentTexture);

        
        
        m_inputSplitter.addProcessor(player);
        m_inputSplitter.addProcessor(m_stage);
        Gdx.input.setInputProcessor(m_inputSplitter);

        
        m_longRange = new Button(new Image(defaultImg), skin);
        m_longRange.pad(10);
    	m_mediumRange = new Button(new Image(defaultImg), skin);
    	m_mediumRange.pad(10);
    	m_shortRange = new Button(new Image(defaultImg), skin);
    	m_shortRange.pad(10);
    	m_changeEquipment = new Button(new Image(changeImg), skin);
    	m_changeEquipment.pad(10);
    	
    	
    	player.m_shortRange = m_shortRange;
        player.m_longRange = m_longRange;
        player.m_mediumRange = m_mediumRange;
        player.m_changeEquipment = m_changeEquipment;


        
        window.setMovable(false);
        window.setPosition(0, HEIGHT/2);
        window.row();
        window.add(m_longRange);
        window.row();
        window.add(m_mediumRange);
        window.row();
        window.add(m_shortRange);
        window.row();
        window.add(m_changeEquipment);
        window.pack();
        
        player.m_window = window;

        // stage.addActor(new Button("Behind Window", skin));
        m_stage.addActor(window);


        m_longRange.addListener( player.m_buttonListener );
        m_mediumRange.addListener( player.m_buttonListener );
        m_shortRange.addListener( player.m_buttonListener );
        m_changeEquipment.addListener( player.m_buttonListener );
        
        
        /*m_shortRange.addListener(new 
        		ChangeListener() 
        		{
        			public void changed (ChangeEvent event, Actor actor) 
        			{
    					new Dialog("Some Dialog", skin, "dialog") 
    					{
							protected void result (Object object) 
							{
								System.out.println("Chosen: " + object);
							}
    					}.text("Are you enjoying this demo?").button("Yes", true).button("No", false).key(Keys.ENTER, true)
						.key(Keys.ESCAPE, false).show(stage);
        			}
        		});*/
        
       // player.AddShortRangeCounterMeasure( new MachineGun( w, player, m_aliveThings, 20 ) );
       // player.AddMidRangeCounterMeasure( new Laser( w, player, m_aliveThings, 40 ) );
        player.AddLongRangeCounterMeasure( new Railgun( w, player, m_aliveThings ) );
        player.AddLongRangeCounterMeasure( new Missile( w, player, m_aliveThings ) );
        player.AddLongRangeCounterMeasure( new Laser( w, player, m_aliveThings ) );
        player.AddLongRangeCounterMeasure( new MagneticWave( w, player, m_aliveThings ) );
        player.AddLongRangeCounterMeasure( new Hacking( w, player, m_aliveThings ) );
        player.AddLongRangeCounterMeasure( new WingBlades( w, player, m_aliveThings ) );
        player.AddLongRangeCounterMeasure( new MachineGun( w, player, m_aliveThings ) );
        player.AddLongRangeCounterMeasure( new LongRangeSensors( w, player, m_aliveThings ) );
        player.AddLongRangeCounterMeasure( new NoWeapon( w, player, m_aliveThings ) );
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
		player.HandleMovement( cam );
    }

	@Override
	public void draw(float delta) 
	{
		if( !player.m_inMenu )
		{
			w.step(1/60f, 60, 20);
			handleInput();
		}
		
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
    		//player.Draw( spriteBatch );
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
    		//if ( !player.m_inMenu )
    		{
	    		rayHandler.update();
	    		rayHandler.render();
    		}

    		/** BOX2D LIGHT STUFF END */
    		
     		
    		debugRenderer.render(w, cam.combined);
    		
    		m_stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
    		m_stage.draw();

    		
    	      Vector2 pos = player.m_body.getPosition();
    	      Vector2 pos2 =  player.m_leftWing.m_body.getPosition();
    		
     	/*/ draw fps
     		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
     		spriteBatch.begin();
     		font.draw(spriteBatch, "angle: " + Math.toRadians( player.m_angleRadians ) , 0, 150);
     		font.draw(spriteBatch, "body x:  " + pos.x*29f + " y: " + pos.y*29f , 0, 120);
     		font.draw(spriteBatch, "wing x:  " + pos2.x*29f + " y: " + pos2.y*29f , 0, 90);
     		font.draw(spriteBatch, "wing x:  " + player.m_leftWing.m_objectXPosition + " y: " + player.m_leftWing.m_objectYPosition , 0, 60);
     		/*font.draw(spriteBatch, "x: " + player.m_body.getPosition().x , 0, 90);
     		font.draw(spriteBatch, "Y: " + player.m_body.getPosition().y , 0, 60);
     		font.draw(spriteBatch, "vel: " + player.m_body.getLinearVelocity().dst(0, 0), 0, 30);
    		spriteBatch.end();*/
    		
    		for(int i = 0; i < m_deadThings.size(); i++)
    		{
    			ViewedCollidable tmp = (ViewedCollidable) m_deadThings.get(i);
    			w.destroyBody(tmp.m_body);
    			tmp.destroy();
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
	public void resize (int width, int height) 
	{
	//stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void dispose()
	{
		background.dispose();
		parralax1.dispose();
		parralax2.dispose();
		spriteBatch.dispose();
		font.dispose();
		
		m_stage.dispose();
		skin.dispose();
		m_changeEquipmentTexture.dispose();
		m_defaultButtonTexture.dispose();
	}

	@Override
	public void beginContact(Contact contact) 
	{
		// TODO Auto-generated method stub
		Body body1 = contact.getFixtureA().getBody();
		Body body2 = contact.getFixtureB().getBody();
		ViewedCollidable object1 = (ViewedCollidable) body1.getUserData();
		ViewedCollidable object2 = (ViewedCollidable) body2.getUserData();
		float crashVelocity = Math.abs( body1.getLinearVelocity().dst(0, 0) - body2.getLinearVelocity().dst(0, 0) );
		
		if( object1 != null && object2 != null )
		{
			object1.damageCalc( object2, crashVelocity );
			object2.damageCalc( object1, crashVelocity );
		}
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
		if( WingBlade.class.isInstance( contact.getFixtureA().getBody().getUserData() ) )
		{
			contact.setEnabled(false);
			WingBlade wb = (WingBlade) contact.getFixtureA().getBody().getUserData();
			wb.HandleContact(contact);
		}
		
		if( WingBlade.class.isInstance( contact.getFixtureB().getBody().getUserData() ) )
		{
			contact.setEnabled(false);
			WingBlade wb = (WingBlade) contact.getFixtureB().getBody().getUserData();
			wb.HandleContact(contact);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) 
	{
		// TODO Auto-generated method stub
		
	}
	
	

}
