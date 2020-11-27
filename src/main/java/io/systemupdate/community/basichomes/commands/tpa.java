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
public class tpa implements TabExecutor {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		return null; // default to online player list
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (!(sender instanceof Player)) {
			sender.sendMessage(BasicHomes.instance.lang.getText("console-sender"));
			return true;
		}

		if(args.length != 1){
			return false; // send "usage" entry (if defined) in plugin.yml
		}

		if (!sender.hasPermission("basichomes.tpa")) {
			sender.sendMessage(BasicHomes.instance.lang.getText("no-permission"));
			return true;
		}

		final Player p0 = (Player)sender;
		Player p1 = Bukkit.getServer().getPlayer(args[0]);
		if (p1 == null) {
			// try case insensitive
			p1 = Bukkit.getServer().getPlayerExact(args[0]);
			if (p1 == null) {
				sender.sendMessage(BasicHomes.instance.lang.getText("Player-Not-Found"));
				return true;
			}
		}
		if (!p1.isOnline()) {
			sender.sendMessage(BasicHomes.instance.lang.getText("Player-Not-Found"));
			return true;
		}
		if (p0 == p1) {
			sender.sendMessage(BasicHomes.instance.lang.getText("tpa-self"));
			return true;
		}

		RequestManager rm = BasicHomes.instance.getRequestManager();
		if (!rm.CanCreateRequest(p0)) {
			sender.sendMessage(BasicHomes.instance.lang.getText("tpa-cooldown"));
			return true;
		}

		rm.CreateTPARequest(p0, p1);

		/*UUID uuid0 = p0.getUniqueId();
		UUID uuid1 = p1.getUniqueId();

		TPRequest req = BasicHomes.instance.tpaReq.get(uuid1);
		if (req != null && !req.isTimeout()) {
			sender.sendMessage(BasicHomes.instance.lang.getText("tpa-pending"));
			return true;
		}

		TPRequest req = new TPRequest(uuid0, uuid1, 0, BasicHomes.instance.tpaAckTimeout);
		BasicHomes.instance.tpaReq.put(uuid1, req);
		BasicHomes.instance.tpaCooldownT.put(uuid0, Instant.now());
		sender.sendMessage(BasicHomes.instance.lang.getText("tpa-request-send"));
		p1.sendMessage(String.format(BasicHomes.instance.lang.getText("tpa-request-received"), p0.getDisplayName()));*/

		//TODO Cooldown + Permission to evade + countdown + permission to evade
		//p0.teleport(p1);
		//sender.sendMessage(BasicHomes.instance.lang.getText("tpa-teleported"));
		return true;
	}
}
