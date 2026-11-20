package com.cine.ms_reservas.kafka;

import com.cine.ms_reservas.dto.ReservaConfirmadaKafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservaKafkaProducer {

    public static final String TOPICO_RESERVA_CONFIRMADA = "reserva-confirmada";

    private final KafkaTemplate<String, ReservaConfirmadaKafkaMessage> kafkaTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publicarReservaConfirmada(ReservaConfirmadaKafkaMessage message) {
        applicationEventPublisher.publishEvent(message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void enviarReservaConfirmada(ReservaConfirmadaKafkaMessage message) {
        try {
            kafkaTemplate.send(TOPICO_RESERVA_CONFIRMADA, message.getReservaId().toString(), message)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("No se pudo publicar en Kafka reserva-confirmada: reservaId={}, motivo={}",
                                    message.getReservaId(), ex.getMessage(), ex);
                        }
                    });
            log.info("Evento de reserva confirmada enviado a Kafka (topic={}): reservaId={}",
                    TOPICO_RESERVA_CONFIRMADA, message.getReservaId());
        } catch (Exception e) {
            log.error("No se pudo enviar el evento de la reserva {} a Kafka: {}",
                    message.getReservaId(), e.getMessage(), e);
        }
    }

}
