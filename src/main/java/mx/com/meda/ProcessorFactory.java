package mx.com.meda;

import org.apache.log4j.Logger;

import mx.com.meda.imp.SanbornsProcessor;
import mx.com.meda.imp.HitssProcessor;
import mx.com.meda.imp.OSTARProcessor;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class ProcessorFactory {

	private static Logger log = Logger.getLogger(ProcessorFactory.class);

	public static Processor getProcessorInstance(Socio peer) {
		log.debug("Se generará un procesador del tipo: "+peer.getNombre());
		Processor procesador = null;
		switch(peer) {
			case HITSS : 
				procesador = new HitssProcessor();
				break;
			case OSTAR :
				procesador = new OSTARProcessor();
				break;
		}
		return procesador;
	}

} 