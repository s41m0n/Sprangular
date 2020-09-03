package it.polito.ai.lab2.pojos;

import lombok.Data;

@Data
public class UpdateVmDetails {
    int vCpu;
    int diskStorage;
    int ram;
}
