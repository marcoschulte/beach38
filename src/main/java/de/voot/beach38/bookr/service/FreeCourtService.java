package de.voot.beach38.bookr.service;

import de.voot.beach38.bookr.model.Court;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class FreeCourtService {

  private static final Pattern ONCLICK = Pattern.compile(".*?area_id=([0-9])&date=(.+?)&time=([0-9]+):00&.*?");

  public Set<Court> freeCourts(LocalDate week, String phpSessionId) throws IOException {
    Document doc = Jsoup
      .connect("https://ssl.forumedia.eu/beach38-courtbuchung.de/reservations_week.php")
      .data("action", "showReservations")
      .data("type_id", "1") // Indoor = 1, Outdoor = 2
      .data("date", week.toString())
      .cookie("PHPSESSID", phpSessionId)
      .get();

    Set<Court> freeCourts = new HashSet<>();

    Elements courts = doc.select(".week-box");
    courts.forEach(c -> {
      String onclick = c.attr("onclick");
      if (StringUtils.hasText(onclick)) {
        Matcher matcher = ONCLICK.matcher(onclick);
        if (matcher.matches()) {
          String courtNo = matcher.group(1);
          String date = matcher.group(2);
          String hour = matcher.group(3);
          log.debug("Free court {}", matcher.group());

          Court court = Court.builder()
            .courtNo(courtNo)
            .date(LocalDate.parse(date))
            .time(LocalTime.of(Integer.parseInt(hour), 0))
            .build();

          log.debug("Free court {}", court);

          freeCourts.add(court);
        }
      }
    });

    return freeCourts;
  }
}
