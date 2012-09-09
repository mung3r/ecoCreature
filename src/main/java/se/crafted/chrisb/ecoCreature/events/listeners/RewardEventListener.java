package se.crafted.chrisb.ecoCreature.events.listeners;

import java.util.HashSet;
import java.util.Set;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.MessageHandler;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.rewards.Reward;

public class RewardEventListener implements Listener
{
    private ecoCreature plugin;

    public RewardEventListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onRewardEvent(RewardEvent event)
    {
        if (!event.isCancelled()) {

            Player player = event.getPlayer();

            if (player != null) {
                Reward reward = event.getReward();

                if (DependencyUtils.hasEconomy()) {
                    double amount = reward.getCoin() * reward.getGain();

                    Set<String> party = new HashSet<String>();
                    if (reward.getParty().size() > 0) {
                        ECLogger.getInstance().debug("Amount to be divided with party: " + amount);
                        party.addAll(reward.getParty());
                        amount /= reward.getParty().size();
                    }
                    else {
                        party.add(player.getName());
                    }

                    if (reward.isIntegerCurrency()) {
                        ECLogger.getInstance().debug("Amount to be rounded: " + amount);
                        amount = Math.round(amount);
                    }

                    Economy economy = DependencyUtils.getEconomy();

                    for (String member : party) {
                        if (amount > 0.0) {
                            economy.depositPlayer(member, amount);
                        }
                        else if (amount < 0.0) {
                            economy.withdrawPlayer(member, Math.abs(amount));
                        }

                        if (member.equals(player.getName())) {
                            Message message = reward.getMessage();
                            message.addParameter(MessageToken.PLAYER, member);
                            message.addParameter(MessageToken.AMOUNT, economy.format(Math.abs(amount)));

                            MessageHandler handler = new MessageHandler(member, message);
                            handler.send();
                        }
                        else {
                            // TODO: send different message to party members
                        }
                    }
                }

                for (ItemStack stack : reward.getDrops()) {
                    reward.getWorld().dropItemNaturally(reward.getLocation(), stack);
                }

                if (reward.getExp() > 0) {
                    ExperienceOrb orb = reward.getWorld().spawn(reward.getLocation(), ExperienceOrb.class);
                    orb.setExperience(reward.getExp());
                }

                plugin.getMetrics().addCount(reward.getType());
            }
        }
    }
}
