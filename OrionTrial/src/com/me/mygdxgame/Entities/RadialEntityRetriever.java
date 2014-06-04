package com.me.mygdxgame.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;

public class RadialEntityRetriever implements QueryCallback
{
	ArrayList<ViewedCollidable> m_detectedEntities = new ArrayList<ViewedCollidable>();
	public RadialEntityRetriever( World m_world, float radius, float centerX, float centerY )
	{
		m_world.QueryAABB(this, centerX - radius,
								centerY - radius,
								centerX + radius,
								centerY + radius );	
	}

	@Override
	public boolean reportFixture(Fixture fixture)
	{
		m_detectedEntities.add( (ViewedCollidable) fixture.getBody().getUserData());
		return true;
	}

}
