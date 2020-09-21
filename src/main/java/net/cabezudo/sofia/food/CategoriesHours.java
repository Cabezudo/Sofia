package net.cabezudo.sofia.food;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.schedule.AbstractTime;
import net.cabezudo.sofia.core.schedule.Day;
import net.cabezudo.sofia.core.schedule.EndEvent;
import net.cabezudo.sofia.core.schedule.Event;
import net.cabezudo.sofia.core.schedule.Schedule;
import net.cabezudo.sofia.core.schedule.StartEvent;
import net.cabezudo.sofia.core.schedule.TimeEvents;
import net.cabezudo.sofia.restaurants.OpenTimes;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.18
 */
public class CategoriesHours {

  private final int dayOfWeek;
  private final String todayName;
  private final OpenTimes events = new OpenTimes();

  CategoriesHours(Categories categories, int offset) {
    Instant instant = Instant.now();
    OffsetDateTime now = instant.atOffset(ZoneOffset.ofHoursMinutes(-offset / 60, -offset % 60));
    dayOfWeek = now.getDayOfWeek().getValue();
    todayName = Day.getShortName(dayOfWeek);

    TimeEvents temporalEvents = getTemporalEvents(categories);
    cleanOverlapedEvents(temporalEvents);

    int hour = now.getHour();
    int minutes = now.getMinute();
    int time = ((hour * 60) + minutes) * 60;
  }

  public JSONObject toJSONTree() {
    JSONObject jsonObject = new JSONObject();

    JSONObject jsonToday = new JSONObject();
    jsonToday.add(new JSONPair("name", todayName));
    jsonObject.add(new JSONPair("today", jsonToday));

    return jsonObject;
  }

  private TimeEvents getTemporalEvents(Categories categories) {
    TimeEvents temporalEvents = new TimeEvents();
    for (Category category : categories) {
      Schedule schedule = category.getSchedule();
      ArrayList<AbstractTime> timeList = schedule.getTimeList();
      for (AbstractTime time : timeList) {
        temporalEvents.add(new StartEvent(time.getIndex(), time.getStart()));
        temporalEvents.add(new EndEvent(time.getIndex(), time.getEnd()));
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
          events.add(event.getDay(), lastEvent, event);
        }
      }
    }
  }
}
