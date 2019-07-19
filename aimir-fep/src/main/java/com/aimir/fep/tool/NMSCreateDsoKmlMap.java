package com.aimir.fep.tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.CursoredStream;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.dao.device.MCUDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.util.DateTimeUtil;
/**
 *  SP-1050
 *  Create MSA Kml Map file for NMS Gadget
 *
 */
@Service
public class NMSCreateDsoKmlMap {
	private static Logger logger = LoggerFactory.getLogger(NMSCreateDsoKmlMap.class);
	
	@Resource(name="transactionManager")
	JpaTransactionManager txmanager;			
	
	@Autowired
	MCUDao mcuDao;
	
	@Autowired
	CodeDao codeDao;
	
	@Autowired
	LocationDao locationDao;
	
	@PersistenceContext
	protected EntityManager em;
	

	String[] dsoNames = null;
	String[] exDsoNames = null;
	int	maxResult=10;
	int queryTimeout=60;
	boolean useLpEm = false;
	
	private static DefaultTransactionDefinition transDef = null;
	static {
		transDef =  new DefaultTransactionDefinition();
		transDef.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		transDef.setReadOnly(true);
	}
	int map_table = 0;
	int beforeDay = 7;
	String outputDir = "/home/aimir/aimir4/aimiramm/aimir-web/target/aimir-web-3.3/kml/data";
	
