package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;
import com.herocraftonline.heroes.characters.Hero;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class HeroMasteredListener implements Listener
{
    private final ecoCreature plugin;

    public HeroMasteredListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHeroChangeLevel(HeroChangeLevelEvent event)
    {
        Hero hero = event.getHero();
        if (hero.getLevel() == event.getHeroClass().getMaxLevel()) {
            plugin.getRewardManager(hero.getPlayer().getWorld()).registerHeroMasteredReward(hero);
        }
    }
}
