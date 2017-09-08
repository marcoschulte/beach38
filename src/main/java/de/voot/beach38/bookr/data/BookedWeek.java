package de.voot.beach38.bookr.data;

import java.time.Instant;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class BookedWeek {

  public BookedWeek(LocalDate week) {
    this.week = week;
  }

  @Id
  @GeneratedValue
  private Long id;

  private LocalDate week;

}
