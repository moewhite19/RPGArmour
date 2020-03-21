package cn.whiteg.rpgArmour;

import java.util.logging.Logger;

public class Debuger {
    private final Logger logger;

    public Debuger(Logger logger) {
        this.logger = logger;
    }

    public void logout(String msg) {
        if (logger == null) return;
        logger.info(msg);
    }
}
