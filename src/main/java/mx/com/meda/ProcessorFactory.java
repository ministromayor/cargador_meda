package mx.com.meda;

import org.apache.log4j.Logger;

import mx.com.meda.imp.SanbornsProcessor;
import mx.com.meda.imp.HitssProcessor;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class ProcessorFactory {


	public static Processor getProcessorInstance(String peerName) {
		Logger log = Logger.getLogger(ProcessorFactory.class);
		Processor procesador = null;
		try {
			SFTPClient sftp = new SFTPClient(peerName);
			DataWrapper dw = new DataWrapper();
			if( peerName.equalsIgnoreCase("hitss") ) {
				procesador = new HitssProcessor(sftp, dw);
			}
		}catch( JSchException ex ) {
			log.error("Ocurrion un error al generar el cliente de SFTP.");
			log.debug(ex.getMessage());
		} finally {
			return procesador;
		}
	}

} 