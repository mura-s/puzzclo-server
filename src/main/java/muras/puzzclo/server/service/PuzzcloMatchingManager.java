/*
 * Copyright (C) 2014 Seiya Muramatsu. All rights reserved.
 */
package muras.puzzclo.server.service;

import java.util.ArrayList;
import java.util.List;

public class PuzzcloMatchingManager {
	private static PuzzcloMatchingManager INSTANCE = new PuzzcloMatchingManager();
	private List<PuzzcloSocketListener> clients = new ArrayList<PuzzcloSocketListener>();

	private PuzzcloMatchingManager() {
	}

	protected static PuzzcloMatchingManager getInstance() {
		return INSTANCE;
	}

	/**
	 * クライアントを追加します
	 * */
	protected void join(PuzzcloSocketListener socket) {
		clients.add(socket);
	}

	/**
	 * クライアントを削除します
	 * */
	protected void bye(PuzzcloSocketListener socket) {
		clients.remove(socket);
	}

	/**
	 * すべてのユーザへメッセージを送信します。
	 * */
	protected void sendToAll(String message) {
		for (PuzzcloSocketListener member : clients) {
			member.getSession().getRemote().sendStringByFuture(message);
		}
	}
}
