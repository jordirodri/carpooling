package org.cabify.carpooling.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @JsonIgnore
    private Integer uuid;
    private Integer id;
    private Integer seats;

    private Integer emptySeats;

    private Boolean waiting = true;

    @JsonIgnore
    private List<Integer> groupIds = new ArrayList<>();

    public void setSeats(Integer seats) {
        this.seats = seats;
        this.emptySeats = seats;
    }
}
