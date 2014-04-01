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

public class EnemyShip extends Ship implements QueryCallback, RayCastCallback
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
		// TODO Auto-generated constructor stub
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
		m_onDeckSeekType = m_seekType = SeekType.EnterFiringRange;
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
		if ((m_body.getLinearVelocity().x > 5 && vec.x < pos.x)
				|| (m_body.getLinearVelocity().x < -5 && vec.x > pos.x)
				|| (m_body.getLinearVelocity().y > 5 && vec.y < pos.y)
				|| (m_body.getLinearVelocity().y < -5 && vec.y > pos.y)
				|| pos.dst(vec) < 5 )
		{
			ce.EngineBrake();
		} else if( pos.dst(vec) > 10 )
		{
			ce.ThrottleForward();
			ce.EngageEngine();
		}
	}

	private void CalculateWaypoint()
	{
		Vector2 point = new Vector2();
		point.x = m_target.m_body.getPosition().x;
		point.y = m_target.m_body.getPosition().y;
		m_world.rayCast(this, m_body.getPosition(), point);
		
		Vector2 wingLeft = new Vector2();
		Vector2 wingRight = new Vector2();
		//m_wayPointX = m_target.m_body.getPosition().x;
		//m_wayPointY = m_target.m_body.getPosition().y;		
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

	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction)
	{
		if( fixture.getBody() == m_target.m_body )
		{
			m_wayPointX = m_target.m_body.getPosition().x;
			m_wayPointY = m_target.m_body.getPosition().y;
			m_seekType = m_onDeckSeekType;
		}
		else
		{
			// deviate the angle set that as the waypoint
			m_onDeckSeekType = m_seekType;
			m_seekType = SeekType.TravelingToWaypoint;
			Vector2 pos = m_body.getPosition();
			Vector2 vec = new Vector2();
			vec.x = fixture.getBody().getPosition().x;
			vec.y = fixture.getBody().getPosition().y;
			m_angleRadians = Math.atan2(vec.y - pos.y, vec.x - pos.x);
			m_angleRadians += Math.PI /2;
			m_wayPointX = (float) (fixture.getBody().getPosition().x + 5 * Math.cos(m_angleRadians));
			m_wayPointY = (float) (fixture.getBody().getPosition().y + 5 * Math.sin(m_angleRadians));
		}
		return 1;
	}

}
