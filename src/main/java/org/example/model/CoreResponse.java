package org.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CoreResponse {

    String returnCode;

    String errorMessage;

}
