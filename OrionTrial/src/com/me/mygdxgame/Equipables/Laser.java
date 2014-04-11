package com.me.mygdxgame.Equipables;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.me.mygdxgame.Entities.Projectile;
import com.me.mygdxgame.Entities.Ship;
import com.me.mygdxgame.Entities.ViewedCollidable;

public class Laser extends CounterMeasure
{
	ViewedCollidable m_secondaryTarget = null;
	int m_activateSecondaryMode = 0;
	
	public Laser(World w, Ship s, ArrayList<ViewedCollidable> aliveThings )
	{
		super(w, s, aliveThings,  new Image( new Texture(Gdx.files.internal("data/laser.png") ) ) );
		// TODO Auto-generated constructor stub
		m_rangeEnablersAndMultipliers[1] = 1f;
	}
	
	@Override
	public Image GetImageCopy()
	{
		return new Image( new Texture(Gdx.files.internal("data/laser.png") ) );
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
			float centerX = m_ship.m_body.getPosition().x;
			float centerY = m_ship.m_body.getPosition().y;
			
			m_world.QueryAABB(this, centerX - m_range / 2,
									centerY - m_range / 2,
									centerX + m_range / 2,
									centerY + m_range / 2 );
		}
		
		if( m_target != null )
		{
			float distanceToCurrentTarget = m_target.m_body.getPosition().dst(m_ship.m_body.getPosition() );
			/*float centerX = m_ship.m_body.getPosition().x;
			float centerY = m_ship.m_body.getPosition().y;
			float targetCenterX = m_target.m_body.getPosition().x;
			float targetCenterY = m_target.m_body.getPosition().y;
			Projectile p = new Projectile("data/bullet.png", m_world, centerX, centerY, m_aliveThings, m_ship.m_factionCode );
			double angleRadians = Math.atan2(centerY - targetCenterY,centerX - targetCenterX);
			float xForce =  (float)(-1250f * Math.cos(angleRadians));
	        float yForce =  (float)(-1250f * Math.sin(angleRadians));
	        p.m_body.applyForceToCenter(xForce, yForce, true);*/
			if( distanceToCurrentTarget <= m_range )
			{
				float centerX = m_ship.m_body.getPosition().x;
				float centerY = m_ship.m_body.getPosition().y;
				Projectile p = new Projectile("data/bullet.png", m_world, centerX, centerY, m_aliveThings, m_ship.m_factionCode );
				p.Fire(m_ship, m_target, (float) Math.random()/2 - .25f);
			}
			else
			{
				m_target = null;
			}
		}
	}

	@Override
	public void EngageCM()
	{
		// TODO Auto-generated method stub
		m_activateSecondaryMode = 120;
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
			vc.m_factionCode != 0 &&
			vc.m_isTargetable )
		{
			m_target = (ViewedCollidable) potentialTarget.getUserData();
		}
		
		return true;
	}

}
