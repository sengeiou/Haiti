//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.22 at 06:41:41 PM KST 
//


package _1_release.gml_v4;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.multispeak.version_4.GMLLine;


/**
 * <p>Java class for LineStringType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LineStringType">
 *   &lt;complexContent>
 *     &lt;extension base="{gml_V4.1_Release}AbstractGeometryType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="coord" type="{gml_V4.1_Release}CoordType" minOccurs="0"/>
 *           &lt;element name="coordinates" type="{gml_V4.1_Release}CoordinatesType" minOccurs="0"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LineStringType", propOrder = {
    "coordOrCoordinates"
})
@XmlSeeAlso({
    GMLLine.class
})
public class LineStringType
    extends AbstractGeometryType
{

    @XmlElements({
        @XmlElement(name = "coord", type = CoordType.class),
        @XmlElement(name = "coordinates", type = CoordinatesType.class)
    })
    protected List<Object> coordOrCoordinates;

    /**
     * Gets the value of the coordOrCoordinates property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the coordOrCoordinates property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCoordOrCoordinates().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CoordType }
     * {@link CoordinatesType }
     * 
     * 
     */
    public List<Object> getCoordOrCoordinates() {
        if (coordOrCoordinates == null) {
            coordOrCoordinates = new ArrayList<Object>();
        }
        return this.coordOrCoordinates;
    }

}
