package usst.group.teachserver.entities.transactEntities;

import java.util.Date;

public class OneToOneStudentInfo {
    private String phoneNumber;
    private String name;
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public OneToOneStudentInfo(String phoneNumber, String name, Date date) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.date = date;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
