package de.voot.beach38.bookr.service;

import de.voot.beach38.bookr.model.Court;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
public class BookingService {

  private final boolean dryRun;

  public BookingService(@Value("${bookr.dry-run}") boolean dryRun) {
    this.dryRun = dryRun;
  }

  public boolean book(Court court, String phpSessionId) throws IOException {
    if (dryRun) {
      log.warn("Dry run: booking {}", court);
      return true;
    }

    Document doc = Jsoup
      .connect("https://ssl.forumedia.eu/beach38-courtbuchung.de/reservation_order.php")
      .data("action", "proceedOrder")
      .data("area_id", court.getCourtNo())
      .data("date", court.getDate().toString())
      .data("time", court.getTime().getHour() + ":00")
      .data("page", "1")
      .data("memo", "")
      .data("sprice_id", "0")
      .data("prepayment", "1")
      .cookie("PHPSESSID", phpSessionId)
      .get();

    Elements headers = doc.select(".headerContent h2");
    Assert.state(headers.size() == 1, "Expected one header element");
    String header = headers.first().text();

    if ("Error".equals(header)) {
      Elements contents = doc.select(".aroundBox .content");
      Assert.state(contents.size() > 0, "No error content found");

      log.warn("Error booking {}: {}", court, contents.first().text());
      return false;
    }

    log.info("Booked court {}", court);
    return true;
  }
}
