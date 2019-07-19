package com.aimir.model.system;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

import javax.persistence.*;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 *
 * <p>GroupStrategy Table</p>
 * <p>Manages Metering Schedule(or Strategy) of Meter-Groups</p>
 * <p>Each groups are specified by DSO(location)</p>
 *
 * @author Han SeJin(sjhan@nuritelecom.com)
 *
 */

@Entity
@Table(name = "GROUP_STRATEGY")
public class GroupStrategy extends BaseObject {

    private static final long serialVersionUID = -1172320728125799276L;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="GROUP_STRATEGY_SEQ")
    @SequenceGenerator(name="GROUP_STRATEGY_SEQ", sequenceName="GROUP_STRATEGY_SEQ", allocationSize=1)
    @ColumnInfo(name="PK", descr="PK")
    private Integer id;	//	ID(PK)

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="group_id")
    @ColumnInfo(name="그룹정보")
    private AimirGroup aimirGroup;

    @Column(name="group_id", nullable=true, updatable=false, insertable=false)
    private Integer groupId;

    @Column(name="config_name",nullable=false,updatable=false)
    @ColumnInfo(descr="설정한 스케줄 명칭")
    private String configName;

    @Column(name="config_value")
    @ColumnInfo(descr="설정한 스케줄의 값")
    private String configValue;

    @Column(name="prev_value")
    @ColumnInfo(descr="변경되기 전 스케줄의 값")
    private String prevValue;
    @Column(name="create_date",length=14,updatable=false)
    private String createDate;

    @Column(name="update_date",length=14)
    private String updateDate;

    @Column(name="login_id")
    @ColumnInfo(descr="생성 혹은 변경한 유저 아이디")
    private String loginId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AimirGroup getAimirGroup() {
        return aimirGroup;
    }

    public void setAimirGroup(AimirGroup aimirGroup) {
        this.aimirGroup = aimirGroup;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getPrevValue() {
        return prevValue;
    }

    public void setPrevValue(String prevValue) {
        this.prevValue = prevValue;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    @Override
    public String toString() {
        String retValue = "";

        retValue = "{"
                + "id:'" + this.id
                + "',aimirGroup:'" + ((this.getAimirGroup() == null)? "null" : this.aimirGroup.getName())
                + "',configName:'" +  ((this.getConfigName() == null)? "null" : this.configName)
                + "',configValue:'" +  ((this.getConfigValue() == null)? "null" : this.configValue)
                + "',prevValue:'" +  ((this.getPrevValue() == null)? "null" : this.prevValue)
                + "',createDate:'" +  ((this.getCreateDate() == null)? "null" : this.createDate)
                + "',updateDate:'" +  ((this.getUpdateDate() == null)? "null" : this.updateDate)
                + "',loginId:'" +  ((this.getLoginId() == null)? "null" : this.loginId)
                + "'}";

        return retValue;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