	static  final String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" + 
			"  <Document>\n" + 
			"    <name>RF Mesh - Topology</name>\n";
	static  final String tail = "    <Style id=\"dcuIconBlue\">\n" + 
			"      <IconStyle>\n" + 
			"        <Icon>\n" + 
			"          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAB9ElEQVR42q1WSy8DURS+f8KGeMTG/7CVWIlHE7QzDTozpMW9Q1jYiBASUgsiKPGKIjQVbSOIingGqUdKqJBYEVtWR2+Tlo72zi3zJWdzc+b77r3nnO8OQgyYbPZCUSEdokw2i0tn37OK3oBGSfnoq1VRA9H1dnODvQBlCkosKGQpTqgXVgUvmMSWfC5yQcI1vMTaEG2tJja5TNS/kidOI+EWw3euDYuEq37duVHk8aiua8pLCFgl4mYl9w2OZH4KWZ3n2v3kjBsorm5uk9YHhl0wveBlisRamPY5K+nj4xPi+Cmy4t2BEZdbr+BtiA5RuoSpuWXQInz3AFt753xtK2E/+jmh2tgIbEMq3EeeuARM5u4XxEpY9vggHS6vw1wiTIGuHiewwCOCqHGxEkJXYabI7OJq2m9r6zufEXVFlkC52QORx6eU5CdnIR0TVH2ItpLeMefdHnCOuiC4fwQXoRvYCR7AgHNM93oEGWNEbZaVtOYLgn/7GIKHIdjcPQVvYB88/j2uAldamnNj00zHOl1S79A49DsnkqIvuqY7AzKZTngRNSajza6mwZGT5KjUYo2za1KW8k2Ijrbjv+TRwjYyXzWLDVcYvnMtaPVFicxwv8UycVUKSnbGfxcxIUVttcp4XZDUyPcAkQdRVr30Sn8VU4MvhOBkkp+tIFMAAAAASUVORK5CYII=</href>\n" + 
			"        </Icon>\n" + 
			"      </IconStyle>\n" + 
			"    </Style>\n" + 
			"    <Style id=\"dcuIconPurple\">\n" + 
			"      <!--UnKnown-->\n" + 
			"      <IconStyle>\n" + 
			"        <Icon>\n" + 
			"          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAABuElEQVR42mNgwAOCEjOVErNKqpOySvc4+i55385Q/B+EvUJnvEnKLtuVmFlSmZBRoMBAKgAZnJBdugZmICGcnFW8IigqW54ow+PTi+KINRgdJ6QVRuE1PCGzpJxcw2E4MaO4mOouR8dx6cWRGGFOLcNhODQ2TQ5uQVJGyWp8irsmzCA9PjJLlxPl+uU9k/6DwPUly1DEe6cs+L8nIR2/JaAkDErn+BT9+PHzPwygWzJjwWq8FiRlFFcwgDIRLgVb84r/o4Obt+8RHUxAC3YyIOdQbBgbuHv/EVEWRMS3PGMgpAgXuN43gShL8FrQ0D75Pz5AjCUMoIILn4JrQEPwAbwZLq3qMQOoVMSnKCxh0//7Dx6RbDg4krPKdjCAkhIxYTl55gIwfSMlDZIPJs8mnNkyiksZQMUstYsJGPaPyJKFlqKly3Ep6pw493/P5HkouAsoRrBUzSxZBC+LQAUT1Qu7uAxplBIVVMRSsbgOwVonACuLQkoNT8gsysVbq8WlF4VT3eXoABT7SRmlS4iuJjNLFvhFJEmR3LoAWQTUXJKUWbw1MbP0PrwVkV16D1gKbwEFKUZkogEAE7gLwt7TTJMAAAAASUVORK5CYII=</href>\n" + 
			"        </Icon>\n" + 
			"      </IconStyle>\n" + 
			"    </Style>\n" + 
			"    <Style id=\"dcuIconGray\">\n" + 
			"      <!--Power Down -->\n" + 
			"      <IconStyle>\n" + 
			"        <Icon>\n" + 
			"          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAB9klEQVR42q1WW0sCQRSe31F0oX/Sa1BBdLEg291Ad1aCcne7vvQQRFFQ2IMRlYVlZEWJkUpWZERXKuyCRRkFPQW91tNpB1J003Gs/eC8DGe/b3bOnO8MQhTU8LiMl5Q+QVK3yyvdH+FwGEhU1DnfBVtniMdKDye2l6J8QYg5m7qSIMwVrZK8VNNkK2Eib7HazazE+uAsHU1Ucg4rXX8lTwQvyrLhO9eH2So3/jpzo8gTUddsKU4KCKLipSUPjznzrwdWPUy7n3V7geDm7j5tfXTCBfNLfroIucLkntOSPj+/IIFUkTX/HjhdXqqAIMrdiDRRtoS5xVXQI/bwBDsHl0zHpAkEUWqH6mMrtAuZ8Bh/YRIwtQy8IVrCqi8A2XB9G2MSoQr0DzqABhYRRIyLlhC9iVFFFpbXszecpfcVEVekCdRzPog/v2QkP7uI0ossdQYQuUq5ftPj9YFj0gWRwxO4it7BXuQIRh1TuZtNlFVEbJaWtBGIQHD3FCLHUdjePwd/6BB8wQOmAlebpKIfF1U92ZKGxqdhxDGTFsPaWk5Xxcp80ouIMRludmaxMM1RicUaaNe1GWeCNiw6/kvOYXsbdaqZrfYGw3euB6m+IKpu5jGJFVeVSSjI+3VBhLSPFQHLmzxW48lXhE190lzYT470VzF1+AatC88x/x7eeAAAAABJRU5ErkJggg==</href>\n" + 
			"        </Icon>\n" + 
			"      </IconStyle>\n" + 
			"    </Style>\n" + 
			"    <Style id=\"dcuIconOrange\">\n" + 
			"      <!-- Comm.Error -->\n" + 
			"      <IconStyle>\n" + 
			"        <Icon>\n" + 
			"          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAABuElEQVR42mNgwAOCEjOVErNKqpOySvc4+i55/zia4T8Ie4XOeJOUXbYrMbOkMiGjQIGBVAAyOCG7dA3MQEI4Oat4RVBUtjxRhsenF8URazA6TkgrjMJreEJmSTm5hsNwYkZxMdVdjo7j0osjMcKcWobDcGhsmhzcgqSMktX4FHdNmEF6fGSWLifK9ct7Jv0HgetLlqGI905Z8H9PQjp+S0BJGJTO8Sn68ePnfxhAt2TGgtV4LUjKKK5gAGUiXAq25hX/Rwc3b98jOpiAFuxkQM6h2DA2cPf+I6IsiIhvecZASBEucL1vAlGW4LWgoX3yf3yAGEsYQAUXPgXXgIbgA3gzXFrVYwZQqYhPUVjCpv/3Hzwi2XBwJGeV7WAAJSViwnLyzAVg+kZKGiQfTJ5NOLNlFJcygIpZahcTMOwfkSULLUVLl+NS1Dlx7v+eyfNQcBdQjGCpmlmyCF4WgQomqhd2cRnSKCUqqIilYnEdgrVOAFYWhZQanpBZlIu3VotLLwqnusvRASj2kzJKlxBdTWaWLPCLSJIiuXUBsgiouSQps3hrYmbpfXgrIrv0HrAU3gIKUozIRAMAyKZrprXoaLMAAAAASUVORK5CYII=</href>\n" + 
			"        </Icon>\n" + 
			"      </IconStyle>\n" + 
			"    </Style>\n" + 
			"    <Style id=\"dcuIconRed\">\n" + 
			"      <!-- Security.Error -->\n" + 
			"      <IconStyle>\n" + 
			"        <Icon>\n" + 
			"          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAABuElEQVR42mNgwAOCEjOVErNKqpOySvc4+i55f4+B4T8Ie4XOeJOUXbYrMbOkMiGjQIGBVAAyOCG7dA3MQEI4Oat4RVBUtjxRhsenF8URazA6TkgrjMJreEJmSTm5hsNwYkZxMdVdjo7j0osjMcKcWobDcGhsmhzcgqSMktX4FHdNmEF6fGSWLifK9ct7Jv0HgetLlqGI905Z8H9PQjp+S0BJGJTO8Sn68ePnfxhAt2TGgtV4LUjKKK5gAGUiXAq25hX/Rwc3b98jOpiAFuxkQM6h2DA2cPf+I6IsiIhvecZASBEucL1vAlGW4LWgoX3yf3yAGEsYQAUXPgXXgIbgA3gzXFrVYwZQqYhPUVjCpv/3Hzwi2XBwJGeV7WAAJSViwnLyzAVg+kZKGiQfTJ5NOLNlFJcygIpZahcTMOwfkSULLUVLl+NS1Dlx7v+eyfNQcBdQjGCpmlmyCF4WgQomqhd2cRnSKCUqqIilYnEdgrVOAFYWhZQanpBZlIu3VotLLwqnusvRASj2kzJKlxBdTWaWLPCLSJIiuXUBsgiouSQps3hrYmbpfXgrIrv0HrAU3gIKUozIRAMAbWHkNyqx+voAAAAASUVORK5CYII=</href>\n" + 
			"        </Icon>\n" + 
			"      </IconStyle>\n" + 
			"    </Style>\n" + 
			"    <Style id=\"meterIconBlue\">\n" + 
			"      <IconStyle>\n" + 
			"        <Icon>\n" + 
			"          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAkAAAAJCAYAAADgkQYQAAAAiklEQVR42mNgQIAoIF4NxGegdCCSHAMzEC81M4v6n56++n9V1RkwbWgY+B8oPhOmKM3WNu3/zJn/MbCFRSxIYSxI0YHi4gNYFRUW7gUp2gtS9LC9/SFWRc3Nt0GKbhNtUizIbmyKjIxCQIpCYI6fD/JdVtZGsO8yMtbBfDeNAQ2AwmkjNJzWIYcTAMk+i9OhipcQAAAAAElFTkSuQmCC</href>\n" + 
			"        </Icon>\n" + 
			"      </IconStyle>\n" + 
			"    </Style>\n" + 
			"    <Style id=\"meterIconGreen\">\n" + 
			"      <IconStyle>\n" + 
			"        <Icon>\n" + 
			"          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAkAAAAJCAYAAADgkQYQAAAAiElEQVR42mNgQIAoIF4NxGegdCCSHAMzEC81izL7n746/X/VmSowbRho+B8oPhOmKM02zfb/TCzQItYCpDAWpOhA8YFirIoK9xaCFO0FKXrY/rAdq6Lm280gRbeJNikWZDc2RUYhRiBFITDHzwf5LmtjFth3GesyYL6bxoAGQOG0ERpO65DDCQDX7ovT++K9KQAAAABJRU5ErkJggg==</href>\n" + 
			"        </Icon>\n" + 
			"      </IconStyle>\n" + 
			"    </Style>\n" + 
			"    <Style id=\"meterIconPurple\">\n" + 
			"      <IconStyle>\n" + 
			"        <Icon>\n" + 
			"          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAkAAAAJCAYAAADgkQYQAAAAi0lEQVR42mNgQIAoIF4NxGegdCCSHAMzEC+NMov6vzp99f8zVWfAdKBh4H+g+EyYorQ027T//2f+x8CxFrEghbEgRQcOFB/Aqmhv4V6Qor0gRQ8ftj/Equh2822QottEmxQLshubohCjEJCiEJjj54N8tzFrI9h36zLWwXw3jQENgMJpIzSc1iGHEwBt95qDejjnKAAAAABJRU5ErkJggg==</href>\n" + 
			"        </Icon>\n" + 
			"      </IconStyle>\n" + 
			"    </Style>\n" + 
			"    <Style id=\"meterIconYellow\">\n" + 
			"      <IconStyle>\n" + 
			"        <Icon>\n" + 
			"          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAkAAAAJCAYAAADgkQYQAAAAi0lEQVR42mNgQIAoIF4NxGegdCCSHAMzEC+NijL7v3p1+v8zZ6rAdGCg4X+g+EyYorS0NNv////PxMCxsRYghbEgRQcOHCjGqmjv3kKQor0gRQ8fPmzHquj27WaQottEmxQLshubopAQI5CiEJjj54N8t3FjFth369ZlwHw3jQENgMJpIzSc1iGHEwB8p5qDBbsHtAAAAABJRU5ErkJggg==</href>\n" + 
			"        </Icon>\n" + 
			"      </IconStyle>\n" + 
			"    </Style>\n" + 
			"    <Style id=\"meterIconRed\">\n" + 
			"      <IconStyle>\n" + 
			"        <Icon>\n" + 
			"          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAkAAAAJCAYAAADgkQYQAAAAiklEQVR42mNgQIAoIF4NxGegdCCSHAMzEC+NUlH5v9rF5f+ZoCAwHaig8B8oPhOmKC1NU/P//7Q0DByrqgpSGAtSdOCAry9WRXt9fECK9oIUPXwYFYVV0e2ICJCi20SbFAuyG5uiECUlkKIQmOPng3y30d0d7Lt1bm4w301jQAOgcNoIDad1yOEEAFm9fSv/VqtJAAAAAElFTkSuQmCC</href>\n" + 
			"        </Icon>\n" + 
			"      </IconStyle>\n" + 
			"    </Style>\n" + 
			"    <Style id=\"line-0288D1-1-nodesc-normal\">\n" + 
			"      <LineStyle>\n" + 
			"        <color>ffD18802</color>\n" + 
			"        <width>1</width>\n" + 
			"      </LineStyle>\n" + 
			"      <BalloonStyle>\n" + 
			"        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
			"      </BalloonStyle>\n" + 
			"    </Style>\n" + 
			"    <Style id=\"line-0288D1-1-nodesc-highlight\">\n" + 
			"      <LineStyle>\n" + 
			"        <color>ffD18802</color>\n" + 
			"        <width>2.0</width>\n" + 
			"      </LineStyle>\n" + 
			"      <BalloonStyle>\n" + 
			"        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
			"      </BalloonStyle>\n" + 
			"    </Style>\n" + 
			"    <StyleMap id=\"line-0288D1-1-nodesc\">\n" + 
			"      <Pair>\n" + 
			"        <key>normal</key>\n" + 
			"        <styleUrl>#line-0288D1-1-nodesc-normal</styleUrl>\n" + 
			"      </Pair>\n" + 
			"      <Pair>\n" + 
			"        <key>highlight</key>\n" + 
			"        <styleUrl>#line-0288D1-1-nodesc-highlight</styleUrl>\n" + 
			"      </Pair>\n" + 
			"    </StyleMap>\n" + 
			"    <Style id=\"line-3949AB-1-nodesc-normal\">\n" + 
			"      <LineStyle>\n" + 
			"        <color>ffAB4939</color>\n" + 
			"        <width>2</width>\n" + 
			"      </LineStyle>\n" + 
			"      <BalloonStyle>\n" + 
			"        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
			"      </BalloonStyle>\n" + 
			"    </Style>\n" + 
			"    <Style id=\"line-3949AB-1-nodesc-highlight\">\n" + 
			"      <LineStyle>\n" + 
			"        <color>ffAB4939</color>\n" + 
			"        <width>3.0</width>\n" + 
			"      </LineStyle>\n" + 
			"      <BalloonStyle>\n" + 
			"        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
			"      </BalloonStyle>\n" + 
			"    </Style>\n" + 
			"    <StyleMap id=\"line-3949AB-1-nodesc\">\n" + 
			"      <Pair>\n" + 
			"        <key>normal</key>\n" + 
			"        <styleUrl>#line-3949AB-1-nodesc-normal</styleUrl>\n" + 
			"      </Pair>\n" + 
			"      <Pair>\n" + 
			"        <key>highlight</key>\n" + 
			"        <styleUrl>#line-3949AB-1-nodesc-highlight</styleUrl>\n" + 
			"      </Pair>\n" + 
			"    </StyleMap>\n" + 
			"    <Style id=\"line-880E4F-3-nodesc-normal\">\n" + 
			"      <LineStyle>\n" + 
			"        <color>ff4F0E88</color>\n" + 
			"        <width>3</width>\n" + 
			"      </LineStyle>\n" + 
			"      <BalloonStyle>\n" + 
			"        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
			"      </BalloonStyle>\n" + 
			"    </Style>\n" + 
			"    <Style id=\"line-880E4F-3-nodesc-highlight\">\n" + 
			"      <LineStyle>\n" + 
			"        <color>ff4F0E88</color>\n" + 
			"        <width>4.0</width>\n" + 
			"      </LineStyle>\n" + 
			"      <BalloonStyle>\n" + 
			"        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
			"      </BalloonStyle>\n" + 
			"    </Style>\n" + 
			"    <StyleMap id=\"line-880E4F-3-nodesc\">\n" + 
			"      <Pair>\n" + 
			"        <key>normal</key>\n" + 
			"        <styleUrl>#line-880E4F-3-nodesc-normal</styleUrl>\n" + 
			"      </Pair>\n" + 
			"      <Pair>\n" + 
			"        <key>highlight</key>\n" + 
			"        <styleUrl>#line-880E4F-3-nodesc-highlight</styleUrl>\n" + 
			"      </Pair>\n" + 
			"    </StyleMap>\n" + 
			"    <Style id=\"line-9C27B0-2-nodesc-normal\">\n" + 
			"      <LineStyle>\n" + 
			"        <color>ffB0279C</color>\n" + 
			"        <width>4</width>\n" + 
			"      </LineStyle>\n" + 
			"      <BalloonStyle>\n" + 
			"        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
			"      </BalloonStyle>\n" + 
			"    </Style>\n" + 
			"    <Style id=\"line-9C27B0-2-nodesc-highlight\">\n" + 
			"      <LineStyle>\n" + 
			"        <color>ffB0279C</color>\n" + 
			"        <width>4.0</width>\n" + 
			"      </LineStyle>\n" + 
			"      <BalloonStyle>\n" + 
			"        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
			"      </BalloonStyle>\n" + 
			"    </Style>\n" + 
			"    <StyleMap id=\"line-9C27B0-2-nodesc\">\n" + 
			"      <Pair>\n" + 
			"        <key>normal</key>\n" + 
			"        <styleUrl>#line-9C27B0-2-nodesc-normal</styleUrl>\n" + 
			"      </Pair>\n" + 
			"      <Pair>\n" + 
			"        <key>highlight</key>\n" + 
			"        <styleUrl>#line-9C27B0-2-nodesc-highlight</styleUrl>\n" + 
			"      </Pair>\n" + 
			"    </StyleMap>\n" + 
			"    <Style id=\"line-A52714-3-nodesc-normal\">\n" + 
			"      <LineStyle>\n" + 
			"        <color>ff1427A5</color>\n" + 
			"        <width>4</width>\n" + 
			"      </LineStyle>\n" + 
			"      <BalloonStyle>\n" + 
			"        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
			"      </BalloonStyle>\n" + 
			"    </Style>\n" + 
			"    <Style id=\"line-A52714-3-nodesc-highlight\">\n" + 
			"      <LineStyle>\n" + 
			"        <color>ff1427A5</color>\n" + 
			"        <width>4.0</width>\n" + 
			"      </LineStyle>\n" + 
			"      <BalloonStyle>\n" + 
			"        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
			"      </BalloonStyle>\n" + 
			"    </Style>\n" + 
			"    <StyleMap id=\"line-A52714-3-nodesc\">\n" + 
			"      <Pair>\n" + 
			"        <key>normal</key>\n" + 
			"        <styleUrl>#line-A52714-3-nodesc-normal</styleUrl>\n" + 
			"      </Pair>\n" + 
			"      <Pair>\n" + 
			"        <key>highlight</key>\n" + 
			"        <styleUrl>#line-A52714-3-nodesc-highlight</styleUrl>\n" + 
			"      </Pair>\n" + 
			"    </StyleMap>\n" + 
			"    <Style id=\"line-FF5252-4-nodesc-normal\">\n" + 
			"      <LineStyle>\n" + 
			"        <color>ff5252FF</color>\n" + 
			"        <width>4</width>\n" + 
			"      </LineStyle>\n" + 
			"      <BalloonStyle>\n" + 
			"        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
			"      </BalloonStyle>\n" + 
			"    </Style>\n" + 
			"    <Style id=\"line-FF5252-4-nodesc-highlight\">\n" + 
			"      <LineStyle>\n" + 
			"        <color>ff5252FF</color>\n" + 
			"        <width>4.0</width>\n" + 
			"      </LineStyle>\n" + 
			"      <BalloonStyle>\n" + 
			"        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
			"      </BalloonStyle>\n" + 
			"    </Style>\n" + 
			"    <StyleMap id=\"line-FF5252-4-nodesc\">\n" + 
			"      <Pair>\n" + 
			"        <key>normal</key>\n" + 
			"        <styleUrl>#line-FF5252-4-nodesc-normal</styleUrl>\n" + 
			"      </Pair>\n" + 
			"      <Pair>\n" + 
			"        <key>highlight</key>\n" + 
			"        <styleUrl>#line-FF5252-4-nodesc-highlight</styleUrl>\n" + 
			"      </Pair>\n" + 
			"    </StyleMap>\n" + 
			"  </Document>\n" + 
			"</kml>\n"; 
	
