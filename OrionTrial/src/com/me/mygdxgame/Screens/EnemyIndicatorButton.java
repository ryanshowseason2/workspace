package com.me.mygdxgame.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.me.mygdxgame.Entities.ViewedCollidable;

public class EnemyIndicatorButton extends Button
{
	ViewedCollidable m_trackedEntity;
	
	public EnemyIndicatorButton( ViewedCollidable trackedEntity )
	{
		super( new Image( new TextureRegion( new Texture(Gdx.files.internal("data/unselected.png")))), new Skin(Gdx.files.internal("data/uiskin.json")) );
		pad(10);
		m_trackedEntity = trackedEntity;
	}
}
