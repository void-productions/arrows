package entity;

import java.util.LinkedList;

import core.Main;
import damage.Damage;
import effect.Effect;
import entity.MinimizedEntity;
import entity.entities.dynamic.SpinnableEntity;
import bullet.ExtendedBullet;
import tile.ExtendedTile;
import game.Game;
import game.ServerGame;
import graphics.Animation;
import graphics.ImageFile;
import graphics.ImageID;
import misc.Debug;
import misc.math.collision.CollisionDetector;
import misc.math.game.GameVector;
import misc.math.game.GamePosition;
import misc.math.game.GameRect;
import misc.math.game.GameSize;
import misc.math.pixel.PixelSize;
import misc.math.Camera;
import player.ServerGamePlayer;
import tile.map.GameTileMap;

public abstract class Entity
{
	private LinkedList<Effect> effects = new LinkedList<Effect>();
	private GamePosition position;
	private Animation animation;

	public Entity(GamePosition position, Animation animation)
	{
		this.position = new GamePosition(position);
		this.animation = animation;
	}

	public void tick()
	{
		checkCollision();
		getAnimation().tick();
		for (int i = 0; i < getEffects().size(); i++)
		{
			if (getEffects().get(i).hasToBeRemoved())
			{
				getEffects().remove(i);
				i--;
			}
			else
			{
				getEffects().get(i).tick();
			}
		}
	}

	protected void onCollide(Entity e) { }

	public final void onDamage(Damage damage, LinkedList<Effect> e)
	{
		applyDamage(damage);
		applyEffects(new LinkedList<Effect>(e));
	}

	public void applyDamage(Damage damage) {}

	// setter
	protected void setPosition(GameVector position)
	{
		this.position = new GamePosition(position);
	}

	// getter

	public boolean isCircular() { return false; }

	public GamePosition getPosition()
	{
		Debug.warnIf(position == null, "Entity.getPosition(): returns null");
		return position;
	}

	public GameSize getSize()
	{
		Debug.warnIf(getImageID() == null, "Entity.getSize(): getImageID is null");
		return Camera.get().pixelSizeToGameSize(new PixelSize(getImageID()));
	}

	public ImageID getImageID()
	{
		Debug.warnIf(getAnimation().getImageID() == null, "Entity.getImageID(): returns null");
		return getAnimation().getImageID();
	}

	protected Animation getAnimation()
	{
		Debug.warnIf(animation == null, "Entity.getAnimation(): returns null");
		return animation;
	}

	public float getLeft() { return getPosition().getX() - getSize().getX() * 0.5f; }
	public float getRight() { return getPosition().getX() + getSize().getX() * 0.5f; }
	public float getTop() { return getPosition().getY() - getSize().getY() * 0.5f; }
	public float getBot() { return getPosition().getY() + getSize().getY() * 0.5f; }
	public final boolean hasToBeRemoved() { return isOutOfMap() || isDead(); }
	protected boolean isDead() { return false; }

	public abstract MinimizedEntity toMinimizedEntity();

	public void applyEffect(Effect effect)
	{
		Effect e = effect.copy();
		e.setTarget(this);
		effects.add(e);
	}

	public final void applyEffects(LinkedList<Effect> effects)
	{
		for (Effect e : effects)
		{
			applyEffect(e);
		}
	}

	public final LinkedList<Effect> getEffects()
	{
		return effects;
	}

	public final LinkedList<Effect> getSpreadingEffects()
	{
		LinkedList<Effect> tmp = new LinkedList<Effect>();
		for (Effect e : getEffects())
		{
			if (e.isSpreading())
			{
				tmp.add(e);
			}
		}
		return tmp;
	}

	private void checkCollision()
	{
		for (Entity entity : getPossibleColliders())
		{
			if (isColliding(entity))
			{
				this.onCollide(entity);
			}
		}
	}

	protected boolean isColliding(Entity entity)
	{
		return CollisionDetector.areCollidingDynamic(this, entity);
	}

	// abstract

	protected abstract boolean isCollidingTiles();
	protected abstract boolean isCollidingPlayers();
	protected abstract boolean isCollidingBullets();

	// getter
	// gibt nur die Tiles zurück, die in der Range sind (mit denen man kollidieren könnte)
	protected final LinkedList<Entity> getPossibleColliders()
	{
		LinkedList<Entity> possibleColliders = new LinkedList<Entity>();

		if (isCollidingTiles())
		{
			for (ExtendedTile tile : GameTileMap.get().getPossibleColliderTiles(this))
			{
				possibleColliders.add(tile);
			}
		}
		if (isCollidingPlayers())
		{
			for (ServerGamePlayer player : ((ServerGame)Game.get()).getPlayers())
			{
				if (this != player)
				{
					possibleColliders.add(player);
				}
			}
		}
		if (isCollidingBullets())
		{
			for (ExtendedBullet bullet : ((ServerGame)Game.get()).getBullets())
			{
				if (this != bullet)
				{
					possibleColliders.add(bullet);
				}
			}
		}
		return possibleColliders;
	}

	private boolean isOutOfMap()
	{
		return !GameTileMap.get().isInMap(getWrapper());
	}

	protected final void move(GameVector v)
	{
		setPosition(getPosition().plus(v));
	}

	protected final void moveX(float f)
	{
		setPosition(getPosition().plusX(f));
	}

	protected final void moveY(float f)
	{
		setPosition(getPosition().plusY(f));
	}

	protected boolean hasEffectWithID(int id)
	{
		for (Effect e : getEffects())
			if (e.getID() == id)
				return true;
		return false;
	}

	public GameRect getWrapper()
	{
		return new GameRect(getPosition(), getSize());
	}

	public boolean isDynamic() { return false; }
}
