package com.me.mygdxgame.Entities;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class GameCharacter
{

	String m_characterName;
	Map  m_characterImages = new HashMap();
	
	
	public GameCharacter( String name )
	{
		m_characterName = name;
		LoadImageFiles( "data/"+name );
	}
	
	private void LoadImageFiles(String directory) 
	{
		FileHandle dirHandle;
		
		if (Gdx.app.getType() == ApplicationType.Android)
		{
			  dirHandle = Gdx.files.internal(directory);			
		} 
		else 
		{
			  // ApplicationType.Desktop ..
			  dirHandle = Gdx.files.internal("./bin/"+ directory);			
		}
		
		for (FileHandle entry: dirHandle.list(".png")) 
		{
			Image i = new Image( new Texture( entry ) );
			m_characterImages.put( entry.nameWithoutExtension(), i );
		}
	}

	public Image GetImage( String charImageString )
	{
	    //try to find it
		Image i = (Image) m_characterImages.values().toArray()[0];
		// if you can't use the default image
		if(m_characterImages.containsKey( charImageString ) )
		{
			i = (Image) m_characterImages.get( charImageString );
		}
		
		return new Image();
	}

}
