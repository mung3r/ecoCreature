package se.crafted.chrisb.ecoCreature.commons;

import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.Bukkit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class UpdateTask implements Runnable
{
    private static final String DEV_BUKKIT_URL = "http://dev.bukkit.org/server-mods/ecocreature";
    private static final long CHECK_DELAY = 0;
    private static final long CHECK_PERIOD = 432000;

    private String pluginName;
    private String pluginVersion;
    private String latestVersion;

    public UpdateTask(ecoCreature plugin)
    {
        pluginName = plugin.getName();
        pluginVersion = plugin.getDescription().getVersion().split("-")[0];
        latestVersion = pluginVersion;

        if (Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, this, CHECK_DELAY, CHECK_PERIOD) < 0) {
            ecoCreature.getECLogger().warning("Failed to schedule UpdateTask task.");
        }
    }

    public void setPluginName(String pluginName)
    {
        this.pluginName = pluginName;
    }

    public String getPluginName()
    {
        return pluginName;
    }

    private void getLatestVersion()
    {
        try {
            URL url = new URL(DEV_BUKKIT_URL + "/files.rss");
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("item");
            Node firstNode = nodes.item(0);
            if (firstNode.getNodeType() == 1) {
                Element firstElement = (Element) firstNode;
                NodeList firstElementTagName = firstElement.getElementsByTagName("title");
                Element firstNameElement = (Element) firstElementTagName.item(0);
                NodeList firstNodes = firstNameElement.getChildNodes();
                latestVersion = firstNodes.item(0).getNodeValue().replace(pluginName, "").replaceFirst("v", "").trim();
            }
        }
        catch (Exception e) {
            ecoCreature.getECLogger().warning(e.getMessage());
        }
    }

    private boolean isOutOfDate()
    {
        boolean isOutOfDate = false;

        getLatestVersion();
        try {
            isOutOfDate = Double.parseDouble(pluginVersion.replaceFirst("\\.", "")) < Double.parseDouble(latestVersion.replaceFirst("\\.", ""));
        }
        catch (NumberFormatException e) {
            ecoCreature.getECLogger().warning(e.getMessage());
        }

        return isOutOfDate;
    }

    @Override
    public void run()
    {
        if (isOutOfDate()) {
            ecoCreature.getECLogger().warning(pluginName + " " + latestVersion + " is out! You are running: " + pluginName + " " + pluginVersion);
            ecoCreature.getECLogger().warning("Update ecoCreature at: " + DEV_BUKKIT_URL);
        }
    }
}
