package com.aimir.schedule.tool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.spi.SchemaFilter;
import org.hibernate.tool.schema.spi.SchemaFilterProvider;

public class HiberateSchemaFilterProvider implements SchemaFilterProvider {
	
	protected static Log log = LogFactory.getLog(HiberateSchemaFilterProvider.class);
	
	@Override
	public SchemaFilter getCreateFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SchemaFilter getDropFilter() {
		return InnerSchemaFilterProvider.Instance;
	}

	@Override
	public SchemaFilter getMigrateFilter() {
		return InnerSchemaFilterProvider.Instance;
	}

	@Override
	public SchemaFilter getValidateFilter() {
		return InnerSchemaFilterProvider.Instance;
	}

	
	public static class InnerSchemaFilterProvider implements SchemaFilter {
		
		public static final InnerSchemaFilterProvider Instance = new InnerSchemaFilterProvider();
		
		@Override
		public boolean includeNamespace(Namespace namespace) {
			return true;
		}

		@Override
		public boolean includeTable(Table table) {
			if(table.getName().toLowerCase().equals("day_em_view") || table.getName().toLowerCase().equals("month_em_view") || 
				table.getName().toLowerCase().equals("day_gm_view") || table.getName().toLowerCase().equals("month_gm_view") ||
				table.getName().toLowerCase().equals("day_wm_view") || table.getName().toLowerCase().equals("month_wm_view") ||
				table.getName().toLowerCase().equals("day_tm_view") || table.getName().toLowerCase().equals("month_tm_view") ||
				table.getName().toLowerCase().equals("day_hm_view") || table.getName().toLowerCase().equals("month_hm_view")) 
			{
				log.debug("### table : "+table.getName()+" return false");
				return false;
			}
			
			log.debug("### table : "+table.getName()+" return true");
			return true;
		}

		@Override
		public boolean includeSequence(Sequence sequence) {
			return true;
		}
	}
	
}
