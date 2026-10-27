package kg.founders.core.model.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCustomerRequest implements CustomerData {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Email must be valid")
    private String email;

    private String phone;

    private String additionalInfo;

    @JsonIgnore
    @AssertTrue(message = "Either email or phone is required")
    public boolean isContactProvided() {
        return (email != null && !email.isBlank()) || (phone != null && !phone.isBlank());
    }
}
