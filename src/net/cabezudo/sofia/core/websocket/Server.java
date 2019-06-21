package net.cabezudo.sofia.core.websocket;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import net.cabezudo.sofia.core.logger.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.21
 */
@WebSocket
public class Server {

  private Session session;

  CountDownLatch latch = new CountDownLatch(1);

  @OnWebSocketMessage
  public void onText(Session session, String message) throws IOException {
    Logger.debug("Message received from server:" + message);
  }

  @OnWebSocketConnect
  public void onConnect(Session session) {
    Logger.debug("Connected to server");
    this.session = session;
    latch.countDown();
  }

  public void sendMessage(String str) {
    try {
      session.getRemote().sendString(str);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public CountDownLatch getLatch() {
    return latch;
  }

}
