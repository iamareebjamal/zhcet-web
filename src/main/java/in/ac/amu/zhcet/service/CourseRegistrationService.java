package in.ac.amu.zhcet.service;

import in.ac.amu.zhcet.data.model.Attendance;
import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.model.dto.upload.AttendanceUpload;
import in.ac.amu.zhcet.data.repository.AttendanceRepository;
import in.ac.amu.zhcet.data.repository.CourseRegistrationRepository;
import in.ac.amu.zhcet.data.repository.FloatedCourseRepository;
import in.ac.amu.zhcet.service.config.ConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class CourseRegistrationService {

    private final FloatedCourseRepository floatedCourseRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final AttendanceRepository attendanceRepository;

    @Autowired
    public CourseRegistrationService(FloatedCourseRepository floatedCourseRepository, CourseRegistrationRepository courseRegistrationRepository, AttendanceRepository attendanceRepository) {
        this.floatedCourseRepository = floatedCourseRepository;
        this.courseRegistrationRepository = courseRegistrationRepository;
        this.attendanceRepository = attendanceRepository;
    }

    private Optional<CourseRegistration> getByStudentAndCourse(String enrolment, Course course) {
        Optional<FloatedCourse> floatedCourseOptional = floatedCourseRepository.getBySessionAndCourse(ConfigurationService.getDefaultSessionCode(), course);
        return floatedCourseOptional.flatMap(floatedCourse ->
                courseRegistrationRepository.findByStudent_EnrolmentNumberAndFloatedCourse(enrolment, floatedCourse));
    }

    @Transactional
    public void setAttendance(Course course, AttendanceUpload attendanceUpload) {
        Optional<CourseRegistration> registrationOptional = getByStudentAndCourse(attendanceUpload.getEnrolment_no(), course);

        registrationOptional.ifPresent(registration -> {
            Attendance storedAttendance = registration.getAttendance();
            if (storedAttendance == null) {
                log.warn("Attendance for {} and {} was null!", course, attendanceUpload.getEnrolment_no());
                storedAttendance = new Attendance(registration, attendanceUpload.getDelivered(), attendanceUpload.getAttended());

                registration.setAttendance(storedAttendance);
                storedAttendance.setCourseRegistration(registration);
                courseRegistrationRepository.save(registration);
            }

            storedAttendance.setDelivered(attendanceUpload.getDelivered());
            storedAttendance.setAttended(attendanceUpload.getAttended());
        });
    }

    @Transactional
    public List<Attendance> getAttendanceByStudent(String studentId) {
        return attendanceRepository.getByCourseRegistration_Student_EnrolmentNumberAndCourseRegistration_FloatedCourse_Session(studentId, ConfigurationService.getDefaultSessionCode());
    }

    @Transactional
    public void registerStudents(Course course, Set<CourseRegistration> courseRegistrations) {
        floatedCourseRepository.getBySessionAndCourse(ConfigurationService.getDefaultSessionCode(), course).ifPresent(floatedCourse -> {
            List<CourseRegistration> registrations = new ArrayList<>();

            for (CourseRegistration registration : courseRegistrations) {
                registration.setFloatedCourse(floatedCourse);
                registration.getAttendance().setId(registration.generateId());
                registrations.add(registration);
            }

            floatedCourse.getCourseRegistrations().addAll(registrations);
            floatedCourseRepository.save(floatedCourse);
        });
    }
}
