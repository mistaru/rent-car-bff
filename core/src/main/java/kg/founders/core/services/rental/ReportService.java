package kg.founders.core.services.rental;

import kg.founders.core.model.rental.ReportDto;
import kg.founders.core.model.rental.ReportFilterRequest;
import org.springframework.transaction.annotation.Transactional;

public interface ReportService {
    @Transactional(readOnly = true)
    ReportDto generateReport(ReportFilterRequest filter);
}