	public void setDsoNames(String[] dsoNames)
	{
		this.dsoNames = dsoNames;
	}
	
	public void setExDsoNames(String[] dsoNames)
	{
		this.exDsoNames = dsoNames;
	}
	
	public void NMSDspMapCreateKml( ){
	}
	
	@Transactional(readOnly=true)
	public void writeDcus(PrintWriter pw, String dsoName)  throws Exception {
		try {
			String mcuQueryStr = "select '<Placemark><name>DCU: '||sys_id||'</name><description><![CDATA[Device ID: '||sys_id||'<br/>IP Address: '||nvl(ip_addr,'')||'</br>IPV6 Address: '||nvl(ipv6_addr,'')||" + 
					"            '</br>Status: '||nvl((select name from code where id = mcu_status),'UnKnwon')||" + 
					"            '</br>GpioX: '||gpiox||'</br>GpioY: '||gpioy||'</br>GpioZ: '||nvl(gpioz,0.0)||']]></description>'||" + 
					"             '<Point><coordinates>'|| gpiox || ',' || gpioy || ',0.0</coordinates></Point><styleUrl>'||" + 
					"             (case  when code.code='1.1.4.1' then '#dcuIconBlue' when code.code='1.1.4.3' then '#dcuIconGray' when code.code='1.1.4.4' then '#dcuIconRed' when code.code='1.1.4.5' then '#dcuIconOrange' when code.code is null then '#dcuIconPurple' end ) ||" + 
					"             '</styleUrl>'||" + 
					"             '<ExtendedData><Data name=\"mcu\"><value>{\"id\":'||mcu.id||',\"sysId\":\"'||sys_id||'\"}</value></Data></ExtendedData>'||                  '</Placemark>'" + 
					"      from mcu left outer join code code on mcu.mcu_status = code.id \n" +
					"      where code.code <> '1.1.4.2' and  mcu.gpiox is not null and  mcu.location_id=(select id from location where name= ?) ";
			Query query = em.createNativeQuery(mcuQueryStr.toString());
			query.setParameter(1, dsoName);
			int k = 0;
			List<Object>  resultList  = query.getResultList();
			for( Object element : resultList ) {
				if ( element instanceof String ) {
					String placemarkStr = (String)element;
					logger.debug(k + ":" + placemarkStr);
					pw.println(element);
				}
				else if ( element instanceof Object[] ){
					Object[] objArray = (Object[])element;
					String placemarkStr = (String)objArray[0];
					logger.debug(k + ":" + placemarkStr);
					pw.println(placemarkStr);
				}
				k++;
			}
		}catch (Exception e) {
			
		}
	}

