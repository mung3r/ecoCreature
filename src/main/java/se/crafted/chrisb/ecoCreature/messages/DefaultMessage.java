package se.crafted.chrisb.ecoCreature.messages;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DefaultMessage implements Message
{
    private static final String DEFAULT_TEMPLATE = "Default message";

    private boolean messageOutputEnabled;
    private boolean coinLoggingEnabled;

    private String template;
    private Map<MessageToken, String> parameters;

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
    public boolean isAmountInMessage()
    {
        return template != null && template.contains(MessageToken.AMOUNT.toString());
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

    @Override
    public String getAssembledMessage()
    {
        String assembledMessage = template;

        if (assembledMessage != null && assembledMessage.length() > 0) {
            for (Entry<MessageToken, String> entry : parameters.entrySet()) {
                if (entry.getKey() == MessageToken.AMOUNT) {
                    assembledMessage = assembledMessage.replaceAll(entry.getKey().toString(), entry.getValue().replaceAll("\\$", "\\\\\\$"));
                }
                else if (entry.getKey() == MessageToken.ITEM) {
                    assembledMessage = assembledMessage.replaceAll(entry.getKey().toString(), toCamelCase(entry.getValue()));
                }
                else {
                    assembledMessage = assembledMessage.replaceAll(entry.getKey().toString(), entry.getValue());
                }
            }
        }

        return assembledMessage;
    }

    private static String toCamelCase(String rawItemName)
    {
        String[] rawItemNameParts = rawItemName.split("_");
        StringBuilder itemNameBuilder = new StringBuilder("");

        for (String itemNamePart : rawItemNameParts) {
            itemNameBuilder.append(" ").append(toProperCase(itemNamePart));
        }

        String itemName = itemNameBuilder.toString();
        if (itemName.trim().equals("Air")) {
            itemName = "Fists";
        }

        if (itemName.trim().equals("Bow")) {
            itemName = "Bow & Arrow";
        }

        return itemName.trim();
    }

    private static String toProperCase(String str)
    {
        if (str.length() < 1) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private static String convertMessage(String message)
    {
        if (message != null) {
            return message.replaceAll("&&", "\b").replaceAll("&", "§").replaceAll("\b", "&");
        }

        return null;
    }
}
