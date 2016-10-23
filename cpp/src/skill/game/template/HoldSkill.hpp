#ifndef __HOLDSKILL_CLASS__
#define __HOLDSKILL_CLASS__

#include <skill/Skill.hpp>

class HoldSkill : public Skill
{
	public:
		HoldSkill(Mob*);
		virtual void tick() override;
	protected:
		virtual float getReduction();
};

#endif