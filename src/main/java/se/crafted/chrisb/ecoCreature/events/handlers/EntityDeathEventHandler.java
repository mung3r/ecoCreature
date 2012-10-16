package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.settings.WorldSettings;

public class EntityDeathEventHandler extends AbstractEventHandler
{
    public EntityDeathEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public boolean canCreateRewardEvents(Event event)
    {
        return event instanceof EntityKilledEvent;
    }

    @Override
    public Set<RewardEvent> createRewardEvents(Event event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        if (event instanceof EntityKilledEvent) {
            events = new HashSet<RewardEvent>();
            events.addAll(getRewardEvents((EntityKilledEvent) event));
        }

        return events;
    }

    private Set<RewardEvent> getRewardEvents(EntityKilledEvent event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        Player killer = event.getKiller();
        WorldSettings settings = getSettings(killer.getWorld());
        event.setSpawnerMobTracking(settings);

        if (settings.hasReward(event)) {
            Reward reward = settings.getReward(event);
            reward.setGain(settings.getGainMultiplier(killer));
            reward.setParty(settings.getParty(killer));
            reward.addParameter(MessageToken.CREATURE, reward.getName())
                .addParameter(MessageToken.ITEM, event.getWeaponName());

            if (settings.isOverrideDrops() || (settings.isClearOnNoDrops() && !reward.hasDrops())) {
                event.getDrops().clear();
            }

            if (reward.getEntityDrops().contains(EntityType.EXPERIENCE_ORB)) {
                event.setDroppedExp(0);
            }

            events = new HashSet<RewardEvent>();
            events.add(new RewardEvent(killer, reward));
        }

        return events;
    }
}
