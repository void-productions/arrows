#ifndef __DYNAMICENTITY_CLASS__
#define __DYNAMICENTITY_CLASS__

#include "Entity.hpp"
#include <vector>

class DynamicEntity : public Entity
{
	public:
		DynamicEntity(const EntityGivethrough&);
		virtual ~DynamicEntity() {}

		virtual bool hasToBeCloned() const override;

		void checkWrapperPartners();

		virtual bool hasChanged() const override;
		virtual void setChanged(bool b) override;

		virtual void addCollisionPartner(Entity* e) override;
		virtual void addWrapperPartner(Entity* e) override;
		virtual void removeCollisionPartner(Entity* e) override;
		virtual void removeWrapperPartner(Entity* e) override;
		virtual const std::vector<Entity*>& getCollisionPartners() const override;
		virtual const std::vector<Entity*>& getWrapperPartners() const override;
		virtual bool hasCollisionPartner(Entity* e) const override;
		virtual bool hasWrapperPartner(Entity* e) const override;
	private:
		bool changed;
		std::vector<Entity*> collisionPartners;
		std::vector<Entity*> wrapperPartners;

	friend class ClientGameInterface; // needed to update (w/c)partners on GameUpdatePacket
};

#endif
