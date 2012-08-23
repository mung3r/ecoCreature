package se.crafted.chrisb.ecoCreature.messages;

enum MessageToken {
    PLAYER_TOKEN("<plr>"),
    AMOUNT_TOKEN("<amt>"),
    ITEM_TOKEN("<itm>"),
    CREATURE_TOKEN("<crt>");
    
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
