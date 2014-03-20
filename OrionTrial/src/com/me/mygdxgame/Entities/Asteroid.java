package com.me.mygdxgame.Entities;

import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;

public class Asteroid extends ViewedCollidable {

	public Asteroid(String appearanceLocation, World world, float startX, float startY) {
		super(appearanceLocation, world, startX, startY);
		// TODO Auto-generated constructor stub
		
		MassData data = m_body.getMassData();
		data.mass = 500;
		m_body.setMassData(data);
		m_body.setUserData(this);
	}

}
