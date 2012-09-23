package se.crafted.chrisb.ecoCreature.messages;

public enum MessageToken {
    PLAYER("<plr>"),
    AMOUNT("<amt>"),
    ITEM("<itm>"),
    CREATURE("<crt>");

    String name;

    private MessageToken(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
