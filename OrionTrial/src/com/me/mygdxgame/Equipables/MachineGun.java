package com.me.mygdxgame.Equipables;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Entities.Ship;
import com.me.mygdxgame.Entities.ViewedCollidable;

public class MachineGun extends CounterMeasure
{

	public MachineGun(World w, Ship s)
	{
		super(w, s);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void AcquireAndFire()
	{
		if( m_target != null && m_target.m_integrity <= 0 )
		{
			m_target = null;
		}
		
		if( m_target == null )
		{
			float centerX = m_ship.m_body.getPosition().x;
			float centerY = m_ship.m_body.getPosition().y;
			
			m_world.QueryAABB(this, centerX - m_range / 2,
									centerY - m_range / 2,
									centerX + m_range / 2,
									centerY + m_range / 2 );
		}
		
		if( m_target != null )
		{
			
		}
	}

	@Override
	public void EngageCM()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void DisengageCM()
	{
		// TODO Auto-generated method stub

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
			vc.m_factionCode != 0 )
		{
			m_target = (ViewedCollidable) potentialTarget.getUserData();
		}
		
		return true;
	}

}
