#ifndef __PHYSICALENTITY_CLASS__
#define __PHYSICALENTITY_CLASS__

class CompressBuffer;

#include "DynamicEntity.hpp"
#include <misc/compress/Compressable.hpp>

class ImpactfulDynamicEntity : public DynamicEntity, public Compressable
{
	public:
		ImpactfulDynamicEntity(Body*);
		ImpactfulDynamicEntity(CompressBuffer*);
		virtual ~ImpactfulDynamicEntity() {}
};

#include <misc/compress/CompressBuffer.hpp>

#endif