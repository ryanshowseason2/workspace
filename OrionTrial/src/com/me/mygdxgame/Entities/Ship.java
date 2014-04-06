package com.me.mygdxgame.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Equipables.ConventionalCruiseEngine;
import com.me.mygdxgame.Equipables.ConventionalManeuverEngine;
import com.me.mygdxgame.Equipables.CounterMeasure;
import com.me.mygdxgame.Equipables.CruiseEngine;
import com.me.mygdxgame.Equipables.ManeuverEngine;

public class Ship extends ViewedCollidable 
{

	public CruiseEngine ce;
	public ManeuverEngine me;
	ArrayList<CounterMeasure> m_shortRangeCMS = new ArrayList< CounterMeasure >();
	ArrayList<CounterMeasure> m_mediumRangeCMS = new ArrayList< CounterMeasure >();
	ArrayList<CounterMeasure> m_longRangeCMS = new ArrayList< CounterMeasure >();
	float m_maxVelocity;
	ArrayList<ViewedCollidable> m_aliveThings;
	
	ParticleEffect m_shieldEffect = new ParticleEffect();
    ParticleEffectPool m_shieldEffectPool;
    PooledEffect m_pooledShieldEffect;
	
	
	public Ship(String appearanceLocation, World world, float startX, float startY, float maxV, ArrayList<ViewedCollidable> aliveThings, int factionCode ) 
	{
		super(appearanceLocation, world, startX, startY, aliveThings, factionCode );
		// TODO Auto-generated constructor stub
		m_maxVelocity = maxV;
		me = new ConventionalManeuverEngine( this, maxV );
		ce = new ConventionalCruiseEngine( this, maxV );
		m_aliveThings = aliveThings;
		
		m_shieldEffect.load(Gdx.files.internal("data/shield.p"), Gdx.files.internal("data/"));
		m_shieldEffectPool = new ParticleEffectPool(m_shieldEffect, 1, 2);
		m_pooledShieldEffect = m_shieldEffectPool.obtain();
	}

	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity) 
	{
		// TODO Auto-generated method stub
		int i = 0;
	}
	
	public void AddShortRangeCounterMeasure( CounterMeasure c)
	{
		m_shortRangeCMS.add(c);
	}

	public void ProcessCounterMeasures()
	{
		for( int i = 0; i < m_shortRangeCMS.size(); i++ )
		{
			m_shortRangeCMS.get(i).AcquireAndFire();
		}
	}
	
	@Override
	public void Draw( SpriteBatch renderer )
	{
		super.Draw(renderer);
		ProcessCounterMeasures();
		
		float[] r ={1,1,0,1};
		m_pooledShieldEffect.getEmitters().get(0).getTint().setColors( r );
		m_pooledShieldEffect.setPosition( m_objectXPosition , m_objectYPosition );
		m_pooledShieldEffect.draw(renderer, 1f/60f);
	}
}
