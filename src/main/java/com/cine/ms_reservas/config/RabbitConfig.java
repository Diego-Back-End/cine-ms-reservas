package com.cine.ms_reservas.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_RESERVAS = "reservas.exchange";
    public static final String ROUTING_KEY_RESERVA_CONFIRMADA = "reserva.confirmada";
    public static final String ROUTING_KEY_RESERVA_FALLIDA = "reserva.fallida";

    @Bean
    public TopicExchange reservasExchange() {
        return new TopicExchange(EXCHANGE_RESERVAS, true, false);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

}
