package muras.puzzclo.server.servlet;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class PuzzcloWebSocketListener {
	private Session session;
	
	@OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        
    }

    @OnWebSocketMessage
    public void onText(String message) {
        
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        
    }
    
    public Session getSession(){
        return this.session;
    }
}