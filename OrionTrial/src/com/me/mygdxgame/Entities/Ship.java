package com.me.mygdxgame.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.physics.box2d.World;

public class Ship extends ViewedCollidable 
{

	public CruiseEngine ce;
	public ManeuverEngine me;
	ArrayList<CounterMeasure> m_shortRangeCMS = new ArrayList< CounterMeasure >();
	ArrayList<CounterMeasure> m_mediumRangeCMS = new ArrayList< CounterMeasure >();
	ArrayList<CounterMeasure> m_longRangeCMS = new ArrayList< CounterMeasure >();
	float m_maxVelocity;
	
	public Ship(String appearanceLocation, World world, float startX, float startY, float maxV ) 
	{
		super(appearanceLocation, world, startX, startY);
		// TODO Auto-generated constructor stub
		m_maxVelocity = maxV;
		me = new ConventionalManeuverEngine( this, maxV );
		ce = new ConventionalCruiseEngine( this, maxV );
	}

	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity) 
	{
		// TODO Auto-generated method stub

	}

}
