package de.voot.beach38.bookr;

import de.voot.beach38.bookr.service.WeekBookingService;
import de.voot.beach38.bookr.service.WeekRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
public class BookrCommandLineRunner implements CommandLineRunner {

  private final WeekBookingService weekBookingService;
  private final WeekRepository weekRepository;
  private ExecutorService pool;

  public BookrCommandLineRunner(WeekBookingService weekBookingService, WeekRepository weekRepository) {
    this.weekBookingService = weekBookingService;
    this.weekRepository = weekRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

    log.info("Start the booking");

    String phpSessionId = "p6ulkn578pebso2modsai7lbq2";

    Collection<LocalDate> unbookedWeeks = weekRepository.unbookedWeeks();

    if (CollectionUtils.isEmpty(unbookedWeeks)) {
      log.info("No weeks to book");
      return;
    }

    log.info("{} weeks to book", unbookedWeeks.size());

    pool = Executors.newFixedThreadPool(unbookedWeeks.size());

    List<Callable<Void>> weekTasks = new ArrayList<>();
    for (LocalDate week : unbookedWeeks) {
      weekTasks.add(() -> {
        Thread.currentThread().setName("week-" + week);

        while (true) {
          try {
            boolean success = weekBookingService.bookWeek(week, phpSessionId);

            if (!success) {
              log.warn("Booking of week {} unsuccessful", week);

            } else {
              log.info("Booked week {}", week);
              try {
                weekRepository.markAsBooked(week);
              } finally {
                return null;
              }
            }

          } catch (Exception e) {
            log.error("Exception booking week {}", week, e);
          }

          Thread.sleep(10000);
        }
      });
    }

    pool.invokeAll(weekTasks);
    pool.shutdown();

    log.info("HOORAAAY! All booked");
  }
}
