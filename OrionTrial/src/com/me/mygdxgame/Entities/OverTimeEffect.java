package com.me.mygdxgame.Entities;

public abstract class OverTimeEffect
{
	float m_counter = 60;
	int m_effectCode;
	public OverTimeEffect( float counter, int effectCode )
	{
		m_counter = counter;
		m_effectCode = effectCode;
	}
	
	public abstract boolean Action();

}