	@Transactional(readOnly=true)
	public void writeMsaMetersWithLpEm(PrintWriter pw, HashMap<String,String> dsoMsa  )  throws Exception {
		logger.info("start Meters Placemark :" + dsoMsa.get("dso") + ":" + dsoMsa.get("msa") );
		
		String dsoName = dsoMsa.get("dso");
		String msa = dsoMsa.get("msa");
		String str1 = "";
		String str2 = "";
		String str3 = "";
		if ( map_table > 0 ){
			str1 = "             '<br/>GPS X: '||c.cust_gpiox||'<br/>GPS Y: '||c.cust_gpioy||'<br/>RSSI: '||a.rssi||\n";
			str2 = "             '<Point><coordinates>'||c.cust_gpiox || ',' ||c.cust_gpioy || ',0.0</coordinates></Point><styleUrl>'||\n";
			str3 = "      join meter_map c on b.mds_id=c.mds_id\n";
		}
		else {
			str1 = "             '<br/>GPS X: '||b.gpiox||'<br/>GPS Y: '||b.gpioy||'<br/>RSSI: '||a.rssi||\n";
			str2 = "             '<Point><coordinates>'||b.gpiox || ',' ||b.gpioy || ',0.0</coordinates></Point><styleUrl>'||\n";
		}

		String meterQeury = "      select '<Placemark><name>Meter: '||b.mds_id||'</name>'||\n" + 
				"             '<description><![CDATA[Device ID: '||b.mds_id||'<br/>Modem: '||a.device_serial||'<br/>GS1: '||b.gs1||\n" + 
				"             '<br/>SW Ver: '||b.sw_version||\n" + 
				"             '<br/>FW Ver: '||a.fw_ver||'('||trim(to_char(a.fw_revision,'XXXX'))||')'||\n" + 
				str1 + 
				"             '<br/>Last LP date(last 3 days) : '|| to_char(to_date(l.maxreaddate,'yyyymmddhh24'),'yyyy/mm/dd hh24')||\n" + 
				"             '<br/>LP Count(last 3 days) : '||nvl(l.cnt,0)||\n" + 
				"             '<br/>Parent : ]]></description>'||\n" + 
				str2 + 
				"             (case when 3*24*60/b.LP_INTERVAL/cnt*100 = 100  then '#meterIconBlue' when cnt is null then '#meterIconRed' else '#meterIconYellow' end)||'</styleUrl>'||\n" + 
				"             '<ExtendedData><Data name=\"meter\"><value>{\"id\":'||b.id||',\"mdsId\":\"'||b.mds_id||'\",\"modemId\":'||a.id||'}</value></Data>'||\n" + 
				"             '<Data name=\"modem\"><value>{\"id\":'||a.id||',\"deviceSerial\":\"'||a.device_serial||'\",\"status\":\"'||(nvl((select name from code where id = a.modem_status), 'UnKown'))||'\",\"protocol\":\"'||a.protocol_type||'\",\"type\":\"'||a.modem_type||\n" + 
				"            '\",\"fwver\":\"'||a.fw_ver||'('||trim(to_char(a.fw_revision,'XXXX'))||')'||\n" + 
				"            '\",\"x\":'||nvl(a.gpiox,0)||',\"y\":'||nvl(a.gpioy,0)||',\"z\":'||nvl(a.gpioz,0)||'}</value></Data>'||\n" + 
				"            '</ExtendedData></Placemark>' as RESULT" + 
				"      from modem a\n" + 
				"      join meter b on a.id = b.modem_id\n" + 
				str3 + 
				"      left outer join (\n" + 
				"          select mdev_id,sum(value_cnt) cnt,max(yyyymmddhh) maxreaddate from lp_em\n" + 
				"          where mdev_id in (select mds_id from meter where location_id=(select id from location where name='"+ dsoName + "') and nvl(msa,';;')=nvl('" + msa + "',';;') and modem_id in (select id from modem where modem='MMIU' and location_id=(select id from location where name='" + dsoName + "')))\n" + 
				"            and channel=1\n" + 
				"            and yyyymmdd between to_char(sysdate-3,'yyyymmdd') and to_char(sysdate,'yyyymmdd')\n" + 
				"            and yyyymmddhh between to_char(sysdate-3,'yyyymmdd') and to_char(sysdate,'yyyymmdd')\n" + 
				"          group by mdev_id\n" + 
				"      ) l on b.mds_id=l.mdev_id\n" + 
				"      where a.modem='MMIU' and b.location_id=(select id from location where name='" + dsoName+"') and nvl(b.msa, ';;')=nvl('" + msa +"',';;') and b.gpiox is not null and (b.meter_status!=(select id from code where code='1.3.3.9') or b.meter_status is null)\n";
		
	    JpaEntityManager eclipseLinkem = em.unwrap(JpaEntityManager.class);
	    DataReadQuery q = new DataReadQuery(meterQeury);
	    CursoredStream eclipseLinkCursor = null;
		try {
			q.setQueryTimeout(queryTimeout);
			q.useCursoredStream(maxResult, maxResult);
			q.doNotCacheQueryResults();
			
			Session eclipseLinkSession = eclipseLinkem.getActiveSession();
			eclipseLinkCursor = (CursoredStream) eclipseLinkSession.executeQuery(q);
			int i=0;
			while (eclipseLinkCursor.hasNext()) {
				DatabaseRecord row  = (DatabaseRecord)eclipseLinkCursor.next();
				String placemark = (String)row.get("RESULT");
				pw.println(placemark);
				logger.debug("-" + i + ":" + placemark);
				i++;
				//eclipseLinkCursor.releasePrevious();
				if (i % 1000 == 0) {
					eclipseLinkCursor.clear();
				}
			}
			eclipseLinkCursor.close();
		}catch (Exception e) {
			logger.error("writeMeters fail :" + dsoName + e, e);
			if ( eclipseLinkCursor != null )
				eclipseLinkCursor.close();
			throw e ;
		}

	}
	
