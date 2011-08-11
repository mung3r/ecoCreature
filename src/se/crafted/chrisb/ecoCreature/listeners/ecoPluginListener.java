package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import com.nijikokun.register.payment.Methods;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ecoPluginListener extends ServerListener
{
    private ecoCreature plugin;
    private Methods methods;

    public ecoPluginListener(ecoCreature plugin)
    {
        this.plugin = plugin;
        methods = new Methods();
    }

    @Override
    public void onPluginDisable(PluginDisableEvent event)
    {
        if (methods != null && methods.hasMethod()) {
            Boolean check = methods.checkDisabled(event.getPlugin());

            if (check) {
                this.plugin.method = null;
            }
        }
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event)
    {
        if (!methods.hasMethod()) {
            if (methods.setMethod(event.getPlugin())) {
                this.plugin.method = methods.getMethod();
            }
        }
    }
}
