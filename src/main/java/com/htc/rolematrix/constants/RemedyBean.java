package com.htc.rolematrix.constants;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.corundumstudio.socketio.store.MemoryStoreFactory;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.File;

/**
 * Created by poovarasanv on 19/10/17.
 * Project : role-matrix
 */
@Component
public class RemedyBean {

    @Value("${socketio.port}")
    Integer socketPort;

    @Value("${mapdb.path}")
    String mapDBPath;



    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public SocketIOServer socketIOServer() {

        Configuration config = new Configuration();
        config.setHostname("0.0.0.0");
        config.setPort(socketPort);
        config.setJsonSupport(new JacksonJsonSupport());
        config.setWorkerThreads(100);
        config.setPingInterval(60000);
        config.setPingTimeout(60000);
        config.setWebsocketCompression(true);
        config.setMaxFramePayloadLength(1024 * 1024);
        config.setMaxHttpContentLength(1024 * 1024);
        config.setStoreFactory(new MemoryStoreFactory());
        SocketIOServer server = new SocketIOServer(config);

        return server;
    }



    @Bean
    public DB db() {
        return DBMaker.newFileDB(new File(mapDBPath))
                .make();
    }


    @Bean
    public ExpressionParser expressionParser() {
        return new SpelExpressionParser();
    }
}
