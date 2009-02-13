package org.sonata.framework.control.invoker;

import org.sonata.framework.common.ConnectionTranslation;
import org.sonata.framework.common.SymphonyObject;

/**
 * D�crit le lien 1-* entre un OME (instance) et les OIE correspondants (instance),
 * et vice-versa
 * Contient �galement la r�f�rence vers la translation charg�e d'effectuer la conversion
 * entre la source et la destination (la translation est cens�e int�grer la m�thode � appeler)
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
