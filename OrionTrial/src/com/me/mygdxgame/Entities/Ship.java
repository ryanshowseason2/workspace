package com.me.mygdxgame.Entities;

import java.util.ArrayList;
import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Entities.Projectile.Characters;
import com.me.mygdxgame.Entities.ViewedCollidable.DamageType;
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
	
    float[] m_shieldDamageResistances = {1,1,1,1};
    float[] m_shieldDamageReductions = {0,0,0,0};
    public float m_shieldIntegrity = 1000f;
    int m_shieldRechargeDelay = 120;
    int m_shieldRechargeCounter = 0;
    public float m_shieldIntegrityRechargeFactor = 1;
    float m_sensorRange = 30;
    public ArrayList<ViewedCollidable> m_trackedTargets = new ArrayList<ViewedCollidable>();
    public float m_softwareIntegrity = 1000f;
    int m_hackedDrawCounter = 0;
    BitmapFont m_font;
    EnumMap<Characters, Boolean> m_specialAbilitiesActivated = new EnumMap<Characters, Boolean>(Characters.class);
    
    ArrayList<OverTimeEffect> m_overTimeEffects = new ArrayList< OverTimeEffect >();
	
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
		m_detectionRange = 50f;
		m_font = new BitmapFont(Gdx.files.internal("data/font16.fnt"), false);
		m_shieldDamageReductions[DamageType.Energy.value] = 1f;
	}

	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity) 
	{
		// TODO Auto-generated method stub
		int i = 0;
	}
	
	@Override
	public void damageIntegrity(float damage, DamageType type )
    {
	    damageIntegrity(damage, type, false );
    }
	
	@Override
	public void damageIntegrity(float damage, DamageType type, boolean bypassResistances )
    {
		float damageToIntegrity = damage;
		
	    if( m_shieldIntegrity > 0 && !bypassResistances )
	    {		   		  
		   damage = damage * m_shieldDamageResistances[type.value];
		   damage = damage > m_shieldDamageReductions[type.value] ? damage - m_damageReductions[type.value] : 0;
		   damageToIntegrity = 0;
		   m_shieldIntegrity = damage > m_shieldIntegrity ? 0 : m_shieldIntegrity - damage;		  
	    }	    

	    if( damage > 0 )
	    {
		   m_shieldRechargeCounter = m_shieldRechargeDelay;
	    }
	    
	    //super.damageIntegrity(damageToIntegrity, type, bypassResistances );
    }
	
	public void AddShortRangeCounterMeasure( CounterMeasure c)
	{
		m_shortRangeCMS.add(c);
		m_shortRangeCMS.get(0).Equip(0);
	}
	
	public void AddMidRangeCounterMeasure( CounterMeasure c)
	{
		m_mediumRangeCMS.add(c);
		m_mediumRangeCMS.get(0).Equip(1);
	}
	
	public void AddLongRangeCounterMeasure( CounterMeasure c)
	{
		m_longRangeCMS.add(c);
		m_longRangeCMS.get(0).Equip(2);
	}

	public void ProcessCounterMeasures(SpriteBatch renderer)
	{
		for( int i = 0; i < m_shortRangeCMS.size(); i++ )
		{
			m_shortRangeCMS.get(i).AcquireAndFire( renderer );
		}
		
		for( int i = 0; i < m_mediumRangeCMS.size(); i++ )
		{
			m_mediumRangeCMS.get(i).AcquireAndFire( renderer );
		}
		
		for( int i = 0; i < m_longRangeCMS.size(); i++ )
		{
			m_longRangeCMS.get(i).AcquireAndFire( renderer );
		}
	}
	
	@Override
	public void Draw( SpriteBatch renderer )
	{
		super.Draw(renderer);
		
		if( !m_inMenu )
		{
			m_detectionRange = 50f;
			ProcessCounterMeasures( renderer );
			ce.Draw(renderer);
			HandleShieldRecharging();
			
			SetShieldColor();
			m_pooledShieldEffect.setPosition( m_objectXPosition , m_objectYPosition );
			m_pooledShieldEffect.draw(renderer, 1f/60f);
			
			DrawHackedIndicator(renderer);
			
			for( int i = 0; i < m_overTimeEffects.size(); i++ )
			{
				OverTimeEffect e = m_overTimeEffects.get(i);
				if( !e.Action() )
				{
					m_overTimeEffects.remove(e);
				}
			}
		}
	}

	private void DrawHackedIndicator(SpriteBatch renderer)
	{
		if( m_hackedDrawCounter > 0 )
		{
			m_font.draw(renderer, "HACKED!"  , m_body.getPosition().x * 29f, m_body.getPosition().y * 29f + m_hackedDrawCounter% 60);
			m_hackedDrawCounter--;
		}
	}

	private void HandleShieldRecharging()
	{		
		if( m_shieldIntegrity < 1000f && m_shieldRechargeCounter <= 0 )
		{
			m_shieldIntegrity+= m_shieldIntegrityRechargeFactor;
		}
		else
		{
			m_shieldRechargeCounter--;
		}		
		
		if( m_shieldIntegrity <= 0 && m_shieldIntegrityRechargeFactor < 0)
		{
			m_shieldIntegrityRechargeFactor= 1;
		}
	}

	private void SetShieldColor()
	{
		float[] r ={1,1,1,1};
		float shieldPercent = m_shieldIntegrity / 1000f;
		
		if(shieldPercent > .9f )
		{
			// white
			r[0] = 1f;
			r[1] = 1f;
			r[2] = 1f;
		}
		else if(shieldPercent > .75f )
		{
			//blue
			r[0] = .1f;
			r[1] = .75f;
			r[2] = 1f;
		}
		else if(shieldPercent > .5f )
		{
			//yellow
			r[0] = 1f;
			r[1] = 1f;
			r[2] = .25f;
		}
		else if(shieldPercent > .3f )
		{
			//orange
			r[0] = 1f;
			r[1] = .25f;
			r[2] = .1f;
		}
		else if(shieldPercent > 0f )
		{
			//red
			r[0] = 1f;
			r[1] = 0f;
			r[2] = 0f;
		}
		else
		{
			// black no shields
			r[0] = 0f;
			r[1] = 0f;
			r[2] = 0f;
		}
		
		m_pooledShieldEffect.getEmitters().get(0).getTint().setColors( r );
	}

	public void IncreaseDetectionRange(float f)
	{
		m_detectionRange += f;		
	}

	public boolean AttemptHack(float d)
	{
		m_softwareIntegrity-=d;
		float attempt =  (float) (Math.random() * 1000);
		boolean b = attempt > m_softwareIntegrity ? true : false;
		m_hackedDrawCounter = ( b ? m_hackedDrawCounter == 0 ? 60: m_hackedDrawCounter : m_hackedDrawCounter );
		return b;
	}
	
	@Override
	public void destroy()
	{
	}
	
	public void AddOverTimeEffect( OverTimeEffect e )
	{
		boolean found = false;
		for( int i = 0; i < m_overTimeEffects.size() && !found; i++ )
		{
			OverTimeEffect tmp = m_overTimeEffects.get(i);
			if( tmp.m_effectCode == e.m_effectCode )
			{
				tmp.m_counter = e.m_counter;
				found = true;
			}
		}
		
		if( !found )
		{
			m_overTimeEffects.add(e);
		}
	}
}
