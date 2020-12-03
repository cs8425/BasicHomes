package io.systemupdate.community.basichomes.commands;

import io.systemupdate.community.basichomes.BasicHomes;
import io.systemupdate.community.basichomes.utils.User;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SystemUpdate (http://systemupdate.io) on 16/06/15.
 */
public class delhome implements TabExecutor {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		// check not player
		if(!(sender instanceof Player)){
			return null;
		}

		List<String> toreturn = new ArrayList<String>();
		Player player = (Player)sender;
		if(!sender.hasPermission("basichomes.delhome")){
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
		boolean isPlayer = true;
		if(!(sender instanceof Player)){
			isPlayer = false;
		}
		if(args.length == 1 && (sender.hasPermission("basichomes.delhome") || !isPlayer)){
			String homeName = args[0];
			User userProfile = null;
			if(homeName.contains(":") && (sender.hasPermission("basichomes.delhome.other") || !isPlayer)){
				String userName = homeName.split(":", 2)[0];
				userProfile = User.getUser(userName);
				if(userProfile == null){
					sender.sendMessage(BasicHomes.instance.lang.getText("Player-Not-Found"));
					return true;
				}
				homeName = homeName.replaceFirst(userName + ":", "");
			}else if(!isPlayer){
				sender.sendMessage(BasicHomes.instance.lang.getText("delhome-invalid-usage-admin"));
				return false;
			}else{
				userProfile = BasicHomes.instance.userProfiles.get(((Player)sender).getUniqueId());
			}

			Location homeLoc = userProfile.getHome(homeName);
			if(homeLoc == null){
				sender.sendMessage(BasicHomes.instance.lang.getText("home-dont-exist"));
				return true;
			}

			userProfile.delHome(homeName);
			sender.sendMessage(String.format(BasicHomes.instance.lang.getText("home-deleted"), homeName));
			return true;
		}else{
			if(sender.hasPermission("basichomes.delhome.other") || !isPlayer){
				sender.sendMessage(BasicHomes.instance.lang.getText("delhome-invalid-usage-admin"));
			}else if(sender.hasPermission("basichomes.delhome")){
				sender.sendMessage(BasicHomes.instance.lang.getText("delhome-invalid-usage"));
			}else{
				sender.sendMessage(BasicHomes.instance.lang.getText("no-permission"));
			}
		}
		return false;
	}
}
