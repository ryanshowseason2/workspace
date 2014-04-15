package com.me.mygdxgame.Equipables;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.me.mygdxgame.Entities.PlayerEntity;
import com.me.mygdxgame.Entities.Projectile;
import com.me.mygdxgame.Entities.Ship;
import com.me.mygdxgame.Entities.ViewedCollidable;

public class MachineGun extends CounterMeasure
{
	ViewedCollidable m_secondaryTarget = null;
	int m_activateSecondaryMode = 0;
	int m_secondaryFireFrequency = 6;
	int m_secondaryFireCounter = 0;
	
	public MachineGun(World w, Ship s, ArrayList<ViewedCollidable> aliveThings )
	{
		super(w, s, aliveThings, new Image( new Texture(Gdx.files.internal("data/machinegun.png") ) ) );
		// TODO Auto-generated constructor stub
		m_rangeEnablersAndMultipliers[0] = 1f;
	}
	
	@Override
	public Image GetImageCopy()
	{
		return new Image( new Texture(Gdx.files.internal("data/machinegun.png") ) );
	}

	@Override
	public void AcquireAndFire()
	{
		if( ( m_target != null && m_target.m_integrity <= 0 ) )
		{
			m_target = null;
		}
		
		if( m_target == null )
		{
			// Pull the closest tracked target from the ship computer! 
			float leastDistance = Float.MAX_VALUE;
			for( int i = 0; i < m_ship.m_trackedTargets.size(); i++ )
			{
				ViewedCollidable vc = m_ship.m_trackedTargets.get(i);
				float distance = vc.m_body.getPosition().dst(m_ship.m_body.getPosition());
				if( distance <= m_range && distance < leastDistance )
				{
					leastDistance = distance;
					m_target = vc;
				}
			}
		}
		
		if( m_target != null && m_fireCounter <= 0 )
		{
			float distanceToCurrentTarget = m_target.m_body.getPosition().dst(m_ship.m_body.getPosition() );

			if( distanceToCurrentTarget <= m_range )
			{
				float centerX = m_ship.m_body.getPosition().x;
				float centerY = m_ship.m_body.getPosition().y;
				Projectile p = new Projectile("data/bullet.png", m_world, centerX, centerY, m_aliveThings, m_ship.m_factionCode );				
				p.Fire(m_ship, m_target, (float) Math.random()/2 - .25f);
				m_fireCounter = m_fireFrequency;
				m_ship.IncreaseDetectionRange( 5f );
			}
			else
			{
				m_target = null;
			}
		}
		else
		{
			m_fireCounter-= 1;
		}
		
		if( m_activateSecondaryMode > 0 )
		{
			if( m_activateSecondaryMode > 40 )
			{
				if( ( m_secondaryTarget != null && m_secondaryTarget.m_integrity <= 0 ) )
				{
					m_secondaryTarget = null;
				}
				
				if( m_secondaryTarget == null )
				{
					if( m_target != null )
					{
						m_secondaryTarget = m_target;
						m_target = null;
					}
				}
				
				if( m_secondaryTarget != null && m_secondaryFireCounter <= 0 )
				{
					float distanceToCurrentTarget = m_secondaryTarget.m_body.getPosition().dst(m_ship.m_body.getPosition() );
		
					if( distanceToCurrentTarget <= m_range )
					{
						float centerX = m_ship.m_body.getPosition().x;
						float centerY = m_ship.m_body.getPosition().y;
						Projectile p = new Projectile("data/bullet.png", m_world, centerX, centerY, m_aliveThings, m_ship.m_factionCode );				
						p.Fire(m_ship, m_secondaryTarget, (float) Math.random()/2 - .25f);
						m_secondaryFireCounter = m_secondaryFireFrequency;
						m_ship.IncreaseDetectionRange( 5f );					
					}
					else
					{
						m_secondaryTarget = null;
					}
				}
				else
				{
					m_secondaryFireCounter-= 1;
				}
			}
			
			m_activateSecondaryMode--;
			
			if(m_activateSecondaryMode <= 0)
			{
				DisengageCM();
			}
		}
	}

	@Override
	public void EngageCM( Button b )
	{
		super.EngageCM(b);
		m_activateSecondaryMode = 280;
		m_secondaryFireFrequency = m_fireFrequency;
		m_secondaryFireCounter = m_fireCounter = 0;				
	}

	@Override
	public void DisengageCM()
	{
		super.DisengageCM();		
	}

	@Override
	public boolean reportFixture(Fixture fixture)
	{
		Body potentialTarget = fixture.getBody();
		float distanceToPotential = potentialTarget.getPosition().dst(m_ship.m_body.getPosition() );
		float distanceToCurrentTarget = Float.MAX_VALUE;
		ViewedCollidable vc = (ViewedCollidable) potentialTarget.getUserData();
		
		if(m_target != null)
		{
			distanceToCurrentTarget = m_target.m_body.getPosition().dst(m_ship.m_body.getPosition() );
		}
		
		if( potentialTarget != m_ship.m_body && 
			distanceToPotential <= m_range &&
			distanceToPotential < distanceToCurrentTarget &&
			m_ship.m_factionCode != vc.m_factionCode &&
			vc.m_factionCode != 0 &&
			vc.m_isTargetable &&
			vc.m_body != m_secondaryTarget.m_body )
		{
			m_target = (ViewedCollidable) potentialTarget.getUserData();
		}
		
		return true;
	}

}
