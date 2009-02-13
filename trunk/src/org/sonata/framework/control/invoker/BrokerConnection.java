package org.sonata.framework.control.invoker;

import org.sonata.framework.common.ConnectionTranslation;
import org.sonata.framework.common.SymphonyObject;

/**
 * Décrit le lien 1-* entre un OME (instance) et les OIE correspondants (instance),
 * et vice-versa
 * Contient également la référence vers la translation chargée d'effectuer la conversion
 * entre la source et la destination (la translation est censée intégrer la méthode à appeler)
 * @author Guillaume Godet-Bar
 *
 */
public class BrokerConnection {

	private SymphonyObject source ;
	private ConnectionTranslation translation ;
	
	
	public BrokerConnection() {} ;
	
	public BrokerConnection(SymphonyObject source, ConnectionTranslation wrapper) {
		this.source = source ;
		this.translation = wrapper ;
	}

	public SymphonyObject getSource() {
		return source;
	}

	public ConnectionTranslation getTranslation() {
		return translation;
	}

	public void setSource(final SymphonyObject source) {
		this.source = source ;
	}
	
	public void setTranslation(final ConnectionTranslation wrapper) {
		this.translation = wrapper ;
	}
}
