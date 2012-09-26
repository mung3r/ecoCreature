package se.crafted.chrisb.ecoCreature.events.listeners;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.MessageHandler;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.rewards.Reward;

public class RewardEventListener implements Listener
{
    private static final String PARTY_REWARD_MESSAGE = "&7Party awarded &6<amt>&7.";
    private static final String PARTY_PENALTY_MESSAGE = "&Party penalized &6<amt>&7.";

    private ecoCreature plugin;

    public RewardEventListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onRewardEvent(RewardEvent event)
    {
        if (!event.isCancelled()) {
            Reward reward = event.getReward();
            Player player = event.getPlayer();

            dropCoin(player.getName(), reward);
            dropItems(reward);
            dropEntities(reward);

            plugin.getMetrics().addCount(reward.getName());
        }
    }

    private void dropCoin(String player, Reward reward)
    {
        if (!DependencyUtils.hasDeathTpPlus()) {
            return;
        }

        double amount = calculateAmount(reward);

        Set<String> party = new HashSet<String>();
        party.add(player);
        party.addAll(reward.getParty());

        for (String member : party) {
            registerAmount(member, amount);

            Message message = member.equals(player) ? reward.getMessage() : getPartyMessage(amount);
            message.addParameter(MessageToken.PLAYER, member);
            message.addParameter(MessageToken.AMOUNT, DependencyUtils.getEconomy().format(Math.abs(amount)));

            MessageHandler handler = new MessageHandler(member, message);
            handler.send();
        }
    }

    private double calculateAmount(Reward reward)
    {
        double amount = reward.getCoin() * reward.getGain();
        ECLogger.getInstance().debug("Amount to be awarded: " + amount);

        if (reward.getParty().size() > 1) {
            amount /= reward.getParty().size();
            ECLogger.getInstance().debug("Amount divided with party: " + amount);
        }

        if (reward.isIntegerCurrency()) {
            ECLogger.getInstance().debug("Amount to be rounded: " + amount);
            amount = Math.round(amount);
        }

        return amount;
    }

    private void registerAmount(String member, double amount)
    {
        if (!DependencyUtils.hasEconomy()) {
            return;
        }

        if (amount > 0.0) {
            DependencyUtils.getEconomy().depositPlayer(member, amount);
        }
        else if (amount < 0.0) {
            DependencyUtils.getEconomy().withdrawPlayer(member, Math.abs(amount));
        }
    }

    private Message getPartyMessage(double amount)
    {
        Message message = new DefaultMessage("");

        if (amount > 0.0) {
            message = new DefaultMessage(PARTY_REWARD_MESSAGE);
        }
        else if (amount < 0.0) {
            message = new DefaultMessage(PARTY_PENALTY_MESSAGE);
        }

        return message;
    }

    private void dropItems(Reward reward)
    {
        for (ItemStack stack : reward.getItemDrops()) {
            reward.getWorld().dropItemNaturally(reward.getLocation(), stack);
        }
    }

    private void dropEntities(Reward reward)
    {
        for (EntityType type : reward.getEntityDrops()) {
            Entity entity = reward.getWorld().spawn(reward.getLocation(), type.getEntityClass());
            if (entity instanceof ExperienceOrb) {
                ((ExperienceOrb) entity).setExperience(1);
            }
        }
    }
}
