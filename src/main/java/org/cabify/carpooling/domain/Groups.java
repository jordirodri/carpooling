package org.cabify.carpooling.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Groups {

    @Id
    @JsonIgnore
    private Integer uuid;
    private Integer id;
    private Integer people;
    private Boolean waiting;
}
