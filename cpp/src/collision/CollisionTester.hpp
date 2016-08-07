#ifndef __COLLISIONTESTER_CLASS__
#define __COLLISIONTESTER_CLASS__

class Entity;
class RectBody;

class CollisionTester
{
	public:
		static bool isColliding(Entity*, Entity*, float borderAddition=0);
	private:
		static bool isCollidingEvenRects(const RectBody*, const RectBody*, float borderAddition);
};

#include "../entity/Entity.hpp"
#include "RectBody.hpp"

#endif
