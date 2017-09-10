package de.voot.beach38.bookr.service;

import com.google.common.collect.Sets;
import de.voot.beach38.bookr.data.BookedWeek;
import de.voot.beach38.bookr.repository.BookedWeekRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WeekRepository {

  private final BookedWeekRepository bookedWeekRepository;
  private final Set<LocalDate> exclude;

  public WeekRepository(BookedWeekRepository bookedWeekRepository) {
    this.bookedWeekRepository = bookedWeekRepository;
    this.exclude = Collections.unmodifiableSet(Sets.newHashSet(
      LocalDate.of(2017, 12, 26),
      LocalDate.of(2018, 1, 2)));
  }

  public Collection<LocalDate> unbookedWeeks() {
    List<LocalDate> result = new ArrayList<>();

    // Starte Dienstags, in der WS gibt es Dienstags keine Feiertage. Feiertage zeigen keinen einzigen Court an, auch nicht in der
    // Wochenansicht
    /*LocalDate startInclusive = LocalDate.of(2017, 10, 3);
    LocalDate endExclusive = LocalDate.of(2018, 5, 8);*/

    LocalDate startInclusive = LocalDate.of(2017, 9, 18);
    LocalDate endExclusive = LocalDate.of(2017, 9, 26);

    LocalDate week = startInclusive;
    while (week.isBefore(endExclusive)) {
      result.add(week);
      week = week.plusWeeks(1);
    }

    result.removeAll(exclude);

    List<BookedWeek> previouslyBooked = bookedWeekRepository.findAll();
    previouslyBooked.forEach(booked -> result.remove(booked.getWeek()));

    log.info("Weeks unbooked {}", result);

    return result;
  }

  public void markAsBooked(LocalDate week) {
    bookedWeekRepository.save(new BookedWeek(week));
  }

}
