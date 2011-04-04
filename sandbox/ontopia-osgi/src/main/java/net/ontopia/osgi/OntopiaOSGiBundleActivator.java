package net.ontopia.osgi;

import net.ontopia.Ontopia;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class OntopiaOSGiBundleActivator implements BundleActivator {

	private Logger logger = Logger.getLogger(OntopiaOSGiBundleActivator.class);
	
	public void start(BundleContext context) throws Exception {
		logger.info("Started [" + Ontopia.getInfo() + "]");
	}

	public void stop(BundleContext context) throws Exception {
		logger.info("Stopped [" + Ontopia.getInfo() + "]");
	}

}
