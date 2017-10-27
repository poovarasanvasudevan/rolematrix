package com.htc.rolematrix.services;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by kvivek on 10/20/2017.
 */
@Service
public class LoggerService {

    @Autowired
    SocketIOServer socketIOServer;

    @Value("${app.logpath}")
    String logPath;

    public void log(String log) {
        String logString = String.format("Log : %s From Server With Log Data : %s \n", new Date().toString(), log);
        // System.out.println(logString);
        //socketIOServer.getBroadcastOperations().sendEvent("logger", logString);

        try {
            fileLog(logString);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void errorLof(String errorLog) {

       // socketIOServer.getClient("")
        socketIOServer.getBroadcastOperations().sendEvent("error", errorLog + "<br/>");
        try {
            fileLog(errorLog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void roleLog(String errorLog) {
        socketIOServer.getBroadcastOperations().sendEvent("rolelog", errorLog);
        try {
            fileLog(errorLog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void excelProgress(String progressString) {
        socketIOServer.getBroadcastOperations().sendEvent("excelprogress",progressString);
        try {
            fileLog(progressString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fileLog(String log) throws IOException {
        FileWriter logWriter1 = new FileWriter(new File(logPath), true);
        try (BufferedWriter logWriter = new BufferedWriter(logWriter1)) {
            logWriter.write(log);
        }
    }
}
