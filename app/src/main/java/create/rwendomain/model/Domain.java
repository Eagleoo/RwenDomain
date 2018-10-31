package create.rwendomain.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

public class Domain extends Model {
    @Column
    private String domain;
    @Column
    private String registration;
    @Column
    private String ip_address;

    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String remark;

    @Column
    private String util_time;


    public Domain() {
    }

    public Domain(String domain, String remark) {
        this.domain = domain;
        this.remark = remark;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUtil_time() {
        return util_time;
    }

    public void setUtil_time(String util_time) {
        this.util_time = util_time;
    }
}
