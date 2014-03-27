package com.me.mygdxgame.Equipables;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Entities.Ship;

public class MachineGun extends CounterMeasure
{

	public MachineGun(World w, Ship s)
	{
		super(w, s);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void AcquireAndFire()
	{
		QueryCallback callback;
		// TODO Auto-generated method stub
		Fixture fixture;
		fixture.
		m_world.QueryAABB(callback, lowerX, lowerY, upperX, upperY);
	}

	@Override
	public void EngageCM()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void DisengageCM()
	{
		// TODO Auto-generated method stub

	}

}
