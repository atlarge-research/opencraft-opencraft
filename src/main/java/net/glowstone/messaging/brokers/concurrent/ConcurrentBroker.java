package net.glowstone.messaging.brokers.concurrent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.glowstone.messaging.Broker;

/**
 * The concurrent broker uses a concurrent hash map to store topic-channel pairs. The concurrent
 * hash map allows multiple publishers and subscribers to access the broker simultaneously.
 *
 * @param <Topic> the type of topics that is allowed to identify channels.
 * @param <Subscriber> the type of subscribers that is allowed to subscribe to a channel.
 * @param <Message> the type of messages that is allowed to be published to a channel.
 */
public final class ConcurrentBroker<Topic, Subscriber, Message> implements Broker<Topic, Subscriber, Message> {

    private final Map<Topic, ConcurrentChannel<Subscriber, Message>> channels;

    /**
     * Create a concurrent broker.
     */
    public ConcurrentBroker() {
        channels = new ConcurrentHashMap<>();
    }

    @Override
    public void subscribe(Topic topic, Subscriber subscriber, Consumer<Message> callback) {
        channels.compute(topic, (t, channel) -> {
            if (channel == null) {
                channel = new ConcurrentChannel<>();
            }
            channel.subscribe(subscriber, callback);
            return channel;
        });
    }

    @Override
    public void unsubscribe(Topic topic, Subscriber subscriber) {
        channels.computeIfPresent(topic, (t, channel) -> {
            channel.unsubscribe(subscriber);
            if (channel.isEmpty()) {
                return null;
            }
            return channel;
        });
    }

    @Override
    public void publish(Topic topic, Message message) {
        channels.computeIfPresent(topic, (t, channel) -> {
            channel.publish(message);
            return channel;
        });
    }

    @Override
    public void close() {
        // Nothing to close
    }
}