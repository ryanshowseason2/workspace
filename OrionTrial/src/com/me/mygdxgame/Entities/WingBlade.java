package com.me.mygdxgame.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Entities.ViewedCollidable.DamageType;

public class WingBlade extends ViewedCollidable
{
	boolean m_activated = true;
	Ship m_ship;
	
	public WingBlade(String appearanceLocation,
			World world, float startX, float startY,
			ArrayList<ViewedCollidable> aliveThings, int factionCode, Ship s)
	{
		super(appearanceLocation, "", world, startX, startY,
				aliveThings, factionCode);
		m_body.setUserData(this);
		m_ignoreForPathing = true;
		m_body.getFixtureList().get(0).setSensor(true);
		
		m_objectSprite.setOrigin(0, m_objectSprite.getHeight()/2);
		m_objectSprite.setPosition(startX, startY);
		m_ship = s;
	}

	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity)
	{
		// TODO Auto-generated method stub
		if( m_activated)
		{
			
		}
	}

	@Override
	public void damageIntegrity(float damage, DamageType type, boolean bypassResistances )
    {
    }
	
	public void Draw( SpriteBatch renderer )
    {
		if(m_activated)
		{
			float degrees = m_ship.m_angleDegrees + 73;
			m_body.setTransform( m_body.getPosition(), (float) Math.toRadians( degrees ) );
			m_objectSprite.setRotation(degrees);
			super.Draw(renderer);
		}
    }
}
