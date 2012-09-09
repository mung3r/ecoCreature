package se.crafted.chrisb.ecoCreature.messages;

public class NoCoinRewardMessage extends DefaultMessage
{
    boolean noRewardMessageEnabled;

    public NoCoinRewardMessage(String template)
    {
        super(template);
        noRewardMessageEnabled = false;
    }

    public boolean isNoRewardMessageEnabled()
    {
        return noRewardMessageEnabled;
    }

    public void setNoRewardMessageEnabled(boolean noRewardMessageEnabled)
    {
        this.noRewardMessageEnabled = noRewardMessageEnabled;
    }

    @Override
    public boolean isMessageOutputEnabled()
    {
        return noRewardMessageEnabled && super.isMessageOutputEnabled();
    }
}
