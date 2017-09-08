package de.voot.beach38.bookr;

import de.voot.beach38.bookr.service.WeekBookingService;
import de.voot.beach38.bookr.service.WeekRepository;
import java.time.LocalDate;
import java.util.Collection;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookrCommandLineRunner implements CommandLineRunner {

  private final WeekBookingService weekBookingService;
  private final WeekRepository weekRepository;

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

    for (LocalDate week : unbookedWeeks) {
      try {
        boolean success = weekBookingService.bookWeek(week, phpSessionId);

        if (!success) {
          log.error("Booking of week {} unsuccessful", week);

        } else {
          log.info("Booked week {}", week);
          weekRepository.markAsBooked(week);
        }

      } catch (Exception e) {
        log.error("Could not book week {}", week, e);
      }
    }
  }
}
