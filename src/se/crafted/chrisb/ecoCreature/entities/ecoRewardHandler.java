package se.crafted.chrisb.ecoCreature.entities;

import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.utils.ecoConstants;
import se.crafted.chrisb.ecoCreature.utils.ecoEcon;

public class ecoRewardHandler
{
    private final ecoCreature plugin;

    public ecoRewardHandler(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    public void registerAccident(Player player)
    {
        double playerBalance = ecoEcon.getBalance(player);
        double penaltyAmount;
        if (ecoConstants.hasPenaltyType)
            penaltyAmount = playerBalance * ecoConstants.penaltyAmount / 100.0D;
        else
            penaltyAmount = ecoConstants.penaltyAmount;
        ecoEcon.addMoney(player, -penaltyAmount);
        player.sendMessage(ecoConstants.deathPenaltyMessage.replaceAll("<amt>", ecoEcon.format(penaltyAmount).replaceAll("\\$", "\\\\\\$")));
    }

    public void registerReward(Player player, int creatureIndex, String itemNameInHand)
    {
        if (creatureIndex < 0)
            return;
        double amount = ecoEcon.computeAmount(ecoConstants.CreatureCoinMin[creatureIndex], ecoConstants.CreatureCoinMax[creatureIndex], ecoConstants.CreatureCoinPercentage[creatureIndex]);
        if (ecoConstants.isIntegerCurrency)
            amount = Math.round(amount);
        if (ecoConstants.Gain.containsKey(ecoCreature.permissionsHandler.getGroup(player.getWorld().getName(), player.getName())))
            amount *= ((Double) ecoConstants.Gain.get(ecoCreature.permissionsHandler.getGroup(player.getWorld().getName(), player.getName()))).doubleValue();
        if (amount > 0.0D) {
            ecoEcon.addMoney(player, amount);
            if (ecoConstants.shouldOutputMessages)
                player.sendMessage(ecoConstants.CreatureRewardMessage[creatureIndex].replaceAll("<amt>", ecoEcon.format(amount).replaceAll("\\$", "\\\\\\$")).replaceAll("<itm>", toCamelCase(itemNameInHand))
                        .replaceAll("<crt>", ecoConstants.Creatures[creatureIndex]));
        }
        else if (amount == 0.0D) {
            if ((ecoConstants.shouldOutputMessages) && (ecoConstants.shouldOutputNoRewardMessage))
                player.sendMessage(ecoConstants.CreatureNoRewardMessage[creatureIndex].replaceAll("<crt>", ecoConstants.Creatures[creatureIndex]).replaceAll("<itm>", toCamelCase(itemNameInHand)));
        }
        else if (amount < 0.0D) {
            ecoEcon.addMoney(player, amount);
            if (ecoConstants.shouldOutputMessages)
                player.sendMessage(ecoConstants.CreaturePenaltyMessage[creatureIndex].replaceAll("<amt>", ecoEcon.format(amount).replaceAll("\\$", "\\\\\\$")).replaceAll("<itm>", toCamelCase(itemNameInHand))
                        .replaceAll("<crt>", ecoConstants.Creatures[creatureIndex]));
        }
    }

    private static String toCamelCase(String rawItemName)
    {
        String[] rawItemNameParts = rawItemName.split("_");
        String itemName = "";
        for (String itemNamePart : rawItemNameParts)
            itemName = itemName + " " + toProperCase(itemNamePart);
        if (itemName.trim().equals("Air"))
            return "Fists";
        if (itemName.trim().equals("Bow"))
            return "Bow & Arrow";
        return itemName.trim();
    }

    private static String toProperCase(String str)
    {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}