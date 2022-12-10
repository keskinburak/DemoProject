package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditUserInput {
    @NotNull
    private String id;
    private String name;
    private String surname;
    private String password;
    private String username;
}
