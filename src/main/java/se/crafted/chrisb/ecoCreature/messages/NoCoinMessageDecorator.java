package se.crafted.chrisb.ecoCreature.messages;

public class NoCoinMessageDecorator extends AbstractMessageDecorator
{
    private boolean noRewardMessageEnabled;

    public NoCoinMessageDecorator(Message decoratedMessage)
    {
        super(decoratedMessage);
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
