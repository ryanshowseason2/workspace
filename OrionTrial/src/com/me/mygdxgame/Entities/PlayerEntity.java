package com.me.mygdxgame.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Screens.CombatScreen.ParallaxCamera;

public class PlayerEntity extends ViewedCollidable {

	public PlayerEntity(String appearanceLocation, World world, float startX,
			float startY, float initialAngleAdjust ) {
		super(appearanceLocation, world, startX, startY);
		// TODO Auto-generated constructor stub
		
		m_objectSprite.rotate((float) initialAngleAdjust);
		m_body.setFixedRotation(true);
		MassData data = m_body.getMassData();
		data.mass = 100;
		m_body.setMassData(data);
	}
	
	float m_maxVelocity = 20;
	float m_angle = 0;
	   
   public void HandleMovement(ParallaxCamera cam)
   {
      float vel = m_body.getLinearVelocity().dst(0, 0);
      Vector2 pos = m_body.getPosition();
      Vector3 vec = new Vector3( Gdx.input.getX(0), Gdx.input.getY(0) ,0 );
      cam.unproject( vec );
      double angle = Math.atan2(vec.y - pos.y, vec.x - pos.x);
      
      float degrees = (float) (angle * 180 / Math.PI);
      float difference = degrees - m_angle;
      m_objectSprite.rotate( (float) (difference) );
      m_angle = (float) degrees;
      m_body.setTransform(m_body.getPosition(), (float) Math.toRadians( m_angle ) );
      
      
      float xForce = 0;
      float yForce = 0;
      
      // apply left impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.A)) 
      {          
           //m_body.applyForce(-900.0f, 0, pos.x, pos.y, true);
           xForce =  (float)(-450f * Math.sin(angle));
           yForce =  (float)(450.0f * Math.cos(angle));
      }

      // apply right impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.D) ) 
      {
           
          xForce = (float)(450f * Math.sin(angle));
          yForce = (float)(-450.0f * Math.cos(angle));
           
      }
      
            // apply left impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.S) ) 
      {          
          xForce = (float)(-450f * Math.cos(angle));
          yForce = (float)(-450.0f * Math.sin(angle));
      }

      // apply right impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.W) ) 
      {
          xForce = (float)(900f * Math.cos(angle));
          yForce = (float)(900.0f * Math.sin(angle));
      }
      
      // apply stopping impulse
      if (Gdx.input.isKeyPressed(Keys.X) ) 
      {
          xForce = (float)(-300.0f * m_body.getLinearVelocity().x);
          yForce = (float)(-300.0f * m_body.getLinearVelocity().y);
      }
      
      if( Math.abs(vel) > m_maxVelocity )
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
      }      
      
      m_body.applyForce( xForce, yForce, pos.x, pos.y, true);

   }

}
