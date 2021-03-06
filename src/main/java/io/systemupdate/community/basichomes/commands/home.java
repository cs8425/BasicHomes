package io.systemupdate.community.basichomes.commands;

import io.systemupdate.community.basichomes.BasicHomes;
import io.systemupdate.community.basichomes.utils.User;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by SystemUpdate (http://systemupdate.io) on 16/06/15.
 */
public class home implements TabExecutor {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		// check not player
		if(!(sender instanceof Player)){
			return null;
		}

		List<String> toreturn = new ArrayList<String>();
		Player player = (Player)sender;
		if(!sender.hasPermission("basichomes.home")){
			return toreturn; // no permission, return empty
		}

		// TODO: TabComplete other user's home
		if (args.length == 1){
			User user = BasicHomes.instance.userProfiles.get(player.getUniqueId());
			for(String i : user.getHomes()){
				toreturn.add(i);
			}
		}
		return toreturn;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(BasicHomes.instance.lang.getText("console-sender"));
			return false;
		}

		if(args.length == 1){
			if(sender.hasPermission("basichomes.home")){
				Player player = (Player)sender;
				String homeName = args[0];
				User userProfile = null;
				if(homeName.contains(":") && sender.hasPermission("basichomes.home.other")){
					String userName = homeName.split(":", 2)[0];
					userProfile = User.getUser(userName);
					if(userProfile == null){
						sender.sendMessage(BasicHomes.instance.lang.getText("Player-Not-Found"));
						return true;
					}

					homeName = homeName.replaceFirst(userName + ":", "");
				} else {
					userProfile = BasicHomes.instance.userProfiles.get(player.getUniqueId());
				}

				Location homeLoc = userProfile.getHome(homeName);
				if(homeLoc == null){
					sender.sendMessage(BasicHomes.instance.lang.getText("home-dont-exist"));
					return true;
				}

				// TODO: permission to tp leashEntities
				// copy leashEntities
				Set<LivingEntity> leashEntities = userProfile.getLeashEntity();
				// final User userProfile0 = userProfile;
				for(LivingEntity ent : leashEntities){
					BasicHomes.instance.leashTPing.put(ent, ent);
				}

				// TODO: Cooldown + Permission to evade + countdown + permission to evade
				//player.teleport(homeLoc, TeleportCause.PLUGIN);
				player.teleportAsync(homeLoc, TeleportCause.PLUGIN).thenApply(s -> {
					// tp leashEntities
					BasicHomes.instance.runTaskLater(() -> {
						for(LivingEntity ent : leashEntities){
							/*ent.teleportAsync(homeLoc, TeleportCause.PLUGIN).thenApply(s -> {
								ent.setLeashHolder(player);
								return s;
							});*/
							ent.teleportAsync(homeLoc);
							ent.setLeashHolder(player);
							// userProfile0.addLeashEntity(ent);
							BasicHomes.instance.leashTPing.remove(ent);
						}
					}, 4);
					return s;
				});

				sender.sendMessage(BasicHomes.instance.lang.getText("teleported-home"));
				return true;
			}else{
				sender.sendMessage(BasicHomes.instance.lang.getText("no-permission"));
			}
		}else{
			if(sender.hasPermission("basichomes.home.other")){
				sender.sendMessage(BasicHomes.instance.lang.getText("home-invalid-usage-admin"));
			}else{
				sender.sendMessage(BasicHomes.instance.lang.getText("home-invalid-usage"));
			}
		}

		return false;
	}
}
