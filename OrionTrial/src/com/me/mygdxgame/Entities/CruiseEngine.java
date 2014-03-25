package com.me.mygdxgame.Entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;

public abstract class CruiseEngine 
{
	Ship m_ship;
	float m_maxVelocity;
	boolean m_enginesEngaged = false;
	
	ParticleEffect m_engineEffect = new ParticleEffect();
    ParticleEffectPool m_engineEffectPool;
    PooledEffect m_pooledEngineEffect;
	ParticleEffect m_engineTrailEffect = new ParticleEffect();
    ParticleEffectPool m_engineTrailEffectPool;
    PooledEffect m_pooledEngineTrailEffect;
    
	public CruiseEngine( Ship s, float maxVelocity )
	{
		m_ship = s;
		m_maxVelocity = maxVelocity;
	}
	
	public abstract void ThrottleForward();
	public abstract void ThrottleBackward();
	public abstract void ThrottlePort();
	public abstract void ThrottleStarboard();
	public abstract void EngineBrake();
	public abstract void DisengageEngine();
	public abstract void EngageEngine();
	public abstract void ProcessVelocity();
	public abstract void Draw( SpriteBatch renderer );
}
