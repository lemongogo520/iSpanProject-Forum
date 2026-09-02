package forum.board.home;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {

    @NotBlank(message = "請輸入帳號")
    @Size(min = 6, max = 20, message = "帳號長度必須介於6~20個字元,並且包含英文或數字")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).{6,20}$",
            message = "帳號只能使用英文、數字、底線，並且包含英文或數字"
    )
    private String account;

    @NotBlank(message = "請輸入密碼")
    @Size(min = 6, max = 20, message = "密碼長度必須介於6~20個字元")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).{6,20}$",
            message = "密碼只能使用英文、數字、底線，並且包含英文或數字"
    )
    private String password;

    @NotBlank(message = "請再次輸入密碼")
    private String confirmPassword;

//    @NotBlank(message = "請輸入姓名")
//    @Size(min = 2, max = 30, message = "姓名長度必須介於2~30個字")
//    private String name;

//    @NotBlank(message = "請輸入Email")
//    @Email(message = "Email格式不正確")
//    private String email;
//
//    @Pattern(
//            regexp = "^09\\d{8}$",
//            message = "手機格式不正確"
//    )
//    private String phone;
//
//    @AssertTrue(message = "請勾選同意會員條款")
//    private boolean agree;
//
//    
}