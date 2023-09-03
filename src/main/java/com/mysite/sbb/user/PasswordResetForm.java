package com.mysite.sbb.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetForm {

    @NotEmpty(message = "변경할 비밀번호는 필수항목입니다.")
    private String newPassword;

    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    private String confirmPassword;

    @NotEmpty(message = "현재 비밀번호는 필수항목입니다.")
    private String currentPassword;

}
