#ifndef __GAMEINTERFACE_CLASS__
#define __GAMEINTERFACE_CLASS__

#include <vector>

#include "../menu/NetworkingMenu.hpp"
#include "../tile/map/GameTileMap.hpp"
#include "../view/View.hpp"

class LobbyTileMap;
class LobbyPlayer;
class Mob;
class Tile;
class Bullet;
class CollisionEvent;
class GamePlayer;

class GameInterface : public NetworkingMenu
{
	public:
		GameInterface(LobbyTileMap*, const std::vector<LobbyPlayer*>&);
		virtual ~GameInterface();
		virtual void tick() override;
		virtual void render() const override;
	protected:
		virtual GamePlayer* getLocalPlayer() const = 0;
		std::vector<Mob*> mobs;
		std::vector<Tile*> tiles;
		std::vector<Bullet*> bullets;
	private:
		// functions
		void controlLocalPlayer();
		void tickEntities();
		void tickPhysics();
		void renderMap() const;
		void renderBars() const;
		void renderEntities() const;
		GameTileMap* getGameTileMap() const;
		const View& getView() const;
		Entity* getDynamicEntity(unsigned int);
		unsigned int getDynamicEntityAmount();

		// physics/collision-system
		CollisionEvent* cutFirstEvent(std::vector<CollisionEvent*>* events);
		void updateEventsFrom(Entity* entity, std::vector<CollisionEvent*>* events, float timeLeft);
		void addEventsFrom(Entity* entity, std::vector<CollisionEvent*>* events, float timeLeft);
		void moveAllEntities(float time);
		void removeOutdatedCollisionPartners();
		void deglitchCollisionPartners();
		
		// elements
		View view;
		GameTileMap* tileMap;
};

#include "../tile/map/LobbyTileMap.hpp"
#include "../player/LobbyPlayer.hpp"
#include "../entity/Mob.hpp"
#include "../entity/Tile.hpp"
#include "../entity/Bullet.hpp"
#include "../collision/CollisionEvent.hpp"
#include "../player/GamePlayer.hpp"

#endif
