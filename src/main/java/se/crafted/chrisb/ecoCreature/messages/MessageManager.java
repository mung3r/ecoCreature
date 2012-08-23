package se.crafted.chrisb.ecoCreature.messages;

import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class MessageManager
{
    public static final String NO_CAMP_MESSAGE = "&7You find no rewards camping monster spawners.";
    public static final String NO_BOW_REWARD_MESSAGE = "&7You find no rewards on this creature.";
    public static final String DEATH_PENALTY_MESSAGE = "&7You wake up to find &6<amt>&7 missing from your pockets!";
    public static final String PVP_REWARD_MESSAGE = "&7You are awarded &6<amt>&7 for murdering &5<crt>&7.";

    public static final String NO_REWARD_MESSAGE = "&7You slayed a &5<crt>&7 using a &3<itm>&7.";
    public static final String REWARD_MESSAGE = "&7You are awarded &6<amt>&7 for slaying a &5<crt>&7.";
    public static final String PENALTY_MESSAGE = "&7You are penalized &6<amt>&7 for slaying a &5<crt>&7.";

    public boolean shouldOutputMessages;
    public boolean shouldOutputNoReward;
    public boolean shouldOutputSpawnerCamp;
    public boolean shouldLogCoinRewards;

    public String noBowRewardMessage;
    public String noCampMessage;
    public String deathPenaltyMessage;
    public String pvpRewardMessage;

    public void basicMessage(String template, Player player)
    {
        Message message = new BasicMessage(this);
        message.setTemplate(template);
        message.addParameter(MessageToken.PLAYER_TOKEN, player.getName());
        message.send();
    }

    public void spawnerMessage(String template, Player player)
    {
        Message message = new SpawnerMessage(this);
        message.setTemplate(template);
        message.addParameter(MessageToken.PLAYER_TOKEN, player.getName());
        message.send();
    }

    public void penaltyMessage(String template, Player player, double amount)
    {
        Message message = new RewardMessage(this);
        message.setTemplate(template);
        message.addParameter(MessageToken.PLAYER_TOKEN, player.getName());
        message.addParameter(MessageToken.AMOUNT_TOKEN, ecoCreature.getEconomy().format(amount));
        message.send();
    }

    public void rewardMessage(String template, Player player, double amount, String creatureName, String weaponName)
    {
        Message message = new RewardMessage(this);
        message.setTemplate(template);
        message.addParameter(MessageToken.PLAYER_TOKEN, player.getName());
        message.addParameter(MessageToken.AMOUNT_TOKEN, ecoCreature.getEconomy().format(amount));
        message.addParameter(MessageToken.CREATURE_TOKEN, creatureName);
        message.addParameter(MessageToken.ITEM_TOKEN, weaponName);
        message.send();
    }

    public void noRewardMessage(String template, Player player, String creatureName, String weaponName)
    {
        Message message = new NoRewardMessage(this);
        message.setTemplate(template);
        message.addParameter(MessageToken.PLAYER_TOKEN, player.getName());
        message.addParameter(MessageToken.CREATURE_TOKEN, creatureName);
        message.addParameter(MessageToken.ITEM_TOKEN, weaponName);
        message.send();
    }
}
