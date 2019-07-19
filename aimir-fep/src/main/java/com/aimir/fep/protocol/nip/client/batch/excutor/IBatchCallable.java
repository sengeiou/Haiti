/**
 * 
 */
package com.aimir.fep.protocol.nip.client.batch.excutor;

import java.util.Map;
import java.util.concurrent.Callable;

import com.aimir.fep.protocol.nip.client.batch.excutor.CallableBatchExcutor.CBE_RESULT_CONSTANTS;

/**
 * @author simhanger
 *
 */
@Deprecated
public interface IBatchCallable extends Callable<Map<CBE_RESULT_CONSTANTS, Object>> {
}