	@Transactional(readOnly=true)
	public void writeMsaMetersWithSlaRawdata(PrintWriter pw, HashMap<String,String> dsoMsa )  throws Exception {
		logger.info("start Meters Placemark :" + dsoMsa.get("dso") + ":" + dsoMsa.get("msa") );

		String dsoName = dsoMsa.get("dso");
		String msa = dsoMsa.get("msa");
		String str1 = "";
		String str2 = "";
		String str3 = "";
		if ( map_table > 0 ){
			str1 = "             '<br/>GPS X: '||c.cust_gpiox||'<br/>GPS Y: '||c.cust_gpioy||'<br/>RSSI: '||a.rssi||\n";
			str2 = "             '<Point><coordinates>'||c.cust_gpiox || ',' ||c.cust_gpioy || ',0.0</coordinates></Point><styleUrl>'||\n";
			str3 = "      join meter_map c on b.mds_id=c.mds_id\n";
		}
		else {
			str1 = "             '<br/>GPS X: '||b.gpiox||'<br/>GPS Y: '||b.gpioy||'<br/>RSSI: '||a.rssi||\n";
			str2 = "             '<Point><coordinates>'||b.gpiox || ',' ||b.gpioy || ',0.0</coordinates></Point><styleUrl>'||\n";
		}

		String meterQeury = "      select '<Placemark><name>Meter: '||b.mds_id||'</name>'||\n" + 
				"             '<description><![CDATA[Device ID: '||b.mds_id||'<br/>Modem: '||a.device_serial||'<br/>GS1: '||b.gs1||\n" + 
				"             '<br/>SW Ver: '||b.sw_version||\n" + 
				"             '<br/>FW Ver: '||a.fw_ver||'('||trim(to_char(a.fw_revision,'XXXX'))||')'||\n" + 
				str1 + 
				"             '<br/>Last LP date(last 3 days) : '|| to_char(to_date(l.maxreaddate,'yyyymmdd'),'yyyy/mm/dd')||\n" + 
				"             '<br/>LP Count(last 3 days) : '||nvl(l.cnt,0)||\n" + 
				"             '<br/>Parent : ]]></description>'||\n" + 
				str2 + 
				"             (case when 3*24*60/b.LP_INTERVAL/cnt*100 = 100  then '#meterIconBlue' when cnt is null then '#meterIconRed' else '#meterIconYellow' end)||'</styleUrl>'||\n" + 
				"             '<ExtendedData><Data name=\"meter\"><value>{\"id\":'||b.id||',\"mdsId\":\"'||b.mds_id||'\",\"modemId\":'||a.id||'}</value></Data>'||\n" + 
				"             '<Data name=\"modem\"><value>{\"id\":'||a.id||',\"deviceSerial\":\"'||a.device_serial||'\",\"status\":\"'||(nvl((select name from code where id = a.modem_status), 'UnKown'))||'\",\"protocol\":\"'||a.protocol_type||'\",\"type\":\"'||a.modem_type||\n" + 
				"            '\",\"fwver\":\"'||a.fw_ver||'('||trim(to_char(a.fw_revision,'XXXX'))||')'||\n" + 
				"            '\",\"x\":'||nvl(a.gpiox,0)||',\"y\":'||nvl(a.gpioy,0)||',\"z\":'||nvl(a.gpioz,0)||'}</value></Data>'||\n" + 
				"            '</ExtendedData></Placemark>' as RESULT" + 
				"      from modem a\n" + 
				"      join meter b on a.id = b.modem_id\n" + 
				str3 + 
				"    left outer join (\n" + 
				"      select meter_id,sum(current_mv_count) cnt,  max(yyyymmdd) maxreaddate from sla_rawdata \n" + 
				"      where meter_id in (select mds_id from meter where location_id=(select id from location where name='" + dsoName + "') and nvl(msa,';;')=nvl('" + msa + "',';;'))\n" + 
				"        and yyyymmdd between to_char(sysdate-3,'yyyymmdd') and to_char(sysdate-1,'yyyymmdd')\r\n" + 
				"      group by meter_id\r\n" + 
				"    ) l on b.mds_id=l.meter_id" +
				"      where a.modem='MMIU' and b.location_id=(select id from location where name='" + dsoName+"') and nvl(b.msa, ';;')=nvl('" + msa +"',';;')\n" +
				"            and b.gpiox is not null and (b.meter_status!=(select id from code where code='1.3.3.9') or b.meter_status is null)\n";


		JpaEntityManager eclipseLinkem = em.unwrap(JpaEntityManager.class);
		DataReadQuery q = new DataReadQuery(meterQeury);
		CursoredStream eclipseLinkCursor = null;
		try {
			q.setQueryTimeout(queryTimeout);
			q.useCursoredStream(maxResult, maxResult);
			q.doNotCacheQueryResults();
			Session eclipseLinkSession = eclipseLinkem.getActiveSession();
			eclipseLinkCursor = (CursoredStream) eclipseLinkSession.executeQuery(q);
			int i=0;
			while (eclipseLinkCursor.hasNext()) {
				DatabaseRecord row  = (DatabaseRecord)eclipseLinkCursor.next();
				String placemark = (String)row.get("RESULT");
				pw.println(placemark);
				logger.debug("-" + i + ":" + placemark);
				i++;
				//eclipseLinkCursor.releasePrevious();
				if (i % 1000 == 0) {
					eclipseLinkCursor.clear();
				}
			}
			eclipseLinkCursor.close();
		}catch (Exception e) {
			logger.error("writeMeters fail :" + dsoName + e, e);
			if ( eclipseLinkCursor != null )
				eclipseLinkCursor.close();
			throw e ;
		}

	}

