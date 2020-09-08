package it.polito.ai.lab2.pojos;

import lombok.Data;

@Data
public class SetVmsResourceLimits {
    int vCpu;
    int diskStorage;
    int ram;
    int maxActiveInstances;
    int maxTotalInstances;
}
