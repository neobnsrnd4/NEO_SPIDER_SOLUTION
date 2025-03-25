package neo.spider.admin.flow.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SearchApplicationResultDto {
    private long applicationId;
    private String applicationName;
    private LocalDate createdDate;
    private LocalDate modifiedDate;
}
