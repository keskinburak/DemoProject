package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTournamentInput {
    private String name;
    private String game;
    private BigDecimal prize;
    private String currency;
    private Integer teamSize;
    private String bracketType;
    private String dateTime;
    private String region;
}
