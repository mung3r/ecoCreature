package se.crafted.chrisb.ecoCreature.messages;

public interface Message
{
    public void setTemplate(String template);

    public void addParameter(MessageToken token, String parameter);

    public void send();
}
