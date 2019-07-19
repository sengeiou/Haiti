package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <pre>
 * </pre>
 * 
 * @author 
 */

@Entity
@Table(name = "METERINGDATA_NM")
@Index(name="IDX_METERINGDATA_NM_01", columnNames={"yyyymmddhhmmss", "mdev_id", "location_id"})
public class MeteringDataNM extends MeteringData{	
}
