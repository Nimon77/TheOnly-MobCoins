package me.aglerr.mobcoins.listeners;

import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import me.aglerr.mobcoins.MobCoins;
import me.aglerr.mobcoins.listeners.listeners.EntityDeathListener;
import me.aglerr.mobcoins.listeners.listeners.MythicMobsDeathListener;
import me.aglerr.mobcoins.listeners.listeners.PlayerListeners;
import me.aglerr.mobcoins.listeners.listeners.PlayerRedeemCoins;
import me.aglerr.mobcoins.managers.managers.DependencyManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class ListenerHandler {

    private final List<Listener> listenerList = new ArrayList<>();

    private final MobCoins plugin;
    public ListenerHandler(MobCoins plugin){
        this.plugin = plugin;

        this.listenerList.add(new PlayerListeners(plugin));
        this.listenerList.add(new PlayerRedeemCoins(plugin));
        this.listenerList.add(new EntityDeathListener(plugin));

        if(DependencyManager.MYTHIC_MOBS){
            this.listenerList.add(new MythicMobsDeathListener(plugin));
        }
    }

    public void registerAllListeners(){
        for(Listener listener : this.listenerList){
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        }
    }

}
