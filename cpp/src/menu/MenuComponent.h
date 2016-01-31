#ifndef __MENUCOMPONENT_CLASS__
#define __MENUCOMPONENT_CLASS__

#include "../math/pixel/PixelRect.h"

class ComponentContainer;
class PixelPosition;

class MenuComponent
{
	public:
		MenuComponent(ComponentContainer* parent, const PixelRect& rect);
		~MenuComponent();
		virtual void tick();
		virtual void render() const = 0;
		virtual void onClick(int mouseButton);
		virtual void onMouseEnter(const PixelVector&);
		virtual void onMouseMove(const PixelVector&);
		virtual void onMouseExit(const PixelVector&);
		virtual void onTextEntered(char key);
		virtual PixelVector getOffset() const;
		virtual MenuComponent* getHoveredComponentRecursively() const; // overwritten by panels etcs..
		PixelRect getRect() const;
	protected:
		ComponentContainer* getParent() const;
	private:
		PixelRect rect;
		ComponentContainer* parent;
};

#include "ComponentContainer.h"
#include "../math/pixel/PixelVector.h"

#endif
