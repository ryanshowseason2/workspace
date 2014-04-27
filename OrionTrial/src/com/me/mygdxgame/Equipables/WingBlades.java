package com.me.mygdxgame.Equipables;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
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

public class WingBlades extends CounterMeasure
{

	ParticleEffect m_sawEffect = new ParticleEffect();
    ParticleEffectPool m_sawEffectPool;
    PooledEffect m_pooledSawEffect;
    PooledEffect m_pooledSawEffectOriginal;
    
	public WingBlades(World w, Ship s, ArrayList<ViewedCollidable> aliveThings )
	{
		super(w, s, aliveThings, new Image( new Texture(Gdx.files.internal("data/blade.png") ) ) );
		// TODO Auto-generated constructor stub
		m_rangeEnablersAndMultipliers[0] = 1f;
		m_rangeEnablersAndMultipliers[1] = -1f;
		m_rangeEnablersAndMultipliers[2] = -1f;
		
		m_sawEffect.load(Gdx.files.internal("data/chainsaw.p"), Gdx.files.internal("data/"));
		m_sawEffectPool = new ParticleEffectPool(m_sawEffect, 1, 2);
		m_pooledSawEffect = m_sawEffectPool.obtain();
		m_pooledSawEffectOriginal = m_sawEffectPool.obtain();
	}
	
	@Override
	public Image GetImageCopy()
	{
		return new Image( new Texture(Gdx.files.internal("data/blade.png") ) );
	}

