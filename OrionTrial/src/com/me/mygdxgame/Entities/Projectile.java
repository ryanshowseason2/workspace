package com.me.mygdxgame.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.physics.box2d.World;

public class Projectile extends ViewedCollidable
{

	public Projectile(String appearanceLocation, World world, float startX,
			float startY, ArrayList<ViewedCollidable> aliveThings)
	{
		super(appearanceLocation, world, startX, startY, aliveThings);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity)
	{
		// TODO Auto-generated method stub

	}

}
