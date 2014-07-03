package com.me.mygdxgame.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Entities.EnemyShip.SeekType;
import com.me.mygdxgame.Entities.ViewedCollidable.DamageType;
import com.me.mygdxgame.Equipables.ConventionalManeuverEngine;
import com.me.mygdxgame.Equipables.TeleportManeuverEngine;

public class CivilianShuttle extends EnemyShip
{
	enum CivilianBehavior
	{
		HarvestAsteroids,
		ShipBetweenStations,
		ReturnToStation
	}
	
	CivilianBehavior m_behavior;
	PoorStation m_homeStation;
	private int m_unloadCounter;
	
	public CivilianShuttle( World world, float startX, float startY, int factionCode, ArrayList<ViewedCollidable> aliveThings, PoorStation p)
	{
		super("civilianshuttle", 1.5f, world, startX, startY, 0, 10, factionCode, aliveThings);
		me = new ConventionalManeuverEngine(this, 10);
		m_onDeckSeekType = m_seekType = SeekType.EnterFiringRange;
		me.m_boostJuiceMax = 100;
		MassData data = new MassData();
		data.mass = 5;
		m_body.setMassData(data);
		m_pooledShieldEffect.getEmitters().get(0).getScale().setHigh(83);
		m_pooledShieldEffect.getEmitters().get(0).getScale().setLow(71);
		m_behavior = CivilianBehavior.HarvestAsteroids;
		m_homeStation = p;
	}
	
	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity)
	{		
		object2.damageIntegrity((crashVelocity * 1), DamageType.Collision );
		me.RegisterCollision();		
	}

	@Override
	public void Draw(SpriteBatch renderer)
	{
		if( m_behavior == CivilianBehavior.HarvestAsteroids && m_target == null )
		{
			FindAnAsteroidAndTargetIt();
		}
		
		ViewedCollidable currentTarget = m_target;
		
		super.Draw(renderer);	
		
		if( m_behavior == CivilianBehavior.HarvestAsteroids &&
			(m_target == null || currentTarget != m_target ) )
		{
			m_target = null;
			 m_behavior = CivilianBehavior.ReturnToStation;
			 m_navigatingTo = m_homeStation.m_body.getPosition();
			 m_unloadCounter = 60;
		}
		
		if( m_behavior == CivilianBehavior.ReturnToStation && !ce.m_enginesEngaged )
		{
			if( m_unloadCounter <= 0 )
			{
				m_behavior = CivilianBehavior.HarvestAsteroids;
			}
			
			float distance = m_body.getPosition().dst(m_homeStation.m_body.getPosition());
			if( distance < 50 )
			{
				m_unloadCounter -=1;
			}
		}								
	}

	protected void FindAnAsteroidAndTargetIt()
	{
		RadialEntityRetriever rer = new RadialEntityRetriever( m_world, 500, m_body.getPosition().x, m_body.getPosition().y );
		for( int i = 0; i < rer.m_detectedEntities.size() && m_target == null; i++ )
		{
			ViewedCollidable vc = rer.m_detectedEntities.get(i);
			
			if( Asteroid.class.isInstance(vc) )
			{
				m_target = vc;
				m_trackedTargets.add(vc);
			}
		}
	}
	
	@Override
	public boolean reportFixture(Fixture fixture)
	{
		ViewedCollidable p = (ViewedCollidable) fixture.getBody().getUserData();
		
		if (p != null && 
			p.m_factionCode != m_factionCode &&
			p.m_isTargetable &&
			p.m_factionCode != 0 &&
			p.m_factionCode != 1 &&
			Ship.class.isInstance(p) )
		{			
			Ship s = (Ship) fixture.getBody().getUserData();
			
			if( s != null )
			{
				if( s.m_body.getPosition().dst(m_body.getPosition()) <= s.m_detectionRange )
				{
					m_target = p;
					m_targetBody = p.m_body;
					m_trackedTargets.remove(p);
					m_trackedTargets.add(p);
				}
			}
			else
			{
				m_target = p;
				m_targetBody = p.m_body;
				m_trackedTargets.remove(p);
				m_trackedTargets.add(p);
			}
		}
		return true;
	}
	
}
