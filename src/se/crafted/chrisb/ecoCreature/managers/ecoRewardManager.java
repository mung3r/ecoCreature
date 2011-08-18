package se.crafted.chrisb.ecoCreature.managers;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.models.ecoReward;

public class ecoRewardManager
{
    private final ecoCreature plugin;

    public static boolean isIntegerCurrency;

    public static boolean canCampSpawner;
    public static boolean shouldOverrideDrops;
    public static boolean isFixedDrops;
    public static boolean shouldClearCampDrops;
    public static int campRadius;
    public static boolean hasBowRewards;
    public static boolean hasDeathPenalty;
    public static boolean isPercentPenalty;
    public static double penaltyAmount;
    public static boolean canHuntUnderSeaLevel;
    public static boolean isWolverineMode;

    public static boolean shouldOutputMessages;
    public static boolean shouldOutputNoRewardMessage;
    public static boolean shouldOutputSpawnerMessage;
    public static String noBowRewardMessage;
    public static String noCampMessage;
    public static String deathPenaltyMessage;

    public static HashMap<String, Double> groupMultiplier = new HashMap<String, Double>();
    public static HashMap<CreatureType, ecoReward> rewards;
    public static ecoReward spawnerReward;

    public ecoRewardManager(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    public void registerDeathPenalty(Player player)
    {
        if (player == null) {
            return;
        }

        if (!hasDeathPenalty) {
            return;
        }

        double amount = isPercentPenalty ? plugin.method.getAccount(player.getName()).balance() * (penaltyAmount / 100.0D) : penaltyAmount;
        plugin.method.getAccount(player.getName()).subtract(amount);

        if (ecoRewardManager.shouldOutputMessages) {
            player.sendMessage(deathPenaltyMessage.replaceAll("<amt>", plugin.method.format(amount).replaceAll("\\$", "\\\\\\$")));
        }
    }

    public void registerCreatureReward(Player player, CreatureType tamedCreature, CreatureType killedCreature)
    {
        if (player == null) {
            return;
        }

        if (killedCreature == null) {
            return;
        }

        ecoReward reward = rewards.get(killedCreature);
        String weaponName = tamedCreature != null ? tamedCreature.getName() : Material.getMaterial(player.getItemInHand().getTypeId()).name();

        double amount = computeAmount(reward);

        if (isIntegerCurrency) {
            amount = Math.round(amount);
        }

        if (groupMultiplier.containsKey(ecoCreature.permissionsHandler.getGroup(player.getWorld().getName(), player.getName()))) {
            amount *= ((Double) groupMultiplier.get(ecoCreature.permissionsHandler.getGroup(player.getWorld().getName(), player.getName()))).doubleValue();
        }

        if (amount > 0.0D) {
            plugin.method.getAccount(player.getName()).add(amount);
            if (ecoRewardManager.shouldOutputMessages) {
                player.sendMessage(reward.getRewardMessage().replaceAll("<amt>", plugin.method.format(amount).replaceAll("\\$", "\\\\\\$")).replaceAll("<itm>", toCamelCase(weaponName)).replaceAll("<crt>", reward.getRewardName()));
            }
        }
        else if (amount < 0.0D) {
            plugin.method.getAccount(player.getName()).add(amount);
            if (ecoRewardManager.shouldOutputMessages) {
                player.sendMessage(reward.getPenaltyMessage().replaceAll("<amt>", plugin.method.format(amount).replaceAll("\\$", "\\\\\\$")).replaceAll("<itm>", toCamelCase(weaponName)).replaceAll("<crt>", reward.getRewardName()));
            }
        }
        else {
            if ((ecoRewardManager.shouldOutputMessages) && (ecoRewardManager.shouldOutputNoRewardMessage)) {
                player.sendMessage(reward.getNoRewardMessage().replaceAll("<crt>", reward.getRewardName()).replaceAll("<itm>", toCamelCase(weaponName)));
            }
        }
    }

    public void registerSpawnerReward(Player player, Block block)
    {
        if (player == null) {
            return;
        }

        if (block == null || !block.getType().equals(Material.MOB_SPAWNER)) {
            return;
        }

        if (ecoCreature.permissionsHandler.has(player, "ecoCreature.Creature.Spawner")) {
            for (ItemStack itemStack : spawnerReward.computeDrops()) {
                block.getWorld().dropItemNaturally(block.getLocation(), itemStack);
            }
        }
    }

    private static double computeAmount(ecoReward reward)
    {
        double amount = 0.0D;
        if ((reward.getCoinMin() == 0.0D) && (reward.getCoinMax() == 0.0D))
            amount = 0.0D;
        else if (reward.getCoinMax() == 0.0D)
            amount = reward.getCoinMin();
        else
            amount = reward.getCoinMin() + Math.random() * (reward.getCoinMax() - reward.getCoinMin());
        if (reward.getCoinPercentage() == 0.0D)
            return 0.0D;
        if (reward.getCoinPercentage() == 100.0D)
            return amount;
        if (Math.random() < reward.getCoinPercentage() / 100.0D)
            return amount;
        return 0.0D;
    }

    private static String toCamelCase(String rawItemName)
    {
        String[] rawItemNameParts = rawItemName.split("_");
        String itemName = "";

        for (String itemNamePart : rawItemNameParts) {
            itemName = itemName + " " + toProperCase(itemNamePart);
        }

        if (itemName.trim().equals("Air")) {
            return "Fists";
        }

        if (itemName.trim().equals("Bow")) {
            return "Bow & Arrow";
        }

        return itemName.trim();
    }

    private static String toProperCase(String str)
    {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}