package it.polito.ai.lab2.pojos;

import lombok.Data;

@Data
public class UpdateCourseDetails {
    int teamMinSize;
    int teamMaxSize;
    boolean enabled;
}
