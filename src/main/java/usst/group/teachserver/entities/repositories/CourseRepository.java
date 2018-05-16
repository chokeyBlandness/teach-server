package usst.group.teachserver.entities.repositories;

import org.springframework.data.repository.CrudRepository;
import usst.group.teachserver.entities.Course;

public interface CourseRepository extends CrudRepository<Course,Long> {
}
