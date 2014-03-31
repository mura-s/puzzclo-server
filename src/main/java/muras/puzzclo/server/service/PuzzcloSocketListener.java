/*
 * Copyright (C) 2014 Seiya Muramatsu. All rights reserved.
 */
package muras.puzzclo.server.service;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class PuzzcloSocketListener {
	private Session session;

	private String myName = "";

	private String opponentName = "";
	
	// 対戦相手接続待ちか
	private boolean isWaitConnect = false;

	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = session;
		System.out.println("接続されました。");
		PuzzcloMatchingManager.getInstance().join(this);
	}

	@OnWebSocketMessage
	public void onText(String message) {
		PuzzcloMatchingManager.getInstance().sendMessage(this, message);
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		System.out.println("切断されました。");
		PuzzcloMatchingManager.getInstance().bye(this);
	}

	public Session getSession() {
		return this.session;
	}

	public String getMyName() {
		return myName;
	}

	public void setPlayerName(String myName) {
		this.myName = myName;
	}

	public String getOpponentName() {
		return opponentName;
	}

	public void setOpponentName(String opponentName) {
		this.opponentName = opponentName;
	}

	public boolean isWaitConnect() {
		return isWaitConnect;
	}

	public void setWaitConnect(boolean isWaitConnect) {
		this.isWaitConnect = isWaitConnect;
	}
}