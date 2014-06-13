package com.me.mygdxgame.Equipables;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.me.mygdxgame.Entities.Ship;

public class InertialManeuverEngine extends ManeuverEngine
{
	boolean m_dodging = false;
	float m_origxVelocity = 0;
	float m_origyVelocity = 0;
	float m_xVelocity = 0;
	float m_yVelocity = 0;
	int m_dodgeCounter = 30;
	
	public InertialManeuverEngine(Ship s, float maxVelocity)
	{
		super(s, maxVelocity);
		m_boostMagnitude = m_maxVelocity;
	}
	
	public void ApplyVelocity()
	{
		m_dodging = true;
		m_origxVelocity = m_ship.m_body.getLinearVelocity().x;
		m_origyVelocity = m_ship.m_body.getLinearVelocity().y;
		
		m_ship.m_body.setLinearVelocity(m_xVelocity, m_yVelocity);
		m_dodgeCounter = 30;
		m_boostJuice -= 1;	
	}

	@Override
	public void ManeuverForward()
	{
		if( !m_dodging )
		{			
			m_xVelocity = (float)(m_boostMagnitude * Math.cos(m_ship.m_angleRadians));
			m_yVelocity = (float)(m_boostMagnitude * Math.sin(m_ship.m_angleRadians));
	        
	        ApplyVelocity();
		}
	}

	@Override
	public void ManeuverBackward()
	{
		if( !m_dodging )
		{				
			m_xVelocity = (float)(-m_boostMagnitude * Math.cos(m_ship.m_angleRadians));
			m_yVelocity = (float)(-m_boostMagnitude * Math.sin(m_ship.m_angleRadians));
	        	        
	        ApplyVelocity();
		}
	}

	@Override
	public void ManeuverPort()
	{
		if( !m_dodging )
		{
			m_xVelocity = (float)(-m_boostMagnitude * Math.sin(m_ship.m_angleRadians));
			m_yVelocity = (float)(m_boostMagnitude * Math.cos(m_ship.m_angleRadians));
	        
	        
	        ApplyVelocity();
		}
	}

	@Override
	public void ManeuverStarboard()
	{
		if( !m_dodging )
		{
			m_xVelocity = (float)(m_boostMagnitude * Math.sin(m_ship.m_angleRadians));
			m_yVelocity = (float)(-m_boostMagnitude * Math.cos(m_ship.m_angleRadians));
	        
	        ApplyVelocity();
		}
	}

	@Override
	public void Draw(SpriteBatch renderer)
	{
		
		if( m_dodging )
		{
			m_dodgeCounter--;
			m_ship.m_body.setLinearVelocity(m_xVelocity, m_yVelocity );
			m_boostJuice -= 1;	
			if(m_ship.ce.m_brakesEngaged)
			{
				m_dodgeCounter = 0;
			}
		}
		
		if( m_dodging && m_dodgeCounter <= 0 )
		{
			m_ship.m_body.setLinearVelocity(m_origxVelocity, m_origyVelocity );
			m_dodging = false;
		}
	}

	@Override
	public void RegisterCollision()
	{
		m_dodgeCounter = 0;		
	}

}
