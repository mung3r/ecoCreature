package se.crafted.chrisb.ecoCreature.rewards.rules;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;

public class SimpleClansRule extends AbstractPlayerRule
{
    public SimpleClansRule()
    {
        setClearDrops(true);
        setClearExpOrbs(true);
    }

    public void setClearNonRivalDrops(boolean clearNonRivalDrops)
    {
        setClearDrops(clearNonRivalDrops);
        setClearExpOrbs(clearNonRivalDrops);
    }

    @Override
    protected boolean isBroken(PlayerKilledEvent event)
    {
        return DependencyUtils.hasSimpleClans() && !SimpleClans.getInstance().getClanManager().getClanPlayer(event.getVictim()).isRival(event.getKiller());
    }

    public static Map<Class<? extends AbstractRule>, Rule> parseConfig(ConfigurationSection system)
    {
        Map<Class<? extends AbstractRule>, Rule> rules = Collections.emptyMap();
        SimpleClansRule rule = new SimpleClansRule();
        rule.setClearNonRivalDrops(system.getBoolean("Hunting.SimpleClans.ClearNonRivalDrops", false));
        rules = new HashMap<Class<? extends AbstractRule>, Rule>();
        rules.put(SimpleClansRule.class, rule);
        return rules;
    }
}
