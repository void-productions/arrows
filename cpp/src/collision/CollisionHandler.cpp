#include "CollisionHandler.hpp"

#include "../misc/Converter.hpp"
#include "../misc/Debug.hpp"
#include "PhysicsHandler.hpp"

/*
	CollisionPoints
	EscapeVector
	Status
	CollisionType
	PartnerHandling (onEvent...)
	if (enterEvent && solid)
	{
		PhysicsHandler::handleSolidCollision(Entities, EscapeVectoren, CollisionPoints)
	}
*/

void CollisionHandler::handleCollisionEvent(CollisionEvent* ev)
{
	Debug::funcOn("CollisionHandler::handleCollisionEvent: " + ev->toString());
	// Calculate CollisionPoints
	std::vector<GameVector> collisionPoints = getCollisionPoints(ev);
	for (GameVector v : collisionPoints)
	{
		Debug::test("collisionPoints=" + v.toString());
	}

	// Calculate EscapeVectors
	std::vector<GameVector> escapeVectors1 = getEscapeVectors(ev->getEntity1(), collisionPoints);
	for (GameVector v : escapeVectors1)
	{
		Debug::test("escapeVector1=" + v.toString());
	}
	std::vector<GameVector> escapeVectors2 = getEscapeVectors(ev->getEntity2(), collisionPoints);
	for (GameVector v : escapeVectors2)
	{
		Debug::test("escapeVector2=" + v.toString());
	}

	switch (getCollisionStatus(ev, collisionPoints, escapeVectors1)) // switch between the new collision status
	{
		case CollisionStatus::IN:
		{
			Debug::test("in");
			// add partner

			CollisionType type = Entity::getCollisionTypeBetween(ev->getEntity1(), ev->getEntity2());
			if (type == CollisionType::SOLID)
			{
				PhysicsHandler::handlePhysics(ev->getEntity1(), ev->getEntity2(), collisionPoints, escapeVectors1, escapeVectors2);
			}
			break;
		}
		case CollisionStatus::OUT:
		{
			Debug::test("out");
			// remove partner
			break;
		}
		case CollisionStatus::BORDER:
		{
			Debug::test("border");
			break;
		}
		default:
		{
			Debug::error("CollisionHandler::handleCollisionEvent(): unknown CollisionStatus");
		}
	}

	Debug::funcOff("CollisionHandler::handleCollisionEvent: " + ev->toString());
}

std::vector<GameVector> CollisionHandler::getCollisionPoints(CollisionEvent* event)
{
	std::vector<GameVector> result;
	Entity *e1 = event->getEntity1(), *e2 = event->getEntity2();
	if (e1->getBody()->getBodyType() == BodyType::RECT && e2->getBody()->getBodyType() == BodyType::RECT)
	{
		const RectBody *b1 = dynamic_cast<const RectBody*>(e1->getBody());
		const RectBody *b2 = dynamic_cast<const RectBody*>(e2->getBody());
		if (b1->isEven() && b2->isEven())
		{
			if (b1->getLeft() == b2->getRight())
			{
				Debug::test("1");
				result.push_back(GameVector(b1->getLeft(), std::max(b1->getTop(), b2->getTop())));
				result.push_back(GameVector(b1->getLeft(), std::min(b1->getBot(), b2->getBot())));
			}
			else if (b2->getLeft() == b1->getRight())
			{
				Debug::test("2");
				result.push_back(GameVector(b2->getLeft(), std::max(b1->getTop(), b2->getTop())));
				result.push_back(GameVector(b2->getLeft(), std::min(b1->getBot(), b2->getBot())));
			}

			if (b1->getTop() == b2->getBot())
			{
				Debug::test("3");
				result.push_back(GameVector(std::max(b1->getLeft(), b2->getLeft()), b1->getTop()));
				result.push_back(GameVector(std::min(b1->getRight(), b2->getRight()), b1->getTop()));
			}
			else if (b2->getTop() == b1->getBot())
			{
				result.push_back(GameVector(std::max(b1->getLeft(), b2->getLeft()), b2->getTop()));
				result.push_back(GameVector(std::min(b1->getRight(), b2->getRight()), b2->getTop()));
				Debug::test("4, e1=" + event->getEntity1()->toString() + ", e2=" + event->getEntity2()->toString());
			}

			if (result.size() == 0)
			{
				Debug::warn("CollisionHandler::getCollisionPoints(): result.size() == null, on b1=" + b1->getPosition().toString() + " b2=" + b2->getPosition().toString());
			}
		}
	}
	//Debug::warn("CollisionHandler::getCollisionPoints(): TODO");
	return result;
}

std::vector<GameVector> CollisionHandler::getEscapeVectors(Entity* e, const std::vector<GameVector>& collisionPoints)
{
	if (collisionPoints.size() == 2)
	{
		GameVector ab = collisionPoints[1] - collisionPoints[0]; // Vector vom einen zum anderen CollisionPoint
		GameVector am = e->getBody()->getPosition() - collisionPoints[0]; // Vector von a nach Mittelpunkt der Entity
		GameVector o = GameVector(ab.getY(), -ab.getX()); // gedrehter ab Vector um 90°
		GameVector f = o * GameVector::getScalarProduct(o, am);
		Debug::test("a=" + collisionPoints[0].toString());
		Debug::test("b=" + collisionPoints[1].toString());
		Debug::test("ab=" + ab.toString());
		Debug::test("am=" + am.toString());
		Debug::test("o=" + o.toString());
		Debug::test("escapevec=" + f.toString());

		std::vector<GameVector> result;
		result.push_back(f);
		return result;
	}
	else
	{
		Debug::warn("CollisionHandler::getEscapeVectors(): collisionPoints.size() != 2");
		return std::vector<GameVector>();
	}
}

CollisionStatus CollisionHandler::getCollisionStatus(CollisionEvent* ev, const std::vector<GameVector>& collisionPoints, const std::vector<GameVector>& escapeVectors)
{
	if (collisionPoints.size() == 0)
	{
		Debug::error("CollisionHandler::getCollisionStatus(): collisionPoints.size == 0");
		return CollisionStatus::IN;
	}

	GameVector speedAt(ev->getEntity1()->getBody()->getSpeedAt(collisionPoints[0]));
	for (unsigned int i = 0; i < escapeVectors.size(); i++)
	{
		float angle = GameVector::getScalarProduct(speedAt, escapeVectors[i]);
		if (angle > 0)
		{
			return CollisionStatus::OUT;
		}
		else if (angle == 0)
		{
			return CollisionStatus::BORDER;
		}
		else if (angle < 0)
		{
			// Weiter prüfen
		}
		else
		{
			Debug::warn("CollisionHandler::getCollisionStatus(): WTF, angle=" + Converter::floatToString(angle));
		}
	}

	return CollisionStatus::IN;
}
