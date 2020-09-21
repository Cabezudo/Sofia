package net.cabezudo.sofia.restaurants;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.schedule.AbstractTime;
import net.cabezudo.sofia.core.schedule.Day;
import net.cabezudo.sofia.core.schedule.EndEvent;
import net.cabezudo.sofia.core.schedule.Event;
import net.cabezudo.sofia.core.schedule.Schedule;
import net.cabezudo.sofia.core.schedule.StartEvent;
import net.cabezudo.sofia.core.schedule.TimeEvents;
import net.cabezudo.sofia.food.Categories;
import net.cabezudo.sofia.food.Category;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.18
 */
public class RestaurantBusinessHours {

  private final boolean todayStatus;
  private final int dayOfWeek;
  private final int tomorrowDayOfWeek;
  private final OpenTimes todayEvents = new OpenTimes();
  private final OpenTimes tomorrowEvents = new OpenTimes();
  private Event todayOpenAt = null;
  private Event tomorrowOpenAt = null;
  private final String todayName;
  private final String tomorrowName;

  RestaurantBusinessHours(Categories categories, int offset) {
    Instant instant = Instant.now();
    OffsetDateTime now = instant.atOffset(ZoneOffset.ofHoursMinutes(-offset / 60, -offset % 60));
    dayOfWeek = now.getDayOfWeek().getValue();
    todayName = Day.getShortName(dayOfWeek);
    tomorrowDayOfWeek = now.getDayOfWeek().plus(1).getValue();
    tomorrowName = Day.getShortName(tomorrowDayOfWeek);

    TimeEvents temporalEvents = getTemporalEvents(categories);
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
    todayStatus = isOpen;
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
    jsonToday.add(new JSONPair("isOpen", todayStatus));
    jsonToday.add(new JSONPair("openAt", todayOpenAt == null ? null : Utils.toHour(todayOpenAt.getTime())));
    jsonToday.add(new JSONPair("times", todayEvents.toJSONTree()));
    jsonObject.add(new JSONPair("today", jsonToday));

    JSONObject jsonTomorrow = new JSONObject();
    jsonTomorrow.add(new JSONPair("name", tomorrowName));
    jsonTomorrow.add(new JSONPair("openAt", tomorrowOpenAt == null ? null : Utils.toHour(tomorrowOpenAt.getTime())));
    jsonTomorrow.add(new JSONPair("times", tomorrowEvents.toJSONTree()));
    jsonObject.add(new JSONPair("tomorrow", jsonTomorrow));

    // TODO add a deep search for the next day open
    jsonObject.add(new JSONPair("openUntil", null));
    return jsonObject;
  }

  private TimeEvents getTemporalEvents(Categories categories) {
    TimeEvents temporalEvents = new TimeEvents();
    for (Category category : categories) {
      Schedule schedule = category.getSchedule();
      ArrayList<AbstractTime> timeList = schedule.getTimeList();
      for (AbstractTime time : timeList) {
        if (time.dayIs(dayOfWeek) || time.dayIs(tomorrowDayOfWeek)) {
          temporalEvents.add(new StartEvent(time.getIndex(), time.getStart()));
          temporalEvents.add(new EndEvent(time.getIndex(), time.getEnd()));
        }
      }
    }
    return temporalEvents;
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
}
