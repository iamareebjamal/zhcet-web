package in.ac.amu.zhcet.service.security.permission;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.CourseInCharge;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.model.notification.Notification;
import in.ac.amu.zhcet.data.model.notification.NotificationRecipient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

@Slf4j
public class DomainPermissionExpression extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    private Object target;

    public DomainPermissionExpression(Authentication authentication) {
        super(authentication);
    }

    public boolean isDepartment(Department department) {
        return department != null && PermissionManager.hasPermissionOfDepartment(authentication, department);

    }

    public boolean isOfDepartment(Department department, Course course) {
        return !(department == null || course == null) && PermissionManager.hasPermissionOfDepartmentAndCourse(authentication, department, course);

    }

    public boolean isFloated(FloatedCourse floatedCourse) {
        return floatedCourse != null && isOfDepartment(floatedCourse.getCourse().getDepartment(), floatedCourse.getCourse());
    }

    public boolean isCourseInCharge(CourseInCharge courseInCharge) {
        return courseInCharge != null;
    }

    public boolean createdNotification(Notification notification) {
        return notification != null && PermissionManager.createdNotification(authentication, notification);
    }

    public boolean hasNotificationPermission(NotificationRecipient notification) {
        return notification != null && PermissionManager.hasPermissionOfNotification(authentication, notification);
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    void setThis(Object target) {
        this.target = target;
    }

    @Override
    public Object getThis() {
        return target;
    }
}
