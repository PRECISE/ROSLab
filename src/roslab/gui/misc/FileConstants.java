/**
 * Copyright 2009-2011, Kansas State University, 
 * PRECISE Center at the University of Pennsylvania, and
 * Yu Jin Kim
 *
 *
 * This file is part of the Medical Device Coordination Framework (aka the MDCF)
 *
 *   The MDCF is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   The MDCF  is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the MDCF .  If not, see <http://www.gnu.org/licenses/>.
 */
package roslab.gui.misc;

import java.io.File;

public class FileConstants {
	public static final String VERSION = "0.1";
	
	public static final String WORKSPACE = ".wksp.xml";
	public static final String COMPONENTSIG = ".compsig.xml";
	public static final String APPCONFIGURATION = ".cfg.xml";
	public static final String SKELETON = ".java";
	
	public static final String SKELETONFOLDER = File.separator + "mdcf" + File.separator + "app";
	public static final String APPCONFIGFOLDER = File.separator + "appcfg";
	public static final String APPCOMPFOLDER	= File.separator + "appcomp";
	public static final String DESCFOLDER = File.separator + "desc";
	
	public static final String CORERELATIVECPATHFROMAPPDEV = ".." + File.separator + "mdcf2-core" + File.separator + "bin";
	public static final String CHANNELSERVICERELATIVECPATHFROMAPPDEV = ".." + File.separator + "mdcf2-channelservice" + File.separator + "bin";
	public static final String APPARCHIVERELATVIEPATHFROMAPPDEV = ".." + File.separator + "mdcf2-apps" + File.separator + "app_archives";
	
	public static final String DESCRIPTORNAME = "appdesc.data";
	public static final String USERSETTINGFILE = "user.settings";
}
