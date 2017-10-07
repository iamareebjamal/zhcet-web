package in.ac.amu.zhcet.controller.profile;

import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.user.Type;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.data.model.user.UserDetail;
import in.ac.amu.zhcet.service.core.FacultyService;
import in.ac.amu.zhcet.service.core.StudentService;
import in.ac.amu.zhcet.service.core.UserService;
import in.ac.amu.zhcet.service.user.UserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Slf4j
@Controller
public class ProfileController {

    private final UserService userService;
    private final StudentService studentService;
    private final FacultyService facultyService;
    private final UserDetailService userDetailService;

    @Autowired
    public ProfileController(UserService userService, StudentService studentService, FacultyService facultyService, UserDetailService userDetailService) {
        this.userService = userService;
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.userDetailService = userDetailService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        UserAuth userAuth = userService.getLoggedInUser();
        model.addAttribute("user", userAuth);
        model.addAttribute("user_details", userAuth.getDetails());

        model.addAttribute("page_title", "Profile");
        model.addAttribute("page_subtitle", "Profile Settings for " + userAuth.getName());
        model.addAttribute("page_description", "Manage Profile Details and Information");

        if (userAuth.getType().equals(Type.STUDENT)) {
            Student student = studentService.getLoggedInStudent();
            model.addAttribute("student", student);
        } else {
            FacultyMember facultyMember = facultyService.getLoggedInMember();
            model.addAttribute("faculty", facultyMember);
        }

        return "user/profile";
    }

    @PostMapping("/profile/details")
    public String saveProfile(@ModelAttribute UserDetail userDetail, final RedirectAttributes redirectAttributes) {
        try {
            userDetailService.updateDetails(userService.getLoggedInUser(), userDetail);
            redirectAttributes.addFlashAttribute("success", true);
        } catch (Exception exc) {
            exc.printStackTrace();
            redirectAttributes.addFlashAttribute("errors", Collections.singletonList(exc.getMessage()));
        }

        return "redirect:/profile";
    }

}