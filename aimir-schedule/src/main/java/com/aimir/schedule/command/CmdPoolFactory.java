package com.aimir.schedule.command;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.aimir.constants.CommonConstants.Protocol;

public class CmdPoolFactory {
    
    static Map<Protocol, CmdPool> cmdPoolMap = new HashMap<Protocol, CmdPool>();
    
    public static synchronized CmdPool getInstance(Protocol protocol) throws IOException {
        CmdPool cmdPool = cmdPoolMap.get(protocol);
        if (cmdPool == null) {
            cmdPool = new CmdPool(1, 10, 30, true, protocol);
            
            cmdPoolMap.put(protocol, cmdPool);
        }
        
        return cmdPool;
    }
}
