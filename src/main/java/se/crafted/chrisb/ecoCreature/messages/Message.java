package se.crafted.chrisb.ecoCreature.messages;

import java.util.Map;

public interface Message
{
    boolean isMessageOutputEnabled();

    void setMessageOutputEnabled(boolean messageOutputEnabled);

    boolean isCoinLoggingEnabled();

    void setCoinLoggingEnabled(boolean coinLoggingEnabled);

    String getTemplate();

    void setTemplate(String template);

    boolean isAmountInMessage();

    void addParameter(MessageToken token, String parameter);

    void removeParameter(MessageToken token);

    Map<MessageToken, String> getParameters();

    String getAssembledMessage();
}
