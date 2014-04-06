package com.me.mygdxgame.Entities;

import com.badlogic.gdx.math.Vector2;


public class Waypoint
{
	public boolean m_isLeftPath = false;
	public boolean m_isRightPath = false;
	public boolean m_isLeftFork = false;
	Vector2 m_origin;
	Vector2 m_waypoint = new Vector2();
	
	public Waypoint( Vector2 origin )
	{
		m_origin = new Vector2(origin);
	}

}
