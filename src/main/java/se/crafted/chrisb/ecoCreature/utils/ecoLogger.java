package se.crafted.chrisb.ecoCreature.utils;

import java.util.logging.Logger;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.managers.ecoConfigManager;

public class ecoLogger
{
    private ecoCreature plugin;
    private Logger logger;

    public ecoLogger(ecoCreature plugin)
    {
        this.plugin = plugin;
        logger = Logger.getLogger("Minecraft");
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
        if (ecoConfigManager.debug) {
            logger.info(format("DEBUG: " + msg));
        }
    }

    public String format(String msg)
    {
        return String.format("[%s] %s", plugin.getDescription().getName(), msg);
    }
}
