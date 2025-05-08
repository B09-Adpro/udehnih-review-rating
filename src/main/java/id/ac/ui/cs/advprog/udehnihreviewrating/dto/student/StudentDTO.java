package id.ac.ui.cs.advprog.udehnihreviewrating.dto.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private String id;
    private String email;
    private String name;
}
