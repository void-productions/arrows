package entity.entities;

import java.util.LinkedList;

import static core.Main.DRAG_X;
import static core.Main.DRAG_Y;
import static core.Main.GRAVITY;
import effect.Effect;
import game.Game;
import game.ServerGame;
import entity.Entity;
import entity.entities.dynamic.SpinnableEntity;
import bullet.ExtendedBullet;
import tile.ExtendedTile;
import graphics.Animation;
import misc.Debug;
import misc.math.collision.CollisionDetector;
import misc.math.game.GameVector;
import misc.math.game.GamePosition;
import player.ServerGamePlayer;
import tile.map.GameTileMap;

public abstract class DynamicEntity extends Entity
{
	private GameVector velocity = new GameVector();
	private GameVector oldVelocity = new GameVector();
	private GameVector drag;

	private int mass = 0;

	public DynamicEntity(GamePosition position, Animation animation)
	{
		super(position, animation);
		setDrag(getDefaultDrag());
	}

	@Override public void tick()
	{
		super.tick();
		if ((!isFloating()) && canMove())
			accelerate(0, GRAVITY);
		if (canMove())
			setVelocity(getVelocity().timesX(1/getDrag().getX()).timesY(1/getDrag().getY()));
		oldVelocity = new GameVector(velocity);
		updatePositionByVelocity();
		super.tick();
		// if (oldVelocity.minus(getVelocity()).getMagnitude() > DAMAGE_BORDER) { onDamage(...); }
	}

	protected void updatePositionByVelocity()
	{
		if (canMove())
		{
			setPosition(getPosition().plus(getVelocity()));
		}
	}

	public void accelerate(GameVector p)
	{
		if (canMove())
		{
			setVelocity(getVelocity().plus(p));
		}
	}

	public void accelerate(float x, float y)
	{
		if (canMove())
		{
			accelerate(new GameVector(x, y));
		}
	}

	public void accelerateWithDragBalance(GameVector v)
	{
		if (canMove())
		{
			accelerate(v.timesX(DRAG_X).timesY(DRAG_Y*0.5f));
		}
	}

	public GameVector getVelocity() { return velocity; }
	public GameVector getOldVelocity() { return oldVelocity; }
	public final void stop() { velocity = new GameVector(0, 0); }
	protected GameVector getDrag() { return drag; }
	protected GameVector getDefaultDrag() { return new GameVector(DRAG_X, DRAG_Y); }

	// setter
	protected void setDrag(GameVector drag)
	{
		Debug.warnIf(drag == null, "DynamicEntity.setDrag(): drag == null");
		this.drag = drag;
	}

	public void flash(GamePosition pos)
	{
		setPosition(pos);
	}

	public boolean isFlashPossible(GamePosition pos)
	{
		return canMove() && (!isCollidingTiles() || GameTileMap.get().couldGoHere(this, pos));
	}

	protected void setPosition(float x, float y)
	{
		setPosition(new GamePosition(x, y));
	}

	protected void setVelocity(float x, float y)
	{
		setVelocity(new GameVector(x, y));

	}

	protected void stopX()
	{
		setVelocity(0, getVelocity().getY());
	}

	protected void stopY()
	{
		setVelocity(getVelocity().getX(), 0);
	}

	public boolean canMove()
	{
		return !hasEffectWithID(Effect.STUN_ID); // + hasEffectWithID(Effect.ROOT_ID)
	}


	protected void setVelocity(GameVector velocity)
	{
		Debug.warnIf(velocity == null, "DynamicEntity.setVelocity(): velocity == null");
		this.velocity = velocity;
	}

	protected boolean isFloating() { return true; } // no gravity is applied
	@Override public boolean isDynamic() { return true; }
}
