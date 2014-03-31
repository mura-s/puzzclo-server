/*
 * Copyright (C) 2014 Seiya Muramatsu. All rights reserved.
 */
package muras.puzzclo.server.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import muras.puzzclo.server.domain.SocketMatchData;

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
		// 対戦相手がいたら、切断されたことを通知
		PuzzcloSocketListener opponentSocket = getOpponent(socket);

		if (opponentSocket != null) {
			SocketMatchData opponentData = new SocketMatchData();
			opponentData.setConnected(false);
			String opponentMessage = toJson(opponentData);
			opponentSocket.getSession().getRemote()
					.sendStringByFuture(opponentMessage);
		}

		// 自分をリストからremove
		clients.remove(socket);
	}

	/**
	 * ユーザへメッセージを送信する。
	 */
	void sendMessage(PuzzcloSocketListener socket, String message) {
		SocketMatchData matchData = toMatchData(message);
		String sendMessage = "";
		PuzzcloSocketListener opponentSocket = null;
		SocketMatchData opponentData = new SocketMatchData();
		String opponentMessage = "";

		switch (GameState.valueOf(matchData.getState())) {
		case CONNECT_SERVER_AS_SERVER:
			setPlayerName(socket, matchData);
			socket.setWaitConnect(true);
			matchData.setMyName(socket.getMyName());

			sendMessage = toJson(matchData);
			socket.getSession().getRemote().sendStringByFuture(sendMessage);
			break;

		case CONNECT_SERVER_AS_CLIENT:
			setPlayerName(socket, matchData);
			matchData.setMyName(socket.getMyName());

			// 自分以外をSocketMatchDataの対戦相手リストに追加する
			setOpponentListExceptOwn(socket, matchData);

			sendMessage = toJson(matchData);
			socket.getSession().getRemote().sendStringByFuture(sendMessage);
			break;

		case SELECT_OPPONENT:
			// 対戦相手を設定する
			opponentSocket = setOpponent(socket, matchData);

			if (socket.getOpponentName().equals("")) {
				// 相手がいない、または対戦中
				matchData.setOpponentName("");
				sendMessage = toJson(matchData);
				socket.getSession().getRemote().sendStringByFuture(sendMessage);
			} else {
				// 相手がいる

				// 先行・後攻を決める (0:自分が先行、1:相手が先行)
				int whichTurn = (int) (Math.random() * 2);
				if (whichTurn == 0) {
					matchData.setMyTurn(true);
					opponentData.setMyTurn(false);
				} else if (whichTurn == 1) {
					matchData.setMyTurn(false);
					opponentData.setMyTurn(true);
				} else {
					throw new AssertionError("ここには到達しない");
				}

				// まず自分にメッセージを送る (myName,opponentNameは設定済み)
				sendMessage = toJson(matchData);
				socket.getSession().getRemote().sendStringByFuture(sendMessage);

				// 相手にメッセージを送る
				opponentData.setMyName(matchData.getOpponentName());
				opponentData.setOpponentName(matchData.getMyName());
				opponentMessage = toJson(opponentData);
				opponentSocket.getSession().getRemote()
						.sendStringByFuture(opponentMessage);
			}

			break;

		case MY_TURN:
			// 相手に得点とターンのメッセージを送る
			opponentSocket = getOpponent(socket);
			opponentData.setLastOneScore(matchData.getLastOneScore());
			opponentData.setMyTurn(true);
			opponentMessage = toJson(opponentData);
			opponentSocket.getSession().getRemote()
					.sendStringByFuture(opponentMessage);

			break;

		case GAME_WIN:
			// 相手に負けたことを知らせるメッセージを送る
			opponentSocket = getOpponent(socket);
			opponentData.setLastOneScore(matchData.getLastOneScore());
			opponentData.setState(GameState.GAME_LOST.toString());
			opponentMessage = toJson(opponentData);
			opponentSocket.getSession().getRemote()
					.sendStringByFuture(opponentMessage);

			break;

		default:
			// 何もしない
			break;
		}

	}

	/**
	 * socketにプレイヤー名を設定する。</ br>
	 * 
	 * 同じ名前のplayerがいるか確認し、いたらサフィックスをつける
	 * 
	 * @param socket
	 * @param matchData
	 */
	private void setPlayerName(PuzzcloSocketListener socket,
			SocketMatchData matchData) {
		// 名前の２重登録防止のために、同期を取る
		synchronized (clients) {
			int existNameCount = 0;
			for (PuzzcloSocketListener member : clients) {
				if (matchData.getMyName().equals(member.getMyName())) {
					existNameCount++;
				}
			}

			if (existNameCount == 0) {
				socket.setPlayerName(matchData.getMyName());
			} else {
				socket.setPlayerName(matchData.getMyName() + "_"
						+ existNameCount);
			}
		}
	}

	/**
	 * 自分以外をSocketMatchDataの対戦相手リストに追加する
	 */
	private void setOpponentListExceptOwn(PuzzcloSocketListener socket,
			SocketMatchData matchData) {
		List<String> opponentList = new ArrayList<>();
		for (PuzzcloSocketListener member : clients) {
			String memberName = member.getMyName();
			if (!matchData.getMyName().equals(memberName)
					&& member.isWaitConnect()) {
				opponentList.add(member.getMyName());
			}
		}
		matchData.setOpponentList(opponentList);
	}

	/**
	 * お互いの対戦相手を設定する。
	 * 
	 * @param socket
	 *            自分のソケット
	 * @param matchData
	 *            SocketMatchDataオブジェクト
	 * @return 対戦相手のソケット
	 */
	private PuzzcloSocketListener setOpponent(PuzzcloSocketListener socket,
			SocketMatchData matchData) {
		String opponentName = matchData.getOpponentName();
		PuzzcloSocketListener opponentSocket = null;

		// 2重設定されないように同期をとる
		synchronized (clients) {
			for (PuzzcloSocketListener member : clients) {
				if (member.isWaitConnect()
						&& member.getOpponentName().equals("")
						&& opponentName.equals(member.getMyName())) {
					socket.setOpponentName(opponentName);
					member.setOpponentName(socket.getMyName());
					member.setWaitConnect(false);
					opponentSocket = member;
					break;
				}
			}
		}

		return opponentSocket;
	}

	private PuzzcloSocketListener getOpponent(PuzzcloSocketListener socket) {
		String opponentName = socket.getOpponentName();
		PuzzcloSocketListener opponentSocket = null;

		if (!opponentName.equals("")) {
			for (PuzzcloSocketListener member : clients) {
				if (member.getMyName().equals(opponentName)) {
					opponentSocket = member;
					break;
				}
			}
		}

		return opponentSocket;
	}

	private String toJson(SocketMatchData matchData) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonData = "";
		try {
			jsonData = mapper.writeValueAsString(matchData);
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
		SocketMatchData matchData = null;

		try {
			matchData = mapper.readValue(json, SocketMatchData.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return matchData;
	}
}
