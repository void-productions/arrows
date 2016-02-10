#ifndef __PLAYERPROPERTYICON_CLASS__
#define __PLAYERPROPERTYICON_CLASS__

#include "Icon.hpp"

class PlayerProperty;

class PlayerPropertyIcon : public Icon
{
	public:
		PlayerPropertyIcon(ComponentContainer*, const PixelRect&, PlayerProperty*);
		static PixelVector getSize();
		void setPlayerProperty(PlayerProperty*);
		virtual TextureID getTextureID() const override;
		PlayerProperty* getPlayerProperty() const;
	private:
		PlayerProperty* property;
};

#include "../../player/property/PlayerProperty.hpp"

#endif
