package se.crafted.chrisb.ecoCreature.models;

public class ecoMessage
{
    private boolean isEnabled = true;
    private String message;

    public ecoMessage(String message)
    {
        this.message = message;
    }

    public ecoMessage(String message, boolean isEnabled)
    {
        this.message = message;
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled()
    {
        return isEnabled;
    }

    public void isEnabled(boolean isEnabled)
    {
        this.isEnabled = isEnabled;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return String.format("ecoMessage [isEnabled=%s, message=%s]", isEnabled, message);
    }
}
