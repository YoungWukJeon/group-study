package model.optional;

import lombok.*;

import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    private Optional<Insurance> insurance;
}