	public void writeMetersDisconnectModem(PrintWriter pw, HashMap<String,String> dsoMsa )  throws Exception {

		String dsoName = dsoMsa.get("dso");
		String msa = dsoMsa.get("msa");
		logger.info("start wrute Meters(Disconnect Modem) Placemark :" + dsoName + " : " + msa);
		
		String meterQeury = "   select '<Placemark><name>Meter: '||b.mds_id||'</name>'||\n" + 
				"           '<description><![CDATA[Meter: '||b.mds_id||'<br/>Modem: <br/>GS1: '||b.gs1||\n" + 
				"           '<br/>SW Ver: '||b.sw_version||\n" + 
				"           '<br/>FW Ver: '||\n" + 
				"           '<br/>GPS X: '|| b.gpiox||'<br/>GPS Y: '||b.gpioy||'<br/>RSSI: '||\n" + 
				"           '<br/>Last LP date(last 3 days) : '||\n" + 
				"           '<br/>LP Count(last 3 days) : '||\n" + 
				"           '<br/>Parent : <br/>Hop: ]]></description>'||\n" + 
				"           '<Point><coordinates>'|| b.gpiox || ',' || b.gpioy || ',0.0</coordinates></Point><styleUrl>#meterIconRed</styleUrl></Placemark>'\n" + 
				"           as result\n" + 
				"    from meter b\n" + 
				"    where b.location_id = (select id from location where name= '"+ dsoName + "' ) and nvl(b.msa, ';;')=nvl('" + msa +"',';;')\n" + 
				"    and b.modem_id is null and b.gpiox is not null and (b.meter_status!=(select id from code where code='1.3.3.9') or b.meter_status is null)";
		

	    JpaEntityManager eclipseLinkem = em.unwrap(JpaEntityManager.class);
	    DataReadQuery q = new DataReadQuery(meterQeury);
	    CursoredStream eclipseLinkCursor = null;
		try {
			q.setQueryTimeout(queryTimeout);
			q.useCursoredStream(maxResult,maxResult);
			q.doNotCacheQueryResults();
			
	        Session eclipseLinkSession = eclipseLinkem.getActiveSession();
	        eclipseLinkCursor = (CursoredStream) eclipseLinkSession.executeQuery(q);
	        int i=0;
	        while (eclipseLinkCursor.hasNext()) {
	        	DatabaseRecord row  = (DatabaseRecord)eclipseLinkCursor.next();
	        	String placemark = (String)row.get("RESULT");
	        	pw.println(placemark);
	        	logger.debug("-No Modem-" + i + ":" + placemark);
	            i++;
	            if (i % 1000 == 0) {
	                eclipseLinkCursor.clear();
	            }
	        }
	        eclipseLinkCursor.clear();
	        eclipseLinkCursor.close();
		}catch (Exception e) {
			logger.error("writeMeters fail :" + dsoName + e, e);
			if ( eclipseLinkCursor != null )
				eclipseLinkCursor.close();
			throw e;
		}

	}

