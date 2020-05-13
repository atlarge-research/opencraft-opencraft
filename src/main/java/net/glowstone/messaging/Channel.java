package net.glowstone.messaging;

import java.util.function.Consumer;

/**
 * The Channel interface defines the methods to be implemented by a class,
 * such that it can function as channel in the pub/sub design pattern.
 *
 * @param <Subscriber> the type of subscribers that is allowed to subscribe.
 * @param <Message> the type of messages that is allowed to be published.
 */
public interface Channel<Subscriber, Message> {

    /**
     * Check whether the channel is empty, meaning that there are no subscribers.
     *
     * @return whether there are any subscribers to the channel.
     */
    boolean isEmpty();

    /**
     * Check whether a subscriber is subscribed to the channel.
     *
     * @param subscriber the subscriber whom's subscription should be checked.
     * @return whether the subscriber is subscribed to the channel.
     */
    boolean isSubscribed(Subscriber subscriber);

    /**
     * Register the subscriber to receive messages via the callback.
     * Do not update the value if the subscriber is already subscribed.
     *
     * @param subscriber the subscriber that would like to receive messages.
     * @param callback the callback that should be used to provide messages.
     * @return whether the subscriber was previously unsubscribed.
     */
    boolean subscribe(Subscriber subscriber, Consumer<Message> callback);

    /**
     * Unregister the subscriber from receiving messages.
     *
     * @param subscriber the subscriber that would no longer like to receive messages.
     * @return whether the subscriber was previously subscribed.
     */
    boolean unsubscribe(Subscriber subscriber);

    /**
     * Broadcasts the given message to all subscribers.
     *
     * @param message the message to be published to subscribers.
     */
    void publish(Message message);
}