package edu.missouriwestern.csmp.gg.server.persistance;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;
import edu.missouriwestern.csmp.gg.server.game.MissouriWizardStateUniversityGame;

import java.util.logging.Logger;

public class EventLogger implements EventListener {

    private static Logger logger = Logger.getLogger(MissouriWizardStateUniversityGame.class.getCanonicalName());

    @Override
    public void accept(Event event) {
        logger.info(event.toString());
    }
}
