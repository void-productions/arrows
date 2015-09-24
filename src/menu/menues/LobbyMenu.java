package menu.menues;

import java.util.LinkedList;
import java.net.InetAddress;

import core.Main;
import menu.NetworkingMenu;
import menu.components.*;
import game.Team;
import game.avatar.Avatar;
import game.skill.Skill;
import game.item.Item;
import graphics.ImageFile;
import misc.Debug;
import misc.math.Rect;
import network.lobby.LobbyPlayer;
import network.lobby.packets.user.LockUserPacket;
import network.Packet;

public abstract class LobbyMenu extends NetworkingMenu
{
	public static final int TEAM_PHASE = 0;
	public static final int AVATAR_PHASE = 1;
	public static final int SKILL_PHASE = 2;
	public static final int ITEM_PHASE = 3;
 
	private int phase;
	protected Button lockButton; // Verweiß auf den NextStep/LockIn Button
	private LinkedList<LobbyPlayer> players; // Bluemi: protected -> private; added setter
	private TeamListPanel teamListPanel; // Nicht zwingend nötig, nur einfacherer Zugriff, da nicht über index

	public LobbyMenu()
	{
		phase = TEAM_PHASE;
		players = new LinkedList<LobbyPlayer>();

		getComponents().add(new Label(this, new Rect(300, 10, 100, 20), "Lobby"));

		teamListPanel = new TeamListPanel(this, new Rect(100, 100, 600, 600));
		getComponents().add(teamListPanel);

		lockButton = new Button(this, new Rect(1000, 600, 200, 60), getLockButtonCaption())
		{
			@Override public void onClick(int mouseButton)
			{
				lockPressed();
			}
		}; // Lock-Button
		getComponents().add(lockButton);

		getComponents().add(new Button(this, new Rect(1000, 100, 200, 140), "Map Placeholder")
		{
			@Override public void onClick(int mouseButton)
			{
				mapPressed();
			}
		}); // Map Placeholder
		getComponents().add(new Button(this, new Rect(20, 500, 20, 20), "Disconnect")
		{
			@Override public void onClick(int mouseButton)
			{
				disconnectPressed();	
			}
		});
	}

	// Wird von unterklassen aufgerufen, nachdem ein Spieler bearbeitet wurde
	protected final void updatePlayerIcons()
	{
		teamListPanel.update();
	}

	public abstract void handlePacket(Packet packet, InetAddress ip);
	public abstract void lockPressed();
	public abstract void teamPressed(Team team);
	public abstract void avatarPressed(Avatar avatar);
	public abstract void skillPressed(Skill[] skills);
	public abstract void itemPressed(Item[] items);
	public abstract void mapPressed();
	public abstract void disconnectPressed();
	public abstract LobbyPlayer getLocalPlayer();

	public int getPhase() { return phase; }
	public LinkedList<LobbyPlayer> getPlayers() { return players; } // public damit TeamListPanel darauf zugreifen kann

	protected void setPlayers(LinkedList<LobbyPlayer> players) 
	{ 
		this.players = players; 
	}

	private String getLockButtonCaption()
	{
		if (this instanceof ClientLobbyMenu)
		{
			return "Ready";
		}
		else if (this instanceof ServerLobbyMenu)
		{
			return "Next Phase";
		}
		Debug.error("LobbyMenu.getLockButtonCaption: wrong lobby-menu-subclass");
		return null;
	}

	protected void nextPhase()
	{
		phase++;
		switch (getPhase())
		{
			case AVATAR_PHASE:
			{
				ImageFile.loadFromTo(ImageFile.ARCHER_ICON, ImageFile.ARCHER_ICON);
				break;	
			}
		}
		unlockAll();
	}

	protected void unlockAll()
	{
		for (LobbyPlayer player : getPlayers())
		{
			player.applyUserPacket(new LockUserPacket(false));
		}
	}

	protected void removePlayer(LobbyPlayer player)
	{
		getPlayers().remove(player);
	}
}
