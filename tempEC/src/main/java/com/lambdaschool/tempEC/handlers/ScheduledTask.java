package com.lambdaschool.tempEC.handlers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.lambdaschool.tempEC.repository.ConversationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {
    @Autowired
    private ConversationRepository convoRepos;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron="0 0 0 7 * *")
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        Date startDate = new Date(cal.getTimeInMillis());

        // start of the next week
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        Date endDate = new Date(cal.getTimeInMillis());
        log.info(startDate.toString() + " / " + endDate.toString());
        convoRepos.deleteConvotask(startDate, endDate);
    }

}
