package com.me.mygdxgame.Screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.me.mygdxgame.Entities.PlayerEntity;


public class TargetButtonListener extends ChangeListener
{
	PlayerEntity m_player;
	public TargetButtonListener(PlayerEntity p)
	{
		m_player = p;
	}

	@Override
	public void changed(ChangeEvent event, Actor actor)
	{
		EnemyIndicatorButton eib = ( EnemyIndicatorButton ) actor;
		
		m_player.SetPlayerTarget(eib.m_trackedEntity);
	}
}
