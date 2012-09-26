package se.crafted.chrisb.ecoCreature.messages;

public class NoCampMessageDecorator extends AbstractMessageDecorator
{
    private boolean spawnerCampMessageEnabled;

    public NoCampMessageDecorator(Message decoratedMessage)
    {
        super(decoratedMessage);
        spawnerCampMessageEnabled = false;
    }

    public boolean isSpawnerCampMessageEnabled()
    {
        return spawnerCampMessageEnabled;
    }

    public void setSpawnerCampMessageEnabled(boolean spawnerCampMessageEnabled)
    {
        this.spawnerCampMessageEnabled = spawnerCampMessageEnabled;
    }

    @Override
    public boolean isMessageOutputEnabled()
    {
        return spawnerCampMessageEnabled && super.isMessageOutputEnabled();
    }
}
