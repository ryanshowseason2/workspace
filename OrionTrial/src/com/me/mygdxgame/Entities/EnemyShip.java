package com.me.mygdxgame.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Equipables.InertialCruiseEngine;

public class EnemyShip extends Ship implements QueryCallback
{
	public enum SeekType
	{
		RamTarget, EnterFiringRange, TravelingToWaypoint
	}

	SeekType m_seekType;
	SeekType m_onDeckSeekType;
	int m_detectionRange = 30;
	ViewedCollidable m_target = null;
	float m_wayPointX;
	float m_wayPointY;

	public EnemyShip(String appearanceLocation, World world, float startX,
			float startY, float initialAngleAdjust, float maxV,
			int factionCode, ArrayList<ViewedCollidable> aliveThings)
	{
		super(appearanceLocation, world, startX, startY, maxV, aliveThings,
				factionCode);
		m_factionCode = factionCode;
		m_objectSprite.rotate((float) initialAngleAdjust);
		MassData data = m_body.getMassData();
		data.mass = 10;
		m_body.setMassData(data);
		m_body.setUserData(this);
		m_deathEffect.load(Gdx.files.internal("data/explosionred.p"),
				Gdx.files.internal("data/"));
		m_deathEffectPool = new ParticleEffectPool(m_deathEffect, 1, 2);
		m_pooledDeathEffect = m_deathEffectPool.obtain();
		m_onDeckSeekType = m_seekType = SeekType.RamTarget;
		ce = new InertialCruiseEngine(this, maxV);
	}

	@Override
	public void Draw(SpriteBatch renderer)
	{
		super.Draw(renderer);
		ce.Draw(renderer);
		if (m_target == null)
		{
			float centerX = m_body.getPosition().x;
			float centerY = m_body.getPosition().y;
			m_world.QueryAABB(this, centerX - m_detectionRange / 2, centerY
					- m_detectionRange / 2, centerX + m_detectionRange / 2,
					centerY + m_detectionRange / 2);
		}

		if (m_target != null)
		{
			NavigateToTarget();
		}
	}

	private void NavigateToTarget()
	{
		CalculateWaypoint();
		DriveEnginesToWaypoint();
	}

	private void DriveEnginesToWaypoint()
	{
		Vector2 pos = m_body.getPosition();
		Vector2 vec = new Vector2();
		vec.x = m_wayPointX;
		vec.y = m_wayPointY;
		m_angleRadians = Math.atan2(vec.y - pos.y, vec.x - pos.x);
		float degrees = (float) (m_angleRadians * 180 / Math.PI);
		float difference = degrees - m_angleDegrees;
		m_objectSprite.rotate((float) (difference));
		m_angleDegrees = (float) degrees;
		m_body.setTransform(m_body.getPosition(), (float) Math.toRadians(m_angleDegrees));
		
		switch (m_seekType)
		{
		case RamTarget:
			// Keep accelerating into target
			RammingLogic(pos, vec);
			break;
		case EnterFiringRange:
			//  get within firing range and drift.
			FiringDistanceLogic(pos, vec);
			break;
		case TravelingToWaypoint:
			//  get within firing range and drift.
			RammingLogic(pos, vec);
			break;
		default:
			// do nothing
			break;
		}
		
		

		
	}

	private void RammingLogic(Vector2 pos, Vector2 vec)
	{
		if ((m_body.getLinearVelocity().x > 5 && vec.x < pos.x)
				|| (m_body.getLinearVelocity().x < -5 && vec.x > pos.x)
				|| (m_body.getLinearVelocity().y > 5 && vec.y < pos.y)
				|| (m_body.getLinearVelocity().y < -5 && vec.y > pos.y))
		{
			ce.EngineBrake();
		} else
		{
			ce.ThrottleForward();
			ce.EngageEngine();
		}
	}
	
	private void FiringDistanceLogic(Vector2 pos, Vector2 vec)
	{
		float distance = pos.dst(vec);
		if ((m_body.getLinearVelocity().x > 5 && vec.x < pos.x)
				|| (m_body.getLinearVelocity().x < -5 && vec.x > pos.x)
				|| (m_body.getLinearVelocity().y > 5 && vec.y < pos.y)
				|| (m_body.getLinearVelocity().y < -5 && vec.y > pos.y)
				|| pos.dst(vec) < 5 )
		{
			ce.EngineBrake();
		} else if( distance > 10f )
		{
			ce.ThrottleForward();
			ce.EngageEngine();
		}
	}

	private void CalculateWaypoint()
	{
		float radius = Math.max(m_objectAppearance.getWidth() / 2, m_objectAppearance.getHeight() / 2) / 29f;
		Vector2 source = new Vector2();
		Vector2 destination = new Vector2();
		destination.x = m_target.m_body.getPosition().x;
		destination.y = m_target.m_body.getPosition().y;		
		source.x = m_body.getPosition().x;
		source.y = m_body.getPosition().y;	
		
		// Check LOS to target only! return true or false
		 if( CheckWingAndCenterPaths(radius, source, destination) )
		 {
			 // We're good to go
			m_wayPointX = m_target.m_body.getPosition().x;
			m_wayPointY = m_target.m_body.getPosition().y;
			m_seekType = m_onDeckSeekType;
		 }
		 else
		 {
			 RegularPathFinding(radius);			 
		 }
	}

