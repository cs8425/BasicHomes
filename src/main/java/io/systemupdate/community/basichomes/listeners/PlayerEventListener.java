package io.systemupdate.community.basichomes.listeners;

import io.systemupdate.community.basichomes.BasicHomes;
import io.systemupdate.community.basichomes.utils.User;
import io.systemupdate.community.basichomes.utils.RequestManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;

import java.util.UUID;
import java.util.List;

/**
 * Created by SystemUpdate (http://systemupdate.io) on 16/06/15.
 */
public class PlayerEventListener implements Listener {

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event){
		UUID playerUUID = event.getPlayer().getUniqueId();
		BasicHomes.instance.userProfiles.put(playerUUID, new User(playerUUID));
	}

	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent event){
		UUID playerUUID = event.getPlayer().getUniqueId();
		clearAndFlush(playerUUID);
	}

	@EventHandler
	public void onPlayerKick(final PlayerKickEvent event){
		UUID playerUUID = event.getPlayer().getUniqueId();
		clearAndFlush(playerUUID);
	}

	protected void clearAndFlush(UUID playerUUID) {
		BasicHomes.instance.userProfiles.remove(playerUUID);

		// TODO: delayed clean up
		BasicHomes.instance.tpHomeCooldownT.remove(playerUUID);
		BasicHomes.instance.tpBackCooldownT.remove(playerUUID);

		RequestManager rm = BasicHomes.instance.getRequestManager();
		rm.ClearPlayer(playerUUID);
	}

	@EventHandler
	public void onPlayerDeathEvent(final PlayerDeathEvent event){
		Player player = event.getEntity();
		UUID playerUUID = player.getUniqueId();
		Location loc = player.getLocation();
		User userProfile = BasicHomes.instance.userProfiles.get(playerUUID);
		userProfile.setLastDeath(loc);

		if (player.hasPermission("basichomes.keepinventory")) {
			event.setKeepInventory(true);
			List<ItemStack> drops = event.getDrops();
			drops.clear();
		}
		if (player.hasPermission("basichomes.keeplevel")) {
			event.setKeepLevel(true);
		}
	}
}
