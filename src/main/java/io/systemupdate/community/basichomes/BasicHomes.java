package io.systemupdate.community.basichomes;

import io.systemupdate.community.basichomes.commands.*;
import io.systemupdate.community.basichomes.listeners.PlayerEventListener;
import io.systemupdate.community.basichomes.utils.Lang;
import io.systemupdate.community.basichomes.utils.User;
import io.systemupdate.community.basichomes.utils.RequestManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;

/**
 * Created by SystemUpdate (http://systemupdate.io) on 16/06/15.
 */
public class BasicHomes extends JavaPlugin {
	public PluginManager pm = Bukkit.getServer().getPluginManager();
	public static BasicHomes instance;

	// configs
	public Lang lang;
	public List<String> illegalCharacters = new ArrayList<>();
	protected long tpHomeCooldown;
	protected long tpBackCooldown;
	protected long tpBackTimeout;


	// runtime data
	public ConcurrentHashMap<UUID, User> userProfiles = new ConcurrentHashMap<>();

	public ConcurrentHashMap<UUID, Instant> tpHomeCooldownT = new ConcurrentHashMap<>();
	public ConcurrentHashMap<UUID, Instant> tpBackCooldownT = new ConcurrentHashMap<>();

	RequestManager rm;

	@Override
	public void onEnable(){
		this.registerEvents();
	}

	@Override
	public void onDisable(){
		org.bukkit.event.HandlerList.unregisterAll(this);
		userProfiles.clear();
		tpHomeCooldownT.clear();
		tpBackCooldownT.clear();
		Bukkit.getServer().getScheduler().cancelTasks(this);
	}

	private void registerEvents(){
		BasicHomes.instance = this;
		this.saveDefaultConfig();
		lang = new Lang();
		lang.initilize();

		this.rm = new RequestManager(this, lang);

		ConfigurationSection config = this.getConfig();
		if(config.getConfigurationSection("illegalCharacters") != null){
			for(String i : this.getConfig().getStringList("illegalCharacters")){
				illegalCharacters.add(i.toLowerCase());
			}
		}
		illegalCharacters.add(".");

		tpHomeCooldown = config.getLong("tp-home-cooldown", 1000);
		tpBackCooldown = config.getLong("tp-back-cooldown", 3000);
		tpBackTimeout = config.getLong("tp-back-timeout", 1800000L);

		rm.tpaReqOverwrite = config.getBoolean("tpa-overwrite", false);
		rm.tpaCooldown = config.getLong("tpa-cooldown", 5000);
		rm.tpaAckTimeout = config.getLong("tpa-ack-timeout", 3000);

		File folder = new File(getDataFolder(), "userdata");
		if(!folder.exists()){
			folder.mkdirs();
		}
		for(Player i : Bukkit.getServer().getOnlinePlayers()){
			userProfiles.put(i.getUniqueId(), new User(i.getUniqueId()));
		}

		pm.registerEvents(new PlayerEventListener(), this);
		this.getCommand("basichomes").setExecutor(new basichomes());
		this.getCommand("homes").setExecutor(new homes());
		this.getCommand("home").setExecutor(new home());
		this.getCommand("sethome").setExecutor(new sethome());
		this.getCommand("delhome").setExecutor(new delhome());

		this.getCommand("tpa").setExecutor(new tpa());
		this.getCommand("tpahere").setExecutor(new tpahere());
		this.getCommand("tpaccept").setExecutor(new tpaccept());
		this.getCommand("tpdeny").setExecutor(new tpdeny());
		this.getCommand("back").setExecutor(new back());
	}

	public long getTpBackTimeout() {
		return tpBackTimeout;
	}

	public RequestManager getRequestManager() {
		return rm;
	}
}
