package core.menu.menues;

import java.net.InetAddress;

import core.Main;
import game.Team;
import misc.Debug;
import network.Packet;
import network.lobby.LobbyPlayer;
import network.lobby.packets.user.*;
import network.lobby.packets.*;

public class ClientLobbyMenu extends LobbyMenu // menu of client when in lobby
{
	private LobbyPlayer localPlayer; // Verweiß auf den eigenen LobbyPlayer
	private InetAddress serverIP; // Speichert die IP adresse des Servers

	public ClientLobbyMenu(String serverIPString)
	{
		try
		{
			this.serverIP = InetAddress.getByName(serverIPString); // Speichert die Internet-Adresse vom Server; Wird von JoinServerMenu übergeben
		} catch (Exception e) { Debug.log("ClientLobbyMenu not a valid ip address: " + serverIPString); }
	}

	// Wird ausgeführt von Main.networkDevice.receive()
	@Override public void handlePacket(Packet packet, InetAddress ip)
	{
		if (packet instanceof LobbyPlayersPacket) // Wenn es sich bei dem Packet um die Liste aller Player handelt, dann
		{
			setPlayers(((LobbyPlayersPacket) packet).getPlayers()); // setzte die komplette Liste neu
			updatePlayerIcons();
		}
		else if (packet instanceof MapPacket)
		{
			if (getPhase() == TEAM_PHASE)
			{
				// TODO apply
				unlockAll();
			}
			else
			{
				Debug.quit("client> got mappacket out of teamphase");
			}
		}
		else if (packet instanceof UserPacketWithID) // Wenn es sich bei dem Packet um UserPacketWithID handelt, dann
		{
			UserPacket userPacket = ((UserPacketWithID) packet).getUserPacket(); // entpacken des UserPackets aus dem UserpacketWithID

			if (userPacket instanceof LockUserPacket) // wenn du ein lock-packet erhalten hast
			{
				if (isServerPlayer(getPlayer(packet)))
				{
					nextPhase();
				}
				else
				{
					getPlayer(packet).applyUserPacket(userPacket);
				}
			}
			else if (userPacket instanceof DisconnectUserPacket)
			{
				getPlayers().remove(getPlayer(packet));
				unlockAll();
				updatePlayerIcons();
			}
			else // falls nicht.
			{
				switch (getPhase())
				{
					case TEAM_PHASE: // Falls man in der Team Phase ist
						if (userPacket instanceof TeamUserPacket) // Wenn Spieler das Team wechselt
						{
							getPlayer(packet).applyUserPacket(userPacket); // setzte das Team des jeweilgen Spielers
							updatePlayerIcons();
							unlockAll();
						}
						else if (userPacket instanceof LoginUserPacket) // Wenn sich ein Spieler einloggt
						{
							LobbyPlayer player = new LobbyPlayer((LoginUserPacket) userPacket); // Neuen LobbyPlayer erstellen
							getPlayers().add(player); // Zur Liste der LobbyPlayer hinzufügen
							if (localPlayer == null) // Da das erste LoginUserPacket, das man bekommt, das eigene ist
							{
								localPlayer = player; // Diesen LobbyPlayer als eigenen abspeichern
							}
							unlockAll();
							updatePlayerIcons();
						}
						else
						{
							Debug.quit("Client can't accept packet in team phase"); // Alle anderen Packete werden in dieser Phase nicht angenommen
						}
						break;
					case AVATAR_PHASE: // Wenn man sich in der AvatarPhase befindet
						if (userPacket instanceof AvatarUserPacket) // Falls ein Spieler seinen Avatar wechselt
						{
							getPlayer(packet).applyUserPacket(userPacket); // Des jeweiligen Spielers Avatar wird in den LobbyPlayern geändert
						}
						else
						{
							Debug.quit("Client can't accept packet in avatar phase"); // Alle anderen Packete werden in dieser Phase nicht angenommen
						}
						break;
					case SKILL_PHASE: // Wenn man in der AttributPhase ist (Items/Skills)
						if (userPacket instanceof SkillUserPacket) // Wenn ein Spieler ein Skill/Item wechselt
						{
							getPlayer(packet).applyUserPacket(userPacket); // In der LobbyPlayer Liste übernehmen
						}
						else
						{
							Debug.quit("Client can't accept packet in attribute phase"); // Alle anderen Packete werden in dieser Phase nicht angenommen
						}
						break;
					case ITEM_PHASE:
						break;
					default:
						Debug.quit("ClientLobbyMenu.handlePacket(...): wrong phase"); // Da ist was ganz komisch gelaufen; Ungültige Phase
						break;
				}
			}
		}
		else
		{
			Debug.quit("Client received wrong packet"); // packets, die nicht vom Typ UserPacketWithID sind werden nicht angenommen
		}
	}

	// Wird aufgerufen, wenn man auf die Map clickt
	@Override public void mapPressed()
	{
		// TODO: Map anzeigen
	}

	// Wird aufgerufen, wenn man sein Team wechselt
	@Override public void lockPressed()
	{
		sendToServer(new LockUserPacket(!getLocalPlayer().isLocked())); // sendet an den Server, dass man den lock-button betätigt hat
	}

	@Override public void disconnectPressed()
	{
		sendToServer(new DisconnectUserPacket());
		Main.getMenues().remove(Main.getMenues().getLast());
	}

	// Wird aufgerufen, wenn man sein Team wechselt
	@Override public void teamPressed(Team team)
	{
		if (!getLocalPlayer().isLocked() && !getLocalPlayer().getTeam().equals(team))
		{
			sendToServer(new TeamUserPacket(team)); // An die anderen Spieler senden
		}
	}


	@Override public void avatarPressed(AvatarInfo avatar) {}
	@Override public void skillPressed(SkillInfo[] skills) {}
	@Override public void itemPressed(ItemInfo[] items) {}

	@Override protected LobbyPlayer getLocalPlayer() { return localPlayer; }

	// returnt den Spieler, der den das übergebene Packet anspricht
	private LobbyPlayer getPlayer(Packet packet)	
	{
		UserPacketWithID userPacketWithID = (UserPacketWithID) packet;
		return getPlayers().get(userPacketWithID.getID());
	}

	// Sendet das Packet zum Server
	private void sendToServer(Packet packet)
	{
		send(packet, serverIP);
	}

	private boolean isServerPlayer(LobbyPlayer player)
	{
		return player == getServerPlayer();
	}

	private LobbyPlayer getServerPlayer()
	{
		return getPlayers().get(0);
	}
}
