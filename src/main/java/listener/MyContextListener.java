/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package listener;

import bdb.MyBDB;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Web application lifecycle listener.
 *
 * @author zxu
 */
@WebListener()
public class MyContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
//        NLP.process("", new HashMap<String,Double>(), new HashMap<String,Double>());
//        System.out.println("Stanford NLP is initialized!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        MyBDB.getBDB().close();
        System.out.println("MyBDB is shut down!");
    }
}
