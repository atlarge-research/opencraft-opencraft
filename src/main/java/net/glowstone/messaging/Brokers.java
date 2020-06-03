package net.glowstone.messaging;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import net.glowstone.messaging.brokers.ChannelFactory;
import net.glowstone.messaging.brokers.ConcurrentBroker;
import net.glowstone.messaging.brokers.JmsBroker;
import net.glowstone.messaging.brokers.JmsCodec;
import net.glowstone.messaging.brokers.ReadWriteBroker;
import net.glowstone.messaging.brokers.codecs.ProtocolCodec;
import net.glowstone.messaging.channels.ConcurrentChannel;
import net.glowstone.messaging.channels.GuavaChannel;
import net.glowstone.messaging.channels.ReadWriteChannel;
import net.glowstone.messaging.channels.UnsafeChannel;
import net.glowstone.net.protocol.PlayProtocol;
import net.glowstone.util.config.BrokerConfig;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * A factory class to for creating multiple types of brokers.
 */
public final class Brokers {

    public static <Topic, Subscriber, Message extends com.flowpowered.network.Message> Broker<Topic, Subscriber, Message> newConfiguredBroker(BrokerConfig config) {

        ChannelFactory<Subscriber, Message> channelFactory;
        String channelType = config.getChannel().getType();
        switch (channelType) {
            case "Concurrent":
                channelFactory = ConcurrentChannel::new;
                break;
            case "Guava":
                channelFactory = GuavaChannel::new;
                break;
            case "ReadWrite":
                channelFactory = ReadWriteChannel::new;
                break;
            case "Unsafe":
                channelFactory = UnsafeChannel::new;
                break;
            default:
                throw new RuntimeException("Unknown channel type: " + channelType);
        }

        String brokerType = config.getType();
        switch (brokerType) {
            case "ActiveMQ":
                try {
                    return Brokers.newActivemqBroker(
                            ActiveMQConnectionFactory.DEFAULT_BROKER_URL,
                            new ProtocolCodec<>(new PlayProtocol())
                    );
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            case "Concurrent":
                return new ConcurrentBroker<>(channelFactory);
            case "RabbitMQ":
                try {
                    return Brokers.newRabbitmqBroker(
                            "amqp://guest:guest@localhost:5672/%2F",
                            new ProtocolCodec<>(new PlayProtocol())
                    );
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            case "ReadWrite":
                return new ReadWriteBroker<>(channelFactory);
            default:
                throw new RuntimeException("Unkown broker type: " + brokerType);
        }
    }

    /**
     * Create a ConcurrentBroker.
     *
     * @param <Topic> The type of topics that is allowed to identify channels.
     * @param <Subscriber> The type of subscribers that is allowed to subscribe to a channel.
     * @param <Message> The type of messages that is allowed to be published to a channel.
     * @return The concurrent broker.
     */
    public static <Topic, Subscriber, Message> Broker<Topic, Subscriber, Message> newConcurrentBroker() {
        return new ConcurrentBroker<>(ConcurrentChannel::new);
    }

    /**
     * Create a ReadWriteBroker.
     *
     * @param <Topic> The type of topics that is allowed to identify channels.
     * @param <Subscriber> The type of subscribers that is allowed to subscribe to a channel.
     * @param <Message> The type of messages that is allowed to be published to a channel.
     * @return The concurrent broker.
     */
    public static <Topic, Subscriber, Message> Broker<Topic, Subscriber, Message> newReadWriteBroker() {
        return new ReadWriteBroker<>(ReadWriteChannel::new);
    }

    /**
     * Create a GuavaBroker.
     *
     * @param <Topic> The type of topics that is allowed to identify channels.
     * @param <Subscriber> The type of subscribers that is allowed to subscribe to a channel.
     * @param <Message> The type of messages that is allowed to be published to a channel.
     * @return The concurrent broker.
     */
    public static <Topic, Subscriber, Message> Broker<Topic, Subscriber, Message> newGuavaBroker() {
        return new ConcurrentBroker<>(GuavaChannel::new);
    }

    /**
     * The ActiveMQ broker, this broker requires an ActiveMQ server to be running that wil handle the sending and
     * receiving of messages.
     *
     * @param uri The link used to connect to the ActiveMQ server.
     * @param codec The codec that has to be used for encoding and decoding messages.
     * @param <Topic> The type of topics that is allowed to identify jms topics.
     * @param <Subscriber> The type of subscribers that is allowed to subscribe to topics.
     * @param <Message> The type of messages that is allowed to be published to a jms topic.
     * @return The ActiveMQ broker.
     */
    public static <Topic, Subscriber, Message> Broker<Topic, Subscriber, Message> newActivemqBroker(
            String uri,
            JmsCodec<Message> codec
    ) throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory(uri);
        Connection connection = factory.createConnection();
        return new JmsBroker<>(connection, codec);
    }

    /**
     * The RabbitMQ broker, this broker requires a RabbitMQ server to be running that wil handle the sending and
     * receiving of messages. Before running this server you need to install the `rabbitmq_jms_topic_exchange` plugin
     * for RabbitMQ.
     *
     * @param uri The link used to connect to the RabbitMQ server.
     * @param codec The codec that has to be used for encoding and decoding messages.
     * @param <Topic> The type of topics that is allowed to identify jms topics.
     * @param <Subscriber> The type of subscribers that is allowed to subscribe to topics.
     * @param <Message> The type of messages that is allowed to be published to a jms topic.
     * @return The rabbitmq jms broker.
     * @throws JMSException Whenever a topic connection could not be created.
     */
    public static <Topic, Subscriber, Message> JmsBroker<Topic, Subscriber, Message> newRabbitmqBroker(
            String uri,
            JmsCodec<Message> codec
    ) throws JMSException {
        RMQConnectionFactory factory = new RMQConnectionFactory();
        factory.setUri(uri);
        Connection connection = factory.createTopicConnection();
        return new JmsBroker<>(connection, codec);
    }

    /**
     * The constructor is private to prevent the initialization of the factory class.
     */
    private Brokers() {}
}
