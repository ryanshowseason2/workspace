package com.me.mygdxgame.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Entities.EnemyShip.SeekType;

public class MissileEntity extends EnemyShip implements QueryCallback
{
	float m_missileDamage = 40;
	public MissileEntity( ViewedCollidable target, World world, float startX, float startY, float initialAngleAdjust,
			float maxV, int factionCode, ArrayList<ViewedCollidable> aliveThings )
	{
		super("missile", 1.0f, world, startX, startY,
				initialAngleAdjust, maxV, factionCode, aliveThings);
		m_onDeckSeekType = m_seekType = SeekType.RamTarget;
		m_target = target;
		MassData data = new MassData();
		data.mass = 2f;
		m_body.setMassData(data);
		m_body.setBullet(true);
		m_shieldIntegrityRechargeFactor = 0;
		m_shieldIntegrity = 0;
	}

	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity) 
	{
		
		m_integrity = 0;
		float centerX = m_body.getPosition().x;
		float centerY = m_body.getPosition().y;
		m_world.QueryAABB(this, centerX - 3f / 2f,
								centerY - 3f / 2f,
								centerX + 3f / 2f,
								centerY + 3f / 2f );
	}
	
	@Override
	public void Draw( SpriteBatch renderer )
	{	
		super.Draw(renderer);
		m_integrity-=5;
		
		if(m_integrity <=0 && m_missileDamage > 0)
		{
			float centerX = m_body.getPosition().x;
			float centerY = m_body.getPosition().y;
			m_world.QueryAABB(this, centerX - 3f / 2f,
									centerY - 3f / 2f,
									centerX + 3f / 2f,
									centerY + 3f / 2f );
			m_missileDamage/=2;
		}
	}
	
	@Override
	public boolean reportFixture(Fixture fixture)
	{
		ViewedCollidable vc = (ViewedCollidable) fixture.getBody().getUserData();
		
		if (vc != null && 
			vc.m_factionCode != m_factionCode )
		{			
			float dst = vc.m_body.getPosition().dst(m_body.getPosition());
			vc.damageIntegrity(m_missileDamage/dst, DamageType.Explosion );
			
			float centerX = m_body.getPosition().x;
			float centerY = m_body.getPosition().y;
			float targetCenterX = vc.m_body.getPosition().x;
			float targetCenterY = vc.m_body.getPosition().y;
			double angleRadians = Math.atan2(centerY - targetCenterY,centerX - targetCenterX);
			float xForce =  (float)( -m_missileDamage * Math.cos(angleRadians));
		    float yForce =  (float)( -m_missileDamage * Math.sin(angleRadians));
		    vc.m_body.applyLinearImpulse(2*xForce, 2*yForce, vc.m_body.getPosition().x, vc.m_body.getPosition().y, true);
		}
		return true;
	}
}
