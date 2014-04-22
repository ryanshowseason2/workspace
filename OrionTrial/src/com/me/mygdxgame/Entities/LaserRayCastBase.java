package com.me.mygdxgame.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.physics.box2d.RayCastCallback;

public abstract class LaserRayCastBase implements RayCastCallback
{

	public LaserRayCastBase()
	{
		// TODO Auto-generated constructor stub
	}
	
	public abstract float GetDistanceTraveled();
	public abstract ArrayList<ViewedCollidable> GetEntitiesHit();

}
