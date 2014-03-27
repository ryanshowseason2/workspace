package com.me.mygdxgame.Equipables;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.me.mygdxgame.Entities.Ship;

public abstract class CruiseEngine 
{
	Ship m_ship;
	float m_maxVelocity;
	boolean m_enginesEngaged = false;
	boolean m_jetsEngaged = false;
	boolean m_brakesEngaged = false;
	float m_airJetAttachX;
	float m_airJetAttachY;
	float m_lastXForce;
	float m_lastYForce;
	
	ParticleEffect m_engineEffect = new ParticleEffect();
    ParticleEffectPool m_engineEffectPool;
    PooledEffect m_pooledEngineEffect;
    ParticleEffect m_engineTrailEffect = new ParticleEffect();
    ParticleEffectPool m_engineTrailEffectPool;
    PooledEffect m_pooledEngineTrailEffect;
    
    ParticleEffect m_airJetEffectRight = new ParticleEffect();
    ParticleEffect m_airJetEffectDown = new ParticleEffect();
    ParticleEffect m_airJetEffectLeft = new ParticleEffect();
    ParticleEffect m_airJetEffectUp = new ParticleEffect();
    ParticleEffectPool m_airJetEffectPoolRight;
    PooledEffect m_pooledAirJetEffectRight;
    ParticleEffectPool m_airJetEffectPoolDown;
    PooledEffect m_pooledAirJetEffectDown;
    ParticleEffectPool m_airJetEffectPoolLeft;
    PooledEffect m_pooledAirJetEffectLeft;
    ParticleEffectPool m_airJetEffectPoolUp;
    PooledEffect m_pooledAirJetEffectUp;
    
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
	public abstract void EngageAirJets();
	public abstract void DisengageAirJets();

	public abstract void DisengageBrake();
	
}
