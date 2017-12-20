package in.ac.amu.zhcet.controller.auth;

import in.ac.amu.zhcet.service.security.password.PasswordReset;
import in.ac.amu.zhcet.service.security.password.PasswordResetService;
import in.ac.amu.zhcet.service.security.password.PasswordVerificationException;
import in.ac.amu.zhcet.service.security.password.TokenValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
public class ResetPasswordController {

    private final PasswordResetService passwordResetService;

    public ResetPasswordController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/login/password/reset")
    public String resetPassword(Model model, @RequestParam String hash, @RequestParam("auth") String token){

        try {
            passwordResetService.validate(hash, token);
            PasswordReset passwordReset = new PasswordReset();
            passwordReset.setHash(hash);
            passwordReset.setToken(token);
            model.addAttribute("password", passwordReset);
        } catch (TokenValidationException tve) {
            log.warn("Token Verification : Password Reset : {}", tve);
            model.addAttribute("error", tve.getMessage());
        }

        return "user/reset_password";
    }

    @PostMapping("/login/password/reset")
    public String savePassword(@Valid PasswordReset passwordReset, RedirectAttributes redirectAttributes) {
        String redirectUrl = String.format("redirect:/login/password/reset?hash=%s&auth=%s", passwordReset.getHash(), passwordReset.getToken());

        try {
            passwordResetService.resetPassword(passwordReset);
            redirectAttributes.addFlashAttribute("reset_success", true);
            return "redirect:/login";
        } catch (TokenValidationException tve) {
            log.warn("Token Verification : Password Reset : {}", tve.getMessage());
            redirectAttributes.addAttribute("error", tve.getMessage());
        } catch (PasswordVerificationException pve) {
            log.info("Password Verification Exception", pve);
            redirectAttributes.addFlashAttribute("pass_errors", pve.getErrors());
        }

        return redirectUrl;
    }

}
