package com.me.mygdxgame.Equipables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.me.mygdxgame.Entities.Ship;

public class InertialCruiseEngine extends CruiseEngine
{
	public InertialCruiseEngine(Ship s, float maxVelocity)
	{
		super(s, maxVelocity);
		// TODO Auto-generated constructor stub
		m_engineEffect.load(Gdx.files.internal("data/engine.p"), Gdx.files.internal("data/"));
		m_engineEffectPool = new ParticleEffectPool(m_engineEffect, 1, 2);
		m_pooledEngineEffect = m_engineEffectPool.obtain();
		
		m_engineTrailEffect.load(Gdx.files.internal("data/enginetrail.p"), Gdx.files.internal("data/"));
				
		m_engineTrailEffectPool = new ParticleEffectPool(m_engineTrailEffect, 1, 2);
		m_pooledEngineTrailEffect = m_engineTrailEffectPool.obtain();
		
		m_airJetEffectRight.load(Gdx.files.internal("data/airstream.p"), Gdx.files.internal("data/"));
		m_airJetEffectDown.load(Gdx.files.internal("data/airstream.p"), Gdx.files.internal("data/"));
		m_airJetEffectLeft.load(Gdx.files.internal("data/airstream.p"), Gdx.files.internal("data/"));
		m_airJetEffectUp.load(Gdx.files.internal("data/airstream.p"), Gdx.files.internal("data/"));
		
		m_airJetEffectRight.getEmitters().get(0).setAttached(true);
		m_airJetEffectRight.getEmitters().get(0).getAngle().setHigh(5f, -5f);
		m_airJetEffectPoolRight = new ParticleEffectPool(m_airJetEffectRight, 1, 2);
		m_pooledAirJetEffectRight = m_airJetEffectPoolRight.obtain();
		
		m_airJetEffectDown.getEmitters().get(0).setAttached(true);
		m_airJetEffectDown.getEmitters().get(0).getAngle().setHigh(265f, 275f);
		m_airJetEffectPoolDown = new ParticleEffectPool(m_airJetEffectDown, 1, 2);
		m_pooledAirJetEffectDown = m_airJetEffectPoolDown.obtain();
		
		m_airJetEffectLeft.getEmitters().get(0).setAttached(true);
		m_airJetEffectLeft.getEmitters().get(0).getAngle().setHigh(175f, 185f);
		m_airJetEffectPoolLeft = new ParticleEffectPool(m_airJetEffectLeft, 1, 2);
		m_pooledAirJetEffectLeft = m_airJetEffectPoolLeft.obtain();
		
		m_airJetEffectUp.getEmitters().get(0).setAttached(true);
		m_airJetEffectUp.getEmitters().get(0).getAngle().setHigh(85f, 95f);
		m_airJetEffectPoolUp = new ParticleEffectPool(m_airJetEffectUp, 1, 2);
		m_pooledAirJetEffectUp = m_airJetEffectPoolUp.obtain();
		
	}
	
	public void ApplyThrust( float forceX, float forceY )
	{
		ApplyThrust(forceX, forceY, false );
	}
	
	public void ApplyThrust( float forceX, float forceY, boolean brakingOrForwardMotion )
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
		
		if( !brakingOrForwardMotion )
		{
			m_lastXForce = forceX;
			m_lastYForce = forceY;
		}
		
