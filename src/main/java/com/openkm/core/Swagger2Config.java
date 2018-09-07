/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.core;

import org.apache.cxf.jaxrs.swagger.Swagger2Feature;

public class Swagger2Config extends Swagger2Feature {

    /*
     * Swagger2 integration notes
     *
     * The reason why has been created the class rather using xml annotation into appContext.xml class is that the number
     * of properties available from there are limited and someones like licenseUrl raise and error meanwhile from class
     * it works right.
     *
     * The best starting point at -> http://cxf.apache.org/docs/swagger2feature.html
     * For looking -> https://adarshthimmappa.wordpress.com/2017/11/22/rest-api-documentation-via-swagger/
     * For looking -> http://massfords.com/Spring-Swagger-CXF/
     *
     * Explain mandatory tags ( in our case @Path was missing what caused the services where not scanned )
     * https://github.com/swagger-api/swagger-core/wiki/Annotations-1.5.X
     *
     * Issue with multiple jars server, to solve should be used setUsePathBasedConfig but at the end has not been working
     * and decided going into single server point rather than multiple for each service class
     * https://stackoverflow.com/questions/32948315/swagger-integration-for-multiple-jaxrs-servers
     *
     * Another good documentation but because we are using Apache CXF can not be applied in our case:
     * https://www.concretepage.com/spring-4/spring-rest-swagger-2-integration-with-annotation-xml-example
     * http://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
     * https://stackoverflow.com/questions/26720090/a-simple-way-to-implement-swagger-in-a-spring-mvc-application
     */

    public Swagger2Config() {
        super();
        // this.setBasePath("/OpenKM/services");
        // this.setUsePathBasedConfig(true); // not working for several jaxrs:server
        this.setResourcePackage("com.openkm.rest");
        this.setScan(true);
        this.setTitle("OpenKM REST API");
        this.setContact("contact@openkm.com");
        this.setDescription("Copyright © OpenKM Knowledge Management System S.L. \n"
                + "This program is free software; you can redistribute it and/or modify\n"
                + "it under the terms of the GNU General Public License as published by\n"
                + "the Free Software Foundation; either version 2 of the License.");
        this.setLicense("Community license of API");
        this.setLicenseUrl("https://docs.openkm.com/kcenter/view/licenses/");
    }
}