package se.crafted.chrisb.ecoCreature.models;

public class ecoMessage
{
    private Boolean isEnabled = true;
    private String message;

    public ecoMessage(String message)
    {
        this.message = message;
    }

    public ecoMessage(String message, Boolean isEnabled)
    {
        this.message = message;
        this.isEnabled = isEnabled;
    }

    public Boolean isEnabled()
    {
        return isEnabled;
    }

    public void isEnabled(Boolean isEnabled)
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
}
