/*
 * Copyright (C) 2014 Seiya Muramatsu. All rights reserved.
 */
package muras.puzzclo.server.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import muras.puzzclo.server.domain.SocketMatchData;
import muras.puzzclo.server.utils.GameState;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

class PuzzcloMatchingManager {
	private static PuzzcloMatchingManager INSTANCE = new PuzzcloMatchingManager();
	private List<PuzzcloSocketListener> clients = Collections
			.synchronizedList(new ArrayList<PuzzcloSocketListener>());

	private PuzzcloMatchingManager() {
	}

	static PuzzcloMatchingManager getInstance() {
		return INSTANCE;
	}

	/**
	 * クライアントを追加する。
	 */
	void join(PuzzcloSocketListener socket) {
		clients.add(socket);
	}

	/**
	 * クライアントを削除する。
	 */
	void bye(PuzzcloSocketListener socket) {
		clients.remove(socket);
	}

	/**
	 * ユーザへメッセージを送信する。
	 */
	void sendMessage(PuzzcloSocketListener socket, String message) {
		SocketMatchData data = toMatchData(message);
		String sendMessage = "";

		switch (GameState.valueOf(data.getState())) {
		case CONNECT_SERVER_AS_SERVER:
			setPlayerName(socket, data);
			
			data.setMyName(socket.getPlayerName());
			sendMessage = toJson(data);
			
			socket.getSession().getRemote().sendStringByFuture(sendMessage);
			break;

		case CONNECT_SERVER_AS_CLIENT:
			setPlayerName(socket, data);
			data.setMyName(socket.getPlayerName());
			
			List<String> opponentList = new ArrayList<>();
			for (PuzzcloSocketListener member : clients) {
				if (!data.getMyName().equals(member.getPlayerName())) {
					opponentList.add(member.getPlayerName());
				}
			}
			data.setOpponentList(opponentList);
			
			sendMessage = toJson(data);
			
			socket.getSession().getRemote().sendStringByFuture(sendMessage);
			break;
		
		case SELECT_OPPONENT:
			// TODO
			
			break;

		default:
			break;
		}

	}

	/**
	 * socketにプレイヤー名を設定する。</ br>
	 * 
	 * 同じ名前のplayerがいるか確認し、いたらサフィックスをつける
	 * 
	 * @param socket
	 * @param data
	 */
	private void setPlayerName(PuzzcloSocketListener socket,
			SocketMatchData data) {
		int existNameCount = 0;
		for (PuzzcloSocketListener member : clients) {
			if (data.getMyName().equals(member.getPlayerName())) {
				existNameCount++;
			}
		}

		if (existNameCount == 0) {
			socket.setPlayerName(data.getMyName());
		} else {
			socket.setPlayerName(data.getMyName() + "_" + existNameCount);
		}
	}

	private String toJson(SocketMatchData data) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonData = "";
		try {
			jsonData = mapper.writeValueAsString(data);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonData;
	}

	private SocketMatchData toMatchData(String json) {
		ObjectMapper mapper = new ObjectMapper();
		SocketMatchData data = null;

		try {
			data = mapper.readValue(json, SocketMatchData.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return data;
	}
}
