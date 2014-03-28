package com.me.mygdxgame.Equipables;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.me.mygdxgame.Entities.Ship;
import com.me.mygdxgame.Entities.ViewedCollidable;

public abstract class CounterMeasure implements QueryCallback
{
	int m_range;
	World m_world;
	Ship m_ship;
	ViewedCollidable m_target = null;
	
	public CounterMeasure( World w, Ship s )
	{
		m_world = w;
		m_ship = s;		
	}
	
	public abstract void AcquireAndFire();
	public abstract void EngageCM();
	public abstract void DisengageCM();
}
