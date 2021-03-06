#include "Frame.hpp"

#include <map>
#include <assert.h>

#include <collision/CollisionDetector.hpp>
#include <avatar/LobbyAvatar.hpp>
#include <player/LobbyPlayer.hpp>
#include <network/packets/AvatarPacket.hpp>
#include <network/packets/TeamPacket.hpp>
#include <math/game/GameVector.hpp>
#include <math/game/GameRect.hpp>
#include <misc/Global.hpp>
#include <entity/DynamicEntity.hpp>
#include <entity/Entity.hpp>
#include <collision/CollisionEvent.hpp>
#include <entity/Tile.hpp>
#include <entity/Idler.hpp>
#include <entity/Mob.hpp>
#include <player/GamePlayer.hpp>
#include <tilemap/GameTileMap.hpp>
#include "FrameCloneable.hpp"
#include <game/message/Message.hpp>

static const int LOOP_LIMIT = 300;
static const float FREQ = 10.f;

Frame::Frame(LobbyTileMap* map, const std::vector<LobbyPlayer*>& lobbyPlayers)
	: tileMap(new GameTileMap(map))
{
	for (unsigned int i = 0; i < lobbyPlayers.size(); i++)
	{
		players.push_back(LobbyAvatar::get(lobbyPlayers[i]->getAvatarPacket()->getAvatarID())->createGamePlayer(
			tileMap->teamToSpawnPosition(lobbyPlayers[i]->getTeamPacket()->getTeam()), lobbyPlayers[i]));
	}
}

Frame::~Frame()
{
	deleteAndClearVector(players);
	deleteAndClearVector(idlers);
	deleteAndClearVector(mobs);
}

Frame* Frame::clone() const
{
	Frame* clone = new Frame(*this);
	std::map<FrameCloneable*, FrameCloneable*> map;
	clone->cloneMembers(&map);
	return clone;
}

void Frame::tick()
{
	tickEntities();
	tickPhysics();
}

void Frame::applyEntries(std::vector<Calendar::Entry> entries)
{
	for (auto i = entries.begin(); i != entries.end(); ++i)
	{
		Calendar::Entry entry = *i;
		players[entry.playerID]->setActions(entry.actions);
	}
}

void Frame::applyMessage(const AddIdlerMessage& m)
{
	idlers.push_back(m.getIdler());
}

void Frame::applyMessage(const DestroyMessage& m)
{
	DynamicEntity* e = m.getEntity();

	for (unsigned int i = 0; i < players.size(); i++)
	{
		if (players[i] == e)
		{
			players.erase(players.begin() + i);
			delete e;
			return;
		}
	}
	for (unsigned int i = 0; i < idlers.size(); i++)
	{
		if (idlers[i] == e)
		{
			idlers.erase(idlers.begin() + i);
			delete e;
			return;
		}
	}
	for (unsigned int i = 0; i < mobs.size(); i++)
	{
		if (mobs[i] == e)
		{
			mobs.erase(mobs.begin() + i);
			delete e;
			return;
		}
	}

	Debug::warn("Frame::applyMessage(DestroyMessage): entity not found");
}

void Frame::tickEntities()
{
	for (unsigned int i = 0; i < getDynamicEntityAmount(); i++)
	{
		Entity* entity = getDynamicEntity(i);
		entity->tick();
		entity->setChanged(true); // wichtig für tickPhysics()
	}

	pollAndProcessMessages();
}

void Frame::tickPhysics()
{
	Debug::funcOn("Frame::tickPhysics()");

	int c = 0;
	int checkCounter = 1;
	float timeLeft = global::GAME_FRAME_TIME;
	std::deque<CollisionEvent*> events;

	updateChanged(&events, timeLeft);

	while (true)
	{
		enum : char {CHECK, EVENT, END} id;
		float times[3]; /* times until frame ends */
		times[CHECK] = global::GAME_FRAME_TIME - (checkCounter * 1.f / FREQ);
		times[EVENT] = -1;
		times[END] = 0;

		if (! events.empty())
		{
			times[EVENT] = events.front()->getTimeUntilFrameEnds();
		}

		if (times[CHECK] >= times[END] && times[CHECK] >= times[EVENT])
		{
			id = CHECK;
		}
		else if (times[EVENT] >= times[END])
		{
			id = EVENT;
		}
		else
		{
			id = END;
		}

		moveAllEntities(timeLeft - times[id]); // bewege die Entities um die Zeitänderung
		timeLeft = times[id];

		switch (id)
		{
			case CHECK:
				checkCounter++;
				for (unsigned int i = 0; i < getDynamicEntityAmount(); ++i)
				{
					getDynamicEntity(i)->checkWrapperPartners();
				}
				updateChanged(&events, timeLeft);
				break;
			case END:
				Debug::funcOff("Frame::tickPhysics()");
				return;
			case EVENT:
				if (events.empty())
				{
					Debug::error("Frame::tickPhysics(): events.size() == 0");
					return;
				}
				// add Wrapper Partners
				if (Entity::areWrapperPartners(events.front()->getEntity1(), events.front()->getEntity2()))
				{
					Debug::warn("collision detected between wrapper partners:\n\t"
						+ events.front()->getEntity1()->toString()
						+ "\n\t" + events.front()->getEntity2()->toString());
				}
				else
				{
					events.front()->getEntity1()->addWrapperPartner(events.front()->getEntity2());
					events.front()->getEntity2()->addWrapperPartner(events.front()->getEntity1());
				}

				events.front()->getEntity1()->setChanged(true);
				events.front()->getEntity2()->setChanged(true);

				removeEventsBetween(events.front()->getEntity1(), events.front()->getEntity2(), &events);
		}

		if (c++ > LOOP_LIMIT)
		{
			Debug::error("Frame::tickPhysics(): infinite loop");
			break;
		}
	}
}

