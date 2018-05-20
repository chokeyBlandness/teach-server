package usst.group.teachserver.entities.transactEntities;

import java.util.Date;

public class TransCourse {
    String teacherPhone;
    Date date;

    public String getTeacherPhone() {
        return teacherPhone;
    }

    public void setTeacherPhone(String teacherPhone) {
        this.teacherPhone = teacherPhone;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
