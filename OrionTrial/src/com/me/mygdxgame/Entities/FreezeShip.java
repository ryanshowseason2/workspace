package com.me.mygdxgame.Entities;

import com.me.mygdxgame.Entities.ViewedCollidable.DamageType;

public class FreezeShip extends OverTimeEffect
{
	Ship m_ship;
	public FreezeShip(float counter, int effectCode, Ship ship)
	{
		super(counter, effectCode);
		m_ship = ship;
	}

	@Override
	public boolean Action()
	{
		m_counter--;
		m_ship.m_freezeShip = m_counter > 0 ? true : false;
		
		return m_counter > 0;
	}

}
