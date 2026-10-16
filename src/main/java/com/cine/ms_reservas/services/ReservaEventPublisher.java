package com.cine.ms_reservas.services;

import com.cine.ms_reservas.dto.ReservaConfirmadaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

/**
 * Publica el evento de reserva confirmada en RabbitMQ.
 *
 * El mensaje NO se envia de inmediato: {@link #publicarReservaConfirmada} solo
 * publica un evento de aplicacion y la entrega real a RabbitMQ ocurre en
 * {@link #enviarReservaConfirmada} recien despues de que la transaccion haga
 * commit (AFTER_COMMIT). Asi nunca se notifica una confirmacion que luego se
 * revierta.
 *
 * Si RabbitMQ falla, la excepcion se captura y se registra en el log: la
 * reserva ya confirmada no debe fallar por un problema de mensajeria.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Value("${app.rabbitmq.queue.reserva-confirmada}")
    private String reservaConfirmadaQueue;

    public void publicarReservaConfirmada(Long reservaId,
                                          String usuarioId,
                                          String emailUsuario,
                                          Long funcionId,
                                          List<String> asientos,
                                          Double total) {
        ReservaConfirmadaMessage message = new ReservaConfirmadaMessage(
                reservaId, usuarioId, emailUsuario, funcionId, asientos, total);
        applicationEventPublisher.publishEvent(message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void enviarReservaConfirmada(ReservaConfirmadaMessage message) {
        try {
            rabbitTemplate.convertAndSend(reservaConfirmadaQueue, message);
            log.info("Evento de reserva confirmada enviado a RabbitMQ (cola={}): reservaId={}",
                    reservaConfirmadaQueue, message.getReservaId());
        } catch (Exception e) {
            log.error("No se pudo enviar el evento de la reserva {} a RabbitMQ: {}",
                    message.getReservaId(), e.getMessage(), e);
        }
    }

}
