package com.me.mygdxgame.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Entities.ViewedCollidable.DamageType;

public class WingBlade extends ViewedCollidable
{
	boolean m_activated = true;
	Ship m_ship;
	boolean m_rightSide = false;
	
	public WingBlade(String appearanceLocation,
			World world, float startX, float startY,
			ArrayList<ViewedCollidable> aliveThings, int factionCode, Ship s)
	{
		super(appearanceLocation, "data/laserblade.json", world, startX, startY,
				aliveThings, factionCode);
		m_body.setUserData(this);
		m_ignoreForPathing = true;
		
		for(int i = 0; i < m_body.getFixtureList().size; i++ )
		{
			m_body.getFixtureList().get(i).setSensor(true);
		}
		
		//m_objectSprite.setOrigin(0, m_objectSprite.getHeight()/2);
		m_objectSprite.setPosition(startX * 29f, startY* 29f);
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
			Vector2 tmp = new Vector2();
			if( m_rightSide )
			{
				tmp.x = m_ship.m_body.getPosition().x + (float) (2.583112706 * Math.cos( m_ship.m_angleRadians - Math.PI/2 + Math.PI/12));
				tmp.y = m_ship.m_body.getPosition().y + (float) (2.583112706 * Math.sin( m_ship.m_angleRadians - Math.PI/2 + Math.PI/12));
				m_body.setTransform(tmp , (float) (m_ship.m_angleRadians - Math.PI/2 + Math.PI/12) );
			}
			else
			{
				tmp.x = m_ship.m_body.getPosition().x + (float) (2.583112706 * Math.cos( m_ship.m_angleRadians + Math.PI/2 - Math.PI/12));
				tmp.y = m_ship.m_body.getPosition().y + (float) (2.583112706 * Math.sin( m_ship.m_angleRadians + Math.PI/2 - Math.PI/12));
				m_body.setTransform(tmp , (float) (m_ship.m_angleRadians + Math.PI/2 - Math.PI/12) );
			}
			m_objectSprite.setPosition(m_body.getPosition().x *29f - m_objectAppearance.getWidth() / 2, m_body.getPosition().y *29f - m_objectAppearance.getHeight() / 2   );
			//m_objectSprite.setOrigin(0, m_objectSprite.getHeight()/2);
			float degrees = (float) Math.toDegrees( m_body.getAngle() );
			//m_body.setTransform( m_body.getPosition(), (float) Math.toRadians( degrees ) );
			m_objectSprite.setRotation(degrees);
			
			//m_objectXPosition = m_ship.m_body.getPosition().x*29f;
		   // m_objectYPosition = m_ship.m_body.getPosition().y*29f;

		    
		  // m_objectSprite.setPosition(m_objectXPosition, m_objectYPosition  );
		   m_objectSprite.setScale(.5f);
		   m_objectSprite.draw( renderer );
		   
		   if( m_integrity <= 0 )
		   {
			   m_pooledDeathEffect.setPosition(m_objectXPosition, m_objectYPosition);
			   m_pooledDeathEffect.draw(renderer, 1f/60f );   
		   }
		}
    }
}
