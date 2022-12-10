package org.example.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "Tournament")
@AllArgsConstructor
@NoArgsConstructor
public class Tournament {
    @Id
    private String id;
    private String name;
    private String game;
    private BigDecimal prize;
    private String currency;
    private Integer teamSize;
    private String bracketType;
    private LocalDateTime dateTime;
    private String region;
    private String ownerId;
    private List<String> userList;

    public List<String> getUserList(){
        if (userList == null){
            userList = new ArrayList<>();
        }
        return userList;
    }
}
