package io.systemupdate.community.basichomes.utils;

import org.bukkit.entity.Player;

import java.util.UUID;
import java.time.Instant;

/**
 * Created by cs8425 on 2020/11/28.
 */
public class TPRequest {
	public enum Type {
		TPA,
		TPAHere;
	}

	protected Type type; // 0: tpa, 1: tpahere
	//protected UUID requestPlayerUUID;
	//protected UUID targetPlayerUUID;
	protected Player requestPlayer;
	protected Player targetPlayer;
	protected Instant timeout;

	public TPRequest(final Player p0, final Player p1, Type reqType, long timeoutMs) {
		requestPlayer = p0;
		targetPlayer = p1;
		type = reqType;
		timeout = Instant.now().plusMillis(timeoutMs);
	}

	public boolean isTimeout() {
		Instant now = Instant.now();
		return now.isAfter(timeout);
	}

	public Type getReqType() {
		return this.type;
	}
}
