package com.me.mygdxgame.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;

public class Projectile extends ViewedCollidable
{
	float m_originX;
	float m_originY;
	
	public Projectile(String appearanceLocation, World world, float startX,
			float startY, ArrayList<ViewedCollidable> aliveThings, int factionCode)
	{
		super(appearanceLocation, world, startX, startY, aliveThings, factionCode);
		// TODO Auto-generated constructor stub
		
		m_originX = startX;
		m_originY = startY;
		MassData data = m_body.getMassData();
		data.mass = 0.00005f;
		m_body.setMassData(data);
		m_body.setBullet(true);
		m_body.setUserData(this);
		m_integrity = 1;
		//m_body.getFixtureList().get(0).setSensor(true);
	}

	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity)
	{
		// TODO Auto-generated method stub
		float mass = m_body.getMass();
		object2.damageIntegrity(crashVelocity / 5 );	
		m_integrity -=1;
	}
	
	public void Fire( Ship origin, ViewedCollidable target, float accuracy )
	{
		float centerX = origin.m_body.getPosition().x;
		float centerY = origin.m_body.getPosition().y;
		float targetCenterX = target.m_body.getPosition().x;
		float targetCenterY = target.m_body.getPosition().y;		
		double angleRadians = Math.atan2(centerY - targetCenterY,centerX - targetCenterX) + accuracy;		
		m_objectSprite.rotate((float) Math.toDegrees(angleRadians));
		m_body.setFixedRotation(true);
		float xSpeed =  (float)(-50f * Math.cos(angleRadians));
        float ySpeed =  (float)(-50f * Math.sin(angleRadians));
        
        xSpeed = xSpeed + ( origin.m_body.getLinearVelocity().x * xSpeed > 0 ? origin.m_body.getLinearVelocity().x : 0);
        ySpeed = ySpeed + ( origin.m_body.getLinearVelocity().y * ySpeed > 0 ? origin.m_body.getLinearVelocity().y : 0);
        m_body.setLinearVelocity( xSpeed, ySpeed );
	}
	
	@Override
	public void Draw( SpriteBatch renderer)
	{
		super.Draw(renderer);
		
		if( m_body.getPosition().dst(m_originX, m_originY) > 25 )
		{
			m_integrity = 0;
		}
	}

}
