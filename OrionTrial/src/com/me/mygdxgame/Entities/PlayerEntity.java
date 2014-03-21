package com.me.mygdxgame.Entities;

import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Screens.CombatScreen.ParallaxCamera;

public class PlayerEntity extends ViewedCollidable implements InputProcessor
{

	public PlayerEntity(String appearanceLocation, World world, float startX,
			float startY, float initialAngleAdjust ) {
		super(appearanceLocation, world, startX, startY);
		// TODO Auto-generated constructor stub
		
		m_objectSprite.rotate((float) initialAngleAdjust);
		m_body.setFixedRotation(true);
		MassData data = m_body.getMassData();
		data.mass = 10;
		m_body.setMassData(data);
		m_body.setUserData(this);		
	}
	
	float m_maxVelocity = 50;
	float m_angleDegrees = 0;
	double m_angleRadians = 0;
	int m_lastKey = -1;
	long m_keyPressedMilliseconds = 0;
	public int m_boostJuice = 12000;
	   
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
      
      
      float xForce = 0;
      float yForce = 0;
      
      // apply left impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.A)) 
      {          
           //m_body.applyForce(-900.0f, 0, pos.x, pos.y, true);
           xForce =  (float)(-450f * Math.sin(m_angleRadians));
           yForce =  (float)(450.0f * Math.cos(m_angleRadians));
      }

      // apply right impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.D) ) 
      {
           
          xForce = (float)(45f * Math.sin(m_angleRadians));
          yForce = (float)(-45.0f * Math.cos(m_angleRadians));
           
      }
      
            // apply left impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.S) ) 
      {          
          xForce = (float)(-45f * Math.cos(m_angleRadians));
          yForce = (float)(-45.0f * Math.sin(m_angleRadians));
      }

      // apply right impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.W) ) 
      {
          xForce = (float)(90f * Math.cos(m_angleRadians));
          yForce = (float)(90.0f * Math.sin(m_angleRadians));
      }
      
      // apply stopping impulse
      if (Gdx.input.isKeyPressed(Keys.X) ) 
      {
          xForce = (float)(-30.0f * m_body.getLinearVelocity().x);
          yForce = (float)(-30.0f * m_body.getLinearVelocity().y);
      }
      
      if( Math.abs(vel) >= m_maxVelocity )
      {
    	  if( m_body.getLinearVelocity().x > 0 && xForce > 0 ||
        	  m_body.getLinearVelocity().x < 0 && xForce < 0 )
    	  {
    		  xForce = 0;
    	  }
    	  
    	  if( m_body.getLinearVelocity().y > 0 && yForce > 0 ||
        	  m_body.getLinearVelocity().y < 0 && yForce < 0 )
    	  {
    		  yForce = 0;
    	  }
    	  
    	  m_body.setLinearDamping( 4f );
      }   
      else
      {
    	  m_body.setLinearDamping(0f);
    	  if( m_boostJuice < 12500 )
    		  m_boostJuice += 20;
      }
      
      m_body.applyForce( xForce, yForce, pos.x, pos.y, true);

   }

	@Override
	public boolean keyDown(int keycode) 
	{
		Date d = new Date();
		if( keycode == m_lastKey &&
			( d.getTime() - m_keyPressedMilliseconds ) < 300 &&
			m_body.getLinearDamping() == 0 && 
			m_boostJuice > 4500 )
		{
		  float xForce = 0;
		  float yForce = 0;
			
	      if (keycode == Keys.A ) 
	      {          
	           //m_body.applyForce(-900.0f, 0, pos.x, pos.y, true);
	           xForce =  (float)(-15000f * Math.sin(m_angleRadians));
	           yForce =  (float)(15000.0f * Math.cos(m_angleRadians));
	           m_boostJuice -= 4500;
	      }
	
	      // apply right impulse, but only if max velocity is not reached yet
	      if ( keycode == Keys.D ) 
	      {
	           
	          xForce = (float)(15000f * Math.sin(m_angleRadians));
	          yForce = (float)(-15000.0f * Math.cos(m_angleRadians));
	          m_boostJuice -= 4500;
	           
	      }
		      
	      // apply left impulse, but only if max velocity is not reached yet
	      if (keycode == Keys.S ) 
	      {          
	          xForce = (float)(-15000f * Math.cos(m_angleRadians));
	          yForce = (float)(-15000.0f * Math.sin(m_angleRadians));
	          m_boostJuice -= 4500;
	      }
	
	      // apply right impulse, but only if max velocity is not reached yet
	      if (keycode == Keys.W ) 
	      {
	          xForce = (float)(15000f * Math.cos(m_angleRadians));
	          yForce = (float)(15000.0f * Math.sin(m_angleRadians));
	          m_boostJuice -= 4500;
	      }
	      
	      Vector2 pos = m_body.getPosition();
	      m_body.applyForce( xForce, yForce, pos.x, pos.y, true);
		}
		return true;
	}
	
	@Override
	public boolean keyUp(int keycode) 
	{
		m_lastKey = keycode;
		Date d = new Date();
		m_keyPressedMilliseconds = d.getTime();
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
		object2.damageIntegrity(crashVelocity * m_body.getMass() );	
	}

}
