package se.crafted.chrisb.ecoCreature.messages;

public class SpawnerCampMessage extends DefaultMessage
{
    boolean spawnerCampMessageEnabled;

    public SpawnerCampMessage(String template)
    {
        super(template);
        spawnerCampMessageEnabled = true;
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
