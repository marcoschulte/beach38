package de.voot.beach38.bookr.service;

import de.voot.beach38.bookr.data.BookedWeek;
import de.voot.beach38.bookr.repository.BookedWeekRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WeekRepository {

  private final BookedWeekRepository bookedWeekRepository;

  public WeekRepository(BookedWeekRepository bookedWeekRepository) {
    this.bookedWeekRepository = bookedWeekRepository;
  }

  public Collection<LocalDate> unbookedWeeks() {
    List<LocalDate> result = new ArrayList<>();

    LocalDate startInclusive = LocalDate.of(2017, 9, 11);
    LocalDate endExclusive = LocalDate.of(2017, 10, 25);

    LocalDate week = startInclusive;
    while (week.isBefore(endExclusive)) {
      result.add(week);
      week = week.plusWeeks(1);
    }

    List<BookedWeek> previouslyBooked = bookedWeekRepository.findAll();
    previouslyBooked.forEach(booked -> result.remove(booked.getWeek()));

    log.info("Weeks unbooked {}", result);

    return result;
  }

  public void markAsBooked(LocalDate week) {
    bookedWeekRepository.save(new BookedWeek(week));
  }

}
