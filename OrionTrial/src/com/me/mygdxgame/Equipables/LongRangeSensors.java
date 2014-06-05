package com.me.mygdxgame.Equipables;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.me.mygdxgame.Entities.Projectile;
import com.me.mygdxgame.Entities.Ship;
import com.me.mygdxgame.Entities.ViewedCollidable;
import com.me.mygdxgame.Entities.Projectile.Characters;

public class LongRangeSensors extends CounterMeasure implements QueryCallback
{
	
	public LongRangeSensors(World w, Ship s, ArrayList<ViewedCollidable> aliveThings )
	{
		super(w, s, aliveThings,  new Image( new Texture(Gdx.files.internal("data/sensors.png") ) ) );
		// TODO Auto-generated constructor stub
		m_rangeEnablersAndMultipliers[0] = -1f;
		m_rangeEnablersAndMultipliers[1] = -1f;
		m_rangeEnablersAndMultipliers[2] = 1f;
		m_fireFrequency = 30;
	}
	
	@Override
	public Image GetImageCopy()
	{
		return new Image( new Texture(Gdx.files.internal("data/sensors.png") ) );
	}

	@Override
	public void AcquireAndFire( SpriteBatch renderer )
	{
		if( m_fireCounter > m_fireFrequency )
		{
			float centerX = m_ship.m_body.getPosition().x;
			float centerY = m_ship.m_body.getPosition().y;
			m_world.QueryAABB(this, centerX - m_range,
									centerY - m_range,
									centerX + m_range,
									centerY + m_range );
			m_fireCounter = 0;
		}
		else
		{
			m_fireCounter++;
		}
	}

	@Override
	public void EngageCM( Button b )
	{
		super.EngageCM(b);
		m_activateSecondaryMode = 120;
	}

	@Override
	public void DisengageCM()
	{
		super.DisengageCM();

	}
	
	@Override
	public boolean reportFixture(Fixture fixture)
	{
		ViewedCollidable vc = (ViewedCollidable) fixture.getBody().getUserData();
		if( vc.m_factionCode != 0 && 
			vc.m_factionCode != m_ship.m_factionCode &&
			vc.m_detectionRange > vc.m_body.getPosition().dst(m_ship.m_body.getPosition()))
		{
			m_ship.m_trackedTargets.remove( vc );
			m_ship.m_trackedTargets.add( vc );
		}
		
		return true;
	}

}
