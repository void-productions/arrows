#ifndef __POPUPMENU_CLASS__
#define __POPUPMENU_CLASS__

#include <menu/Menu.hpp>
#include <string>

class PopupMenu : public Menu
{
	public:
		PopupMenu(const std::string&);
	private:
		std::string text;
};

#endif
