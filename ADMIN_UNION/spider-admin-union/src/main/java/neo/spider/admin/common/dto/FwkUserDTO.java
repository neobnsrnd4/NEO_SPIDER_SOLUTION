package neo.spider.admin.common.dto;

import java.util.Date;

import org.apache.ibatis.type.Alias;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Alias("FwkUserDTO")
@Getter
@Setter
@ToString
public class FwkUserDTO implements Cloneable {	// Spider 사용자 정보 DTO
    private String userId;
    @NotBlank(message = "사용자ID는 필수 항목입니다")
    @Size(min = 3, max = 20, message = "사용자ID는 3~20 자리의 문자를 입력해주세요")
    private String username;

    @NotBlank(message = "EMAIL은 필수 항목입니다")
    @Email(message = "EMAIL 규격에 맞지 않습니다")
    private String email;

    @NotBlank(message = "패스워드는 필수 항목입니다")
    @Size(min = 8, max = 50, message = "패스워드는 8~50 자리의 문자를 입력해주세요")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$",
        message = "패스워드는 숫자, 영문, 특수문자를 포함해야 합니다"
    )
    private String password;

    @Pattern(
    		regexp = "^01(?:0|1|[6-9])-(\\d{3,4})-(\\d{4})$",
    		message = "전화번호는 010-1234-5678 형식이어야 합니다"
    		)
    private String phone;
    @Pattern(
    		regexp = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$",
    		message = "IP 주소 형식이 잘못되었습니다"
    		)
    private String accessIp;
    
    @Pattern(
    		regexp = "^\\d{6}-\\d{7}$",
    		message = "주민등록번호는 000000-0000000 형식이어야 합니다"
    		)
    private String userSsn;
    
    @NotBlank(message = "권한(role)은 필수 항목입니다")
    private String role;

    @NotBlank(message = "주소는 필수 항목입니다")
    @Size(min = 20, max = 100, message = "주소는 20~100 자리의 문자를 입력해주세요")
    private String address;


    @Pattern(regexp = "^[A-Z]{2}[0-9]{3}$", message = "사용자 상태 코드는 AA123 형식이어야 합니다")
    private String userStateCode;

//    @NotBlank(message = "클래스명은 필수 항목입니다")
//    @Size(max = 50, message = "클래스명은 최대 50 자리의 문자를 입력해주세요")
    private String className;
    
    @NotBlank(message = "업무 권한 코드는 필수 항목입니다")
    @Size(max = 10, message = "업무 권한 코드는 최대 10 자리의 문자를 입력해주세요")
    private String bizAuthCode;
 
    
    private String lastUpdateDtime;

    private String lastUpdateUserId;


    private String positionName;


    private String regReqUserName;


    private String empNo;

    private String branchNo;


    private Integer loginFailCount;

    private Date finalApprovalDtime;

    private String finalApprovalUserId;
    private String titleName;

    private String finalApprovalUserName;

    private Date lastPwdUpdateDtime;

    private Integer defaultProjectId;

    private String paUser;

    private String prePaUser;

    private String paUserSalt;

    private String prePaUserSalt;

    private String finalApprovalState;

    private String paUserInfo;



    

	@Override
	public FwkUserDTO clone() throws CloneNotSupportedException {
		return (FwkUserDTO) super.clone();
	}



}
