package se.crafted.chrisb.ecoCreature.messages;

import java.util.HashMap;
import java.util.Map;

public class DefaultMessage implements Message
{
    private static final String DEFAULT_TEMPLATE = "Default message";

    protected boolean messageOutputEnabled;
    protected boolean coinLoggingEnabled;

    protected String template;
    protected Map<MessageToken, String> parameters;

    public DefaultMessage()
    {
        this(DEFAULT_TEMPLATE);
    }

    public DefaultMessage(String template)
    {
        this.template = convertMessage(template);
        parameters = new HashMap<MessageToken, String>();

        messageOutputEnabled = true;
        coinLoggingEnabled = false;
    }

    @Override
    public boolean isMessageOutputEnabled()
    {
        return messageOutputEnabled;
    }

    @Override
    public void setMessageOutputEnabled(boolean messageOutputEnabled)
    {
        this.messageOutputEnabled = messageOutputEnabled;
    }

    @Override
    public boolean isCoinLoggingEnabled()
    {
        return coinLoggingEnabled;
    }

    @Override
    public void setCoinLoggingEnabled(boolean coinLoggingEnabled)
    {
        this.coinLoggingEnabled = coinLoggingEnabled;
    }

    @Override
    public String getTemplate()
    {
        return template;
    }

    @Override
    public void setTemplate(String template)
    {
        this.template = template;
    }

    @Override
    public void addParameter(MessageToken token, String parameter)
    {
        parameters.put(token, parameter);
    }

    @Override
    public void removeParameter(MessageToken token)
    {
        parameters.remove(token);
    }

    @Override
    public Map<MessageToken, String> getParameters()
    {
        return parameters;
    }

    private static String convertMessage(String message)
    {
        if (message != null) {
            return message.replaceAll("&&", "\b").replaceAll("&", "§").replaceAll("\b", "&");
        }

        return null;
    }
}
