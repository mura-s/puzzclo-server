package muras.puzzclo.server.servlet;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WebSocketServletImpl extends WebSocketServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void configure(WebSocketServletFactory factory) {
		// Listenerクラスを登録
		factory.register(muras.puzzclo.server.service.PuzzcloSocketListener.class);
	}

}
