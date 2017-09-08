package de.voot.beach38.bookr.service;

import de.voot.beach38.bookr.model.Court;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BookingService {

  public boolean book(Court court) {
    log.info("Booking {}", court);
    return new Random().nextBoolean();
  }
}
