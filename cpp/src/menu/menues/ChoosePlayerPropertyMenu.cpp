#include "ChoosePlayerPropertyMenu.hpp"

#include <misc/Global.hpp>
#include <core/Screen.hpp>
#include <menu/MenuList.hpp>
#include <core/Main.hpp>
#include <menu/components/BackButton.hpp>
#include <menu/components/PlayerPropertyIcon.hpp>
#include <menu/menues/LobbyMenu.hpp>
#include <playerproperty/PlayerProperty.hpp>
#include <network/packets/PlayerPropertyPacket.hpp>

class SlotIcon : public PlayerPropertyIcon
{
	public:
		SlotIcon(ChoosePlayerPropertyMenu* c, PlayerProperty* p, const PixelRect& r) : PlayerPropertyIcon(c, r, p) {}
		virtual void onClick(int mouseButton) override
		{
			setPlayerProperty(nullptr);
		}
};

class ChooseIcon : public PlayerPropertyIcon
{
	public:
		ChooseIcon(ChoosePlayerPropertyMenu* c, const PixelRect& r, PlayerProperty* p) : PlayerPropertyIcon(c, r, p), menu(c) {}
		virtual void onClick(int mouseButton) override
		{
			menu->chooseIconPressed(getPlayerProperty());
		}
	private:
		ChoosePlayerPropertyMenu* menu;
};

class OkButton : public Button
{
	public:
		OkButton(ChoosePlayerPropertyMenu* c, const PixelRect& r, const std::string& s) : Button(c, r, s), menu(c) {}
		virtual void onPress() override
		{
			menu->okPressed();
		}
	private:
		ChoosePlayerPropertyMenu* menu;
	
};

ChoosePlayerPropertyMenu::ChoosePlayerPropertyMenu(LobbyMenu* lobbyMenu, PlayerPropertyPacket* packet, const std::vector<PlayerProperty*>& props)
	: chooseProperties(props)
{
	if (lobbyMenu == nullptr) Debug::error("ChoosePlayerPropertyMenu::ChoosePlayerPropertyMenu(): lobbyMenu == nullptr");
	if (packet == nullptr) Debug::error("ChoosePlayerPropertyMenu::ChoosePlayerPropertyMenu(): packet == nullptr");

	lobby = lobbyMenu;
	slotPacket = packet; // copy-constructor of packet already used when this constructor is called

	// Slot Icons
	for (unsigned int i = 0; i < slotPacket->getPlayerProperties().size(); i++)
	{
		PlayerPropertyIcon* icon = new SlotIcon(this, slotPacket->getPlayerProperties()[i], PixelRect(60 + i * (PlayerPropertyIcon::getSize().x + 15), 40, PlayerPropertyIcon::getSize().x, PlayerPropertyIcon::getSize().y));
		addComponent(icon);
		slotIcons.push_back(icon);
	}

	// Choose Icons
	for (unsigned int i = 0; i < chooseProperties.size(); i++)
	{
		PlayerPropertyIcon* icon = new ChooseIcon(this, PixelRect(40 + i * (PlayerPropertyIcon::getSize().x + 5), 140, PlayerPropertyIcon::getSize().x, PlayerPropertyIcon::getSize().y), chooseProperties[i]);
		addComponent(icon);
	}

	// Ok Button
	addComponent(new OkButton(this, PixelRect(100, Screen::getSize().y-100, 30, 60), "Ok"));

	// Back Button
	addComponent(new BackButton(this, PixelVector(300, Screen::getSize().y-100)));
}

ChoosePlayerPropertyMenu::~ChoosePlayerPropertyMenu()
{
	deleteAndNullptr(slotPacket);
}

void ChoosePlayerPropertyMenu::tick()
{
	Menu::tick();
	lobby->tick(); // so that lobby networking doesn't stop
}

PlayerPropertyIcon* ChoosePlayerPropertyMenu::getFirstVoidSlotIcon() const
{
	for (unsigned int i = 0; i < slotIcons.size(); i++)
	{
		if (slotIcons[i]->getPlayerProperty() == nullptr)
		{
			return slotIcons[i];
		}
	}
	Debug::note("ChoosePlayerPropertyMenu.getFirstVoidSlotIcon(): no void SlotIcon");
	return nullptr;
}

const std::vector<PlayerPropertyIcon*>& ChoosePlayerPropertyMenu::getSlotIcons() const
{
	return slotIcons;
}

LobbyMenu* ChoosePlayerPropertyMenu::getLobbyMenu() const
{
	return lobby;
}

void ChoosePlayerPropertyMenu::okPressed()
{
	std::string ids = "";
	for (unsigned int i = 0; i < getSlotIcons().size(); i++)
	{
		if (getSlotIcons()[i]->getPlayerProperty() != nullptr)
		{
			ids.push_back(getSlotIcons()[i]->getPlayerProperty()->getID());
		}
		else
		{
			ids.push_back(-1);
		}
		//slotPacket->getPlayerProperty()[i] = slotIcons.get(i).getPlayerProperty();
	}
	slotPacket->setIDs(ids);
	getLobbyMenu()->playerPropertySelected(slotPacket); // weist das LobbyMenu darauf hin, dass die Packets neu versendet werden müssen
	Main::getMenuList()->back();
}

void ChoosePlayerPropertyMenu::chooseIconPressed(PlayerProperty* prop)
{
	if (getFirstVoidSlotIcon() != nullptr)
	{
		getFirstVoidSlotIcon()->setPlayerProperty(prop);
	}
}
