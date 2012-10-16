package se.crafted.chrisb.ecoCreature.messages;

import java.util.Map;

abstract class AbstractMessageDecorator implements Message
{
    private Message decoratedMessage;

    public AbstractMessageDecorator(Message decoratedMessage)
    {
        this.decoratedMessage = decoratedMessage;
    }

    @Override
    public boolean isMessageOutputEnabled()
    {
        return decoratedMessage.isMessageOutputEnabled();
    }

    @Override
    public void setMessageOutputEnabled(boolean messageOutputEnabled)
    {
        decoratedMessage.setMessageOutputEnabled(messageOutputEnabled);
    }

    @Override
    public boolean isCoinLoggingEnabled()
    {
        return decoratedMessage.isCoinLoggingEnabled();
    }

    @Override
    public void setCoinLoggingEnabled(boolean coinLoggingEnabled)
    {
        decoratedMessage.setCoinLoggingEnabled(coinLoggingEnabled);
    }

    @Override
    public String getTemplate()
    {
        return decoratedMessage.getTemplate();
    }

    @Override
    public void setTemplate(String template)
    {
        decoratedMessage.setTemplate(template);
    }

    @Override
    public boolean isAmountInMessage()
    {
        return decoratedMessage.isAmountInMessage();
    }

    @Override
    public String getAssembledMessage(Map<MessageToken, String> parameters)
    {
        return decoratedMessage.getAssembledMessage(parameters);
    }
}
