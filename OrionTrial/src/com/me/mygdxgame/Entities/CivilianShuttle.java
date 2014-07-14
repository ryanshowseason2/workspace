package com.me.mygdxgame.Entities;

import java.util.ArrayList;
import java.util.Random;

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
	public enum CivilianBehavior
	{
		HarvestAsteroids,
		ShipBetweenStations,
		ShipAndLeaveBeforeShipped,
		ShipAndLeaveAfterShipped,
		ReturnToStation,
		FleeTostation,
		Flee
	}
	
	CivilianBehavior m_behavior;
	private int m_unloadCounter = 200;
	ViewedCollidable m_detectChangedTarget;
	ViewedCollidable m_currentlyShippingTo = null;
	public ArrayList<ViewedCollidable> m_shippingTargets = new ArrayList<ViewedCollidable>();
	ViewedCollidable m_aggressor = null;
	CivilianBehavior m_previousBehavior;	
	
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
		m_shippingTargets.add( p );
	}
	
	public void SetBehavior( CivilianBehavior cb )
	{
		m_behavior = cb;
	}
	
	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity)
	{		
		object2.damageIntegrity( this, (crashVelocity * 1), DamageType.Collision );
		me.RegisterCollision();		
	}
	
	@Override
	public void damageIntegrity( ViewedCollidable damageOrigin, float damage, DamageType type, boolean bypassShieldResistances, boolean bypassShields, boolean bypassResistances )
    {
		super.damageIntegrity(damageOrigin, damage, type, bypassShieldResistances, bypassShields, bypassResistances);
		
		if( damageOrigin.m_factionCode != 0 )
		{
			if( m_shippingTargets.size() > 0 )
			{
				m_previousBehavior = m_behavior;
				m_behavior = CivilianBehavior.FleeTostation;
				m_aggressor = damageOrigin;
			}
			else
			{
				m_behavior = CivilianBehavior.Flee;
			}
		}
    }

	@Override
	public void Draw(SpriteBatch renderer)
	{
		RemoveDeadShippingTargets();
		RunAwayIfNoStations();
		
		//////BEFORE DRAW BEHAVIOR ROUTINES//////
		ShipBetweenStationsBeforeDraw();	   //
		HarvestAsteroidsBeforeDraw();		   //
		FleeToStationBeforeDraw();			   //
		FleeBeforeDraw();
		/////////////////////////////////////////		
		
		ShipAndLeaveBeforeBeforeDraw();		
		ShipAndLeaveAfterBeforeDraw();
		
		DrawWarpingInWhenAppropriate(renderer);		
		super.Draw(renderer);
		
		ShipAndLeaveBeforeAfterDraw();		
		ShipAndLeaveAfterAfterDraw(renderer);
					
		//////AFTER DRAW BEHAVIOR ROUTINES///////
		ShipBetweenStationsAfterDraw();		   //
		HarvestAsteroidsAfterDraw();		   //
		ReturnToStationAfterDraw();			   //
		FleeToStationAfterDraw();			   //
		FleeAfterDraw(renderer);
		/////////////////////////////////////////
	}

	private void ShipAndLeaveAfterAfterDraw(SpriteBatch renderer)
	{
		if( m_behavior == CivilianBehavior.ShipAndLeaveAfterShipped && m_body.getPosition().len() > 400 )
		{
			m_pooledShieldEffect.allowCompletion();
			ce.m_pooledEngineEffect.allowCompletion();
			ce.m_pooledEngineTrailEffect.allowCompletion();
			m_objectSprite.setAlpha( Math.max(0, (.5f-m_pooledStarSlingEnterEffect.getEmitters().get(1).getPercentComplete())) );
			m_pooledStarSlingEnterEffect.setPosition(m_objectXPosition, m_objectYPosition);
			m_pooledStarSlingEnterEffect.getEmitters().get(0).getAngle().setHigh((float) m_angleDegrees);
			m_pooledStarSlingEnterEffect.getEmitters().get(1).getAngle().setHigh((float) m_angleDegrees);
			m_pooledStarSlingEnterEffect.getEmitters().get(0).getRotation().setHigh((float) m_angleDegrees);
			m_pooledStarSlingEnterEffect.getEmitters().get(1).getRotation().setHigh((float) m_angleDegrees);
			m_pooledStarSlingEnterEffect.draw(renderer, 1f / 60f);
			if( m_pooledStarSlingEnterEffect.isComplete() )
			{
				m_pooledStarSlingEnterEffect.reset();

				double angle = Math.random() * Math.PI * 2;
				float x = (float) (Math.cos( angle ) * 600);
				float y = (float) (Math.sin( angle ) * 600);
				m_behavior = CivilianBehavior.ShipAndLeaveBeforeShipped;
				EnterFromSidelines(x,y);
			}
		}
	}

	private void ShipAndLeaveAfterBeforeDraw()
	{
		if( m_behavior == CivilianBehavior.ShipAndLeaveAfterShipped )
		{
			m_trackedHostileTargets.clear();
			DisengageCurrentTarget();
			double angle = Math.toRadians(m_body.getPosition().angle() );
			m_navigatingTo.x = (float) (Math.cos( angle ) * 2000);
			m_navigatingTo.y = (float) (Math.sin( angle ) * 2000);
		}
	}

	private void ShipAndLeaveBeforeAfterDraw()
	{
		if( m_behavior == CivilianBehavior.ShipAndLeaveBeforeShipped && HasReachedWaypoint() )
		{
			//we've arrived
			if( m_unloadCounter <= 0 )
			{
				// fully unloaded
				m_behavior = CivilianBehavior.ShipAndLeaveAfterShipped;
				m_unloadCounter = 200;
			}
			else
			{
				m_unloadCounter--;
			}
			
		}
	}

	private void ShipAndLeaveBeforeBeforeDraw()
	{
		if( m_behavior == CivilianBehavior.ShipAndLeaveBeforeShipped )
		{
			if( m_shippingTargets.size() > 0 )
			{
				SetCurrentTarget( m_shippingTargets.get(0), false );
				m_navigatingTo = m_shippingTargets.get(0).m_body.getPosition();
			}
			else
			{
				m_behavior = CivilianBehavior.Flee;
			}
		}
	}

	private void DrawWarpingInWhenAppropriate(SpriteBatch renderer)
	{
		if( m_enteringFromSidelines )
		{
			this.m_freezeShip = true;
			m_objectSprite.setAlpha( (float) Math.max(0, m_pooledStarSlingExitEffect.getEmitters().get(0).getPercentComplete() - .55 ) );
			m_pooledStarSlingExitEffect.setPosition(m_objectXPosition, m_objectYPosition);
			m_pooledStarSlingExitEffect.getEmitters().get(0).getAngle().setHigh((float) m_angleDegrees);
			m_pooledStarSlingExitEffect.getEmitters().get(1).getAngle().setHigh((float) m_angleDegrees);
			m_pooledStarSlingExitEffect.getEmitters().get(0).getRotation().setHigh((float) m_angleDegrees);
			m_pooledStarSlingExitEffect.getEmitters().get(1).getRotation().setHigh((float) m_angleDegrees);
			m_pooledStarSlingExitEffect.draw(renderer, 1f / 60f);
			if( m_pooledStarSlingExitEffect.isComplete() )
			{
				m_enteringFromSidelines = false;
				m_objectSprite.setAlpha( 1);
				this.m_freezeShip = false;
			}
		}
	}
	
	public void EnterFromSidelines( float x, float y )
	{
		m_body.setTransform(x, y, m_body.getAngle());
		m_enteringFromSidelines = true;
		m_pooledStarSlingExitEffect.reset();
	}

	private void FleeAfterDraw(SpriteBatch renderer)
	{
		if( m_behavior == CivilianBehavior.Flee && m_body.getPosition().len() > 400 )
		{
			m_pooledShieldEffect.allowCompletion();
			ce.m_pooledEngineEffect.allowCompletion();
			ce.m_pooledEngineTrailEffect.allowCompletion();
			m_objectSprite.setAlpha( Math.max(0, (.5f-m_pooledStarSlingEnterEffect.getEmitters().get(1).getPercentComplete())) );
			m_pooledStarSlingEnterEffect.setPosition(m_objectXPosition, m_objectYPosition);
			m_pooledStarSlingEnterEffect.getEmitters().get(0).getAngle().setHigh((float) m_angleDegrees);
			m_pooledStarSlingEnterEffect.getEmitters().get(1).getAngle().setHigh((float) m_angleDegrees);
			m_pooledStarSlingEnterEffect.getEmitters().get(0).getRotation().setHigh((float) m_angleDegrees);
			m_pooledStarSlingEnterEffect.getEmitters().get(1).getRotation().setHigh((float) m_angleDegrees);
			m_pooledStarSlingEnterEffect.draw(renderer, 1f / 60f);
			if( m_pooledStarSlingEnterEffect.isComplete() )
			{
				m_pooledStarSlingEnterEffect.reset();
				m_pooledDeathEffect.free();
				m_pooledDeathEffect = null;
				m_integrity = 0;
			}
		}
	}

	private void FleeBeforeDraw()
	{
		if( m_behavior == CivilianBehavior.Flee )
		{
			m_trackedHostileTargets.clear();
			DisengageCurrentTarget();
			double angle = Math.toRadians(m_body.getPosition().angle() );
			m_navigatingTo.x = (float) (Math.cos( angle ) * 2000);
			m_navigatingTo.y = (float) (Math.sin( angle ) * 2000);
		}
	}

	protected void FleeToStationAfterDraw()
	{
		if( m_behavior == CivilianBehavior.FleeTostation )
		{
			if( m_aggressor.m_integrity <= 0 )
			{
				//We are safe now
				m_behavior = m_previousBehavior;
				//Cleanup so we'll target again for harvest asteroids
				DisengageCurrentTarget();
				m_currentlyShippingTo = null;
			}
		}
	}

	protected void FleeToStationBeforeDraw()
	{
		if( m_behavior == CivilianBehavior.FleeTostation )
		{
			if( m_shippingTargets.size() > 0 )
			{
				SetCurrentTarget( m_shippingTargets.get(0), false );
				m_navigatingTo = m_shippingTargets.get(0).m_body.getPosition();
			}
		}
	}

	protected void ShipBetweenStationsAfterDraw()
	{
		if( m_behavior == CivilianBehavior.ShipBetweenStations && HasReachedWaypoint() )
		{
			if( m_unloadCounter <= 0 )
			{
				// Go to the home station or a random station
				{
					Random RANDOM = new Random();
					m_currentlyShippingTo = m_shippingTargets.get( RANDOM.nextInt(m_shippingTargets.size() ) );
					m_navigatingTo = m_currentlyShippingTo.m_body.getPosition();
				}
				m_unloadCounter = 200;
			}
			
			float distance = m_body.getPosition().dst(m_currentlyShippingTo.m_body.getPosition());
			if( distance < 50 )
			{
				m_unloadCounter -=1;
			}
		}
	}

	protected void ShipBetweenStationsBeforeDraw()
	{
		if( m_behavior == CivilianBehavior.ShipBetweenStations && m_currentlyShippingTo == null && m_shippingTargets.size() > 0 )
		{
			if( m_shippingTargets.size() > 1 )
			{
				m_navigatingTo = m_shippingTargets.get(0).m_body.getPosition();
				m_currentlyShippingTo = m_shippingTargets.get(0);
			}
			else
			{
				m_behavior = CivilianBehavior.HarvestAsteroids;
			}
		}
	}

	protected void RunAwayIfNoStations()
	{
		if( m_shippingTargets.size() == 0 )
		{
			//OH SHI-
			m_behavior = CivilianBehavior.Flee;
		}
	}

	protected void RemoveDeadShippingTargets()
	{
		ArrayList<ViewedCollidable> deadShippingTargets = new ArrayList<ViewedCollidable>();
		for(int i = 0; i < m_shippingTargets.size(); i++ )
		{
			ViewedCollidable vc = m_shippingTargets.get(i);
			if( vc.m_integrity <= 0 )
			{
				deadShippingTargets.add(vc);
			}
		}
		
		for(int i = 0; i < deadShippingTargets.size(); i++ )
		{
			m_shippingTargets.remove(deadShippingTargets.get(i));
			if( m_currentlyShippingTo == deadShippingTargets.get(i) )
			{
				m_currentlyShippingTo = null;
			}
		}
	}

	protected void ReturnToStationAfterDraw()
	{
		if( m_behavior == CivilianBehavior.ReturnToStation && HasReachedWaypoint() )
		{
			if( m_unloadCounter <= 0 )
			{
				m_behavior = CivilianBehavior.HarvestAsteroids;
			}
			
			float distance = m_body.getPosition().dst(m_shippingTargets.get(0).m_body.getPosition());
			if( distance < 50 )
			{
				m_unloadCounter -=1;
			}
		}
	}

	protected void HarvestAsteroidsAfterDraw()
	{
		if( m_behavior == CivilianBehavior.HarvestAsteroids &&
			( GetTarget() == null || m_detectChangedTarget != GetTarget() ) )
		{
			DisengageCurrentTarget();
			 m_behavior = CivilianBehavior.ReturnToStation;
			 m_navigatingTo = m_shippingTargets.get(0).m_body.getPosition();
			 m_unloadCounter = 200;
		}
	}

	protected void HarvestAsteroidsBeforeDraw()
	{
		if( m_behavior == CivilianBehavior.HarvestAsteroids && GetTarget() == null )
		{
			FindAnAsteroidAndTargetIt();
		}
		
		m_detectChangedTarget = GetTarget();
	}

	protected void FindAnAsteroidAndTargetIt()
	{
		RadialEntityRetriever rer = new RadialEntityRetriever( m_world, 500, m_body.getPosition().x, m_body.getPosition().y );
		for( int i = 0; i < rer.m_detectedEntities.size() && GetTarget() == null; i++ )
		{
			ViewedCollidable vc = rer.m_detectedEntities.get(i);
			
			if( Asteroid.class.isInstance(vc) )
			{
				SetCurrentTarget( vc );
				m_trackedHostileTargets.add(vc);
			}
		}
	}	
}
