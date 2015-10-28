package mx.com.meda.imp;

import org.apache.log4j.Logger;
import mx.com.meda.Processor;
import mx.com.meda.DataWrapper;
import mx.com.meda.SFTPClient;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.JSchException;

import mx.com.meda.Socio;

public class OSTARProcessor implements Processor {

	Logger log = Logger.getLogger(this.getClass());
	private SFTPClient cliente;
	private DataWrapper dw;
	Socio socio = Socio.OSTAR;

	public OSTARProcessor(SFTPClient sftp, DataWrapper dw) throws IOException {
		//Lanzar una excepción si el tipo de socio registrado en el DataWrapper o el SFTPClient es de un tipo distinto a OSTAR
		this.cliente = sftp;
		this.dw = dw;
	}

	public OSTARProcessor() {
		try {
			this.cliente = new SFTPClient(socio);
			this.dw = new DataWrapper(socio);
		} catch (JSchException ex ) {
			log.error("Ocurrio un error al generar el cliente SFTP.");
			log.debug(ex.getMessage());
			//Manejar las excepciones futuras en el manejo de una clase instanciada con errores.
		}
	}

	public boolean procesarEntrada() {
		log.debug("Se comenzará la lectura del archivo de entrada.");
		try {
			if( cliente.conectar() )  {
				BufferedReader br = new BufferedReader(new InputStreamReader(cliente.readLastInFile()));
				String linea = null;	
				while( (linea = br.readLine()) != null ) {
					log.info(linea);
				}
				br.close();
				cliente.desconectar();
			}
		} catch( SftpException ex ) {
			ex.printStackTrace();
		} finally {
			return true;
		}
	}

	public boolean procesarSalida() { 
		//Este método requiere extraer de la base de datos la información para la generación del archivo de carga para sanborns.
		return true;
	}

	public void release() {
		dw.release();
	}


}