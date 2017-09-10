package de.voot.beach38.bookr.service;

import de.voot.beach38.bookr.model.Court;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class WeekBookingService {

  @Getter
  @Builder
  @ToString
  public static class Preference {
    private DayOfWeek dayOfWeek;
    private LocalTime time;
  }


  private final FreeCourtService freeCourtService;
  private final BookingService bookingService;
  private final List<Preference> preferences;

  public WeekBookingService(FreeCourtService freeCourtService, BookingService bookingService) {
    this.freeCourtService = freeCourtService;
    this.bookingService = bookingService;

    preferences = Arrays.asList(
      Preference.builder().dayOfWeek(DayOfWeek.THURSDAY).time(LocalTime.of(19, 00)).build(),
      Preference.builder().dayOfWeek(DayOfWeek.MONDAY).time(LocalTime.of(19, 00)).build(),
      Preference.builder().dayOfWeek(DayOfWeek.THURSDAY).time(LocalTime.of(21, 00)).build(),
      Preference.builder().dayOfWeek(DayOfWeek.MONDAY).time(LocalTime.of(21, 00)).build(),
      Preference.builder().dayOfWeek(DayOfWeek.WEDNESDAY).time(LocalTime.of(19, 00)).build(),
      Preference.builder().dayOfWeek(DayOfWeek.WEDNESDAY).time(LocalTime.of(21, 00)).build()
    );
  }

  public boolean bookWeek(LocalDate week, String phpSessionId) throws IOException {
    Set<Court> freeCourts = freeCourtService.freeCourts(week, phpSessionId);

    if (CollectionUtils.isEmpty(freeCourts)) {
      log.warn("No free courts, all blocked or not logged in in week {}", week);
    }

    for (Preference preference : preferences) {
      List<Court> matchingCourts = freeCourts.stream()
        .filter(court -> Objects.equals(court.getDate().getDayOfWeek(), preference.getDayOfWeek())
          && Objects.equals(court.getTime(), preference.getTime()))
        .sorted(Comparator.comparing(court -> court.getCourtNo()))
        .collect(Collectors.toList());

      if (!CollectionUtils.isEmpty(matchingCourts)) {
        log.debug("Matching courts for week {} are {}", week, matchingCourts);

        for (Court matchingCourt : matchingCourts) {
          boolean success = bookingService.book(matchingCourt, phpSessionId);

          log.debug("Booking of court {} was {}", matchingCourt, success);

          if (success) {
            return true;
          }
        }
      }
    }

    return false;
  }
}
