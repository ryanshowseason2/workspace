package com.me.mygdxgame.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;

public class Projectile extends ViewedCollidable
{

	
	public Projectile(String appearanceLocation, World world, float startX,
			float startY, ArrayList<ViewedCollidable> aliveThings, int factionCode)
	{
		super(appearanceLocation, world, startX, startY, aliveThings, factionCode);
		// TODO Auto-generated constructor stub
		
		MassData data = m_body.getMassData();
		data.mass = 1;
		m_body.setMassData(data);
		m_body.setBullet(true);
		m_body.setUserData(this);
		m_integrity = 1;
	}

	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity)
	{
		// TODO Auto-generated method stub
		object2.damageIntegrity(crashVelocity * m_body.getMass() );	
	}

}
