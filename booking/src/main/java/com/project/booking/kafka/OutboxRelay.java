package com.project.booking.kafka;

import com.project.booking.models.OutboxEvent;
import com.project.booking.models.OutboxStatus;
import com.project.booking.repo.OutboxRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxRelay {

    private final OutboxRepo outboxRepo;
    private final KafkaTemplate<String,String> kafkaTemplate;
    private static final int MAX_RETRIES=5;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void relay(){

        List<OutboxEvent> pending = outboxRepo.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for(OutboxEvent e : pending){

            try{
                kafkaTemplate.send(
                        e.getTopic(),
                       e.getAggregateId(),
                        e.getPayload()
                ).get(); // blocking -> waits for ack

                e.setStatus(OutboxStatus.SENT);
                e.setSentAt(LocalDateTime.now());
                log.info("Outbox event sent: id={} topic={}", e.getId(), e.getTopic());

            }catch (Exception ex){
                e.setRetryCount(e.getRetryCount()+1);

                if(e.getRetryCount() >= MAX_RETRIES){
                    e.setStatus(OutboxStatus.FAILED);
                    log.error("Outbox event permanently failed: id={} topic={}",
                            e.getId(), e.getTopic());
                }else{
                    log.warn("Outbox relay failed, will retry: id={} attempt={}",
                            e.getId(), e.getRetryCount());
                }
            }

        }
    }
}