void Frame::pollAndProcessMessages()
{
	// messages
	std::vector<Message*> messages;
	for (unsigned int i = 0; i < getDynamicEntityAmount(); i++)
	{
		DynamicEntity* e = getDynamicEntity(i);
		e->pollSubMessages();
		while (e->hasMessage())
		{
			messages.push_back(e->pollMessage());
		}
	}
	for (Message* m : messages)
	{
		processMessage(m);
	}
}

void Frame::processMessage(Message* m)
{
	// apply to Listeners
	m->isBroadcast();
	if (m->isBroadcast())
	{
		for (unsigned int i = 0; i < getDynamicEntityAmount(); i++)
		{
			DynamicEntity* e = getDynamicEntity(i);
			e->broadcastMessage(m);
			m->applyTo(e);
		}
	}

	m->applyTo(this);

	delete m;
}

void Frame::moveAllEntities(float time)
{
	Debug::func("Frame::moveAllEntities(" + Converter::floatToString(time) + ")");
	if (time <= 0)
	{
		if (time < 0) Debug::error("Frame::moveAllEntities(" + Converter::floatToString(time) + "): arg is negative");
		return;
	}

	for (unsigned int i = 0; i < getDynamicEntityAmount(); i++)
	{
		getDynamicEntity(i)->move(time);
	}
}

void Frame::updateChanged(std::deque<CollisionEvent*>* events, float timeLeft)
{
	if (timeLeft < 0)
	{
		Debug::error("Frame::updateChanged(..., " + Converter::floatToString(timeLeft) + "): timeLeft is negative");
		return;
	}

	for (unsigned int i = 0; i < getDynamicEntityAmount(); i++)
	{
		Entity* e1 = getDynamicEntity(i);
		for (unsigned int j = i+1; j < getDynamicEntityAmount(); j++)
		{
			Entity* e2 = getDynamicEntity(j);
			if (Entity::areWrapperPartners(e1, e2)) continue;
			if (e1->hasChanged() || e2->hasChanged())
			{
				update(e1, e2, events, timeLeft);
			}
		}
		if (e1->hasChanged())
		{
			std::vector<Tile*> intersectionTiles = getGameTileMap()->getIntersectionTiles(e1->getWrapper(timeLeft));
			for (auto iter = intersectionTiles.begin(); iter != intersectionTiles.end(); ++iter)
			{
				if (Entity::areWrapperPartners(e1, *iter)) continue;
				update(e1, *iter, events, timeLeft);
			}
		}
	}

	for (unsigned int i = 0; i < getDynamicEntityAmount(); i++)
	{
		getDynamicEntity(i)->setChanged(false);
	}
}

void Frame::update(Entity* e1, Entity* e2, std::deque<CollisionEvent*>* events, float timeLeft)
{
	if (Entity::areWrapperPartners(e1, e2))
	{
		Debug::warn(std::string(__PRETTY_FUNCTION__) + " called for Wrapper partners:\n\t" + e1->toString() + "\n\t" + e2->toString());
	}
	else
	{
		removeEventsBetween(e1, e2, events);
		addEventsBetween(e1, e2, events, timeLeft);
	}
}

void Frame::addEventsBetween(Entity* e1, Entity* e2, std::deque<CollisionEvent*>* events, float timeLeft)
{
	if (e1->getCollisionPriority(e2) + e2->getCollisionPriority(e1) > 0)
	{
		CollisionDetector::addCollisionsBetween(e1, e2, events, timeLeft);
	}
}

void Frame::removeEventsBetween(Entity* e1, Entity* e2, std::deque<CollisionEvent*>* events)
{
	for (unsigned int i = 0; i < events->size(); i++)
	{
		Entity* f1 = events->at(i)->getEntity1();
		Entity* f2 = events->at(i)->getEntity2();
		if ((f1 == e1 and f2 == e2) or (f1 == e2 and f2 == e1))
		{
			CollisionEvent* event = events->at(i);
			events->erase(events->begin() + i--);
			delete event;
		}
	}
}

DynamicEntity* Frame::getDynamicEntity(unsigned int id)
{
	if (id >= players.size())
	{
		id -= players.size();
		if (id >= mobs.size())
		{
			id -= mobs.size();
			if (id >= idlers.size())
			{
				Debug::warn("Frame::getDynamicEntity(" + Converter::intToString(id) + "): id too high");
			}
			else
			{
				return idlers[id];
			}
		}
		else
		{
			return mobs[id];
		}
	}
	else
	{
		return players[id];
	}
	Debug::error("Frame::getDynamicEntity(" + Converter::intToString(id) + "): id kinda out of range");
	return nullptr;
}

unsigned int Frame::getDynamicEntityAmount() const
{
	return mobs.size() + players.size() + idlers.size() /*+ cosmetics.size() */;
}

const GameTileMap* Frame::getGameTileMap() const
{
	return tileMap;
}
