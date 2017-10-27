package com.htc.rolematrix.starter;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.corundumstudio.socketio.store.MemoryStoreFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * Created by poovarasanv on 19/10/17.
 * Project : role-matrix
 */
@Component
public class SocketIOStarter implements ApplicationRunner {

    @Autowired
    private SocketIOServer server;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {

        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
            }
        });

        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient socketIOClient) {
                System.out.println("Disconnected");
            }
        });
        server.addEventListener("msg", byte[].class, (client, data, ackRequest) -> client.sendEvent("msg", data));

        server.start();
        System.out.println("APP:Socket IO Server Started");
    }


    @PreDestroy
    public void preDestroy() {
        server.stop();
    }
}
