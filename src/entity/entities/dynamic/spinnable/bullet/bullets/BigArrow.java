package entity.entities.dynamic.spinnable.bullet.bullets;

import damage.Damage;
import entity.Entity;
import entity.entities.dynamic.spinnable.bullet.StickyBullet;
import graphics.animations.StaticAnimation;
import graphics.ImageFile;
import misc.math.game.GamePosition;
import misc.math.game.GameVector;
import player.ServerGamePlayer;

public class BigArrow extends StickyBullet
{
	public BigArrow(ServerGamePlayer owner, GamePosition position, GameVector velocity)
	{
		super(owner, position, new StaticAnimation(ImageFile.BIGARROW.getImageID()), velocity);
	}

	@Override public void tick()
	{
		super.tick();
		updateRotationByVelocity();
	}

	@Override protected Damage getDamage() { return new Damage(10,7,0); }
}