		m_ship.m_body.applyLinearImpulse(forceX, forceY, 0f, 0f, true);
	}

	@Override
	public void ThrottleForward()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
	    float yForce = 0;
	    xForce = (float)(3f * Math.cos(m_ship.m_angleRadians));
        yForce = (float)(3.0f * Math.sin(m_ship.m_angleRadians));
        
        ApplyThrust( xForce, yForce, true );
	}

	@Override
	public void ThrottleBackward()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
	    float yForce = 0;
	    xForce = (float)(-4.5f * Math.cos(m_ship.m_angleRadians));
        yForce = (float)(-4.5f * Math.sin(m_ship.m_angleRadians));
        
        float radius = Math.max(m_ship.m_objectAppearance.getWidth() / 2, m_ship.m_objectAppearance.getHeight() ) / 2;
        m_airJetAttachX = m_ship.m_body.getPosition().x*29f +(float)(radius * Math.cos(m_ship.m_angleRadians));
        m_airJetAttachY = m_ship.m_body.getPosition().y*29f +(float)(radius * Math.sin(m_ship.m_angleRadians));
        
	    ApplyThrust( xForce, yForce );
	}

	@Override
	public void ThrottlePort()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
	    float yForce = 0;
	    xForce =  (float)(-4.5f * Math.sin(m_ship.m_angleRadians));
        yForce =  (float)(4.5f * Math.cos(m_ship.m_angleRadians));
	    ApplyThrust( xForce, yForce );
	    
	    float radius = Math.max(m_ship.m_objectAppearance.getWidth() / 2, m_ship.m_objectAppearance.getHeight() ) / 2;
	    m_airJetAttachX = m_ship.m_body.getPosition().x*29f +(float)(radius * Math.sin(m_ship.m_angleRadians));
	    m_airJetAttachY = m_ship.m_body.getPosition().y*29f -(float)(radius * Math.cos(m_ship.m_angleRadians));
	}

	@Override
	public void ThrottleStarboard()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
	    float yForce = 0;
	    xForce = (float)(4.5f * Math.sin(m_ship.m_angleRadians));
        yForce = (float)(-4.5f * Math.cos(m_ship.m_angleRadians));
	    ApplyThrust( xForce, yForce );
	    
	    float radius = Math.max(m_ship.m_objectAppearance.getWidth() / 2, m_ship.m_objectAppearance.getHeight() ) / 2;
        m_airJetAttachX = m_ship.m_body.getPosition().x*29f -(float)(radius * Math.sin(m_ship.m_angleRadians));
        m_airJetAttachY = m_ship.m_body.getPosition().y*29f +(float)(radius * Math.cos(m_ship.m_angleRadians));
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
			m_ship.IncreaseDetectionRange( 5f );
		}
		
		if( m_ship.m_integrity > 0 )
		{
			m_pooledEngineTrailEffect.setPosition( m_ship.m_objectXPosition - xdelta, m_ship.m_objectYPosition - ydelta );
			m_pooledEngineTrailEffect.draw(renderer, 1f/60f);
		}
		
		if( m_jetsEngaged  )
		{
			m_ship.IncreaseDetectionRange( 5f );
			if( m_lastXForce > 0 && (m_ship.m_angleDegrees > 45f || m_ship.m_angleDegrees < -45f) )
			{
				m_pooledAirJetEffectLeft.setPosition( m_airJetAttachX, m_airJetAttachY );
				m_pooledAirJetEffectLeft.draw(renderer, 1f/60f);
			}
			
			if( m_lastXForce < 0 && (m_ship.m_angleDegrees > 225f || m_ship.m_angleDegrees < 135f) )
			{
				m_pooledAirJetEffectRight.setPosition( m_airJetAttachX, m_airJetAttachY );
				m_pooledAirJetEffectRight.draw(renderer, 1f/60f);
			}
			
			if( m_lastYForce > 0 && (m_ship.m_angleDegrees > 135f || m_ship.m_angleDegrees < 45f) )
			{
				m_pooledAirJetEffectDown.setPosition( m_airJetAttachX, m_airJetAttachY );
				m_pooledAirJetEffectDown.draw(renderer, 1f/60f);
			}
			
			if( m_lastYForce < 0 && (m_ship.m_angleDegrees > 315f || m_ship.m_angleDegrees < 225f) )
			{
				m_pooledAirJetEffectUp.setPosition( m_airJetAttachX, m_airJetAttachY );
				m_pooledAirJetEffectUp.draw(renderer, 1f/60f);
			}
		}
		
		if( m_pooledAirJetEffectRight.isComplete()&&
			m_pooledAirJetEffectDown.isComplete() &&
			m_pooledAirJetEffectLeft.isComplete() &&
			m_pooledAirJetEffectUp.isComplete() )
		{
			m_jetsEngaged = false;
		}
		
		if( m_brakesEngaged )
		{
			
		}
		
		
	}

	@Override
	public void EngineBrake()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
	    float yForce = 0;
	    xForce = (float)(-1.0f * m_ship.m_body.getLinearVelocity().x);
        yForce = (float)(-1.0f * m_ship.m_body.getLinearVelocity().y);
	    ApplyThrust( xForce, yForce, true );
	    m_brakesEngaged = true;
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

	@Override
	public void EngageAirJets()
	{
		// TODO Auto-generated method stub
		m_jetsEngaged = true;
		m_pooledAirJetEffectRight.reset();
		m_pooledAirJetEffectDown.reset();
		m_pooledAirJetEffectLeft.reset();
		m_pooledAirJetEffectUp.reset();
				
	}

	@Override
	public void DisengageAirJets()
	{
		// TODO Auto-generated method stub
		
		m_pooledAirJetEffectRight.allowCompletion();
		m_pooledAirJetEffectDown.allowCompletion();
		m_pooledAirJetEffectLeft.allowCompletion();
		m_pooledAirJetEffectUp.allowCompletion();
	}

	@Override
	public void DisengageBrake()
	{
		// TODO Auto-generated method stub
		m_brakesEngaged = false;
	}
}
