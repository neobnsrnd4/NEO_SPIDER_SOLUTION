package neo.spider.admin.flow.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class SearchDto {
    private String applicationId;
    private String applicationName;
    private LocalDate createdDate;
    private LocalDate modifiedDate;

    @Override
    public String toString() {
        return "SearchApplicationResultDto{" +
                "applicationId=" + applicationId +
                ", applicationName='" + applicationName + '\'' +
                ", createdDate=" + createdDate +
                ", modifiedDate=" + modifiedDate +
                '}';
    }
}
