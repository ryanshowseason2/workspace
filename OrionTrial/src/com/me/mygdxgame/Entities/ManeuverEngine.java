package com.me.mygdxgame.Entities;

public abstract class ManeuverEngine 
{
	Ship m_ship;
	float m_maxVelocity;
	public float m_boostJuice = 100;
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
