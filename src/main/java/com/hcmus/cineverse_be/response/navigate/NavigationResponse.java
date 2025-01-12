package com.hcmus.cineverse_be.response.navigate;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NavigationResponse {
    String route;
    Map<String, Object> params;
    boolean is_success;
}