	private void RegularPathFinding(float radius)
	{
		// we've got a path to find
		 m_seekType = SeekType.TravelingToWaypoint;
		 // initiate path current position to the ship's position add to generation list
		 boolean losToEnemy = false;
		 boolean takeLeftPath = false;
		 Waypoint start = new Waypoint( m_body.getPosition());
		 ArrayList<Waypoint> generationList = new ArrayList<Waypoint>();
		 ArrayList<Waypoint> vetList = new ArrayList<Waypoint>();
		 ArrayList<Waypoint> validWaypoints = new ArrayList<Waypoint>();
		 generationList.add( start );
		 
		 Waypoint leftPath = null;
		 Waypoint rightPath = null;
		 
		 // while we don't have a waypoint with LOS
		 while( !losToEnemy )
		 {			 
			 // for each item in generation list
			 for( int i = 0; i < generationList.size(); i++ )
			 {
				 // look in the direction of the enemy ship and 
				 // Generate left and right possible waypoints
				 WaypointGenerator gen = new WaypointGenerator( m_body, m_world, generationList.get(i), m_target.m_body.getPosition() );
				 gen.Generate( radius );
				 vetList.add(gen.m_left);
				 vetList.add(gen.m_right);
			 }
			 
			 generationList.clear();
			 
			 //for each generated waypoint
			 for( int i = 0; i < vetList.size(); i++)
			 {
				 boolean losToWaypoint = false;
				 Waypoint w = vetList.get(i);
				 //while
				 while ( !losToWaypoint )
				 {						 
				 	//if have LOS to generated waypoint
					 if( CheckWingAndCenterPaths(radius, w.m_origin, w.m_waypoint) )
					 {
				 		// add to list points to check if we have LOS to target
						 validWaypoints.add(w);
						 losToWaypoint = true;
						 
						 // Set the initial left and right forks for later on.
						 if( w.m_isLeftPath && leftPath == null )
						 {
							 leftPath = w;
						 }
						 
						 if( w.m_isRightPath && rightPath == null )
						 {
							 rightPath = w;
						 }
					 }
					 else //else don't have LOS
					 {
				 		// regenerate the point
						 Vector2 conflictedPoint = new Vector2( w.m_waypoint );
						 WaypointGenerator gen = new WaypointGenerator( m_body, m_world, w, conflictedPoint );
						 gen.Generate( radius );
						 
						 if(w.m_isLeftFork)
						 {
							 w.m_waypoint.x = gen.m_left.m_waypoint.x;
							 w.m_waypoint.y = gen.m_left.m_waypoint.y;
						 }
						 else
						 {
							 w.m_waypoint.x = gen.m_right.m_waypoint.x;
							 w.m_waypoint.y = gen.m_right.m_waypoint.y; 
						 }							 
					 }
				 }
			 }
			 
			 vetList.clear();
			 
			 // for each waypoints the ship has LOS to as long as we haven't detected LOS
			 //to enemy ship
			 for( int i = 0; i < validWaypoints.size() && !losToEnemy; i++)
			 {		
				 
				 Waypoint w = validWaypoints.get(i);
			 	// if LOS to enemy ship
				 if( CheckWingAndCenterPaths(radius, w.m_waypoint, m_target.m_body.getPosition() ) )
				 {
			 		// set loop var end loop
					 losToEnemy = true;
					 takeLeftPath = w.m_isLeftPath;
				 }
				 else // else
				 {
			 		// add this waypoint to the generation list
					 w.m_origin = w.m_waypoint;
					 generationList.add(w);
				 }
			 }
			 
			 validWaypoints.clear();
		 }
		 
		 if( takeLeftPath )
		 {
			 m_wayPointX = leftPath.m_waypoint.x;
			 m_wayPointY = leftPath.m_waypoint.y;
		 }
		 else
		 {
			 m_wayPointX = rightPath.m_waypoint.x;
			 m_wayPointY = rightPath.m_waypoint.y;
		 }
	}

	private boolean CheckWingAndCenterPaths(float radius, Vector2 source,
			Vector2 destination)
	{
		boolean hasLineOfSight = CheckPath( source, destination);
		double angleRadians = Math.atan2(destination.y - source.y, destination.x - source.x);
		if( hasLineOfSight )
		{
			Vector2 tmp = new Vector2();
			tmp.x = source.x -(float)(radius * Math.sin(angleRadians));
			tmp.y = source.y +(float)(radius * Math.cos(angleRadians));
			hasLineOfSight = CheckPath( tmp, destination);
		}
		
		if(hasLineOfSight )
		{
			Vector2 tmp = new Vector2();
			tmp.x = source.x +(float)(radius * Math.sin(angleRadians));
			tmp.y = source.y -(float)(radius * Math.cos(angleRadians));
			hasLineOfSight = CheckPath( tmp, destination);
		}
		
		return hasLineOfSight;
	}

	private boolean CheckPath(Vector2 source, Vector2 destination)
	{
		LineOfSightChecker check = new LineOfSightChecker( m_target.m_body, m_body );
		m_world.rayCast(check, source, destination);
		
		return check.m_hasLineOfSight;
	}

	@Override
	public boolean reportFixture(Fixture fixture)
	{
		ViewedCollidable p = (ViewedCollidable) fixture.getBody().getUserData();
		if (p != null && p.m_factionCode != m_factionCode
				&& p.m_factionCode != 0)
		{
			m_target = p;
		}
		return true;
	}

}
