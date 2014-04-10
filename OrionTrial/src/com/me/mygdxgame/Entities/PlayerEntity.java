package com.me.mygdxgame.Entities;

import java.util.ArrayList;
import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Entities.ViewedCollidable.DamageType;
import com.me.mygdxgame.Equipables.CounterMeasure;
import com.me.mygdxgame.Screens.CombatScreen.ParallaxCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class PlayerEntity extends Ship implements InputProcessor, RayCastCallback
{

	public PlayerEntity(String appearanceLocation, World world, float startX,
			float startY, float initialAngleAdjust, float maxV, ArrayList<ViewedCollidable> aliveThings, ParallaxCamera cam ) 
	{
		super(appearanceLocation, world, startX, startY, maxV, aliveThings, 1 );
		// TODO Auto-generated constructor stub
		
		m_objectSprite.rotate((float) initialAngleAdjust);
		m_body.setFixedRotation(true);
		MassData data = m_body.getMassData();
		data.mass = 10;
		m_body.setMassData(data);
		m_body.setUserData(this);		
		m_deathEffect.load(Gdx.files.internal("data/explosionred.p"), Gdx.files.internal("data/"));
		m_deathEffectPool = new ParticleEffectPool(m_deathEffect, 1, 2);
		m_pooledDeathEffect = m_deathEffectPool.obtain();	
		m_cam = cam;
	}
	
	int m_lastKey = -1;
	long m_keyPressedMilliseconds = 0;
	ParallaxCamera m_cam;
	public PlayerButtonListener m_buttonListener = new PlayerButtonListener();
	public Button m_longRange;
	public Button m_mediumRange;
	public Button m_shortRange;
	public Window m_window;
	   
   public void HandleMovement(ParallaxCamera cam)
   {
      float vel = m_body.getLinearVelocity().dst(0, 0);
      Vector2 pos = m_body.getPosition();
      Vector3 vec = new Vector3( Gdx.input.getX(0), Gdx.input.getY(0) ,0 );
      cam.unproject( vec );
      m_angleRadians = Math.atan2(vec.y - pos.y*29f, vec.x - pos.x*29f);
      
      float degrees = (float) (m_angleRadians * 180 / Math.PI);
      float difference = degrees - m_angleDegrees;
      m_objectSprite.rotate( (float) (difference) );
      m_angleDegrees = (float) degrees;
      m_body.setTransform(m_body.getPosition(), (float) Math.toRadians( m_angleDegrees ) );
      
      
      
      // apply left impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.A)) 
      {          
    	  ce.ThrottlePort();
      }

      // apply right impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.D) ) 
      {        
    	  ce.ThrottleStarboard();
           
      }
      
            // apply left impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.S) ) 
      {          
    	  ce.ThrottleBackward();
      }

      // apply right impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.W) ) 
      {
    	  ce.ThrottleForward();
      }
      
      // apply stopping impulse
      if (Gdx.input.isKeyPressed(Keys.X) ) 
      {
    	  ce.EngineBrake();
      }
      
      if( Math.abs(vel) < me.m_maxVelocity )
      {
    	  if( me.m_boostJuice < 100 )
    		  me.m_boostJuice += .2;
      }
      
      ce.ProcessVelocity();
      //m_body.applyForce( xForce, yForce, pos.x, pos.y, true);

   }

	@Override
	public boolean keyDown(int keycode) 
	{
		Date d = new Date();
		if( keycode == m_lastKey &&
			( d.getTime() - m_keyPressedMilliseconds ) < 200 &&
			me.m_boostJuice > 30 )
		{			
	      if (keycode == Keys.A ) 
	      {          
	    	  me.ManeuverPort();
	      }
	
	      // apply right impulse, but only if max velocity is not reached yet
	      if ( keycode == Keys.D ) 
	      {
	          me.ManeuverStarboard();
	      }
		      
	      // apply left impulse, but only if max velocity is not reached yet
	      if (keycode == Keys.S ) 
	      {          
	    	  me.ManeuverBackward();
	      }
	
	      // apply right impulse, but only if max velocity is not reached yet
	      if (keycode == Keys.W ) 
	      {
	    	  me.ManeuverForward();
	      }	      
		}
		
		if( keycode == Keys.W )
		{
			ce.EngageEngine();
		}
		
		if( keycode == Keys.A ||
			keycode == Keys.S ||
			keycode == Keys.D )
		{
			ce.EngageAirJets();
		}
		
		return true;
	}
	
	@Override
	public boolean keyUp(int keycode) 
	{
		m_lastKey = keycode;
		Date d = new Date();
		m_keyPressedMilliseconds = d.getTime();
		
		if( keycode == Keys.W )
		{
			ce.DisengageEngine();			
		}
		
		if( keycode == Keys.X )
		{
			ce.DisengageBrake();			
		}
		
		if( keycode == Keys.A ||
			keycode == Keys.S ||
			keycode == Keys.D )
		{
			ce.DisengageAirJets();
		}
			
		return true;
	}
	
	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		Vector3 vec = new Vector3( screenX, screenY ,0 );
		m_cam.calculateParallaxMatrix(1f, 1f);
		m_cam.unproject( vec );
		float screenXf = vec.x / 29;
		float screenYf = vec.y / 29;
		Vector2 point = new Vector2();
		point.x = screenXf;
		point.y = screenYf;
		
		m_world.rayCast(this, m_body.getPosition(), point);
		
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity) 
	{
		if( crashVelocity > 1 )
		{
			object2.damageIntegrity(crashVelocity * m_body.getMass()/ 30, DamageType.Collision );
		}
	}
	
	@Override
	public void Draw( SpriteBatch renderer )
    {
		super.Draw(renderer);
		
		
		
		ce.Draw(renderer);
		
	
    }
	
	@Override
	public void damageIntegrity( float damage , DamageType type)
	{
		super.damageIntegrity(damage, type);
		m_integrity = 1000f;
	}

	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction)
	{
		ViewedCollidable target = (ViewedCollidable) fixture.getBody().getUserData();
		if( target != this &&
			target.m_isTargetable )
		{
			for( int i = 0; i < m_shortRangeCMS.size(); i++ )
			{
				m_shortRangeCMS.get(i).SetTarget( target );
			}
		}
		return 1;
	}
	
	@Override
	public void AddShortRangeCounterMeasure( CounterMeasure c)
	{
		super.AddShortRangeCounterMeasure(c);
		m_shortRange.clearChildren();
		m_shortRange.add(m_shortRangeCMS.get(0).m_icon);
		m_window.pack();
	}

}
