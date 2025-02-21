package neo.spider.admin.flow.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateApplicationDto {
    String applicationId;
    String applicationName;
    String category;

}
