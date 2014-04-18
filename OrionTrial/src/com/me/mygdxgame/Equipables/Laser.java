package com.me.mygdxgame.Equipables;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

public class Laser extends CounterMeasure
{
	Texture m_objectAppearance;
	Sprite m_objectSprite;
	public Laser(World w, Ship s, ArrayList<ViewedCollidable> aliveThings )
	{
		super(w, s, aliveThings,  new Image( new Texture(Gdx.files.internal("data/laser.png") ) ) );
		// TODO Auto-generated constructor stub
		m_rangeEnablersAndMultipliers[1] = 1f;
		m_objectAppearance = new Texture(Gdx.files.internal("data/bullet.png"));
		m_objectSprite = new Sprite( m_objectAppearance );
	}
	
	@Override
	public Image GetImageCopy()
	{
		return new Image( new Texture(Gdx.files.internal("data/laser.png") ) );
	}

	@Override
	public void AcquireAndFire( SpriteBatch renderer )
	{
		if( m_target == null )
		{
			// Pull the closest tracked target from the ship computer! 
			float leastDistance = Float.MAX_VALUE;
			for( int i = 0; i < m_ship.m_trackedTargets.size(); i++ )
			{
				ViewedCollidable vc = m_ship.m_trackedTargets.get(i);
				float distance = vc.m_body.getPosition().dst(m_ship.m_body.getPosition());
				if( distance <= m_range && distance < leastDistance )
				{
					leastDistance = distance;
					m_target = vc;
				}
			}
		}
		
		if( m_target != null )
		{
			float distanceToCurrentTarget = m_target.m_body.getPosition().dst(m_ship.m_body.getPosition() );
			
			if( distanceToCurrentTarget <= m_range )
			{
				float objectXPosition = m_ship.m_body.getPosition().x*29f;
				float objectYPosition = m_ship.m_body.getPosition().y*29f;

				m_objectSprite.setPosition(objectXPosition ,objectYPosition );
				m_objectSprite.setSize(29f*distanceToCurrentTarget, m_objectSprite.getHeight() );
				float centerX = m_ship.m_body.getPosition().x;
				float centerY = m_ship.m_body.getPosition().y;
				float targetCenterX = m_target.m_body.getPosition().x;
				float targetCenterY = m_target.m_body.getPosition().y;
				double angleRadians = Math.atan2(-centerY + targetCenterY,-centerX + targetCenterX);
				m_objectSprite.setRotation((float) Math.toDegrees(angleRadians));
				renderer.end();    // actual drawing is done on end(); if we do not end, we contaminate previous rendering.
				renderer.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
				renderer.begin();
				m_objectSprite.draw(renderer);
			}
			else
			{
				m_target = null;
			}
		}
		
	}

	@Override
	public void EngageCM( Button b )
	{
		super.EngageCM(b);
	}

	@Override
	public void DisengageCM()
	{
		super.DisengageCM();

	}

	@Override
	public boolean reportFixture(Fixture fixture)
	{
		Body potentialTarget = fixture.getBody();
		float distanceToPotential = potentialTarget.getPosition().dst(m_ship.m_body.getPosition() );
		float distanceToCurrentTarget = Float.MAX_VALUE;
		ViewedCollidable vc = (ViewedCollidable) potentialTarget.getUserData();
		
		if(m_target != null)
		{
			distanceToCurrentTarget = m_target.m_body.getPosition().dst(m_ship.m_body.getPosition() );
		}
		
		if( potentialTarget != m_ship.m_body && 
			distanceToPotential <= m_range &&
			distanceToPotential < distanceToCurrentTarget &&
			m_ship.m_factionCode != vc.m_factionCode &&
			vc.m_factionCode != 0 &&
			vc.m_isTargetable )
		{
			m_target = (ViewedCollidable) potentialTarget.getUserData();
		}
		
		return true;
	}

}
