package se.crafted.chrisb.ecoCreature.commons;

import java.util.logging.Logger;

public class ECLogger
{
    private static final String LOG_NAME = "ecoCreature";
    private static ECLogger ecLogger;

    private Logger logger;
    private String name;
    private boolean isDebug;

    public static ECLogger getInstance()
    {
        if (ecLogger == null) {
            ecLogger = new ECLogger();
        }
        return ecLogger;
    }

    private ECLogger()
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
