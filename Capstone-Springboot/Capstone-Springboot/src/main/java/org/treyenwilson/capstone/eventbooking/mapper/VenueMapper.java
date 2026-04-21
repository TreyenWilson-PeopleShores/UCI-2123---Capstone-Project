package org.treyenwilson.capstone.eventbooking.mapper;

import org.springframework.stereotype.Component;
import org.treyenwilson.capstone.eventbooking.dto.VenueRequest;
import org.treyenwilson.capstone.eventbooking.dto.VenueResponse;
import org.treyenwilson.capstone.eventbooking.entity.Venue;

@Component
public class VenueMapper {
    public VenueResponse toResponse(Venue venue){
        VenueResponse response = new VenueResponse();
        response.setId(venue.getId());
        response.setVenue_name(venue.getVenue_name());
        response.setLocation(venue.getLocation());
        response.setTotal_capacity(venue.getTotal_capacity());
        return response;
    }

    public Venue toEntity(VenueRequest request){
        Venue venue = new Venue();
        venue.setVenue_name(request.getVenue_name());
        venue.setLocation(request.getLocation());
        venue.setTotal_capacity(request.getTotal_capacity());
        return venue;
    }
}
