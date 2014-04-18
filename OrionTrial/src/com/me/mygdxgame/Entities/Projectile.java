package com.me.mygdxgame.Entities;

import java.util.ArrayList;
import java.util.EnumMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Entities.ViewedCollidable.DamageType;

public class Projectile extends ViewedCollidable
{
	public enum Characters
	{
		Sandy,
		Gourt,
		Noel,
		Shavret,
		Bobbi,
		SSid,
		Belice,
		Yashpal
	}
	
	EnumMap<Characters, Boolean> m_specialAbilitiesActivated = new EnumMap<Characters, Boolean>(Characters.class);
	float m_originX;
	float m_originY;
	public float m_projectileVelocity = -50f;
	int m_bulletLife = 100;
	boolean m_etherealBullet = false;
	float m_minDistance;
	Ship m_ship;
	ViewedCollidable m_originalTarget;
	
	public Projectile(String appearanceLocation, World world, float startX,
			float startY, ArrayList<ViewedCollidable> aliveThings, int factionCode)
	{
		super(appearanceLocation, world, startX, startY, aliveThings, factionCode);
		// TODO Auto-generated constructor stub		
		m_originX = startX;
		m_originY = startY;
		MassData data = m_body.getMassData();
		data.mass = 0.00005f;
		m_body.setMassData(data);
		m_body.setBullet(true);
		m_body.setUserData(this);
		m_integrity = 1;
		m_ignoreForPathing = true;
		m_isTargetable = false;
		m_body.getFixtureList().get(0).setSensor(true);
		
		PopulateSpecials();
		SetSpecials( m_specialAbilitiesActivated );
	}

