package com.me.mygdxgame.Entities;

import com.me.mygdxgame.Entities.ViewedCollidable.DamageType;

public class EngineIntegrityCompromisedEffect extends OverTimeEffect
{
	Ship m_ship;
	public EngineIntegrityCompromisedEffect(float counter, int effectCode, Ship ship)
	{
		super(counter, effectCode);
		m_ship = ship;
	}

	@Override
	public boolean Action()
	{
		if( m_ship.ce.m_enginesEngaged )
		{
			m_ship.damageIntegrity( 1, DamageType.Explosion, true);
		}
		m_counter--;
		return m_counter > 0;
	}

}
