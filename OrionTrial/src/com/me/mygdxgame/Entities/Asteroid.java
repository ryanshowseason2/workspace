package com.me.mygdxgame.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Entities.ViewedCollidable.DamageType;

public class Asteroid extends ViewedCollidable {

	public Asteroid(String appearanceLocation, float collisionScale, World world, float startX, float startY, ArrayList<ViewedCollidable> aliveThings ) {
		super(appearanceLocation, collisionScale, world, startX, startY, aliveThings, 0);
		// TODO Auto-generated constructor stub
		
		MassData data = new MassData();
		data.mass = 300;
		data.I = .2f;
		m_body.setMassData(data);
		m_body.setUserData(this);
		//m_body.setLinearDamping(0.2f);
		m_body.setAngularDamping(0.1f);
		m_body.setAngularVelocity((float) (2*Math.random()-1));
		m_deathEffect.load(Gdx.files.internal("data/explosionwhite.p"), Gdx.files.internal("data/"));
		m_deathEffectPool = new ParticleEffectPool(m_deathEffect, 1, 2);
		m_pooledDeathEffect = m_deathEffectPool.obtain();
		
		m_damageResistances[0] = .5f;
		m_damageResistances[1] = .5f;
		
		m_damageReductions[1] = 30;
		m_damageReductions[0] = 10;
	}

	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity) 
	{
		// TODO Auto-generated method stub
		if( crashVelocity > 3 )
		{
			object2.damageIntegrity(crashVelocity * m_body.getMass() / 20, DamageType.Collision );
		}
	}
	
	public void Draw( SpriteBatch renderer )
    {
		m_objectSprite.setRotation((float) Math.toDegrees( m_body.getAngle() ) ); 
		super.Draw(renderer);
    }

}
