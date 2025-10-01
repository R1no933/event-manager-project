package dev.baskakov.eventmanagerservice.events.event.scheduled;

import dev.baskakov.eventmanagerservice.events.event.repository.EventRepository;
import dev.baskakov.eventmanagerservice.events.event.model.EventStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Configuration
@EnableScheduling
public class EventStatusUpdateScheduled {

    private static final Logger log = LoggerFactory.getLogger(EventStatusUpdateScheduled.class);

    private final EventRepository eventRepository;

    public EventStatusUpdateScheduled(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    @Scheduled(cron = "${event.status.cron}")
    public void updateEventStatus(){
        log.info("Start schedule for update event status at {}", LocalDateTime.now());
        eventRepository.updateAllStartedEventsWithStatusAwaitStart(EventStatus.WAIT_START);
        eventRepository.updateAllEndedEventsWithStatusStarted(EventStatus.STARTED);
    }
}
