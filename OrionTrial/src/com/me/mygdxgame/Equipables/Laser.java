package com.me.mygdxgame.Equipables;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import com.me.mygdxgame.Entities.ViewedCollidable.DamageType;

public class Laser extends CounterMeasure
{
	Texture m_laserMidBackground;
	Texture m_laserMidForeground;
	Sprite m_laserMidBackgroundSprite;
	Sprite m_laserMidForegroundSprite;
	
	Texture m_laserEndBackground;
	Texture m_laserEndForeground;
	Sprite m_laserEndBackgroundSprite;
	Sprite m_laserEndForegroundSprite;
	
	float m_chargeUpCounter = 0;
	float m_chargedDuration = 60;
	float m_chargeUpCriticalMass = 300;
	
	public Laser(World w, Ship s, ArrayList<ViewedCollidable> aliveThings )
	{
		super(w, s, aliveThings,  new Image( new Texture(Gdx.files.internal("data/lasericon.png") ) ) );
		// TODO Auto-generated constructor stub
		m_rangeEnablersAndMultipliers[1] = 1f;
		m_laserMidBackground = new Texture(Gdx.files.internal("data/laser.png"));
		m_laserMidBackgroundSprite = new Sprite( m_laserMidBackground );
		m_laserMidForeground = new Texture(Gdx.files.internal("data/laserOverlayStatic.png"));
		m_laserMidForegroundSprite = new Sprite( m_laserMidForeground );
		
		m_laserEndBackground = new Texture(Gdx.files.internal("data/start2.png"));
		m_laserEndBackgroundSprite = new Sprite( m_laserEndBackground );
		
		m_laserEndForeground = new Texture(Gdx.files.internal("data/start2Over.png"));
		m_laserEndForegroundSprite = new Sprite( m_laserEndForeground );
	}
	
	@Override
	public Image GetImageCopy()
	{
		return new Image( new Texture(Gdx.files.internal("data/lasericon.png") ) );
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
				float objectXPosition = m_ship.m_objectXPosition;// - m_ship.m_objectAppearance.getWidth() / 2;
				float objectYPosition = m_ship.m_objectYPosition;// - m_ship.m_objectAppearance.getHeight() / 2 ;
				
				m_laserMidBackgroundSprite.setPosition(objectXPosition ,objectYPosition );
				m_laserMidBackgroundSprite.setOrigin(0, m_laserMidBackgroundSprite.getHeight()/2 );
				m_laserMidBackgroundSprite.setSize(29f*distanceToCurrentTarget, m_laserMidBackgroundSprite.getHeight() );
				m_laserMidBackgroundSprite.setScale( 1f,m_chargeUpCounter / m_chargeUpCriticalMass / 2 );
				
				m_laserMidForegroundSprite.setPosition(objectXPosition ,objectYPosition );
				m_laserMidForegroundSprite.setOrigin(0, m_laserMidForegroundSprite.getHeight()/2 );
				m_laserMidForegroundSprite.setSize(29f*distanceToCurrentTarget, m_laserMidForegroundSprite.getHeight() );
				m_laserMidForegroundSprite.setScale( 1f,m_chargeUpCounter / m_chargeUpCriticalMass );
				
				m_laserEndBackgroundSprite.setPosition(objectXPosition,objectYPosition );
				m_laserEndBackgroundSprite.setOrigin(0, m_laserEndBackgroundSprite.getHeight()/2 );
				m_laserEndBackgroundSprite.setScale( 1f,m_chargeUpCounter / m_chargeUpCriticalMass );
				
				m_laserEndForegroundSprite.setPosition(objectXPosition ,objectYPosition );
				m_laserEndForegroundSprite.setOrigin(0, m_laserEndForegroundSprite.getHeight()/2 );
				m_laserEndForegroundSprite.setScale( 1f,m_chargeUpCounter / m_chargeUpCriticalMass );
				
				float centerX = m_ship.m_body.getPosition().x;
				float centerY = m_ship.m_body.getPosition().y;
				float targetCenterX = m_target.m_body.getPosition().x;
				float targetCenterY = m_target.m_body.getPosition().y;
				double angleRadians = Math.atan2(-centerY + targetCenterY,-centerX + targetCenterX);
				m_laserMidBackgroundSprite.setRotation((float) Math.toDegrees(angleRadians));
				m_laserMidForegroundSprite.setRotation((float) Math.toDegrees(angleRadians));
				m_laserEndBackgroundSprite.setRotation((float) Math.toDegrees(angleRadians));
				m_laserEndForegroundSprite.setRotation((float) Math.toDegrees(angleRadians));
				renderer.end();    // actual drawing is done on end(); if we do not end, we contaminate previous rendering.
				renderer.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
				renderer.begin();
				//renderer.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
				//m_laserMidBackgroundSprite.setAlpha(1f);
				m_laserMidBackgroundSprite.draw(renderer);	
				m_laserEndBackgroundSprite.draw(renderer);	
				
				
				m_target.damageIntegrity(1f, DamageType.Energy);
				
				if( m_chargeUpCounter > m_chargeUpCriticalMass )
				{
					m_laserMidForegroundSprite.draw(renderer);
					m_laserEndForegroundSprite.draw(renderer);
					m_target.damageIntegrity(10f, DamageType.Energy);
					
					if( m_chargeUpCounter > ( m_chargeUpCriticalMass + m_chargedDuration ) )
					{
						m_chargeUpCounter = 0;
						if( m_chargedDuration > 60 )
						{
							m_chargedDuration = 60;
							DisengageCM();
						}
						
					}
				}
				
				m_chargeUpCounter++;
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
		m_chargedDuration = 600;
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
