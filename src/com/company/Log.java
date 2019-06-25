package com.company;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

    void log(boolean x, String msg) throws IOException {
        Logger logger = Logger.getLogger("BusReservationSystem");
        FileHandler fh = new FileHandler("C:\\Users\\Administrator\\Desktop\\Reservation\\logs\\Logs");
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        logger.setUseParentHandlers(false);
        if(!x)
            logger.info(msg);
        else
            logger.warning(msg);
    }
}
