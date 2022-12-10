package org.example.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "Role")
@AllArgsConstructor
@Builder
public class Role {
    @Id
    private String id;

    private String name;
}
