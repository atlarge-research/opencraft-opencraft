package net.glowstone.messaging;

import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * The messaging system provides a generalised interface for subscription management and message routing.
 *
 * @param <Topic> the type of topics used by the messaging system.
 * @param <Subscriber> the type of subscribers that can receive published messages.
 * @param <Publisher> the type of publisher that can publish messages.
 * @param <Message> the type of messages that can be published.
 */
public final class MessagingSystem<Topic, Publisher, Subscriber, Message> {

    private final Policy<Topic, Publisher, Subscriber> policy;
    private final Broker<Topic, Subscriber, Message> broker;
    private final Filter<Subscriber, Message> filter;
    private final Map<Subscriber, Set<Topic>> subscriptions;

    /**
     * Create a messaging system based on the given policy and broker.
     *
     * @param policy the policy used to update subscriptions and select publishing targets.
     * @param broker the broker used to distribute messages to subscribers.
     */
    public MessagingSystem(
            Policy<Topic, Publisher, Subscriber> policy,
            Broker<Topic, Subscriber, Message> broker,
            Filter<Subscriber, Message> filter
    ) {
        this.policy = policy;
        this.broker = broker;
        this.filter = filter;
        subscriptions = new HashMap<>();
    }

    /**
     * Update the subscriptions of the subscriber and register its callback.
     *
     * @param subscriber the subscriber whom's subscriptions should be updated.
     * @param callback the callback that should be used to send messages to the subscriber.
     */
    public void update(Subscriber subscriber, Consumer<Message> callback) {

        Set<Topic> newTopics = policy.computeInterestSet(subscriber);

        if (newTopics.isEmpty()) {

            Set<Topic> oldTopics = subscriptions.remove(subscriber);
            if (oldTopics != null) {
                oldTopics.forEach(topic -> broker.unsubscribe(topic, subscriber));
            }

        } else {

            Consumer<Message> filteredCallback = createFilteredCallback(subscriber, callback);

            Set<Topic> oldTopics = subscriptions.put(subscriber, newTopics);
            if (oldTopics == null) {
                newTopics.forEach(topic -> broker.subscribe(topic, subscriber, filteredCallback));
            } else {
                Sets.difference(oldTopics, newTopics).forEach(topic -> broker.unsubscribe(topic, subscriber));
                Sets.difference(newTopics, oldTopics).forEach(topic -> broker.subscribe(
                        topic,
                        subscriber,
                        filteredCallback
                ));
            }
        }
    }

    /**
     * Determine the topic to which the publisher should publish its message and then publish it.
     *
     * @param publisher the publisher who would like to publish a message.
     * @param message the message to be published.
     */
    public void broadcast(Publisher publisher, Message message) {
        Iterable<Topic> topics = policy.selectTargets(publisher);
        topics.forEach(topic -> broker.publish(topic, message));
    }

    private Consumer<Message> createFilteredCallback(Subscriber subscriber, Consumer<Message> callback) {
        return message -> {
            if (filter.filter(subscriber, message)) {
                callback.accept(message);
            }
        };
    }
}