package se.crafted.chrisb.ecoCreature.utils;

import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ecoUpdate implements Runnable
{
    private String devBukkitUrl;
    private String pluginName;
    private String pluginVersion;
    private String latestVersion;

    public ecoUpdate(ecoCreature plugin, String devBukkitUrl)
    {
        this.devBukkitUrl = devBukkitUrl;
        pluginName = plugin.getName();
        pluginVersion = plugin.getDescription().getVersion().split("-")[0];
        latestVersion = pluginVersion;
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
            URL url = new URL(devBukkitUrl + "/files.rss");
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
            ecoCreature.getEcoLogger().warning(e.getMessage());
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
            ecoCreature.getEcoLogger().warning(e.getMessage());
        }

        return isOutOfDate;
    }

    @Override
    public void run()
    {
        if (isOutOfDate()) {
            ecoCreature.getEcoLogger().warning(pluginName + " " + latestVersion + " is out! You are running: " + pluginName + " " + pluginVersion);
            ecoCreature.getEcoLogger().warning("Update ecoCreature at: " + devBukkitUrl);
        }
    }
}
