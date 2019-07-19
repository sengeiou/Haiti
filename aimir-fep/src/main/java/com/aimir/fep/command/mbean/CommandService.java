package com.aimir.fep.command.mbean;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.aimir.fep.util.DataUtil;

@Path("/command")
@Produces("text/xml")
public class CommandService {
	@GET
	@Path("/auth/{serialNo}/{status}")
	public void cmdAuthSPModem(@PathParam("serialNo") String serialNo, @PathParam("status") int status) {
		CommandGW gw = DataUtil.getBean(CommandGW.class);
		try {
			gw.cmdAuthSPModem(serialNo, status);
		} catch (Exception e) {

		}
	}
}