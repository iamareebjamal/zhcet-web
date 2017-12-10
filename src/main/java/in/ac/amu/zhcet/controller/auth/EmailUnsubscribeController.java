package in.ac.amu.zhcet.controller.auth;

import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class EmailUnsubscribeController {

    private final UserService userService;

    @Autowired
    public EmailUnsubscribeController(UserService userService) {
        this.userService = userService;
    }

    @ResponseBody
    @GetMapping("/login/unsubscribe")
    public String unsubscribe(@RequestParam String email, @RequestParam String conf) {
        if (!SecurityUtils.hashMatches(email, conf))
            return "Invalid Entry";

        userService.getUserByEmail(email).ifPresent(user -> {
            userService.unsubscribeEmail(user, true);
        });

        return "Unsubscribed";
    }

}