	@Override
	public void AcquireAndFire( SpriteBatch renderer )
	{
		float centerX = m_ship.m_body.getPosition().x;
		float centerY = m_ship.m_body.getPosition().y;
		
		float radius = (float) Math.sqrt(  m_pooledSawEffectOriginal.getEmitters().get(0).getXOffsetValue().getLowMax() * m_pooledSawEffectOriginal.getEmitters().get(0).getXOffsetValue().getLowMax() +
										   m_pooledSawEffectOriginal.getEmitters().get(0).getYOffsetValue().getLowMax() * m_pooledSawEffectOriginal.getEmitters().get(0).getYOffsetValue().getLowMax() );
		float newAngleHigh = (float) (-90f + m_ship.m_angleDegrees + m_pooledSawEffectOriginal.getEmitters().get(0).getAngle().getHighMax());
		float newAngleLow = (float) (-90f + m_ship.m_angleDegrees + m_pooledSawEffectOriginal.getEmitters().get(0).getAngle().getLowMax());
		float newRotationHigh = (float) (-90f + m_ship.m_angleDegrees + m_pooledSawEffectOriginal.getEmitters().get(0).getRotation().getHighMax());
		float newRotationLow = (float) (-90f + m_ship.m_angleDegrees + m_pooledSawEffectOriginal.getEmitters().get(0).getRotation().getLowMax());
		m_pooledSawEffect.getEmitters().get(0).getXOffsetValue().setLow((float) (radius * Math.cos(m_ship.m_angleRadians - Math.PI/2)));
		m_pooledSawEffect.getEmitters().get(0).getYOffsetValue().setLow((float) (radius * Math.sin(m_ship.m_angleRadians - Math.PI/2)));
		m_pooledSawEffect.getEmitters().get(0).getAngle().setHigh(newAngleHigh);
		m_pooledSawEffect.getEmitters().get(0).getAngle().setLow(newAngleLow);
		m_pooledSawEffect.getEmitters().get(0).getRotation().setHigh(newRotationHigh);
		m_pooledSawEffect.getEmitters().get(0).getRotation().setLow(newRotationLow);
		
		radius = (float) Math.sqrt(  m_pooledSawEffectOriginal.getEmitters().get(1).getXOffsetValue().getLowMax() * m_pooledSawEffectOriginal.getEmitters().get(1).getXOffsetValue().getLowMax() +
									 m_pooledSawEffectOriginal.getEmitters().get(1).getYOffsetValue().getLowMax() * m_pooledSawEffectOriginal.getEmitters().get(1).getYOffsetValue().getLowMax() );
		newAngleHigh = (float) (-90f + m_ship.m_angleDegrees + m_pooledSawEffectOriginal.getEmitters().get(1).getAngle().getHighMax());
		newAngleLow = (float) (-90f + m_ship.m_angleDegrees + m_pooledSawEffectOriginal.getEmitters().get(1).getAngle().getLowMax());
		newRotationHigh = (float) (-90f + m_ship.m_angleDegrees + m_pooledSawEffectOriginal.getEmitters().get(1).getRotation().getHighMax());
		newRotationLow = (float) (-90f + m_ship.m_angleDegrees + m_pooledSawEffectOriginal.getEmitters().get(1).getRotation().getLowMax());
		m_pooledSawEffect.getEmitters().get(1).getXOffsetValue().setLow((float) (radius * Math.cos(m_ship.m_angleRadians - Math.PI/2)));
		m_pooledSawEffect.getEmitters().get(1).getYOffsetValue().setLow((float) (radius * Math.sin(m_ship.m_angleRadians - Math.PI/2)));
		m_pooledSawEffect.getEmitters().get(1).getAngle().setHigh(newAngleHigh);
		m_pooledSawEffect.getEmitters().get(1).getAngle().setLow(newAngleLow);
		m_pooledSawEffect.getEmitters().get(1).getRotation().setHigh(newRotationHigh);
		m_pooledSawEffect.getEmitters().get(1).getRotation().setLow(newRotationLow);
		
		
		radius = (float) Math.sqrt(  m_pooledSawEffectOriginal.getEmitters().get(2).getXOffsetValue().getLowMax() * m_pooledSawEffectOriginal.getEmitters().get(2).getXOffsetValue().getLowMax() +
									 m_pooledSawEffectOriginal.getEmitters().get(2).getYOffsetValue().getLowMax() * m_pooledSawEffectOriginal.getEmitters().get(2).getYOffsetValue().getLowMax() );
		float additionalAngle = (float) Math.atan( m_pooledSawEffectOriginal.getEmitters().get(2).getYOffsetValue().getLowMax() / m_pooledSawEffectOriginal.getEmitters().get(1).getXOffsetValue().getLowMax() );
		newAngleHigh = (float) (-90f + m_ship.m_angleDegrees + m_pooledSawEffectOriginal.getEmitters().get(2).getAngle().getHighMax());
		newAngleLow = (float) (-90f + m_ship.m_angleDegrees + m_pooledSawEffectOriginal.getEmitters().get(2).getAngle().getLowMax());
		newRotationHigh = (float) (-90f + m_ship.m_angleDegrees + m_pooledSawEffectOriginal.getEmitters().get(2).getRotation().getHighMax());
		newRotationLow = (float) (-90f + m_ship.m_angleDegrees + m_pooledSawEffectOriginal.getEmitters().get(2).getRotation().getLowMax());
		m_pooledSawEffect.getEmitters().get(2).getXOffsetValue().setLow((float) (radius * Math.cos( additionalAngle + m_ship.m_angleRadians - Math.PI/2)));
		m_pooledSawEffect.getEmitters().get(2).getYOffsetValue().setLow((float) (radius * Math.sin( additionalAngle + m_ship.m_angleRadians - Math.PI/2)));
		m_pooledSawEffect.getEmitters().get(2).getAngle().setHigh(newAngleHigh);
		m_pooledSawEffect.getEmitters().get(2).getAngle().setLow(newAngleLow);
		m_pooledSawEffect.getEmitters().get(2).getRotation().setHigh(newRotationHigh);
		m_pooledSawEffect.getEmitters().get(2).getRotation().setLow(newRotationLow);

		m_pooledSawEffect.setPosition( m_ship.m_objectXPosition, m_ship.m_objectYPosition );
		m_pooledSawEffect.draw(renderer, 1f/60f );
	}

	@Override
	public void EngageCM( Button b )
	{
		super.EngageCM(b);
	}

	@Override
	public void DisengageCM()
	{
		super.DisengageCM();
	}

}
