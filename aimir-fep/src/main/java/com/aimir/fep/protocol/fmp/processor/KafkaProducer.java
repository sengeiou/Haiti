package com.aimir.fep.protocol.fmp.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import com.aimir.fep.util.FMPProperty;

@Component
public class KafkaProducer
{
    public static String topic = "aimir";

    public static String messageKey = "aimiramm";

    public static String brokerAddress = FMPProperty.getProperty("kafka.broker.list");

    public MessageHandler handler() throws Exception {
        KafkaProducerMessageHandler<Integer, com.aimir.fep.util.Message> handler =
                new KafkaProducerMessageHandler<>(kafkaTemplate());
        handler.setTopicExpression(new LiteralExpression(KafkaProducer.topic));
        handler.setMessageKeyExpression(new LiteralExpression(KafkaProducer.messageKey));
        return handler;
    }

    private KafkaTemplate<Integer, com.aimir.fep.util.Message> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    private ProducerFactory<Integer, com.aimir.fep.util.Message> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProducer.brokerAddress);
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        // props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16*1024);
        // props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        // props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 32 * 1024 * 1024);
        // props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 2 * 1024 * 1024);
        // props.put(ProducerConfig.ACKS_CONFIG, 1);
        // props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }
}
