package com.me.mygdxgame.Equipables;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.me.mygdxgame.Entities.Ship;

public abstract class CounterMeasure 
{
	int m_range;
	World m_world;
	Ship m_ship;
	Body m_body;
	
	public CounterMeasure( World w, Ship s )
	{
		m_world = w;
		m_ship = s;
		
		BodyDef bodyDef = new BodyDef();
		// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
		bodyDef.type = BodyType.DynamicBody;
		// Set our body's starting position in the world
		bodyDef.position.set(m_ship.m_body.getPosition().x, m_ship.m_body.getPosition().y);
	
		// Create our body in the world using our body definition
		m_body = m_world.createBody(bodyDef);
	
		// Create a circle shape and set its radius
		CircleShape circle = new CircleShape();
		circle.setRadius(m_range);
	
		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.isSensor = true;
	
		// Create our fixture and attach it to the body
		Fixture fixture = m_body.createFixture(fixtureDef);
	
		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		circle.dispose();
	}
	
	public abstract void AcquireAndFire();
	public abstract void EngageCM();
	public abstract void DisengageCM();
}
