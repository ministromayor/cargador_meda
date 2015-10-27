package mx.com.meda.imp;

import org.apache.log4j.Logger;
import mx.com.meda.Processor;
import mx.com.meda.DataWrapper;
import mx.com.meda.SFTPClient;

import java.io.IOException;


public class SanbornsProcessor implements Processor {

	Logger log = Logger.getLogger(this.getClass());

	public SanbornsProcessor(String path) throws IOException {
	}

	public boolean procesarEntrada() {
		//En este caso la entrada es la respuesta de la validación de los tickets por parte de Sanborns.
		return true;
	}

	public boolean procesarSalida() { 
		//Este método requiere extraer de la base de datos la información para la generación del archivo de carga para sanborns.
		return true;
	}

	public void release() {
	}


}