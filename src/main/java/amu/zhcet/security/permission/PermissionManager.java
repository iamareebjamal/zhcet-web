package amu.zhcet.security.permission;

import amu.zhcet.core.auth.CustomUser;
import amu.zhcet.core.notification.Notification;
import amu.zhcet.core.notification.NotificationRepository;
import amu.zhcet.core.notification.recipient.NotificationRecipient;
import amu.zhcet.core.notification.recipient.NotificationRecipientRepository;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.CourseManagementService;
import amu.zhcet.data.user.Roles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@Slf4j
@Service
public class PermissionManager {

    private final RoleHierarchy roleHierarchy;
    private final CourseManagementService courseManagementService;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;

    @Autowired
    public PermissionManager(RoleHierarchy roleHierarchy, CourseManagementService courseManagementService, NotificationRepository notificationRepository, NotificationRecipientRepository notificationRecipientRepository) {
        this.roleHierarchy = roleHierarchy;
        this.courseManagementService = courseManagementService;
        this.notificationRepository = notificationRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
    }

    public List<GrantedAuthority> authorities(List<String> roles) {
        return roles.stream()
                .flatMap(role -> roleHierarchy.getReachableGrantedAuthorities(createAuthorityList(role)).stream())
                .collect(Collectors.toList());
    }

    public boolean hasPermission(Collection<? extends GrantedAuthority> authorities, String permission) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(permission));
    }

    public boolean checkDepartment(Authentication user, String departmentCode) {
        if (hasPermission(user.getAuthorities(), Roles.DEPARTMENT_SUPER_ADMIN))
            return true;

        if (!(user.getPrincipal() instanceof CustomUser))
            return false;
        return hasPermission(user.getAuthorities(), Roles.DEPARTMENT_ADMIN) &&
                ((CustomUser) user.getPrincipal()).getDepartment().getCode().equals(departmentCode);
    }

    public boolean checkCourse(Authentication user, String departmentCode, String courseCode) {
        Course course = courseManagementService.getCourse(courseCode);
        return checkDepartment(user, departmentCode) && (course == null || course.getDepartment().getCode().equals(departmentCode));
    }

    public boolean checkNotificationCreator(Authentication user, String notificationId) {
        boolean hasSendingPermission = hasPermission(user.getAuthorities(), Roles.TEACHING_STAFF) ||
                hasPermission(user.getAuthorities(), Roles.DEVELOPMENT_ADMIN);
        try {
            Notification notification = notificationRepository.findOne(Long.parseLong(notificationId));
            return notification != null && hasSendingPermission && notification.getSender().getUserId().equals(user.getName());
        } catch (NumberFormatException nfe) {
            return true;
        }
    }

    public boolean checkNotificationRecipient(Authentication user, String notificationId) {
        try {
            NotificationRecipient notification = notificationRecipientRepository.findOne(Long.parseLong(notificationId));
            return notification != null && notification.getRecipient().getUserId().equals(user.getName());
        } catch (NumberFormatException nfe) {
            return true;
        }
    }

}