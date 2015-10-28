package mx.com.meda.imp;

import org.apache.log4j.Logger;
import mx.com.meda.Processor;
import mx.com.meda.DataWrapper;
import mx.com.meda.SFTPClient;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.JSchException;

import mx.com.meda.Socio;

public class HitssProcessor implements Processor {

	Logger log = Logger.getLogger(this.getClass());
	private SFTPClient cliente;
	private DataWrapper dw;
	private String delimitador = "|";
	private int itemsXlinea = 7;
	Socio socio = Socio.HITSS;

	public HitssProcessor(SFTPClient sftp, DataWrapper dw) throws IOException {
		//Lanzar una excepción si el tipo de socio registrado en el DataWrapper o el SFTPClient es de un tipo distinto a HITSS
		this.cliente = sftp;
		this.dw = dw;
	}

	public HitssProcessor() {
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
				String file_name = cliente.lastAddedInFileName();
				log.info("Se cargará el achivo: "+file_name);
				BufferedReader br = new BufferedReader(new InputStreamReader(cliente.readLastInFile()));
				String linea = null;	
				while( (linea = br.readLine()) != null ) {
					log.debug(linea);
					//Tokenizar la cadena separando con el caracter delimitador configurado en el properties de esta implementación.
					StringTokenizer stk = new StringTokenizer(linea, delimitador);
					String[] values = new String[itemsXlinea+1];
					values[0] = file_name;
					for(int i = 1; i < values.length; i++) {
						if( stk.hasMoreElements() ) {
							values[i] = stk.nextToken().trim();
						} else {
							log.error("No se encontraron mas elementos en la linea pero se esperaban "+
											itemsXlinea+
											" elementos mientras que se encontraron "+
											(i)+
											" elementos");
							values[i] = "";
						}
					}
					dw.cargarLinea(3, values);
				}
				br.close();
				cliente.desconectar();
				dw.performCommit();
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