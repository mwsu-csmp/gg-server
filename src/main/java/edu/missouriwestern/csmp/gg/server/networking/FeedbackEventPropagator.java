package edu.missouriwestern.csmp.gg.server.networking;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;
import org.neo4j.driver.internal.shaded.io.netty.util.internal.ConcurrentSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;

import java.util.Set;
import java.util.function.Consumer;

public class FeedbackEventPropagator implements EventListener {

    private Set<EventListener> listeners = new ConcurrentSet<>();

    @Autowired @Qualifier("taskExecutor")
    private TaskExecutor executor;

    public void acceptEvent(Event e) {
        // forward event back to game -- temporary hack until we get messaging system fully implemented
        executor.execute(
                () -> {
                    for(var listener: listeners)
                        listener.acceptEvent(e);
                }
        );
    }
    public Consumer<EventListener> registerEventReceiver() {
        return listener -> {
            listeners.add(listener);
        };
    }

}
