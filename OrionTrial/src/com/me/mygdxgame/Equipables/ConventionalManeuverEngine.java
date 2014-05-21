package com.me.mygdxgame.Equipables;

import com.badlogic.gdx.math.Vector2;
import com.me.mygdxgame.Entities.Ship;

public class ConventionalManeuverEngine extends ManeuverEngine
{

	public ConventionalManeuverEngine(Ship s, float maxVelocity)
	{
		super(s, maxVelocity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void ManeuverForward()
	{
		float xForce = 0;
		float yForce = 0;
		// TODO Auto-generated method stub
		xForce = (float)(m_boostMagnitude * Math.cos(m_ship.m_angleRadians));
        yForce = (float)(m_boostMagnitude * Math.sin(m_ship.m_angleRadians));
        m_boostJuice -= 20;	
        
	    m_ship.m_body.applyForceToCenter( xForce, yForce, true);
	}

	@Override
	public void ManeuverBackward()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
		float yForce = 0;
		// TODO Auto-generated method stub
		xForce = (float)(-m_boostMagnitude * Math.cos(m_ship.m_angleRadians));
        yForce = (float)(-m_boostMagnitude * Math.sin(m_ship.m_angleRadians));
        m_boostJuice -= 20;	
        
	    m_ship.m_body.applyForceToCenter( xForce, yForce, true);
	}

	@Override
	public void ManeuverPort()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
		float yForce = 0;
		// TODO Auto-generated method stub
		xForce = (float)(-m_boostMagnitude * Math.sin(m_ship.m_angleRadians));
        yForce = (float)(m_boostMagnitude * Math.cos(m_ship.m_angleRadians));
        m_boostJuice -= 20;	
        
	    m_ship.m_body.applyForceToCenter( xForce, yForce, true);
	}

	@Override
	public void ManeuverStarboard()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
		float yForce = 0;
		// TODO Auto-generated method stub
		xForce = (float)(m_boostMagnitude * Math.sin(m_ship.m_angleRadians));
        yForce = (float)(-m_boostMagnitude * Math.cos(m_ship.m_angleRadians));
        m_boostJuice -= 20;	
        
	    m_ship.m_body.applyForceToCenter( xForce, yForce, true);
	}

}
