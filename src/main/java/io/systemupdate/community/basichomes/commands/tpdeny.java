package io.systemupdate.community.basichomes.commands;

import io.systemupdate.community.basichomes.BasicHomes;
import io.systemupdate.community.basichomes.utils.RequestManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cs8425 on 2020/11/28.
 */
public class tpdeny implements TabExecutor {

	List<String> empty = new ArrayList<String>();

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		return empty; // empty list
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (!(sender instanceof Player)) {
			sender.sendMessage(BasicHomes.instance.lang.getText("console-sender"));
			return true;
		}

		if(args.length != 0){
			return false; // send "usage" entry (if defined) in plugin.yml
		}

		if (!sender.hasPermission("basichomes.tpdeny")) {
			sender.sendMessage(BasicHomes.instance.lang.getText("no-permission"));
			return true;
		}

		final Player p0 = (Player)sender;
		RequestManager rm = BasicHomes.instance.getRequestManager();
		rm.AckDeny(p0);
		return true;
	}
}
