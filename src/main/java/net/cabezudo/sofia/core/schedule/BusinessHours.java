package net.cabezudo.sofia.core.schedule;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.restaurants.OpenTime;
import net.cabezudo.sofia.restaurants.OpenTimes;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.18
 */
public class BusinessHours {

  private boolean openNow;
  private int dayOfWeek;
  private int tomorrowDayOfWeek;
  private final OpenTimes todayEvents = new OpenTimes();
  private final OpenTimes tomorrowEvents = new OpenTimes();
  private Event todayOpenAt = null;
  private Event tomorrowOpenAt = null;
  private String todayName;
  private String tomorrowName;
  private List<AbstractTime> times = new ArrayList<>();
  private Instant instant;

  public BusinessHours() {
    // Nothing to do here
  }

  public void calculateFor(int timezoneOffset) {
    if (instant != null) {
      throw new SofiaRuntimeException("Already calculated for offset " + timezoneOffset);
    }
    instant = Instant.now();
    OffsetDateTime now = instant.atOffset(ZoneOffset.ofHoursMinutes(-timezoneOffset / 60, -timezoneOffset % 60));
    dayOfWeek = now.getDayOfWeek().getValue();
    todayName = Day.getShortName(dayOfWeek);
    tomorrowDayOfWeek = now.getDayOfWeek().plus(1).getValue();
    tomorrowName = Day.getShortName(tomorrowDayOfWeek);

    TimeEvents temporalEvents = createEvents();
    cleanOverlapedEvents(temporalEvents);

    int hour = now.getHour();
    int minutes = now.getMinute();
    int time = ((hour * 60) + minutes) * 60;

    boolean isOpen = false;

    for (OpenTime event : todayEvents) {
      if (event.isOpen(time)) {
        isOpen = true;
        break;
      }
      if (todayOpenAt == null && event.getStart().getTime() > time) {
        todayOpenAt = event.getStart();
      }
    }
    openNow = isOpen;
    for (OpenTime event : tomorrowEvents) {
      if (tomorrowOpenAt == null) {
        tomorrowOpenAt = event.getStart();
      }
    }
  }

  public JSONObject toJSONTree() {
    JSONObject jsonObject = new JSONObject();

    JSONObject jsonToday = new JSONObject();
    jsonToday.add(new JSONPair("name", todayName));
    jsonToday.add(new JSONPair("isOpen", openNow));
    jsonToday.add(new JSONPair("openAt", todayOpenAt == null ? null : Utils.toHour(todayOpenAt.getTime())));
    jsonToday.add(new JSONPair("times", todayEvents.toJSONTree()));
    jsonObject.add(new JSONPair("today", jsonToday));

    JSONObject jsonTomorrow = new JSONObject();
    jsonTomorrow.add(new JSONPair("name", tomorrowName));
    jsonTomorrow.add(new JSONPair("openAt", tomorrowOpenAt == null ? null : Utils.toHour(tomorrowOpenAt.getTime())));
    jsonTomorrow.add(new JSONPair("times", tomorrowEvents.toJSONTree()));
    jsonObject.add(new JSONPair("tomorrow", jsonTomorrow));

    Date closedUntil = null;
    if (!openNow && todayOpenAt == null && tomorrowOpenAt == null) {
      closedUntil = calculateNextOpen();
    }
    jsonObject.add(new JSONPair("closedUntil", closedUntil));
    return jsonObject;
  }

  public void add(AbstractTime time) {
    times.add(time);
  }

  private TimeEvents createEvents() {
    TimeEvents events = new TimeEvents();

    for (AbstractTime time : times) {
      if (time.dayIs(dayOfWeek) || time.dayIs(tomorrowDayOfWeek)) {
        events.add(new StartEvent(time.getIndex(), time.getStart()));
        events.add(new EndEvent(time.getIndex(), time.getEnd()));
      }
    }
    return events;
  }

  private void cleanOverlapedEvents(TimeEvents temporalEvents) {
    Event lastEvent = null;
    int open = 0;

    for (Event event : temporalEvents) {
      if (event.isStart()) {
        if (open == 0) {
          lastEvent = event;
        }
        open++;
      }
      if (event.isEnd()) {
        open--;
        if (open == 0) {
          distributeEvents(lastEvent, event);
        }
      }
    }
  }

  private void distributeEvents(Event lastEvent, Event event) {
    if (dayOfWeek == event.getDay()) {
      todayEvents.add(event.getDay(), lastEvent, event);
    } else {
      tomorrowEvents.add(event.getDay(), lastEvent, event);
    }
  }

  private Date calculateNextOpen() {
    // TODO calculate the next day open
    return null;
  }
}
