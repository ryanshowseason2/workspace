package com.me.mygdxgame.Equipables;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.me.mygdxgame.Entities.Projectile;
import com.me.mygdxgame.Entities.Ship;
import com.me.mygdxgame.Entities.ViewedCollidable;

public class Missile extends CounterMeasure
{
	ViewedCollidable m_secondaryTarget = null;
	int m_activateSecondaryMode = 0;
	
	public Missile(World w, Ship s, ArrayList<ViewedCollidable> aliveThings )
	{
		super(w, s, aliveThings, new Image( new Texture(Gdx.files.internal("data/missile.png") ) ) );
		// TODO Auto-generated constructor stub
		m_rangeEnablersAndMultipliers[1] = 1f;
	}
	
	@Override
	public Image GetImageCopy()
	{
		return new Image( new Texture(Gdx.files.internal("data/missile.png") ) );
	}

	@Override
	public void AcquireAndFire( SpriteBatch renderer )
	{

	}

	@Override
	public void EngageCM( Button b )
	{
		super.EngageCM(b);
		m_activateSecondaryMode = 120;
	}

	@Override
	public void DisengageCM()
	{
		super.DisengageCM();
	}

}
