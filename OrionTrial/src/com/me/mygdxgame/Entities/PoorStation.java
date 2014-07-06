package com.me.mygdxgame.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Entities.EnemyShip.SeekType;
import com.me.mygdxgame.Entities.ViewedCollidable.DamageType;
import com.me.mygdxgame.Equipables.ConventionalManeuverEngine;
import com.me.mygdxgame.Equipables.TeleportManeuverEngine;

public class PoorStation extends EnemyShip
{

	public PoorStation( World world, float startX, float startY, int factionCode, ArrayList<ViewedCollidable> aliveThings)
	{
		super("poorstation", 17.5f, world, startX, startY, 0, 10, factionCode, aliveThings);
		me = new ConventionalManeuverEngine(this, 10);
		m_onDeckSeekType = m_seekType = SeekType.Stationary;
		me.m_boostJuiceMax = 10000;
		MassData data = new MassData();
		data.mass = 1000;
		m_body.setMassData(data);
		ce.m_hasEngines = false;
		m_body.setAngularVelocity((float) (.5));
		m_pooledShieldEffect.getEmitters().get(0).getScale().setHigh(530);
		m_pooledShieldEffect.getEmitters().get(0).getScale().setLow(510);
	}
	
	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity)
	{		
		object2.damageIntegrity( this, (crashVelocity * 1), DamageType.Collision );
		me.RegisterCollision();		
	}

	@Override
	public void Draw(SpriteBatch renderer)
	{
		m_objectSprite.setRotation((float) Math.toDegrees( m_body.getAngle() ) ); 
		m_angleRadians = m_body.getAngle();
		m_angleDegrees = (float) (m_angleRadians * 180 / Math.PI);
		super.Draw(renderer);
		if(!m_inMenu && ! m_freezeShip )
		{			
			ce.EngineBrake();
		}
	}
	
	@Override
	public boolean reportFixture(Fixture fixture)
	{
		ViewedCollidable p = (ViewedCollidable) fixture.getBody().getUserData();
		
		if (p != null && 
			p.m_factionCode != m_factionCode &&
			p.m_isTargetable &&
			p.m_factionCode != 0 &&
			p.m_factionCode != 1 &&
			Ship.class.isInstance(p) )
		{			
			Ship s = (Ship) fixture.getBody().getUserData();
			
			if( s != null )
			{
				if( s.m_body.getPosition().dst(m_body.getPosition()) <= s.m_detectionRange )
				{
					SetCurrentTarget( p );
				}
			}
			else
			{
				SetCurrentTarget( p );
			}
		}
		return true;
	}
	
}
