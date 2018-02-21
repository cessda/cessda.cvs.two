package eu.cessda.cvmanager.security;

import org.springframework.security.access.AccessDeniedException;

import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorEvent;
import com.vaadin.ui.Notification;

/**
 * This class is responsible to deal with the "AccessDeniedException", 
 * which is thrown when trying to execute a method without the sufficient permissions.
 * 
 * This class should be registered as an error handler in your application.
 * 
 * @author Karam
 */
public class ErrorHandler {
	public static void handleError(ErrorEvent event) {
        Throwable t = DefaultErrorHandler.findRelevantThrowable(event.getThrowable());
        if (t instanceof AccessDeniedException) {
            Notification.show("You do not have permission to perform this operation", Notification.Type.WARNING_MESSAGE);
        } else {
            DefaultErrorHandler.doDefault(event);
        }
    }
}