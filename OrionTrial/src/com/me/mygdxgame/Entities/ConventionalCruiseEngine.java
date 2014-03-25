package com.me.mygdxgame.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ConventionalCruiseEngine extends CruiseEngine
{

	public ConventionalCruiseEngine(Ship s, float maxVelocity)
	{
		super(s, maxVelocity);
		// TODO Auto-generated constructor stub
		m_engineEffect.load(Gdx.files.internal("data/engine.p"), Gdx.files.internal("data/"));
		m_engineEffectPool = new ParticleEffectPool(m_engineEffect, 1, 2);
		m_pooledEngineEffect = m_engineEffectPool.obtain();
		
		m_engineTrailEffect.load(Gdx.files.internal("data/enginetrail.p"), Gdx.files.internal("data/"));
				
		m_engineTrailEffectPool = new ParticleEffectPool(m_engineTrailEffect, 1, 2);
		m_pooledEngineTrailEffect = m_engineTrailEffectPool.obtain();
	}
	
	public void ApplyThrust( float forceX, float forceY)
	{
		float vel = m_ship.m_body.getLinearVelocity().dst(0, 0);
		
		if( Math.abs(vel) >= m_maxVelocity )
		{
			  if( m_ship.m_body.getLinearVelocity().x > 0 && forceX > 0 ||
					  m_ship.m_body.getLinearVelocity().x < 0 && forceX < 0 )
			  {
				  forceX = 0;
			  }
			  
			  if( m_ship.m_body.getLinearVelocity().y > 0 && forceY > 0 ||
					  m_ship.m_body.getLinearVelocity().y < 0 && forceY < 0 )
			  {
				  forceY = 0;
			  }
			  
			  m_ship.m_body.setLinearDamping( 1f );
		}   
		else
		{
			m_ship.m_body.setLinearDamping(0f);
		}
		
		m_ship.m_body.applyForceToCenter(forceX, forceY, true);
	}

	@Override
	public void ThrottleForward()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
	    float yForce = 0;
	    xForce = (float)(90f * Math.cos(m_ship.m_angleRadians));
        yForce = (float)(90.0f * Math.sin(m_ship.m_angleRadians));
        
        ApplyThrust( xForce, yForce );
	}

	@Override
	public void ThrottleBackward()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
	    float yForce = 0;
	    xForce = (float)(-45f * Math.cos(m_ship.m_angleRadians));
        yForce = (float)(-45.0f * Math.sin(m_ship.m_angleRadians));
	    ApplyThrust( xForce, yForce );
	}

	@Override
	public void ThrottlePort()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
	    float yForce = 0;
	    xForce =  (float)(-45f * Math.sin(m_ship.m_angleRadians));
        yForce =  (float)(45.0f * Math.cos(m_ship.m_angleRadians));
	    ApplyThrust( xForce, yForce );
	}

	@Override
	public void ThrottleStarboard()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
	    float yForce = 0;
	    xForce = (float)(45f * Math.sin(m_ship.m_angleRadians));
        yForce = (float)(-45.0f * Math.cos(m_ship.m_angleRadians));
	    ApplyThrust( xForce, yForce );
	}

	@Override
	public void DisengageEngine()
	{
		// TODO Auto-generated method stub
		m_enginesEngaged = false;
		m_pooledEngineEffect.reset();
	}

	@Override
	public void EngageEngine()
	{
		// TODO Auto-generated method stub
		m_enginesEngaged = true;
	}

	@Override
	public void Draw(SpriteBatch renderer)
	{
		// TODO Auto-generated method stub
		float radius = Math.max(m_ship.m_objectAppearance.getWidth() / 2, m_ship.m_objectAppearance.getHeight() ) / 2;
		float xdelta = (float) (Math.cos(m_ship.m_angleRadians) * radius);
		float ydelta = (float) (Math.sin(m_ship.m_angleRadians) * radius);
		

		if( m_enginesEngaged )
		{						
			m_pooledEngineEffect.setPosition( m_ship.m_objectXPosition - xdelta, m_ship.m_objectYPosition - ydelta );
			m_pooledEngineEffect.draw(renderer, 1f/60f);
		}
		
		if( m_ship.m_integrity > 0 )
		{
			m_pooledEngineTrailEffect.setPosition( m_ship.m_objectXPosition - xdelta, m_ship.m_objectYPosition - ydelta );
			m_pooledEngineTrailEffect.draw(renderer, 1f/60f);
		}
	}

	@Override
	public void EngineBrake()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
	    float yForce = 0;
	    xForce = (float)(-30.0f * m_ship.m_body.getLinearVelocity().x);
        yForce = (float)(-30.0f * m_ship.m_body.getLinearVelocity().y);
	    ApplyThrust( xForce, yForce );
	}

	@Override
	public void ProcessVelocity()
	{
		// TODO Auto-generated method stub
		float vel = m_ship.m_body.getLinearVelocity().dst(0, 0);
		
		if( vel < m_maxVelocity )
		{
			m_ship.m_body.setLinearDamping(0f);
		}
	}

}
