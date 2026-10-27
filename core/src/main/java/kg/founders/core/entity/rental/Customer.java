package kg.founders.core.entity.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_email", columnList = "email", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    // Email или телефон должен быть заполнен (проверяется в сервисе и CHECK-констрейнтом в БД)
    @Column(unique = true)
    private String email;

    // Не уникален: номера могут быть из разных стран и повторяться у разных клиентов
    private String phone;

    @Column(length = 1024)
    private String additionalInfo;
}
