package com.aimir.dao.system.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.PowerOnOffOrderDao;
import com.aimir.model.system.PowerOnOffOrder;
import com.aimir.util.Condition;

@Repository(value = "powerOnOffOrderDao")
public class PowerOnOffOrderDaoImpl extends AbstractJpaDao<PowerOnOffOrder, Integer> implements
		PowerOnOffOrderDao {
	private static Log log = LogFactory.getLog(PowerOnOffOrderDaoImpl.class);

	public PowerOnOffOrderDaoImpl() {
	    super(PowerOnOffOrder.class);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<PowerOnOffOrder> getPowerOnOffOrder(Long referenceId) {
	    Query query = getEntityManager().createQuery("select p from PowerOnOffOrder p where p.referenceId = :referenceId");
	    query.setParameter("referenceId", referenceId);
	    return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PowerOnOffOrder> searchPowerOnOffOrder(PowerOnOffOrder param,
			String additionalCondition) {

		String sql = "select p from PowerOnOffOrder p \n";

		boolean isFirst = true;
		if (param != null) {
			if (param.getUserName() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "p.userName='" + param.getUserName() + "'";
				isFirst = false;
			}
			if (param.getReferenceId() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "p.referenceId=" + param.getReferenceId();
				isFirst = false;
			}
			if (param.getMeterSerialNumber() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "p.meterSerialNumber='" + param.getMeterSerialNumber()
						+ "'";
				isFirst = false;
			}
			if (param.getPowerOperation() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "p.powerOperation='" + param.getPowerOperation() + "'";
				isFirst = false;
			}
			if (param.getPowerOperationDate() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "p.powerOperationDate='" + param.getPowerOperationDate()
						+ "'";
				isFirst = false;
			}
			if (param.getPowerOperationDateFrom() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "p.powerOperationDate>='"
						+ param.getPowerOperationDateFrom() + "'";
				isFirst = false;
			}
			if (param.getPowerOperationDateTo() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "p.powerOperationDate<='"
						+ param.getPowerOperationDateTo() + "'";
				isFirst = false;
			}
			if (param.getUserReference() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "p.userReference='" + param.getUserReference() + "'";
				isFirst = false;
			}
			if (param.getUserCreateDate() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "p.userCreateDate='" + param.getUserCreateDate() + "'";
				isFirst = false;
			}
			if (param.getUserCreateDateFrom() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "p.userCreateDate>='" + param.getUserCreateDateFrom()
						+ "'";
				isFirst = false;
			}
			if (param.getUserCreateDateTo() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "p.userCreateDate<='" + param.getUserCreateDateTo() + "'";
				isFirst = false;
			}
			if (param.getOrderStatus() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "p.orderStatus=" + param.getOrderStatus() + "";
				isFirst = false;
			}
			if (param.getApplicationFault() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "p.applicationFault=" + param.getApplicationFault() + "";
				isFirst = false;
			}
			if (param.getId() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "p.id='" + param.getId() + "'";
				isFirst = false;
			}
			if (additionalCondition != null) {
				if (isFirst) {
					sql += "where ";
				} else {
					sql += "\n and ";
				}
				sql += additionalCondition;
				isFirst = false;
			}
		}
		log.debug("sql[" + sql + "]");
		Query query = getEntityManager().createQuery(sql);
		return query.getResultList();
	}

	@Override
	public int deletePowerOnOffOrder(PowerOnOffOrder req) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("update PowerOnOffOrder ");
		hqlBuf.append("set orderStatus=103 where \n");
		hqlBuf.append(" orderStatus in (101,102,201,202) ");
		hqlBuf.append(" and referenceId=? ");

		int count = 0;
		try {
		    Query query = getEntityManager().createQuery(hqlBuf.toString());
		    count = query.executeUpdate();
		} catch (Exception e) {
		}

		if (count == 0) {
			PowerOnOffOrder searchParamClone = null;
			try {
				searchParamClone = (PowerOnOffOrder) req.clone();
				searchParamClone.setOrderStatus(null);
			} catch (CloneNotSupportedException e) {
			}

			List<PowerOnOffOrder> list = searchPowerOnOffOrder(
					searchParamClone, "orderStatus in (202, 203, 298, 299)");
			if (list != null & list.size() > 0) {
				return 3;
			}
		}
		if (count > 0) {
			return 1;
		} else {
			return 2;
		}
	}

	@Override
	public int updatePowerOnOffOrder(PowerOnOffOrder data, String[] fieldNames,
			Object[] fieldValues) {

		if (fieldNames == null || fieldNames.length == 0
				|| fieldNames.length != fieldValues.length) {
			return 0;
		}

		StringBuffer sql = new StringBuffer();
		sql.append("update PowerOnOffOrder set ");
		for (int i = 0; i < fieldNames.length; i++) {
			if (i != 0) {
				sql.append(", ");
			}
			if (fieldValues[i] instanceof String) {
				sql.append(fieldNames[i]).append("='").append(fieldValues[i])
						.append("'");
			} else {
				sql.append(fieldNames[i]).append("=").append(fieldValues[i]);				
			}
		}
		if (data.getId() != null) {
			sql.append(" where id=?");
		} else {
			sql.append(" where referenceId=? and meterSerialNumber=? ");
		}

		if (data.getId() != null) {
		    Query query = getEntityManager().createQuery(sql.toString());
		    query.setParameter(1,  data.getId());
		    return query.executeUpdate();
		} else {
			// for callback test
		    Query query = getEntityManager().createQuery(sql.toString());
		    query.setParameter(1,  data.getReferenceId());
		    query.setParameter(2,  data.getMeterSerialNumber());
		    return query.executeUpdate();
		}
	}

	@Override
	public List<PowerOnOffOrder> listPowerOnOffOrder(String condition) {
		if(condition!=null && condition.length()>0) {
		    Query query = getEntityManager().createQuery("select p from PowerOnOffOrder p where " + condition);
		    return query.getResultList();
		} else {
		    Query query = getEntityManager().createQuery("select p from PowerOnOffOrder");
            return query.getResultList();
			
		}
	}

    @Override
    public Class<PowerOnOffOrder> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

}
