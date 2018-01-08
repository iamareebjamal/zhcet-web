package amu.zhcet.core.shared.attendance.download;

import amu.zhcet.common.utils.StringUtils;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.CourseManagementService;
import amu.zhcet.data.course.incharge.CourseInChargeService;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.department.Department;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller for downloading attendance from Dean, Department and Faculty Admin accounts
 * This controller and package presents shared functionality between dean, department and faculty
 * For separate functionality, please see the respective packages of `dean`, `department` and `faculty`
 */
@Slf4j
@Controller
public class AttendanceDownloadController {

    private final AttendanceDownloadService attendanceDownloadService;
    private final CourseManagementService courseManagementService;
    private final CourseInChargeService courseInChargeService;

    @Autowired
    public AttendanceDownloadController(AttendanceDownloadService attendanceDownloadService, CourseManagementService courseManagementService, CourseInChargeService courseInChargeService) {
        this.attendanceDownloadService = attendanceDownloadService;
        this.courseManagementService = courseManagementService;
        this.courseInChargeService = courseInChargeService;
    }

    /**
     * Downloads attendance for course taught by faculty. Shown in attendance upload section of the course in Faculty Panel
     * @param response Response object to be sent, containing the attendance CSV
     * @param code The course and section code for faculty, of the form course:section
     */
    @GetMapping("faculty/courses/{code}/attendance/download")
    public void downloadAttendanceForFaculty(HttpServletResponse response, @PathVariable String code) {
        courseInChargeService.getCourseInCharge(code).ifPresent(courseInCharge -> {
            String section = StringUtils.defaultString(CourseInChargeService.getCodeAndSection(code).getRight(), "all");
            List<CourseRegistration> courseRegistrations = courseInChargeService.getCourseRegistrations(courseInCharge);
            String suffix = courseInCharge.getFloatedCourse().getCourse().getCode() + "_" + section;
            downloadAttendance("faculty", suffix, courseRegistrations, response);
        });
    }

    /**
     * Downloads attendance for course in Dean Admin Panel in Floated Course Edit section
     * @param course Course for which the attendance is to be downloaded
     * @param response Response object to be sent, containing the attendance CSV
     */
    @GetMapping("dean/floated/{course}/attendance/download")
    public void downloadAttendanceForDean(@PathVariable Course course, HttpServletResponse response) {
        downloadAttendance("dean", course, response);
    }

    /**
     * Downloads attendance for the course in Department Admin Panel in Floated Course Edit Section
     * @param department Department to which the course belongs
     * @param course Course for which the attendance is to be downloaded
     * @param response Response object to be sent, containing the attendance CSV
     */
    @GetMapping("department/{department}/floated/{course}/attendance/download")
    public void downloadAttendanceForDepartment(@PathVariable Department department, @PathVariable Course course, HttpServletResponse response) {
        downloadAttendance("department", course, response);
    }

    private void downloadAttendance(String context, Course course, HttpServletResponse response) {
        courseManagementService.getFloatedCourse(course).ifPresent(floatedCourse ->
                downloadAttendance(context, course.getCode(), floatedCourse.getCourseRegistrations(), response));
    }

    private void downloadAttendance(String context, String suffix, List<CourseRegistration> courseRegistrations, HttpServletResponse response) {
        try {
            attendanceDownloadService.download(suffix, context, courseRegistrations, response);
        } catch (IOException e) {
            log.error("Attendance Download", e);
        }
    }

}