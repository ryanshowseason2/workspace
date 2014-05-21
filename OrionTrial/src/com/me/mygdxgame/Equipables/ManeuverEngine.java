package com.me.mygdxgame.Equipables;

import com.me.mygdxgame.Entities.Ship;

public abstract class ManeuverEngine 
{
	Ship m_ship;
	public float m_maxVelocity;
	public float m_boostJuice = 100;
	public float m_boostMagnitude = 15000f;
	public ManeuverEngine( Ship s, float maxVelocity )
	{
		m_ship = s;
		m_maxVelocity = maxVelocity;
	}
	
	public abstract void ManeuverForward();
	public abstract void ManeuverBackward();
	public abstract void ManeuverPort();
	public abstract void ManeuverStarboard();
}
