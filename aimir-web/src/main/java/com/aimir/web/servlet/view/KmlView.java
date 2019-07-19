package com.aimir.web.servlet.view;

import java.io.PrintWriter;
import java.util.Map;
 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

import de.micromata.opengis.kml.v_2_2_0.Kml;

public class KmlView extends AbstractView {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        try {
            Kml kml = (Kml) model.get("kml");
            PrintWriter writer = response.getWriter();
            kml.marshal(writer);
            writer.close();
        } catch (Exception e) {
        }
    }
}