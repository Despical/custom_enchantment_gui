package me.despical.customenchgui;

import me.despical.customenchgui.event.InteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Despical
 * <p>
 * Created at 9.04.2021
 */
public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();


        new InteractEvent(this);
    }
}