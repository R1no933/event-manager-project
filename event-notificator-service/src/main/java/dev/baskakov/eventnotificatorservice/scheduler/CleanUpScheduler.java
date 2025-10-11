package dev.baskakov.eventnotificatorservice.scheduler;

import dev.baskakov.eventnotificatorservice.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@EnableScheduling
@Configuration
public class CleanUpScheduler {
    private static final Logger log = LoggerFactory.getLogger(CleanUpScheduler.class);
    private final NotificationRepository notificationRepository;

    public CleanUpScheduler(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    @Scheduled(cron = "${cleanup.cron}")
    public void cleanUpAllReadNotifications() {
        log.info("Starting clean up all read notifications");
        notificationRepository.deleteAllReadOnlyNotifications();
    }
}
