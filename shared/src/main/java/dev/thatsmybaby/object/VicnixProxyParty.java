package dev.thatsmybaby.object;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@AllArgsConstructor
@Data
public class VicnixProxyParty {

    private String uniqueId;
    private String leader;
    private Set<String> members;
}