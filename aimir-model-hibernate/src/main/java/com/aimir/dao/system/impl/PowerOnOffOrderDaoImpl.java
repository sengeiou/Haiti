package com.aimir.dao.system.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.PowerOnOffOrderDao;
import com.aimir.model.system.PowerOnOffOrder;

@Repository(value = "powerOnOffOrderDao")
public class PowerOnOffOrderDaoImpl extends
		AbstractHibernateGenericDao<PowerOnOffOrder, Integer> implements
		PowerOnOffOrderDao {
	private static Log log = LogFactory.getLog(PowerOnOffOrderDaoImpl.class);

	@Autowired
	protected PowerOnOffOrderDaoImpl(SessionFactory sessionFactory) {
		super(PowerOnOffOrder.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<PowerOnOffOrder> getPowerOnOffOrder(Long referenceId) {
		Query query = getSession().createQuery("from PowerOnOffOrder where referenceId =" + referenceId);
		return query.list();
		
		/*return (List<PowerOnOffOrder>)getHibernateTemplate().find(
				"from PowerOnOffOrder where referenceId = ?", referenceId);*/
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PowerOnOffOrder> searchPowerOnOffOrder(PowerOnOffOrder param,
			String additionalCondition) {

		String sql = "from PowerOnOffOrder \n";

		boolean isFirst = true;
		if (param != null) {
			if (param.getUserName() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "userName='" + param.getUserName() + "'";
				isFirst = false;
			}
			if (param.getReferenceId() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "referenceId=" + param.getReferenceId();
				isFirst = false;
			}
			if (param.getMeterSerialNumber() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "meterSerialNumber='" + param.getMeterSerialNumber()
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
				sql += "powerOperation='" + param.getPowerOperation() + "'";
				isFirst = false;
			}
			if (param.getPowerOperationDate() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "powerOperationDate='" + param.getPowerOperationDate()
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
				sql += "powerOperationDate>='"
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
				sql += "powerOperationDate<='"
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
				sql += "userReference='" + param.getUserReference() + "'";
				isFirst = false;
			}
			if (param.getUserCreateDate() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "userCreateDate='" + param.getUserCreateDate() + "'";
				isFirst = false;
			}
			if (param.getUserCreateDateFrom() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "userCreateDate>='" + param.getUserCreateDateFrom()
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
				sql += "userCreateDate<='" + param.getUserCreateDateTo() + "'";
				isFirst = false;
			}
			if (param.getOrderStatus() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "orderStatus=" + param.getOrderStatus() + "";
				isFirst = false;
			}
			if (param.getApplicationFault() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "applicationFault=" + param.getApplicationFault() + "";
				isFirst = false;
			}
			if (param.getId() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n  and ";
				}
				sql += "id='" + param.getId() + "'";
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
		
		Query query = getSession().createQuery("sql");
		return query.list();
		
		// return (List<PowerOnOffOrder>)getHibernateTemplate().find(sql);
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
			/*count = this.getHibernateTemplate().bulkUpdate(hqlBuf.toString(),
					new Object[] { req.getReferenceId() });*/
		    Query query = getSession().createQuery(hqlBuf.toString());
            query.setParameter(1, req.getReferenceId());
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

		Query query = getSession().createQuery(sql.toString());
        if (data.getId() != null) {
            query.setParameter(1, data.getId());
            /*
            return this.getHibernateTemplate().bulkUpdate(sql.toString(),
                    new Object[] { data.getId() });
                    */
        } else {
            // for callback test
            /*
            return this.getHibernateTemplate().bulkUpdate(
                    sql.toString(),
                    new Object[] { data.getReferenceId(),
                            data.getMeterSerialNumber() });
                            */
            query.setParameter(1, data.getReferenceId());
            query.setParameter(2, data.getMeterSerialNumber());
        }
        
        return query.executeUpdate();
	}

	@Override
	public List<PowerOnOffOrder> listPowerOnOffOrder(String condition) {
		if(condition!=null && condition.length()>0) {
			Query query = getSession().createQuery("from PowerOnOffOrder where" + condition);
			return query.list();
			
			/*return (List<PowerOnOffOrder>)getHibernateTemplate().find(
					"from PowerOnOffOrder where " + condition);*/
		} else {
			Query query = getSession().createQuery("from PowerOnOffOrder");
			return query.list();
			
			/*return (List<PowerOnOffOrder>)getHibernateTemplate().find(
					"from PowerOnOffOrder");*/
		}
	}

}
