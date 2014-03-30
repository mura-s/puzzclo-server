/*
 * copyright (C) 2014 Seiya Muramatsu. All rights reserved.
 */
package muras.puzzclo.server.domain;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class SocketMatchData {
	
	private boolean connected = true;

	@JsonProperty("my_name")
	private String myName = "";

	@JsonProperty("opponent_name")
	private String opponentName = "";
	
	@JsonProperty("opponent_list")
	private List<String> opponentList;

	@JsonProperty("my_score")
	private int myScore = 50;

	@JsonProperty("opponent_score")
	private int opponentScore = 50;

	@JsonProperty("my_turn")
	private boolean myTurn = false;

	@JsonProperty("last_one_score")
	private int lastOneScore = 0;
	
	private String state = "";

	public SocketMatchData() {
	}

	public String getMyName() {
		return myName;
	}

	public void setMyName(String myName) {
		this.myName = myName;
	}

	public String getOpponentName() {
		return opponentName;
	}

	public void setOpponentName(String opponentName) {
		this.opponentName = opponentName;
	}

	public int getMyScore() {
		return myScore;
	}

	public void setMyScore(int myScore) {
		this.myScore = myScore;
	}

	public int getOpponentScore() {
		return opponentScore;
	}

	public void setOpponentScore(int opponentScore) {
		this.opponentScore = opponentScore;
	}

	public boolean isMyTurn() {
		return myTurn;
	}

	public void setMyTurn(boolean myTurn) {
		this.myTurn = myTurn;
	}

	public int getLastOneScore() {
		return lastOneScore;
	}

	public void setLastOneScore(int lastOneScore) {
		this.lastOneScore = lastOneScore;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public List<String> getOpponentList() {
		return opponentList;
	}

	public void setOpponentList(List<String> opponentList) {
		this.opponentList = opponentList;
	}

}