	@Transactional(readOnly=true)
	public void checkMeterMapTable() throws Exception
	{
//		TransactionStatus txstatus = null;

		try {
			//txstatus = txmanager.getTransaction(null);

			try {
				Query query = em.createNativeQuery("select count(*) from user_tables where table_name = 'METER_MAP'");
				Number result = (Number) query.getSingleResult();
				map_table = result.intValue();
			}catch (Exception e) {
				map_table = 0;
			}
			//txmanager.commit(txstatus);
		}catch (Exception e) {
			logger.error("getDsoNameList fail :" + e, e);
			//if (txstatus != null&& !txstatus.isCompleted())
			//	txmanager.rollback(txstatus);
		}
	}

	@Transactional(readOnly=true)
	public ArrayList<HashMap<String,String>> getDsoMsaList() throws Exception
	{
		ArrayList<HashMap<String,String>> retList = new ArrayList<HashMap<String,String>>();
		
		StringBuffer dsoWhere = new StringBuffer();
		if ( dsoNames != null && dsoNames.length > 0 ) {
			dsoWhere.append(" where b.name in ( ");
			for ( int i = 0; i < dsoNames.length; i++ ) {
				dsoWhere.append("'" + dsoNames[i] +"'");
				if ( i == dsoNames.length -1) {
					dsoWhere.append(")\n");
				}
				else {
					dsoWhere.append(",");
				}
			}
		}

		try {
			String queryStr = "select distinct b.name, a.msa \n" + 
					"from meter a join location b on a.location_id=b.id \n" + 
					dsoWhere +
					"order by b.name, a.msa";
			Query query = em.createNativeQuery(queryStr);
			List<Object[]>  resultList  = query.getResultList();

			for ( Object[] result : resultList ) {
				boolean ex = false;
				if ( exDsoNames != null && exDsoNames.length > 0 ) {
					for ( String exDso : exDsoNames ) {
						if ( exDso.equals((String)result[0]) ) {
							ex = true;
							break;
						}
					}
				}
				if ( !ex ) {
					HashMap<String,String> map = new HashMap<String,String>();
					map.put("dso", (String)result[0]);
					map.put("msa", result[1] == null ? "" : (String)result[1]);
					retList.add(map);
				}
			}
		}catch (Exception e) {
			logger.error("getDsoNameList fail :" + e, e);
			throw e;
		}
		return retList;
	}

