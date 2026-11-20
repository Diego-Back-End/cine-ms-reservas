package com.cine.ms_reservas.services;

import com.cine.ms_reservas.config.RabbitConfig;
import com.cine.ms_reservas.dto.ReservaConfirmadaMessage;
import com.cine.ms_reservas.dto.ReservaFallidaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_RESERVAS,
                    RabbitConfig.ROUTING_KEY_RESERVA_CONFIRMADA, message);
            log.info("Evento de reserva confirmada enviado a RabbitMQ (exchange={}, routingKey={}): reservaId={}",
                    RabbitConfig.EXCHANGE_RESERVAS, RabbitConfig.ROUTING_KEY_RESERVA_CONFIRMADA,
                    message.getReservaId());
        } catch (Exception e) {
            log.error("No se pudo enviar el evento de la reserva {} a RabbitMQ: {}",
                    message.getReservaId(), e.getMessage(), e);
        }
    }

    public void publicarReservaFallida(ReservaFallidaMessage message) {
        try {
            applicationEventPublisher.publishEvent(message);
        } catch (Exception e) {
            log.error("No se pudo publicar el evento de reserva fallida reservaId={}: {}",
                    message.getReservaId(), e.getMessage(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void enviarReservaFallida(ReservaFallidaMessage message) {
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_RESERVAS,
                    RabbitConfig.ROUTING_KEY_RESERVA_FALLIDA, message);
            log.info("Evento de reserva fallida enviado a RabbitMQ (exchange={}, routingKey={}): reservaId={}, motivo={}",
                    RabbitConfig.EXCHANGE_RESERVAS, RabbitConfig.ROUTING_KEY_RESERVA_FALLIDA,
                    message.getReservaId(), message.getMotivo());
        } catch (Exception e) {
            log.error("No se pudo enviar el evento de reserva fallida de la reserva {} a RabbitMQ: {}",
                    message.getReservaId(), e.getMessage(), e);
        }
    }

}
