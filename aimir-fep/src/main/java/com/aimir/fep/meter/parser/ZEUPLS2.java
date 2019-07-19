package com.aimir.fep.meter.parser;

import org.springframework.stereotype.Service;

/**
 * parsing ZEUPLS@ meter data
 *
 * ZEUPLS 파서를 사용하지 않는 관계로 통합시키고 UMC2000 과 구분하기 위해서 상속받아서 처리한다.
 * @author J.S Park (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Service
public class ZEUPLS2 extends ZEUPLS
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
}
