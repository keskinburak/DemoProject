package org.example.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "User")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;
    private String name;
    private String surname;
    private String password;
    private String username;
    private List<String> tournamentId;
    private List<Role> roleList;

    public List<String> getTournamentId(){
        if (tournamentId == null){
            tournamentId = new ArrayList<>();
        }
        return tournamentId;
    }

    public List<Role> getRoleList(){
        if (roleList == null){
            roleList = new ArrayList<>();
        }
        return roleList;
    }
}
