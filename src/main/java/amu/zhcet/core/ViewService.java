package amu.zhcet.core;

import amu.zhcet.auth.Auditor;
import amu.zhcet.auth.UserAuth;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.user.Gender;
import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Strings;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ViewService {

    public String getClassForCourse(Course course) {
        if (course.getSemester() == null)
            return "badge-secondary";

        switch (course.getSemester()) {
            case 3:
                return "badge-danger";
            case 4:
                return "badge-info";
            case 5:
                return "bg-pink";
            case 6:
                return "bg-orange";
            case 7:
                return "badge-primary";
            case 8:
                return "badge-success";
            default:
                return "badge-secondary";
        }
    }

    public String getClassForGender(Gender gender) {
        if (gender == null) return "";
        return gender.equals(Gender.M) ? "blue-dark" : "pink-dark";
    }

    public String getAvatarUrl(String url) {
        if (Strings.isNullOrEmpty(url))
            return "/img/account.svg";

        return url;
    }

    public String getStatus(char status) {
        switch (status) {
            case 'G':
                return "Graduated";
            case 'N':
                return "Name Removed";
            default:
                return "Active";
        }
    }

    public boolean isPasswordChanged() {
        UserAuth userAuth = Auditor.getLoggedInUserAuth();

        if (userAuth != null) {
            return userAuth.isPasswordChanged();
        }

        return false;
    }

    public boolean isEmailVerified() {
        UserAuth userAuth = Auditor.getLoggedInUserAuth();

        if (userAuth != null) {
            return userAuth.isEmailVerified();
        }

        return false;
    }

    public boolean p() {
        long time = System.nanoTime();
        boolean b = isPasswordChanged();
        long execTime = System.nanoTime() - time;
        log.debug("Exec time password {} {}", execTime, b);

        log.debug("===================");
        return b;
    }

    public boolean b() {
        long time = System.nanoTime();
        boolean b = isEmailVerified();
        long execTime = System.nanoTime() - time;
        log.debug("Exec time email {} {}", execTime, b);

        log.debug("===================");
        return b;
    }

}
