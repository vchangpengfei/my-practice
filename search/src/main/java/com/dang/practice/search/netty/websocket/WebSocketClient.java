package com.dang.practice.search.netty.websocket;

import io.netty.util.concurrent.Future;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;

import java.util.concurrent.ExecutionException;

/**
 * Created by pfchang on 2018/4/24.
 */
public class WebSocketClient {
    public static void main(String args[]) throws ExecutionException, InterruptedException {

        WebSocketUpgradeHandler.Builder upgradeHandlerBuilder
                = new WebSocketUpgradeHandler.Builder();
        WebSocketUpgradeHandler wsHandler = upgradeHandlerBuilder
                .addWebSocketListener(new WebSocketListener() {

                    public void onOpen(WebSocket websocket) {
                        // WebSocket connection opened
                        System.out.println();
                    }


                    public void onClose(WebSocket websocket, int code, String reason) {
                        // WebSocket connection closed
                        System.out.println();
                    }


                    public void onError(Throwable t) {
                        // WebSocket connection error
                        System.out.println();
                    }

                    public void onTextFrame(String payload, boolean finalFragment, int rsv) {
                        System.out.println();
                    }
                }).build();
        WebSocket webSocketClient = Dsl.asyncHttpClient()
                .prepareGet("ws://localhost:8080/websocket")
//                .addHeader("header_name", "header_value")
//                .addQueryParam("key", "value")
                .setRequestTimeout(5000)
                .execute(wsHandler)
                .get();

        while (webSocketClient.isOpen()) {
            Future<Void> future= webSocketClient.sendTextFrame("test message");
//            webSocketClient.sendBinaryFrame(new byte[]{'t', 'e', 's', 't'});
        }



    }
}