	public void writeKml(HashMap<String, String> dsoMsa) {
		
		String dsoName = dsoMsa.get("dso");
		String msa = dsoMsa.get("msa");
		
		String checkmsa = msa.replaceAll("\\/", "").replaceAll(" ", "");
		String tmpPath = outputDir + java.io.File.separator +"map_temp.kml";
		String kmlPath = outputDir + java.io.File.separator +"map_" +  dsoName + "_" + checkmsa +".kml";
		String errPath = outputDir + java.io.File.separator +"map_" +  dsoName + "_" + checkmsa +".error";
		String renameto = null;
		logger.info("writeKml Start:" + kmlPath);
		PrintWriter pw = null;
		try {
			FileWriter file = new FileWriter(tmpPath);
			pw = new PrintWriter(new BufferedWriter(file));
			pw.write(header);

			writeDcus(pw, dsoName);
			if ( useLpEm ) {
				writeMsaMetersWithLpEm(pw,dsoMsa);
			}
			else {
				writeMsaMetersWithSlaRawdata(pw,dsoMsa);
			}
			writeMetersDisconnectModem(pw,dsoMsa);
			pw.write(tail);
			renameto = kmlPath;
		} catch (Exception e) {
			logger.error("writeKml fail :" + kmlPath  + e, e);
			renameto = errPath;
		}
		finally {
			if ( pw != null )
				pw.close();
			File fOld = new File(tmpPath);
			File fNew = new File(renameto);
			fOld.renameTo(fNew);
			logger.info("writeKml Finish : " + tmpPath + " ---> " + renameto );
		}
	}
	@SuppressWarnings("unchecked")
	public void execute( ) {
		Properties prop = new Properties();
		try {
			try{
				prop.load(getClass().getClassLoader().getResourceAsStream("config/NMSCreateDsoKmlMap.properties"));
			}catch(Exception e){
				logger.error("Can't not read property file. -" + e,e);
			}

//			beforeDay    = Integer.parseInt(prop.getProperty("nms.create.dsokmlmap.beforeday"    ,   "7"));
			outputDir = prop.getProperty("nms.create.dsokmlmap.outputdir" , "/home/aimir/aimir4/aimiramm/aimir-web/target/aimir-web-3.3/kml/data");
			maxResult = Integer.parseInt(prop.getProperty("nms.create.dsokmlmap.maxrecord"    ,   "10"));
			queryTimeout =  Integer.parseInt(prop.getProperty("nms.create.dsokmlmap.querytimeout"    ,   "60"));
			useLpEm = Boolean.parseBoolean(prop.getProperty("nms.create.dsokmlmap.uselpem"    ,   "false"));
			
			
			checkMeterMapTable();
			ArrayList<HashMap<String,String>> dsoMsaList = getDsoMsaList();
			for ( HashMap<String,String> dsoMsa : dsoMsaList ) {	
				writeKml(dsoMsa);
			}
		}
		catch (Exception e) {
			logger.error("NMSCreateDsoKmlMap.execute error - " + e, e);
		}
		finally {
			logger.info("NMSCreateDsoKmlMap.execute finish");
		}
	}

	public static void main(String[] args) {
		logger.info("#### NMSCreateDsoKmlMap start. ###");
		long startTime = System.currentTimeMillis();
		String[] dsoNames = null;
		String[] exDsoNames = null;
		for (int i = 0; i < args.length; i += 2) {
			String nextArg = args[i];

			logger.debug("arg[i]=" + args[i] + "arg[i+1]=" + args[i+1]);

			if (nextArg.startsWith("-dsoName")) {
				if ( args[i + 1] != null && args[i + 1].length() > 0 && !"${dsoName}".equals(args[i + 1])) {
					String dsoName = args[i + 1].trim();
					dsoNames = dsoName.split(",");
				}
			}
			else if (nextArg.startsWith("-exDsoName")) {
				if ( args[i + 1] != null && args[i + 1].length() > 0 && !"${dsoName}".equals(args[i + 1])) {
					String exDsoName = args[i + 1].trim();
					exDsoNames = exDsoName.split(",");
				}
			}
		}
		
		try {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "config/spring-NMSCreateDsoKmlMap.xml" });
			DataUtil.setApplicationContext(ctx);
			
			
			NMSCreateDsoKmlMap task = (NMSCreateDsoKmlMap) ctx.getBean(NMSCreateDsoKmlMap.class);
			if ( dsoNames != null &&  dsoNames.length > 0 ) {
				task.setDsoNames(dsoNames);
			}
			if ( exDsoNames != null &&  exDsoNames.length > 0 ) {
				task.setExDsoNames(exDsoNames);
			}
			task.execute();
		} catch (Exception e) {
			logger.error("NMSCreateDsoKmlMap excute error - " + e, e);
		} finally {
			logger.info("#### NMSCreateDsoKmlMap Task finished - Elapse Time : {} ###", DateTimeUtil.getElapseTimeToString(System.currentTimeMillis() - startTime));
			System.exit(0);
		}
	}
}
