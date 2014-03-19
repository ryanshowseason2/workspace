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
      
      float mass = m_body.getMass();
      float degrees = (float) (angle * 180 / Math.PI);
      float difference = degrees - m_angle;
      m_objectSprite.rotate( (float) (difference) );
      m_angle = (float) degrees;
      m_body.setTransform(m_body.getPosition(), (float) Math.toRadians( m_angle ) );
      
      // apply left impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.A) && vel > -m_maxVelocity) 
      {          
           //m_body.applyForce(-900.0f, 0, pos.x, pos.y, true);
           m_body.applyForce( (float)(-900f * Math.sin(angle)), (float)(900.0f * Math.cos(angle)), pos.x, pos.y, true);
      }

      // apply right impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.D) && vel < m_maxVelocity) 
      {
           //m_body.applyForce(900.0f, 0, pos.x, pos.y, true);
           m_body.applyForce( (float)(900f * Math.sin(angle)), (float)(-900.0f * Math.cos(angle)), pos.x, pos.y, true);
           
      }
      
            // apply left impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.S) && vel > -m_maxVelocity) 
      {          
           //m_body.applyForce(0, -900.0f, pos.x, pos.y, true);
           m_body.applyForce( (float)(-900f * Math.cos(angle)), (float)(-900.0f * Math.sin(angle)), pos.x, pos.y, true);
      }

      // apply right impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.W) && vel < m_maxVelocity) 
      {
           m_body.applyForce( (float)(900f * Math.cos(angle)), (float)(900.0f * Math.sin(angle)), pos.x, pos.y, true);
      }

   }

}
