package de.voot.beach38.bookr.service;

import de.voot.beach38.bookr.model.Court;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BookingService {

  private final boolean dryRun;

  public BookingService(@Value("${bookr.dry-run}") boolean dryRun) {
    this.dryRun = dryRun;
  }

  public boolean book(Court court) {
    if (dryRun) {
      log.warn("Dry run: booking {}", court);
      return true;
    }

    log.info("Booking {}", court);
    return new Random().nextBoolean();
  }
}
