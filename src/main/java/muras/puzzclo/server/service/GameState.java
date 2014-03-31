/*
 * Copyright (C) 2014 Seiya Muramatsu. All rights reserved.
 */
package muras.puzzclo.server.service;

/**
 * ゲームの状態
 * 
 * @author muramatsu
 * 
 */
public enum GameState {

	/**
	 * 初期状態
	 */
	INIT,

	/**
	 * 名前を入力中（接続を待つ側）
	 */
	INPUT_YOUR_NAME_AS_SERVER,

	/**
	 * 名前を入力中（接続を待つ側）
	 */
	INPUT_YOUR_NAME_AS_CLIENT,

	/**
	 * サーバに接続中（接続を待つ側）
	 */
	CONNECT_SERVER_AS_SERVER,

	/**
	 * サーバに接続中（接続を待つ側）
	 */
	CONNECT_SERVER_AS_CLIENT,

	/**
	 * 対戦相手接続待ち
	 */
	WAIT_CONNECT,

	/**
	 * 対戦相手を選択中
	 */
	SELECT_OPPONENT,

	/**
	 * 一人プレイ中
	 */
	PLAY_ONE_PERSON,

	/**
	 * 二人プレイ開始
	 */
	START_PLAY_TWO_PERSON,

	/**
	 * 自分のターン
	 */
	MY_TURN,

	/**
	 * 相手のターン
	 */
	OPPONENT_TURN,

	/**
	 * ゲームクリア後
	 */
	GAME_CLEAR,
	
	/**
	 * 勝った時
	 */
	GAME_WIN,
	
	/**
	 * 負けた時
	 */
	GAME_LOST;


}
