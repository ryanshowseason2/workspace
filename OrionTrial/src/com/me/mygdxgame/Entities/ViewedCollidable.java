package com.me.mygdxgame.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class ViewedCollidable 
{
	float m_objectXPosition;
	float m_objectYPosition;
    Texture m_objectAppearance;
    Sprite m_objectSprite;
    public Body m_body;
    double m_angle = 0;
	   
	   ViewedCollidable( String appearanceLocation, World world, float startX, float startY )
	   {
	      m_objectXPosition = startX;
	      m_objectYPosition = startY;
	      m_objectAppearance = new Texture(Gdx.files.internal(appearanceLocation));
	      m_objectSprite = new Sprite( m_objectAppearance );
	      // First we create a body definition
	      BodyDef bodyDef = new BodyDef();
	      // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
	      bodyDef.type = BodyType.DynamicBody;
	      // Set our body's starting position in the world
	      bodyDef.position.set(m_objectXPosition, m_objectYPosition);

	      // Create our body in the world using our body definition
	      m_body = world.createBody(bodyDef);

	      // Create a circle shape and set its radius to 6
	      CircleShape circle = new CircleShape();
	      float radius = Math.max(m_objectAppearance.getWidth() / 2, m_objectAppearance.getHeight() ) / 2;
	      circle.setRadius(radius);

	      // Create a fixture definition to apply our shape to
	      FixtureDef fixtureDef = new FixtureDef();
	      fixtureDef.shape = circle;
	      fixtureDef.density = 0.1f; 
	      fixtureDef.friction = 0f;
	      fixtureDef.restitution = 0.6f; // Make it bounce a little bit

	      // Create our fixture and attach it to the body
	      Fixture fixture = m_body.createFixture(fixtureDef);

	      // Remember to dispose of any shapes after you're done with them!
	      // BodyDef and FixtureDef don't need disposing, but shapes do.
	      circle.dispose();
	   }
	   
	   public void Draw( SpriteBatch renderer )
	   {
		   m_objectXPosition = m_body.getPosition().x;
		   m_objectYPosition = m_body.getPosition().y;
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
	      //renderer.draw( m_objectAppearance, m_objectXPosition - m_objectAppearance.getWidth() / 2, m_objectYPosition - m_objectAppearance.getHeight() / 2 );
	   }
	   
	   void Setlocation( float x, float y )
	   {
	      m_objectXPosition = x;
	      m_objectYPosition = y;
	   }
}
