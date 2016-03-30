#ifndef __CLIENTGAMEINTERFACE_CLASS__
#define __CLIENTGAMEINTERFACE_CLASS__

#include "GameInterface.hpp"

class LobbyPlayer;

class ClientGameInterface : public GameInterface
{
	public:
		ClientGameInterface(LobbyTileMap*, const std::vector<LobbyPlayer*>&, int, sf::IpAddress*);
		virtual ~ClientGameInterface();
		void handlePacket(Packet*, sf::IpAddress*) override;
	private:
		sf::IpAddress* serverIP;
		int localPlayerID;
};

#include "../player/LobbyPlayer.hpp"

#endif