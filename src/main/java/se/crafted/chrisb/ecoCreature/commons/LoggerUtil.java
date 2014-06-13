/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
 * ecoCreature is licensed under the GNU Lesser General Public License.
 *
 * ecoCreature is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ecoCreature is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.crafted.chrisb.ecoCreature.commons;

import java.util.logging.Logger;

public final class LoggerUtil
{
    private static final String LOG_NAME = "ecoCreature";
    private static LoggerUtil instance = new LoggerUtil();

    private Logger logger;
    private String name;
    private boolean debug;

    public static LoggerUtil getInstance()
    {
        return instance;
    }

    private LoggerUtil()
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

    public void debugTrue(String msg, boolean condition) {
        if (condition) {
            debug(msg);
        }
    }

    public void debug(String msg)
    {
        if (debug) {
            @SuppressWarnings("restriction")
            Class<?> aClass = sun.reflect.Reflection.getCallerClass();

            logger.info(format(aClass.getSimpleName() + ": " + msg));
        }
    }

    public String format(String msg)
    {
        return String.format("[%s] %s", name, msg);
    }
}
