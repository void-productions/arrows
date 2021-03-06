#ifndef __SHAPE_CLASS__
#define __SHAPE_CLASS__

#include <misc/compress/Compressable.hpp>
#include <game/frame/FrameCloneable.hpp>
#include <string>

class Entity;
class CompressBuffer;
class GameRect;
class GameVector;

$$abstract$$
class Shape : public Compressable, public FrameCloneable
{
	public:
		Shape(Entity*);
		Shape(CompressBuffer*);

		virtual ~Shape() {}

		virtual std::string getCompressString() const override;

		virtual GameRect getWrapper(float) const = 0;
		virtual bool isCollidingPoint(const GameVector&) const = 0;
		virtual float getLeftest() const = 0;
		virtual float getRightest() const = 0;
		virtual float getToppest() const = 0;
		virtual float getBottest() const = 0;
		virtual GameVector getSpeedAt(const GameVector&) const = 0;
		virtual GameRect getRenderGameRect() const = 0;

		virtual void reactToCollision_solid(const float, const GameVector&, const GameVector&, float) = 0;
		virtual void reactToCollision_sticky(const float, const GameVector&, const GameVector&);
	protected:
		Entity* $$clone$$ entity $!clone$$;

	friend class ImpactfulDynamicEntity;
};
$!abstract$$

#endif
