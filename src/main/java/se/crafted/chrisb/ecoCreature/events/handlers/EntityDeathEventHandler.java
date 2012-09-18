package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.rewards.WorldSettings;

public class EntityDeathEventHandler extends DefaultEventHandler
{
    public EntityDeathEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public Set<RewardEvent> getRewardEvents(Event event)
    {
        Set<RewardEvent> events = new HashSet<RewardEvent>();

        if (event instanceof EntityKilledEvent) {
            events.addAll(getRewardEvents((EntityKilledEvent) event));
        }

        return events;
    }

    private Set<RewardEvent> getRewardEvents(EntityKilledEvent event)
    {
        Set<RewardEvent> events = new HashSet<RewardEvent>();

        Player killer = event.getKiller();
        WorldSettings settings = plugin.getWorldSettings(killer.getWorld());

        if (settings.hasRewardSource(event)) {
            Reward outcome = settings.getRewardSource(event).getOutcome(event);
            outcome.setGain(settings.getGainMultiplier(killer));
            outcome.setParty(settings.getParty(killer));
            outcome.getMessage().addParameter(MessageToken.CREATURE, outcome.getName());
            outcome.getMessage().addParameter(MessageToken.ITEM, event.getWeaponName());

            if (outcome.getExp() > 0) {
                event.setDroppedExp(0);
            }

            if (settings.isOverrideDrops()) {
                event.getDrops().clear();
            }

            events.add(new RewardEvent(killer, outcome));
        }

        return events;
    }
}
