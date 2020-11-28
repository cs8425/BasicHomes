package io.systemupdate.community.basichomes.utils;

import io.systemupdate.community.basichomes.BasicHomes;
import io.systemupdate.community.basichomes.utils.Lang;
import io.systemupdate.community.basichomes.utils.User;
//import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.ChatColor;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;

/**
 * Created by cs8425 on 2020/11/28.
 */
public class RequestManager {
	//protected JavaPlugin instance;
	protected BasicHomes instance;
	protected Lang lang;

	public boolean tpaReqOverwrite;
	public long tpaCooldown;
	public long tpaAckTimeout;

	protected ConcurrentHashMap<UUID, TPRequest> tpaReq = new ConcurrentHashMap<>();
	protected ConcurrentHashMap<UUID, Instant> tpaCooldownT = new ConcurrentHashMap<>();

	// clear CD for disconnected player
	//public ConcurrentHashMap<UUID, Instant> delayedCleanup = new ConcurrentHashMap<>();

	public RequestManager(BasicHomes plugin, Lang langConfig) {
		this.instance = plugin;
		this.lang = langConfig;
	}

	public void ClearPlayer(UUID uuid) {
		this.tpaReq.remove(uuid);

		// TODO: delayed clean up
		this.tpaCooldownT.remove(uuid);
	}

	public boolean CanCreateRequest(final Player p0) {
		UUID uuid0 = p0.getUniqueId();
		Instant timeout = this.tpaCooldownT.get(uuid0);
		if (timeout == null)  {
			return true;
		}
		Instant now = Instant.now();
		return now.isAfter(timeout);
	}

	public int CreateTPARequest(final Player p0, final Player p1) {
		return CreateRequest(p0, p1, TPRequest.Type.TPA);
	}

	public int CreateTPAHereRequest(final Player p0, final Player p1) {
		return CreateRequest(p0, p1, TPRequest.Type.TPAHere);
	}

	protected int CreateRequest(final Player p0, final Player p1, TPRequest.Type type) {
		UUID uuid0 = p0.getUniqueId();
		UUID uuid1 = p1.getUniqueId();

		TPRequest req = tpaReq.get(uuid1);
		if (req != null && !req.isTimeout()) {
			p0.sendMessage(this.lang.getText("tpa-pending"));
			return -1;
		}

		String msg;
		switch (type) {
		case TPA:
			msg = this.lang.getText("tpa-request-received");
			break;
		case TPAHere:
			msg = this.lang.getText("tpahere-request-received");
			break;
		default:
			return -1;
		}

		req = new TPRequest(p0, p1, type, this.tpaAckTimeout);
		this.tpaReq.put(uuid1, req);
		this.tpaCooldownT.put(uuid0, Instant.now().plusMillis(this.tpaCooldown));
		p0.sendMessage(this.lang.getText("tpa-request-send"));
		p1.sendMessage(String.format(msg, p0.getDisplayName()));
		p1.sendMessage(this.buildAcceptOrDeny());
		return 0;
	}

	public TextComponent buildAcceptOrDeny() {
		TextComponent acceptBtn = new TextComponent(this.lang.getText("request-accept"));
		acceptBtn.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept") );
		acceptBtn.setBold(true);
		acceptBtn.setColor(ChatColor.of("#008000"));

		TextComponent denyBtn = new TextComponent(this.lang.getText("request-deny"));
		denyBtn.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny") );
		denyBtn.setBold(true);
		denyBtn.setColor(ChatColor.of("#c70000"));

		TextComponent hints = new TextComponent(this.lang.getText("request-select"));
		hints.setBold(true);
		hints.setColor(ChatColor.of("#ff7b00"));

		TextComponent text = new TextComponent();
		text.addExtra(hints);
		text.addExtra(acceptBtn);
		text.addExtra(new TextComponent("  "));
		text.addExtra(denyBtn);
		return text;
	}

	public int AckDeny(final Player p0) {
		UUID uuid0 = p0.getUniqueId();
		TPRequest req = tpaReq.get(uuid0);
		if (req == null || req.isTimeout()) {
			p0.sendMessage(this.lang.getText("tpa-no-request"));
			return -1;
		}
		Player p1 = req.targetPlayer;
		UUID uuid1 = p1.getUniqueId();
		this.tpaReq.remove(uuid0);
		p0.sendMessage(String.format(this.lang.getText("tpa-request-denied"), p1.getDisplayName()));
		p1.sendMessage(String.format(this.lang.getText("tpa-request-been-denied"), p0.getDisplayName()));

		//p0.teleport(p1);
		//sender.sendMessage(this.lang.getText("tpa-teleported"));
		return 0;
	}

	public int AckAccept(final Player p0) {
		UUID uuid0 = p0.getUniqueId();
		TPRequest req = tpaReq.get(uuid0);
		if (req == null || req.isTimeout()) {
			p0.sendMessage(this.lang.getText("tpa-no-request"));
			return -1;
		}
		Player p1 = req.targetPlayer;
		UUID uuid1 = p1.getUniqueId();
		this.tpaReq.remove(uuid0);
		//p0.sendMessage(String.format(this.lang.getText("tpa-request-denied"), p1.getDisplayName()));
		//p1.sendMessage(String.format(this.lang.getText("tpa-request-been-denied"), p0.getDisplayName()));

		Player src;
		Player dst;
		switch (req.getReqType()) {
		case TPA:
			src = req.requestPlayer;
			dst = req.targetPlayer;
			break;
		case TPAHere:
			src = req.targetPlayer;
			dst = req.requestPlayer;
			break;
		default:
			return -1;
		}

		if (!dst.isOnline()) {
			src.sendMessage(BasicHomes.instance.lang.getText("Player-Not-Found"));
			return -1;
		}
		src.teleport(dst);
		//src.teleportAsync(dst);
		src.sendMessage(this.lang.getText("tpa-teleported"));

		return 0;
	}
}
