package com.me.mygdxgame.Entities;

import java.util.ArrayList;
import java.util.EnumMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Entities.EnemyShip.SeekType;
import com.me.mygdxgame.Entities.Projectile.Characters;

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
		m_ignoreForPathing = true;
	}

	public void SetSpecials( EnumMap<Characters, Boolean> specialAbilitiesActivated )
	{
		m_specialAbilitiesActivated = specialAbilitiesActivated;
		
		if( m_specialAbilitiesActivated.get(Characters.Sandy) )
		{
			m_shieldIntegrity = 500;
		}
		
		if( m_specialAbilitiesActivated.get(Characters.SSid) )
		{
			m_isTargetable = false;
		}
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
			m_body.setLinearDamping(.5f);
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
	protected void NavigateToTarget()
	{
		super.NavigateToTarget();
		
		if( m_specialAbilitiesActivated.get(Characters.Belice) &&
				m_body.getPosition().dst(m_target.m_body.getPosition()) < 20 && 
				(m_integrity > 0 ) )
		{
			me.ManeuverForward();
		}
		
		if( m_specialAbilitiesActivated.get(Characters.Yashpal) &&
				m_body.getPosition().dst(m_target.m_body.getPosition()) < 5 && 
				(m_integrity > 0 ) )
		{
			m_body.setTransform( m_target.m_body.getPosition(), (float) m_angleRadians);
			m_integrity = 0;
			m_body.setLinearVelocity( m_target.m_body.getLinearVelocity() );
		}
	}
	
	@Override
	protected void RegularPathFinding(float radius)
	{
		if( m_specialAbilitiesActivated.get(Characters.Gourt) )
		{
			super.RegularPathFinding(radius);
		}
		
		
	}
	
	@Override
	protected void RammingLogic(Vector2 pos, Vector2 vec)
	{
		if ((m_body.getLinearVelocity().x > 5 && vec.x < pos.x)
				|| (m_body.getLinearVelocity().x < -5 && vec.x > pos.x)
				|| (m_body.getLinearVelocity().y > 5 && vec.y < pos.y)
				|| (m_body.getLinearVelocity().y < -5 && vec.y > pos.y))
		{
			ce.EngineBrake();
		} 
		else if( m_integrity > 0 ||  m_specialAbilitiesActivated.get(Characters.Bobbi) )
		{
			if( m_integrity < 0 )
			{
				ce.m_enginePotency = (float) (ce.m_enginePotency * 1.3);
			}
			ce.ThrottleForward();
			ce.EngageEngine();
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
			boolean trueDamage = m_specialAbilitiesActivated.get(Characters.Yashpal) && vc == m_target;
			vc.damageIntegrity(m_missileDamage/dst, DamageType.Explosion, trueDamage, trueDamage, trueDamage );
			
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
