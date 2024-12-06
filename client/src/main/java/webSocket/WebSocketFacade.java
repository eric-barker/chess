//package webSocket;
//
//import com.google.gson.Gson;
//import exception.ResponseException;
//import websocket.messages.ServerMessage;
//
//import javax.websocket.*;
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//
////need to extend Endpoint for websocket to work properly
//public class WebSocketFacade extends Endpoint {
//
//    Session session;
//
//
//
//    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
//        try {
//            url = url.replace("http", "ws");
//            URI socketURI = new URI(url + "/ws");
//            this.notificationHandler = notificationHandler;
//
//            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//            this.session = container.connectToServer(this, socketURI);
//
//
//            //set message handler
//            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
//                @Override
//                public void onMessage(String message) {
//                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
//
//                    var type = notification.getServerMessageType();
////                    // Switch statement
////                    switch(type){
////                        case NOTIFICATION ->
////                        case ERROR ->
////                        case LOAD_GAME ->
////                    }
//
//
//                }
//            });
//        } catch (DeploymentException | IOException | URISyntaxException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
//
//    //Endpoint requires this method, but you don't have to do anything
//    @Override
//    public void onOpen(Session session, EndpointConfig endpointConfig) {
//    }
//
//    // Error
//
//    // Notification
//
//    // Load Board
//}