package com.me.mygdxgame.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;

public class EnemyShip extends Ship
{

	public EnemyShip(String appearanceLocation, World world, float startX,
			float startY, float maxV, int factionCode, ArrayList<ViewedCollidable> aliveThings )
	{
		super(appearanceLocation, world, startX, startY, maxV, aliveThings);
		// TODO Auto-generated constructor stub
		m_factionCode = factionCode;
		
		MassData data = m_body.getMassData();
		data.mass = 10;
		m_body.setMassData(data);
		m_body.setUserData(this);		
		m_deathEffect.load(Gdx.files.internal("data/explosionred.p"), Gdx.files.internal("data/"));
		m_deathEffectPool = new ParticleEffectPool(m_deathEffect, 1, 2);
		m_pooledDeathEffect = m_deathEffectPool.obtain();	
	}

}
