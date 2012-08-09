package se.crafted.chrisb.ecoCreature.utils;

import java.util.logging.Logger;

public class ecoLogger
{
    private static final String LOG_NAME = "ecoCreature";

    private Logger logger;
    private String name;
    private boolean isDebug;

    public ecoLogger()
    {
        logger = Logger.getLogger("Minecraft");
        isDebug = false;
        name = LOG_NAME;
    }

    public boolean isDebug()
    {
        return isDebug;
    }

    public void setDebug(boolean isDebug)
    {
        this.isDebug = isDebug;
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
        if (isDebug) {
            logger.info(format("DEBUG: " + msg));
        }
    }

    public String format(String msg)
    {
        return String.format("[%s] %s", name, msg);
    }
}
