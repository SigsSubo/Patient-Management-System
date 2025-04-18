package com.pm.analyticsservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
// maven Lifecycle clean + compile of this analytics-service module, creates target folder which consists of getters and setters retrieved from protobuf.
import patient.events.PatientEvent;

@Service
// Kafka Consumer | consumes data given by the producer.
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    // active listener for Patient kafka topic ( as a consumer ).
    @KafkaListener(topics = "Patient", groupId = "analytics-service")
    public void consumeEvent(byte[] event){
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            log.info("Received Patient Event: [PatientId={},PatientName={},PatientEmail={}]",
                    patientEvent.getPatientId(),
                    patientEvent.getName(),
                    patientEvent.getEmail());

        } catch (InvalidProtocolBufferException e){
            log.error("Error while processing event {}", e.getMessage());
        }
    }
}
