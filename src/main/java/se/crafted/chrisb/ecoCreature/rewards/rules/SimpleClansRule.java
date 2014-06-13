package se.crafted.chrisb.ecoCreature.rewards.rules;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;

public class SimpleClansRule extends AbstractPlayerRule
{
    public SimpleClansRule()
    {
        setClearDrops(false);
        setClearExpOrbs(false);
    }

    public void setClearNonRivalDrops(boolean clearNonRivalDrops)
    {
        setClearDrops(clearNonRivalDrops);
        setClearExpOrbs(clearNonRivalDrops);
    }

    @Override
    protected boolean isBroken(PlayerKilledEvent event)
    {
        boolean ruleBroken = false;

        if (DependencyUtils.hasSimpleClans())
        {
            ClanManager clanManager = SimpleClans.getInstance().getClanManager();
            Player killer = event.getKiller();
            ClanPlayer victim = clanManager.getClanPlayer(event.getVictim());
            ruleBroken = !victim.isRival(killer);
        }

        return ruleBroken;
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
