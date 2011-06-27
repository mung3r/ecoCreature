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
    return getHandle().isTamed();
  }

  public void setTame(boolean paramBoolean)
  {
    if ((paramBoolean) && (!this.wolf.getHandle().isTamed()))
      this.wolf.getHandle().health = (int)Math.round(20.0D * (this.wolf.getHandle().health / 8.0D));
    else if ((!paramBoolean) && (this.wolf.getHandle().isTamed()))
      this.wolf.getHandle().health = (int)Math.round(8.0D * (this.wolf.getHandle().health / 20.0D));
    this.wolf.getHandle().setTamed(paramBoolean);
  }

  public String getOwner()
  {
    return getHandle().getOwnerName();
  }

  public void setOwner(String paramString)
  {
    EntityWolf localEntityWolf = getHandle();
    if ((paramString != null) && (paramString.length() > 0))
    {
      if (!localEntityWolf.isTamed())
        localEntityWolf.health = (int)Math.round(20.0D * (localEntityWolf.health / 8.0D));
      localEntityWolf.setTamed(true);
      localEntityWolf.setPathEntity((PathEntity)null);
      localEntityWolf.setOwnerName(paramString);
    }
    else
    {
      if (localEntityWolf.isTamed())
        localEntityWolf.health = (int)Math.round(8.0D * (localEntityWolf.health / 20.0D));
      localEntityWolf.setTamed(false);
      localEntityWolf.setOwnerName("");
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