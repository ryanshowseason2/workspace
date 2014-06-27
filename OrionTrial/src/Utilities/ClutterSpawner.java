package Utilities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Entities.Asteroid;
import com.me.mygdxgame.Entities.ViewedCollidable;

public class ClutterSpawner
{
	World m_world;
	ArrayList<ViewedCollidable> m_aliveThings;
	
	public ClutterSpawner( World w, ArrayList<ViewedCollidable> aliveThings)
	{
		m_world = w;
		m_aliveThings = aliveThings;
	}
	
	public void SpawnAsteroids( float x, float y, float radius, int amount )
	{
		for( int i = 0; i < amount; i++ )
		{
			float xrandom = (float) ((Math.random() - .5) * 2 * radius);
			float yrandom = (float) ((Math.random() - .5) * 2 * radius);
			new Asteroid("asteroid", 4.5f, m_world, x+ xrandom, y+yrandom, m_aliveThings );
		}
	}
	
	public void SpawnAsteroidsFromImage( String imageLocation, float pixelScale, float radius, int amount )
	{
		Pixmap spawnMap = new Pixmap(Gdx.files.internal(imageLocation));
		float halfWidth = spawnMap.getWidth() /2;
		float halfHeight = spawnMap.getHeight() /2;
		
		for( int i = spawnMap.getWidth()-1; i > 0; i--)
		{
			for( int j = spawnMap.getHeight()-1; j > 0; j-- )
			{
				Color c = new Color( spawnMap.getPixel(i, j));
				
				if( c.a == 1 )
				{
					SpawnAsteroids( (halfWidth - i)*pixelScale, (halfHeight - j)*pixelScale, radius, amount );
				}
				
			}
		}
		spawnMap.dispose();
	}

}
