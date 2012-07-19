package se.crafted.chrisb.ecoCreature.utils;

import java.util.logging.Logger;

public class ecoLogger
{
    private String name = "ecoCreature";
    private Logger logger;

    public ecoLogger()
    {
        logger = Logger.getLogger("Minecraft");
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void info(String msg)
    {
        logger.info(format(msg));
    }

    public void warning(String msg)
    {
        logger.warning(format(msg));
    }

    public void severe(String msg)
    {
        logger.severe(format(msg));
    }

    public void debug(String msg)
    {
        // TODO: enable debug via command
        //logger.info(format("DEBUG: " + msg));
    }

    public String format(String msg)
    {
        return String.format("[%s] %s", name, msg);
    }
}
