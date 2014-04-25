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
import com.me.mygdxgame.Entities.EngineBrakeEffect;
import com.me.mygdxgame.Entities.EngineIntegrityCompromisedEffect;
import com.me.mygdxgame.Entities.FreezeShip;
import com.me.mygdxgame.Entities.Projectile;
import com.me.mygdxgame.Entities.Ship;
import com.me.mygdxgame.Entities.ViewedCollidable;
import com.me.mygdxgame.Entities.Projectile.Characters;
import com.me.mygdxgame.Entities.ViewedCollidable.DamageType;

public class MagneticWave extends CounterMeasure implements QueryCallback
{	
	float m_potency = -12f;
	float m_engagedMultiplier = 50f;
	boolean m_engaged = false;
	float m_beliceSpecialMultiplier = 1f;
	
	public MagneticWave(World w, Ship s, ArrayList<ViewedCollidable> aliveThings )
	{
		super(w, s, aliveThings, new Image( new Texture(Gdx.files.internal("data/magnet.png") ) ) );
		// TODO Auto-generated constructor stub
		m_rangeEnablersAndMultipliers[0] = 1f;
		m_rangeEnablersAndMultipliers[2] = -1f;
	}
	
	@Override
	public Image GetImageCopy()
	{
		return new Image( new Texture(Gdx.files.internal("data/magnet.png") ) );
	}

	


	@Override
	public void EngageCM( Button b )
	{
		super.EngageCM(b);
		m_potency *= m_engagedMultiplier;	
		m_engaged = true;
		
		if( m_specialAbilitiesActivated.get(Characters.Belice ) )
		{
			m_beliceSpecialMultiplier = -.01f;
		}
			
	}

	@Override
	public void DisengageCM()
	{
		super.DisengageCM();
		m_engaged = false;
		
		if( m_specialAbilitiesActivated.get(Characters.Belice ) )
		{
			m_beliceSpecialMultiplier = 1f;
		}
		
	}

	@Override
	public boolean reportFixture(Fixture fixture)
	{
		Body potentialTarget = fixture.getBody();
		float distanceToPotential = potentialTarget.getPosition().dst(m_ship.m_body.getPosition() );
		ViewedCollidable vc = (ViewedCollidable) potentialTarget.getUserData();
				
		if( potentialTarget != m_ship.m_body && 
			distanceToPotential <= m_range &&
			m_ship.m_factionCode != vc.m_factionCode )
		{
			float centerX = m_ship.m_body.getPosition().x;
			float centerY = m_ship.m_body.getPosition().y;
			float targetCenterX = vc.m_body.getPosition().x;
			float targetCenterY = vc.m_body.getPosition().y;
			double angleRadians = Math.atan2(centerY - targetCenterY,centerX - targetCenterX);
			float xForce =  (float)( m_beliceSpecialMultiplier*m_potency * Math.cos(angleRadians) / distanceToPotential);
	        float yForce =  (float)( m_beliceSpecialMultiplier*m_potency * Math.sin(angleRadians) / distanceToPotential);
	        if( m_engaged )
	        {
	        	vc.m_body.applyLinearImpulse(xForce, yForce, vc.m_body.getPosition().x, vc.m_body.getPosition().y, true);
	        }
	        else
	        {
	        	vc.m_body.applyForceToCenter(xForce, yForce, true);
	        }
	        
	        YashpalMagWaveSpecial(vc);
	        
	        SSidMagWaveSpecial(vc);
	        
	        BobbiMagWaveSpecial(vc);
	        
	        ShavretMagWaveSpecial(vc);
	        
	        if( m_specialAbilitiesActivated.get(Characters.Noel ) &&
    			m_engaged &&
    			Ship.class.isInstance(vc) )
    		{
	        	Ship ship = (Ship) vc;
	        	if(ship.AttemptHack(.1f))
	        	{
	        		ship.AddOverTimeEffect( new FreezeShip(300, 3, ship));
	        	}
    		}
		}
		
		return true;
	}

	private void ShavretMagWaveSpecial(ViewedCollidable vc)
	{
		if( m_specialAbilitiesActivated.get(Characters.Shavret ) &&
			m_engaged )
		{
			double damage = Math.sqrt( Math.sqrt( vc.m_body.getLinearVelocity().x * vc.m_body.getLinearVelocity().x + vc.m_body.getLinearVelocity().y * vc.m_body.getLinearVelocity().y ) );
			vc.damageIntegrity( (float) damage, DamageType.Penetration );
			vc.m_body.setLinearVelocity(0, 0);
		}
	}

	private void BobbiMagWaveSpecial(ViewedCollidable vc)
	{
		if( m_specialAbilitiesActivated.get(Characters.Bobbi ) &&
			m_engaged &&
			Ship.class.isInstance(vc) )
		{
			Ship ship = (Ship) vc;
			ship.AddOverTimeEffect( new EngineIntegrityCompromisedEffect(60, 2, ship ));
		}
	}

	private void SSidMagWaveSpecial(ViewedCollidable vc)
	{
		if( m_specialAbilitiesActivated.get(Characters.SSid ) &&
			Ship.class.isInstance(vc) )
		{
			Ship ship = (Ship) vc;
			
			if( ship.AttemptHack( .1f ) )
			{
				ship.AddOverTimeEffect( new EngineBrakeEffect(420f, 1, ship ));
			}
		}
	}

	private void YashpalMagWaveSpecial(ViewedCollidable vc)
	{
		if( m_engaged &&
			m_specialAbilitiesActivated.get(Characters.Yashpal ) &&
			Ship.class.isInstance(vc) )
		{
			Ship ship = (Ship) vc;
			if (ship.me.m_boostJuice > 0 &&
				m_ship.me.m_boostJuice < 100 )
			{
				m_ship.me.m_boostJuice++;
				ship.me.m_boostJuice--;
			}
		}
	}

	@Override
	public void AcquireAndFire(SpriteBatch renderer)
	{
		// TODO Auto-generated method stub
		float centerX = m_ship.m_body.getPosition().x;
		float centerY = m_ship.m_body.getPosition().y;
		
		
		m_world.QueryAABB(this, centerX - m_range / 2,
								centerY - m_range / 2,
								centerX + m_range / 2,
								centerY + m_range / 2 );

		
		if( m_potency < -12 )
		{
			m_potency+= 2;
		}
		else if( m_engaged )
		{
			DisengageCM();
			m_potency = -12;
		}
	}

}
