package wtf.tatp.meowtils.manager.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(name = "MeowtilsAppender", category = "Core", printObject = true)
public class LogAppender extends AbstractAppender {

    protected LogAppender(String name) {
        super(name, null, null);
    }

    @PluginFactory
    public static LogAppender createAppender(@PluginAttribute("name") String name) {
        return new LogAppender(name);
    }

    @Override
    public void append(LogEvent event) {
        String level = event.getLevel().name();
        String message = event.getMessage().getFormattedMessage();

        LogManager.write(level, message);
        LogFrame.appendLog(level, message, new SimpleDateFormat("HH:mm:ss").format(new Date()));
    }
}