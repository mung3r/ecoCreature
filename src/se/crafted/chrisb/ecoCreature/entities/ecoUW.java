package se.crafted.chrisb.ecoCreature.entities;

import net.minecraft.server.EntityWolf;
import net.minecraft.server.PathEntity;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.Wolf;

public class ecoUW
{
  CraftWolf wolf;

  public ecoUW(Wolf paramWolf)
  {
    this.wolf = ((CraftWolf)paramWolf);
  }

  public Wolf getWolf()
  {
    return this.wolf;
  }

  public boolean isAngry()
  {
    return this.wolf.isAngry();
  }

  public void setAngry(boolean paramBoolean)
  {
    this.wolf.setAngry(paramBoolean);
  }

  public boolean isSitting()
  {
    return this.wolf.isSitting();
  }

  public void setSitting(boolean paramBoolean)
  {
    this.wolf.setSitting(paramBoolean);
  }

  public boolean isTame()
  {
    return getHandle().A();
  }

  public void setTame(boolean paramBoolean)
  {
    if ((paramBoolean) && (!this.wolf.getHandle().A()))
      this.wolf.getHandle().health = (int)Math.round(20.0D * (this.wolf.getHandle().health / 8.0D));
    else if ((!paramBoolean) && (this.wolf.getHandle().A()))
      this.wolf.getHandle().health = (int)Math.round(8.0D * (this.wolf.getHandle().health / 20.0D));
    this.wolf.getHandle().d(paramBoolean);
  }

  public String getOwner()
  {
    return getHandle().x();
  }

  public void setOwner(String paramString)
  {
    EntityWolf localEntityWolf = getHandle();
    if ((paramString != null) && (paramString.length() > 0))
    {
      if (!localEntityWolf.A())
        localEntityWolf.health = (int)Math.round(20.0D * (localEntityWolf.health / 8.0D));
      localEntityWolf.d(true);
      localEntityWolf.a((PathEntity)null);
      localEntityWolf.a(paramString);
    }
    else
    {
      if (localEntityWolf.A())
        localEntityWolf.health = (int)Math.round(8.0D * (localEntityWolf.health / 20.0D));
      localEntityWolf.d(false);
      localEntityWolf.a("");
    }
  }

  public EntityWolf getHandle()
  {
    return this.wolf.getHandle();
  }

  public String toString()
  {
    return "CraftWolf[anger=" + isAngry() + ",owner=" + getOwner() + ",tame=" + isTame() + ",sitting=" + isSitting() + "]";
  }
}