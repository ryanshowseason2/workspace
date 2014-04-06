package com.me.mygdxgame.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public abstract class ViewedCollidable 
{
	public float m_objectXPosition;
	public float m_objectYPosition;
    public Texture m_objectAppearance;
    Sprite m_objectSprite;
    public Body m_body;
    public float m_angleDegrees = 0;
    public double m_angleRadians = 0;
    public float m_drawScale = 29f;
    public float m_integrity = 1000f;
    ParticleEffect m_deathEffect = new ParticleEffect();
    ParticleEffectPool m_deathEffectPool;
    PooledEffect m_pooledDeathEffect;
    public int m_factionCode = 0;
    World m_world;
    ArrayList<ViewedCollidable> m_aliveThings;
    float[] m_damageResistances = {1,1,1,1};
    float[] m_damageReductions = {0,0,0,0};
    boolean m_ignoreForPathing = false;
    public boolean m_isTargetable = true;
    

    
    public enum DamageType 
    {
        Collision(0), 
        Penetration(1), 
        Energy(2), 
        Explosion(3);
        
        public int value;

        private DamageType(int value) 
        {
            this.value = value;
        }
        
     };  

	   
	   ViewedCollidable( String appearanceLocation, World world, float startX, float startY, ArrayList<ViewedCollidable> aliveThings, int factionCode )
	   {
		  m_world = world;
		  m_aliveThings = aliveThings;
		  m_factionCode = factionCode;
	      m_objectXPosition = startX*29f;
	      m_objectYPosition = startY*29f;
	      m_objectAppearance = new Texture(Gdx.files.internal(appearanceLocation));
	      m_objectSprite = new Sprite( m_objectAppearance );
	      // First we create a body definition
	      BodyDef bodyDef = new BodyDef();
	      // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
	      bodyDef.type = BodyType.DynamicBody;
	      // Set our body's starting position in the world
	      bodyDef.position.set(startX, startY);

	      // Create our body in the world using our body definition
	      m_body = world.createBody(bodyDef);

	      // Create a circle shape and set its radius to 6
	      CircleShape circle = new CircleShape();
	      float radius = Math.max(m_objectAppearance.getWidth() / 2 / 29f, m_objectAppearance.getHeight() ) / 2 / 29f;
	      circle.setRadius(radius);

	      // Create a fixture definition to apply our shape to
	      FixtureDef fixtureDef = new FixtureDef();
	      fixtureDef.shape = circle;
	      fixtureDef.density = 0.1f; 
	      fixtureDef.friction = 0f;
	      fixtureDef.restitution = 0.1f; // Make it bounce a little bit
	      fixtureDef.filter.groupIndex = (short) (m_factionCode * -1);

	      // Create our fixture and attach it to the body
	      Fixture fixture = m_body.createFixture(fixtureDef);

	      // Remember to dispose of any shapes after you're done with them!
	      // BodyDef and FixtureDef don't need disposing, but shapes do.
	      circle.dispose();
	      aliveThings.add(this);
	   }
	   
	   public void Draw( SpriteBatch renderer )
	   {
		   m_objectXPosition = m_body.getPosition().x*29f;
		   m_objectYPosition = m_body.getPosition().y*29f;
		   /*renderer.draw(m_objectAppearance,
				   (Gdx.graphics.getWidth() - m_objectAppearance.getWidth()) / 2.0f,
				   (Gdx.graphics.getHeight() - m_objectAppearance.getHeight()) / 2.0f
				   ,m_objectAppearance.getWidth()/2.0f,
				   m_objectAppearance.getHeight()/2.0f, 
				   m_objectAppearance.getWidth(), 
				   m_objectAppearance.getHeight(),
				   1f, 
				   1f,
				   false,
				   false);*/
		   m_objectSprite.setPosition(m_objectXPosition - m_objectAppearance.getWidth() / 2, m_objectYPosition - m_objectAppearance.getHeight() / 2 );
		   m_objectSprite.draw( renderer );
		   if( m_integrity <= 0 )
		   {
			   m_pooledDeathEffect.setPosition(m_objectXPosition, m_objectYPosition);
			   m_pooledDeathEffect.draw(renderer, 1f/60f );   
		   }
		   
	      //renderer.draw( m_objectAppearance, m_objectXPosition - m_objectAppearance.getWidth() / 2, m_objectYPosition - m_objectAppearance.getHeight() / 2 );
	   }
	   
	   void Setlocation( float x, float y )
	   {
	      m_objectXPosition = x;
	      m_objectYPosition = y;
	   }

	   public abstract void damageCalc(ViewedCollidable object2, float crashVelocity);
	   
	   public void damageIntegrity(float damage, DamageType type )
	   {
		   damage = damage * m_damageResistances[type.value];
		   damage = damage > m_damageReductions[type.value] ? damage - m_damageReductions[type.value] : 0;
		   m_integrity -= damage;
	   }

	public boolean deathThroesDone() 
	{
		boolean dead = true;
		
		if( m_pooledDeathEffect != null )
		{
			dead = m_pooledDeathEffect.isComplete(); 
		}

		if( dead && m_pooledDeathEffect != null )
		{
			m_pooledDeathEffect.free();
		}
		
		return dead;
	}
}
