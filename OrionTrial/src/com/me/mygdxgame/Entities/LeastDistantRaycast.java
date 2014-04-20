package com.me.mygdxgame.Entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class LeastDistantRaycast implements RayCastCallback
{
	Body m_self;
	public float m_minDistance;
	Vector2 m_contactPoint;
	public ViewedCollidable m_entityHit;
	public LeastDistantRaycast(  Body self )
	{
		m_self = self;
		Reset();
	}
	
	public void Reset()
	{
		m_minDistance = Float.MAX_VALUE;
	}
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction)
	{
		ViewedCollidable navigator = (ViewedCollidable) m_self.getUserData();
		ViewedCollidable inTheWay = (ViewedCollidable) fixture.getBody().getUserData();
		float distance = navigator.m_body.getPosition().dst(point);
		if( fixture.getBody() != m_self &&
			!inTheWay.m_ignoreForPathing &&
			distance < m_minDistance )
		{
			m_contactPoint = point;
			m_minDistance = distance;
			m_entityHit = inTheWay;
		}
		return 1;
	}
}
