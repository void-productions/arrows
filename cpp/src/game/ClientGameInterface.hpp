#ifndef __CLIENTGAMEINTERFACE_CLASS__
#define __CLIENTGAMEINTERFACE_CLASS__

#include "GameInterface.hpp"

class LobbyPlayer;

class ClientGameInterface : public GameInterface
{
	public:
		ClientGameInterface(LobbyTileMap*, const std::vector<LobbyPlayer*>&, int playerID, sf::IpAddress*);
		virtual ~ClientGameInterface();
		void handlePacket(Packet*, sf::IpAddress*) override;
	protected:
		virtual GamePlayer* getLocalPlayer() const override;
		virtual void updateOtherGamers() override;
	private:
		void applyGameUpdate(const std::vector<GamePlayer*>&, const std::vector<Mob*>&, const std::vector<Idler*>&);

		sf::IpAddress* serverIP;
		unsigned int localPlayerID;
};

#include <player/LobbyPlayer.hpp>

#endif
