package se.crafted.chrisb.ecoCreature.commons;

import java.util.logging.Logger;

public class ECLogger
{
    private static final String LOG_NAME = "ecoCreature";
    private static ECLogger instance = new ECLogger();

    private Logger logger;
    private String name;
    private boolean debug;

    public static ECLogger getInstance()
    {
        return instance;
    }

    private ECLogger()
    {
        logger = Logger.getLogger("Minecraft");
        debug = false;
        name = LOG_NAME;
    }

    public boolean isDebug()
    {
        return debug;
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
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
        if (debug) {
            logger.info(format("DEBUG: " + msg));
        }
    }

    public String format(String msg)
    {
        return String.format("[%s] %s", name, msg);
    }
}
