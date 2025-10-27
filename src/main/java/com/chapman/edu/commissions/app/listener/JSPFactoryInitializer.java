package com.chapman.edu.commissions.app.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSPFactoryInitializer - ServletContextListener for JSP Factory Initialization.
 *
 * This listener ensures that the JSP factory is properly initialized when the
 * servlet context is created, preventing NullPointerException errors when JSP
 * pages are accessed.
 *
 * <b>PROBLEM SOLVED:</b>
 *
 * In embedded Tomcat environments, the JSP factory may not be automatically
 * initialized, leading to errors like:
 *   "Cannot invoke jakarta.servlet.jsp.JspFactory.getJspApplicationContext()
 *    because the return value of getDefaultFactory() is null"
 *
 * <b>SOLUTION:</b>
 *
 * This listener explicitly sets the JSP factory during servlet context initialization,
 * ensuring it's available before any JSP pages are compiled or executed.
 *
 * <b>LIFECYCLE:</b>
 *
 * 1. contextInitialized() - Called when servlet context starts
 *    - Sets the default JSP factory
 *    - Preloads JSP runtime classes
 *    - Logs initialization status
 *
 * 2. contextDestroyed() - Called when servlet context stops
 *    - Cleans up JSP factory resources
 *    - Logs cleanup status
 *
 * <b>USAGE:</b>
 *
 * This listener is registered programmatically in DealManagementApp:
 *
 * <pre>
 * context.addApplicationListener(JSPFactoryInitializer.class.getName());
 * </pre>
 *
 * @author Commission Calculator Team
 * @version 1.0
 * @see jakarta.servlet.ServletContextListener
 * @see jakarta.servlet.jsp.JspFactory
 */
public class JSPFactoryInitializer implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(JSPFactoryInitializer.class);

    /**
     * Called when the servlet context is initialized (web application is starting).
     *
     * This method ensures the JSP factory is properly set up before any JSP pages
     * are accessed.
     *
     * @param sce the ServletContextEvent containing the ServletContext being initialized
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing JSP Factory...");

        try {
            // Check if JSP factory is already set
            jakarta.servlet.jsp.JspFactory existingFactory = jakarta.servlet.jsp.JspFactory.getDefaultFactory();

            if (existingFactory == null) {
                logger.info("  JSP factory not found, initializing...");

                // Set the default JSP factory implementation
                // This is the Apache Jasper implementation used by Tomcat
                jakarta.servlet.jsp.JspFactory.setDefaultFactory(
                    new org.apache.jasper.runtime.JspFactoryImpl()
                );

                logger.info("  JSP factory set to: org.apache.jasper.runtime.JspFactoryImpl");
            } else {
                logger.info("  JSP factory already initialized: {}", existingFactory.getClass().getName());
            }

            // Preload JSP runtime classes to avoid lazy-loading issues
            try {
                Class.forName("org.apache.jasper.compiler.JspRuntimeContext");
                Class.forName("org.apache.jasper.servlet.JspServlet");
                logger.info("  JSP runtime classes preloaded successfully");
            } catch (ClassNotFoundException e) {
                logger.warn("  Could not preload JSP runtime classes: {}", e.getMessage());
            }

            logger.info("JSP Factory initialized successfully");

        } catch (Exception e) {
            logger.error("Failed to initialize JSP Factory", e);
            throw new RuntimeException("JSP Factory initialization failed", e);
        }
    }

    /**
     * Called when the servlet context is destroyed (web application is stopping).
     *
     * This method performs cleanup of JSP factory resources.
     *
     * @param sce the ServletContextEvent containing the ServletContext being destroyed
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Cleaning up JSP Factory...");

        try {
            // Note: We don't set the factory to null here because it may be needed
            // during the shutdown process. The JVM will clean it up when the process exits.
            logger.info("JSP Factory cleanup completed");
        } catch (Exception e) {
            logger.error("Error during JSP Factory cleanup", e);
        }
    }
}