package science.atlarge.opencraft.opencraft.i18n;

import java.text.Format;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import science.atlarge.opencraft.opencraft.GlowServer;

class LoggableLocalizedStringImpl extends LocalizedStringImpl
    implements LoggableLocalizedString {

    private final Level logLevel;

    private final Logger logger;

    LoggableLocalizedStringImpl(String key, Level logLevel) {
        super(key);
        this.logLevel = logLevel;
        this.logger = GlowServer.logger;
    }

    @Override
    LoggableLocalizedStringImpl setFormatByArgumentIndex(int argumentIndex, Format format) {
        return (LoggableLocalizedStringImpl)
                (super.setFormatByArgumentIndex(argumentIndex, format));
    }

    LoggableLocalizedStringImpl(String key, Level logLevel,
                                ResourceBundle resourceBundle,
                                Logger logger) {
        super(key, resourceBundle);
        this.logLevel = logLevel;
        this.logger = logger;
    }

    @Override
    public void log() {
        logger.log(logLevel, get());
    }

    @Override
    public void log(Object... args) {
        logger.log(logLevel, get(args));
    }

    @Override
    public void log(Throwable ex) {
        logger.log(logLevel, get(), ex);
    }

    @Override
    public void log(Throwable ex, Object... args) {
        logger.log(logLevel, get(args), ex);
    }
}
