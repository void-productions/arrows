package core.menu.components;

import java.util.LinkedList;

import core.menu.Menu;
import core.menu.menues.LobbyMenu;
import core.menu.ComponentContainer;
import core.menu.components.Button;
import core.menu.components.Panel;
import core.menu.components.PlayerPanel;
import game.Team;
import misc.math.Rect;
import network.lobby.LobbyPlayer;;

public class TeamPanel extends Panel
{
	private Team team;

	public TeamPanel(ComponentContainer parent, Rect rect, Team team)
	{
		super(parent, rect);
		this.team = team; // Jedes TeamPanel representiert ein Team
		update(null);
	}

	public void update(LinkedList<LobbyPlayer> players)
	{
		getComponents().clear();
		getComponents().add(new Button(this, new Rect(20, 20, 100, 30), "Team " + team.getName())
		{		
			@Override public void onClick(int mouseButton)
			{
				((LobbyMenu) getParentMenu()).teamPressed(team); // Übergabe an LobbyMenu, dass wir Team wechseln
			}					
		});
		if (players == null)
		{
			return;
		}
		int i = 0;
		for (LobbyPlayer player : players)
		{
			if (player.getTeam().equals(team))
			{
				getComponents().add(new PlayerPanel(this, new Rect(getX() + 5 + i*65, 55, 60, 60), player));
				i++;
			}
		}

	}
}