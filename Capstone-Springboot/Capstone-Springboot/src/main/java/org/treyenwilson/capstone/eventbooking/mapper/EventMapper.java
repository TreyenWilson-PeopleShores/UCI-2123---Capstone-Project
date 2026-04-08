package org.treyenwilson.capstone.eventbooking.mapper;

import org.springframework.stereotype.Component;
import org.treyenwilson.capstone.eventbooking.dto.EventRequest;
import org.treyenwilson.capstone.eventbooking.dto.EventResponse;
import org.treyenwilson.capstone.eventbooking.entity.Event;

@Component
public class EventMapper {
    public EventResponse toResponse(Event event){
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setEvent_name(event.getEvent_name());
        response.setDate(event.getDate());
        response.setStatus(event.getStatus());
        response.setTotal_spots(event.getTotal_spots());
        response.setVenue_id(event.getVenue_id());
        return response;
    }

    public Event toEntity(EventRequest request){
        Event event = new Event();
        event.setEvent_name(request.getEvent_name());
        event.setDate(request.getDate());
        event.setStatus(request.getStatus());
        event.setTotal_spots(request.getTotal_spots());
        event.setVenue_id(request.getVenue_id());
        return event;
    }
}
