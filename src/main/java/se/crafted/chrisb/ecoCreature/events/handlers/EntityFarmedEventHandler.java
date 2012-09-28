package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.Collections;
import java.util.Set;

import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.EntityFarmedEvent;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.rewards.WorldSettings;

public class EntityFarmedEventHandler extends AbstractEventHandler
{
    public EntityFarmedEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public boolean canCreateRewardEvents(Event event)
    {
        return event instanceof EntityFarmedEvent;
    }

    @Override
    public Set<RewardEvent> createRewardEvents(Event event)
    {
        if (event instanceof EntityFarmedEvent) {
            handleNoFarm((EntityFarmedEvent) event);
        }

        return Collections.emptySet();
    }

    private void handleNoFarm(EntityFarmedEvent event)
    {
        WorldSettings settings = getSettings(event.getEntity().getWorld());

        if (settings.isNoFarm() && event.isFarmed()) {
            deleteDrops(event);
        }

        if (settings.isNoFarmFire() && event.isFireFarmed()) {
            deleteDrops(event);
        }
    }

    private void deleteDrops(EntityFarmedEvent event)
    {
        event.getDrops().clear();
        event.setDroppedExp(0);
    }
}
