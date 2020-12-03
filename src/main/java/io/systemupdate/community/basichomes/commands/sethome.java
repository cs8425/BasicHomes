package io.systemupdate.community.basichomes.commands;

import io.systemupdate.community.basichomes.BasicHomes;
import io.systemupdate.community.basichomes.utils.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by SystemUpdate (http://systemupdate.io) on 16/06/15.
 */
public class sethome implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(BasicHomes.instance.lang.getText("console-sender"));
			return false;
		}

		if(args.length == 1){
			Player player = (Player)sender;
			String homeName = args[0];
			User userProfile = null;
			if(homeName.contains(":") && sender.hasPermission("basichomes.sethome.other")){
				String userName = homeName.split(":", 2)[0];
				userProfile = User.getUser(userName);
				if(userProfile == null){
					sender.sendMessage(BasicHomes.instance.lang.getText("Player-Not-Found"));
					return true;
				}
				homeName = homeName.replaceFirst(userName + ":", "");
			}else{
				userProfile = BasicHomes.instance.userProfiles.get(player.getUniqueId());
			}

			if(userProfile.getHome(homeName) != null){
				sender.sendMessage(BasicHomes.instance.lang.getText("sethome-home-exists"));
				return true;
			}

			if(userProfile.getHomeCount() >= userProfile.getMaxHomes() && !player.hasPermission("basichomes.sethome.other")){
				sender.sendMessage(BasicHomes.instance.lang.getText("max-homes-reached"));
				return true;
			}

			String lowHomeName = homeName.toLowerCase();
			for(String i : BasicHomes.instance.illegalCharacters){ // home-contains-illegal-character
				if(lowHomeName.contains(i)){
					sender.sendMessage(BasicHomes.instance.lang.getText("home-contains-illegal-character"));
					return true;
				}
			}

			userProfile.addHome(homeName, player.getLocation());
			sender.sendMessage(String.format(BasicHomes.instance.lang.getText("sethome-successful"), homeName));
			return true;
		}else{
			if(sender.hasPermission("basichomes.sethome.other")){
				sender.sendMessage(BasicHomes.instance.lang.getText("sethome-invalid-usage-admin"));
			}else if(sender.hasPermission("basichomes.sethome")){
				sender.sendMessage(BasicHomes.instance.lang.getText("sethome-invalid-usage"));
			}else{
				sender.sendMessage(BasicHomes.instance.lang.getText("no-permission"));
			}
		}
		return false;
	}
}
