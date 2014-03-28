package com.me.mygdxgame.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;

public class Asteroid extends ViewedCollidable {

	public Asteroid(String appearanceLocation, World world, float startX, float startY, ArrayList<ViewedCollidable> aliveThings ) {
		super(appearanceLocation, world, startX, startY, aliveThings, 0);
		// TODO Auto-generated constructor stub
		
		MassData data = m_body.getMassData();
		data.mass = 300;
		m_body.setMassData(data);
		m_body.setUserData(this);
		m_deathEffect.load(Gdx.files.internal("data/explosionwhite.p"), Gdx.files.internal("data/"));
		m_deathEffectPool = new ParticleEffectPool(m_deathEffect, 1, 2);
		m_pooledDeathEffect = m_deathEffectPool.obtain();
	}

	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity) 
	{
		// TODO Auto-generated method stub
		object2.damageIntegrity(crashVelocity * m_body.getMass() );
	}

}