	private void PopulateSpecials()
	{
		m_specialAbilitiesActivated.put(Characters.Sandy, false);
		m_specialAbilitiesActivated.put(Characters.Gourt, false);
		m_specialAbilitiesActivated.put(Characters.Noel, true);
		m_specialAbilitiesActivated.put(Characters.Shavret, false);
		m_specialAbilitiesActivated.put(Characters.Bobbi, false);
		m_specialAbilitiesActivated.put(Characters.SSid, false);
		m_specialAbilitiesActivated.put(Characters.Belice, false);
		m_specialAbilitiesActivated.put(Characters.Yashpal, false);
	}

	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity)
	{
		if( (!m_etherealBullet && object2.m_isTargetable) ||
			( m_etherealBullet && object2.m_factionCode != 0 ) )
		{
			DamageType dType = DamageType.Penetration;
			
			dType = ShavretsDamageType(object2);
			
			object2.damageIntegrity(crashVelocity, dType );	
			m_integrity -=1;
			
			BobbisHackingBullets(object2);
			SSidsHackingBullets(object2);
			NoelsHackingBullets(object2);
		}
	}

	private DamageType ShavretsDamageType(ViewedCollidable object2)
	{
		DamageType dType;
		if( m_specialAbilitiesActivated.get(Characters.Shavret) && 
			object2.m_damageResistances[DamageType.Penetration.value] > object2.m_damageResistances[DamageType.Collision.value] )
		{
			dType = DamageType.Collision;
		}
		else
		{
			dType = DamageType.Penetration;
		}
		return dType;
	}

	private void BobbisHackingBullets(ViewedCollidable object2)
	{
		if( m_specialAbilitiesActivated.get(Characters.Bobbi) &&
			Ship.class.isInstance(object2) )
		{				
			Ship s = (Ship) object2;
			if( s != null )
			{
				if( s.AttemptHack( 5.0f ) )
				{
					s.m_shieldRechargeDelay+= 5;
				}
			}
		}
	}
	
	private void SSidsHackingBullets(ViewedCollidable object2)
	{
		if( m_specialAbilitiesActivated.get(Characters.SSid) &&
				EnemyShip.class.isInstance(object2) )
		{				
			EnemyShip s = (EnemyShip) object2;
			if( s != null && s.m_shieldIntegrity <= 0 && s.m_fighterGroup.size() > 0 )
			{
				if( s.AttemptHack( 15.0f ) )
				{
					ViewedCollidable vc = s.m_fighterGroup.get(0);
					m_ship.m_trackedTargets.add( vc );
					s.m_fighterGroup.remove(vc);
					s.m_soundTheAlarmCounter = 0;
				}
			}
		}
	}
	
	private void NoelsHackingBullets(ViewedCollidable object2)
	{
		if( m_specialAbilitiesActivated.get(Characters.Noel) &&
				EnemyShip.class.isInstance(object2) )
		{				
			EnemyShip s = (EnemyShip) object2;
			if( s != null && s.m_shieldIntegrity <= 0 && s.ce.m_enginePotency > 0 )
			{
				if( s.AttemptHack( 15.0f ) )
				{
					s.ce.m_enginePotency*=.75;
				}
			}
		}
	}
	
	public void Fire( Ship origin, ViewedCollidable target, float accuracy )
	{
		m_ship = origin;
		m_originalTarget = target;
		float centerX = origin.m_body.getPosition().x;
		float centerY = origin.m_body.getPosition().y;
		float targetCenterX = target.m_body.getPosition().x;
		float targetCenterY = target.m_body.getPosition().y;		
		m_angleRadians = Math.atan2(centerY - targetCenterY,centerX - targetCenterX) + accuracy;
		m_angleDegrees = (float) (m_angleRadians * 180 / Math.PI);
		m_objectSprite.rotate((float) Math.toDegrees(m_angleRadians));
		m_body.setFixedRotation(true);
		m_body.setTransform(m_body.getPosition(), (float) Math.toRadians( m_angleDegrees ) );
		float xSpeed =  (float)(m_projectileVelocity * Math.cos(m_angleRadians));
        float ySpeed =  (float)(m_projectileVelocity * Math.sin(m_angleRadians));
        
        xSpeed = xSpeed + ( origin.m_body.getLinearVelocity().x * xSpeed > 0 ? origin.m_body.getLinearVelocity().x : 0);
        ySpeed = ySpeed + ( origin.m_body.getLinearVelocity().y * ySpeed > 0 ? origin.m_body.getLinearVelocity().y : 0);
        m_body.setLinearVelocity( xSpeed, ySpeed );
	}
	
	@Override
	public void Draw( SpriteBatch renderer)
	{
		super.Draw(renderer);
		
		m_bulletLife--;
		if(m_bulletLife <= 0 )
		{
			m_integrity = 0;
		}	
		
		if( m_specialAbilitiesActivated.get(Characters.Gourt))
		{
			Vector2 targetPos = m_originalTarget.m_body.getPosition();
			Vector2 bulletPos = m_body.getPosition();
			double angleRadians = Math.atan2(targetPos.y - bulletPos.y, targetPos.x - bulletPos.x);
			float xForce = 0;
		    float yForce = 0;
		    xForce = (float)(30f * Math.cos(angleRadians));
	        yForce = (float)(30f * Math.sin(angleRadians));
	        m_body.applyForceToCenter(xForce, yForce, true);
		}
        
        m_angleRadians = Math.atan2(m_body.getLinearVelocity().y, m_body.getLinearVelocity().x);
        float degrees = (float) (m_angleRadians * 180 / Math.PI);
        float difference = degrees - m_angleDegrees;
        m_objectSprite.rotate( (float) (difference) );
        m_angleDegrees = (float) degrees;
        m_body.setTransform(m_body.getPosition(), (float) Math.toRadians( m_angleDegrees ) );
	}

	public void SetSpecials( EnumMap<Characters, Boolean> specialAbilitiesActivated )
	{
		m_specialAbilitiesActivated = specialAbilitiesActivated;
		
		if( m_specialAbilitiesActivated.get(Characters.Yashpal))
		{
			m_etherealBullet = true;
		}
		
		if( m_specialAbilitiesActivated.get(Characters.Sandy))
		{
			m_body.getFixtureList().get(0).setSensor(false);
			m_body.getFixtureList().get(0).setRestitution(1f);
			m_integrity++;
		}		
	}
	
	@Override
	public void damageIntegrity( float damage , DamageType type)
	{
	}
}
