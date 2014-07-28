package com.me.mygdxgame.Entities;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class VisualNovelStyleMessage extends ChangeListener
{
	GameCharacter m_char;
	String m_message;
	VisualNovelImageState m_state;
	boolean m_buttonPressed = false;
	int m_charactersToDisplay = 0;
	boolean m_started = false;
	
	public static Dialog m_messageDialog;
	public static Label m_textArea;
	public static Button m_button;
	//public static Image m_image;
	public static Map< GameCharacter, VisualNovelImageState>  m_characterImages = new HashMap<GameCharacter, VisualNovelImageState >();
	
	public VisualNovelStyleMessage( GameCharacter character, VisualNovelImageState state, String message )
	{
		m_char = character;
		m_message = message;
		m_state = state;
	}
	
	
	public boolean Display()
	{
		if( !m_started )
		{
			m_started = true;
			m_button.addListener(this);
			
			// Do the image thingy
			if( m_state.m_characterImage.equals("Remove") )
			{
				m_characterImages.remove(m_char);
			}
			else if( !m_state.m_characterImage.equals("None"))
			{
				m_characterImages.put( m_char, m_state);
			}
			
			for (Map.Entry<GameCharacter, VisualNovelImageState> entry : m_characterImages.entrySet())
			{
			    // Draw the damn things
			}


			
		}
		
		if(m_message != null)
		{
			//set the static members to stuff decrement the time to display. 
			m_textArea.setText(m_message.subSequence(0, m_charactersToDisplay));
			
			if( m_charactersToDisplay < (m_message.length() ) )
			{
				m_charactersToDisplay++;
			}
		}
		else
		{
			m_buttonPressed = true;
		}
		

		return m_buttonPressed;
	}
	
	public void Reset()
	{
		m_buttonPressed = false;
		m_charactersToDisplay = 0;
		m_started = false;
	}


	@Override
	public void changed(ChangeEvent event, Actor actor)
	{
		if( m_charactersToDisplay < (m_message.length() ) )
		{
			m_charactersToDisplay = m_message.length();
		}
		else
		{
			m_buttonPressed = true;
			m_button.removeListener(this);
		}
		
	}

}
